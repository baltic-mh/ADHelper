/**
 * TransferController.java
 *
 * Created on 08.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.CheckSumInfo;
import teambaltic.adhelper.remoteaccess.IRemoteAccess;
import teambaltic.adhelper.remoteaccess.LocalRemotePathPair;
import teambaltic.adhelper.utils.CheckSumCreator;
import teambaltic.adhelper.utils.CheckSumCreator.Type;
import teambaltic.adhelper.utils.ICryptUtils;

// ############################################################################
public class TransferController implements ITransferController
{
    private static final Logger sm_Log = Logger.getLogger(TransferController.class);

    // ------------------------------------------------------------------------
    private final CheckSumCreator   m_CheckSumCreator;
    private CheckSumCreator getCheckSumCreator(){ return m_CheckSumCreator; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final ICryptUtils       m_CryptUtils;
    private ICryptUtils getCryptUtils(){ return m_CryptUtils; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IRemoteAccess     m_RemoteAccess;
    private IRemoteAccess getRemoteAccess(){ return m_RemoteAccess; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final ISingletonWatcher m_SingletonWatcher;
    private ISingletonWatcher getSingletonWatcher(){ return m_SingletonWatcher; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Path m_SandBox;
    private Path getSandBox(){ return m_SandBox; }
    // ------------------------------------------------------------------------

    public TransferController(
            final Path              fSandBox,
            final ICryptUtils       fCryptUtils,
            final IRemoteAccess     fRemoteAccess,
            final ISingletonWatcher fSingletonWatcher )
    {
        m_CheckSumCreator   = new CheckSumCreator( Type.MD5 );
        m_SandBox           = fSandBox;
        m_CryptUtils        = fCryptUtils;
        m_RemoteAccess      = fRemoteAccess;
        m_SingletonWatcher  = fSingletonWatcher;
    }

    @Override
    public void start() throws Exception
    {
        getSingletonWatcher().start();
        if( !isConnected() ){
            sm_Log.warn( "Keine Verbindung zum Server!" );
            return;
        }
        downloadBaseInfoFile();
    }

    @Override
    public void shutdown() throws Exception
    {
        getSingletonWatcher().stop();
        getRemoteAccess().close();
    }

    @Override
    public boolean isConnected()
    {
        return getSingletonWatcher().isConnected();
    }

    /**
     * TODO Wie ist das Remote-File angegeben? Hinten mit .cry? Annahme: ohne .cry!
     *      Wahrscheinlich doch besser MIT .cry!
     * @param fPathPair -
     */
    private void download(final LocalRemotePathPair fPathPair)
    {
        final Path aDataFileLocal       = fPathPair.getLocal();
        final Path aDataFileRemote      = fPathPair.getRemote();

        final CheckSumCreator aCSC      = getCheckSumCreator();
        final Path aCheckSumFileLocal   = aCSC.getCheckSumFile( aDataFileLocal );
        final Path aCheckSumFileRemote  = aCSC.getCheckSumFile( aDataFileRemote );

        try{
            final CheckSumInfo aCSILocal = aCSC.calculate( aDataFileLocal );
            if( aCSILocal != null ){
                final Path aCheckSumFileInSandBox = getSandBoxPath( aCheckSumFileLocal );
                final LocalRemotePathPair aPathPair_CheckSum = new LocalRemotePathPair(
                        aCheckSumFileInSandBox, aCheckSumFileRemote );
                m_RemoteAccess.download( aPathPair_CheckSum );
                final CheckSumInfo aCSIRemote = aCSC.calculate( aCheckSumFileInSandBox );
                if( aCSILocal.getHash().equals( aCSIRemote.getHash() )){
                    return;
                }
            }
            final Path aDataFileInSandBox = getSandBoxPath( aDataFileLocal );
            final LocalRemotePathPair aPathPair_DataFiles = new LocalRemotePathPair(
                    aDataFileInSandBox, aDataFileRemote );
            m_RemoteAccess.download( aPathPair_DataFiles );
            final Path aDataFileInSandBox_Decrypted = getCryptUtils().decrypt( aDataFileInSandBox );
            Files.copy( aDataFileInSandBox_Decrypted, aDataFileLocal);
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
    }

    @Override
    public void upload(final LocalRemotePathPair fPathPair)
    {
        final Path aLocal = fPathPair.getLocal();
        try{
            final Path aLocalFileInSandBox = copyFileToSandBox( aLocal );
            final CheckSumInfo aLocal_CSI = getCheckSumCreator().calculate( aLocalFileInSandBox );
            final Path aLocal_Encrypted = getCryptUtils().encrypt( aLocal );
//            m_RemoteAccess.upload( aPathPair_Crypted );
//            m_RemoteAccess.upload( aPathPair_CheckSum );
            // MD5-Datei und verschlüsselte Quelldatei hochladen.
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
    }

    private Path copyFileToSandBox( final Path fFile ) throws IOException
    {
        final Path aFileInSandBox = Files.copy( fFile, getSandBoxPath( fFile ) );
        return aFileInSandBox;
    }

    private Path getSandBoxPath( final Path fFile )
    {
        return Paths.get( getSandBox().toString(), fFile.toFile().getName() );
    }

    private void downloadBaseInfoFile()
    {
        // TODO Auto-generated method stub

    }

}

// ############################################################################
