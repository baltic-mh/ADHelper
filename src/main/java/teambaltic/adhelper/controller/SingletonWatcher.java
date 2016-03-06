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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Scanner;

import org.apache.log4j.Logger;

import teambaltic.adhelper.remoteaccess.IRemoteAccess;
import teambaltic.adhelper.remoteaccess.LocalRemotePathPair;

// ############################################################################
public class SingletonWatcher
{
    private static final Logger sm_Log = Logger.getLogger(SingletonWatcher.class);

    private static final String sm_FileHeader = "TimeStamp;TimeStamp(human readable);Info\r\n";
    private static final String sm_BusyFileBaseName = "BusyFile";
    private static final String sm_BusyFileExt = ".txt";
    private static final String sm_BusyFileName = sm_BusyFileBaseName+sm_BusyFileExt;
    private static final Path   sm_RemoteBusyFilePath = Paths.get( sm_BusyFileName );

    // ------------------------------------------------------------------------
    private final String m_Info;
    private String getInfo(){ return m_Info; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final long m_CycleTime;
    private long getCycleTime(){ return m_CycleTime; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IRemoteAccess m_RemoteAccess;
    private IRemoteAccess getRemoteAccess(){ return m_RemoteAccess; }
    // ------------------------------------------------------------------------

    private Thread m_Thread;

    public SingletonWatcher(
            final String fInfo,
            final long fCycleTime,
            final IRemoteAccess fRemoteAccess )
    {
        m_Info          = fInfo;
        m_CycleTime     = fCycleTime;
        m_RemoteAccess  = fRemoteAccess;
    }

    public boolean amIAlone()
    {
        try{
            final Path aLocalFile = Files.createTempFile( sm_BusyFileBaseName, sm_BusyFileExt );
            final LocalRemotePathPair aPathPair = createLocalRemotePathPair( aLocalFile );
            getRemoteAccess().download( aPathPair );
            boolean aIamAlone = false;
            if( isOutDated( aLocalFile ) ){
                getRemoteAccess().delete( Paths.get( sm_BusyFileName ) );
                aIamAlone = true;
            }
            Files.delete( aLocalFile );
            return aIamAlone;
        }catch( final Exception fEx ){
            // Das ist zwar etwas gewagt, aber ich gehe mal davon aus, dass
            // niemand sonst am Machen ist, wenn etwas mit dem Download schief
            // gegangen ist:
            return true;
        }
    }

    private static LocalRemotePathPair createLocalRemotePathPair( final Path fLocalFile )
    {
        return new LocalRemotePathPair( fLocalFile, sm_RemoteBusyFilePath );
    }

    public void start()
    {
        if( m_Thread != null ){
            return;
        }
        m_Thread = new Thread("BusyController"){
            @Override
            public void run(){
                while( true ){
                    try{
                        uploadBusyFlag();
                        Thread.sleep( getCycleTime() );
                    }catch( final InterruptedException fEx ){
                        break;
                    }
                }
            }
        };
        m_Thread.setDaemon( true );
        m_Thread.start();
    }

    public void stop()
    {
        if( m_Thread == null ){
            return;
        }
        m_Thread.interrupt();
        m_Thread = null;
        removeBusyFlag();
    }

    private void uploadBusyFlag()
    {
        final Path aBusyFile = createBusyFile();
        uploadBusyFile( aBusyFile );
        try{
            Files.delete( aBusyFile );
        }catch( final IOException fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
    }

    private void removeBusyFlag()
    {
        try{
            getRemoteAccess().delete( sm_RemoteBusyFilePath );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
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
            sm_Log.warn("Exception: ", fEx );
            return null;
        }
    }

    private void uploadBusyFile( final Path fBusyFile )
    {
        try{
            final LocalRemotePathPair aPathPair = createLocalRemotePathPair( fBusyFile );
            getRemoteAccess().upload( aPathPair );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }

    }

    private boolean isOutDated( final Path fLocalFile )
    {
        Scanner aScanner = null;
        try{
            aScanner = new Scanner(fLocalFile.toFile());
            // Titelzeile:
            String aLine = aScanner.nextLine();
            // Zeile mit Inhalt:
            aLine = aScanner.nextLine();
            final String[] aParts = aLine.split( ";" );
            final String aTimeStr = aParts[0];
            final long aTimeStamp= Long.parseLong( aTimeStr );
            final long aNow = System.currentTimeMillis();
            return ( aNow - aTimeStamp > 2*getCycleTime() );

        } catch( final FileNotFoundException fEx ){
            return true;
        } finally {
            if( aScanner != null ){
                aScanner.close();
            }
        }
    }

}

// ############################################################################
