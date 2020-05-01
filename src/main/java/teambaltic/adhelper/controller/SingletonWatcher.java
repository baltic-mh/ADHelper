/**
 * SingletonWatcher.java
 *
 * Created on 29.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import teambaltic.adhelper.remoteaccess.IRemoteAccess;
import teambaltic.adhelper.remoteaccess.LocalRemotePathPair;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class SingletonWatcher implements ISingletonWatcher
{
    private static final Logger sm_Log = Logger.getLogger(SingletonWatcher.class);

    private static final String sm_FileHeader = "#TimeStamp;TimeStamp(human readable);Info\r\n";
    private static final String sm_BusyFileBaseName = "BusyFile";
    private static final String sm_BusyFileExt = ".txt";
    private static final String sm_BusyFileName = sm_BusyFileBaseName+sm_BusyFileExt;

    // ------------------------------------------------------------------------
    private final String m_Info;
    private String getInfo(){ return m_Info; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_RootFolderName;
    private String getRootFolderName(){ return m_RootFolderName; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final long m_CycleTime;
    private long getCycleTime(){ return m_CycleTime; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IRemoteAccess m_RemoteAccess;
    private IRemoteAccess getRemoteAccess(){ return m_RemoteAccess; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private boolean m_Connected;
    @Override
    public boolean isConnected(){ return m_Connected; }
    private void setConnected( final boolean fConnected ){ m_Connected = fConnected; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private boolean m_FremderMannAmWerk;
    private boolean isFremderMannAmWerk() { return m_FremderMannAmWerk; }
    private void setFremderMannAmWerk( final boolean fNewVal ) { m_FremderMannAmWerk = fNewVal; }
    // ------------------------------------------------------------------------

    private final Object m_BusyBlocker;

    private Thread m_BusyControllerThread;

    public SingletonWatcher(
            final String fRootFolderName,
            final String fInfo,
            final long fCycleTime,
            final IRemoteAccess fRemoteAccess )
    {
        m_RootFolderName= fRootFolderName;
        m_Info          = fInfo;
        m_CycleTime     = fCycleTime;
        m_RemoteAccess  = fRemoteAccess;
        m_BusyBlocker   = new Object();
    }

    @Override
    public String getRemoteInfo()
    {
        if( getRemoteAccess() == null ){
            return null;
        }
        Path aLocalFile = null;
        try{
            aLocalFile = Files.createTempFile( sm_BusyFileBaseName, sm_BusyFileExt );
            final LocalRemotePathPair aPathPair = createLocalRemotePathPair( aLocalFile );
            getRemoteAccess().download( aPathPair );
            if( isOutDated( aLocalFile ) ){
                getRemoteAccess().delete( Paths.get( sm_BusyFileName ) );
                return null;
            }
            final List<String> aLines = FileUtils.readAllLines( aLocalFile, 1 );
            return aLines.get( 0 );
        }catch( final Exception fEx ){
            sm_Log.warn(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName() , fEx.getMessage() ));
            return null;
        } finally {
            if( aLocalFile != null ){
                try{
                    Files.delete( aLocalFile );
                }catch( final IOException fEx ){
                    sm_Log.warn(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName() , fEx.getMessage() ));
                }
            }
        }
    }

    private LocalRemotePathPair createLocalRemotePathPair( final Path fLocalFile )
    {
        return new LocalRemotePathPair( fLocalFile, getRemoteBusyFilePath() );
    }

    private Path getRemoteBusyFilePath() {
        return Paths.get( getRootFolderName(), sm_BusyFileName );
    }

    @Override
    public String start()
    {
        final IRemoteAccess aRemoteAccess = getRemoteAccess();
        if( aRemoteAccess == null ){
            sm_Log.warn("No remote access object!");
            return null;
        }

        // Hier gibt es eine kleine Lücke!
        // Wenn auf Rechner1 das Programm gestartet und hier festgestellt wird,
        // dass noch keine andere Applikation läuft, dauert es ein paar Milli-
        // sekunden, bis die Busy-Datei von Rechner1 auf dem Server erzeugt
        // worden ist. Wenn in dieser Zeit das Programm, das auf Rechner2
        // gestartet worden ist, nachfragt, ob schon eine andere Applikation
        // läuft, bekommt es fälschlich freie Fahrt signalisiert!
        // Das Risiko lasse ich mal so stehen.
        setFremderMannAmWerk(false);
        final String aRemoteInfo = getRemoteInfo();
        if( aRemoteInfo != null ){
            sm_Log.warn( "Es läuft schon eine andere Applikation: "+aRemoteInfo );
            setFremderMannAmWerk(true);
        }
        if( m_BusyControllerThread != null ){
            return aRemoteInfo;
        }
        // Damit es schon einmal gemacht ist, bevor die Methode beendet ist:
        checkConnection();
        m_BusyControllerThread = new Thread("BusyController"){
            @Override
            public void run(){
                while( true ){
                    try{
                        synchronized( m_BusyBlocker ){
                            if( isInterrupted() ){
                                break;
                            }
                            m_BusyBlocker.wait( getCycleTime() );
                            checkConnection();
                        }
                    }catch( final InterruptedException fEx ){
                        break;
                    }
                }
            }
        };
        m_BusyControllerThread.setDaemon( true );
        m_BusyControllerThread.start();
        return aRemoteInfo;
    }
    @Override
    public void stop()
    {
        if( m_BusyControllerThread == null ){
            return;
        }
        synchronized( m_BusyBlocker ){
            m_BusyControllerThread.interrupt();
            m_BusyControllerThread = null;
            removeBusyFlag();
        }
    }

    private void checkConnection() {
        final Path aBusyFile = createBusyFile();
        if( isFremderMannAmWerk() ) {
            try {
                getRemoteAccess().exists( Paths.get(getRootFolderName()));
                setConnected(true);
            } catch ( final Exception fEx ) {
                setConnected(false);
                sm_Log.warn(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName() , fEx.getMessage() ));

            }
        } else {
            uploadBusyFlag( aBusyFile );
        }
    }

    private void uploadBusyFlag(final Path fBusyFile)
    {
        uploadBusyFile( fBusyFile );
        try{
            Files.delete( fBusyFile );
        }catch( final IOException fEx ){
            sm_Log.warn(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName() , fEx.getMessage() ));
        }
    }

    private void removeBusyFlag()
    {
        try{
            getRemoteAccess().delete( getRemoteBusyFilePath() );
        }catch( final Exception fEx ){
            sm_Log.warn(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName() , fEx.getMessage() ));
        }
    }

    private Path createBusyFile()
    {
        try{
            final Path aLocalFile = Files.createTempFile( sm_BusyFileBaseName, sm_BusyFileExt );
            Files.write( aLocalFile, sm_FileHeader.getBytes(), StandardOpenOption.WRITE );
            final Date aNow = new Date();
            final String aString = String.format( "%d;%s;%s\r\n", aNow.getTime(), aNow, getInfo() );
            Files.write( aLocalFile, aString.getBytes(), StandardOpenOption.APPEND );
            return aLocalFile;
        }catch( final IOException fEx ){
            sm_Log.warn(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName() , fEx.getMessage() ));
            return null;
        }
    }

    private void uploadBusyFile( final Path fBusyFile )
    {
        final IRemoteAccess aRemoteAccess = getRemoteAccess();
        if( aRemoteAccess == null ){
            return;
        }
        try{
            final LocalRemotePathPair aPathPair = createLocalRemotePathPair( fBusyFile );
            aRemoteAccess.upload( aPathPair );
            setConnected(true);
        }catch( final Exception fEx ){
            setConnected(false);
            sm_Log.warn(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName() , fEx.getMessage() ));
        }

    }

    private boolean isOutDated( final Path fLocalFile )
    {
        final File aLocalFile = fLocalFile.toFile();
        if( aLocalFile.length() == 0 ){
            return true;
        }
        Scanner aScanner = null;
        try{
            aScanner = new Scanner(aLocalFile);
            if( !aScanner.hasNextLine() ) {
                return true;
            }
            // Titelzeile:
            String aLine = aScanner.nextLine();
            // Zeile mit Inhalt:
            aLine = aScanner.nextLine();
            final String[] aParts = aLine.split( ";" );
            final String aTimeStr = aParts[0];
            final long aTimeStamp= Long.parseLong( aTimeStr );
            final long aNow = System.currentTimeMillis();
            return ( aNow - aTimeStamp > 3*getCycleTime() );

        } catch( final FileNotFoundException fEx ){
            return false;
        } finally {
            if( aScanner != null ){
                aScanner.close();
            }
        }
    }

}

// ############################################################################
