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
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
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
    private final Path m_SandBox;
    private Path getSandBox(){ return m_SandBox; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Path m_RootFolder;
    private Path getRootFolder(){ return m_RootFolder; }
    // ------------------------------------------------------------------------

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

    public TransferController(
            final Path              fRootFolder,
            final Path              fSandBox,
            final ICryptUtils       fCryptUtils,
            final IRemoteAccess     fRemoteAccess,
            final ISingletonWatcher fSingletonWatcher )
    {
        m_CheckSumCreator   = new CheckSumCreator( Type.MD5 );
        m_RootFolder        = fRootFolder;
        m_SandBox           = initSandBox(fSandBox);
        m_CryptUtils        = fCryptUtils;
        m_RemoteAccess      = fRemoteAccess;
        m_SingletonWatcher  = fSingletonWatcher;
    }

    @Override
    public void start() throws Exception
    {
        getSingletonWatcher().start();
    }

    @Override
    public void shutdown() throws Exception
    {
        getSingletonWatcher().stop();
        getRemoteAccess().close();
        Files.deleteIfExists( getSandBox() );
    }

    @Override
    public boolean isConnected()
    {
        return getSingletonWatcher().isConnected();
    }

    @Override
    public void download(final Path fFileToDownload)
    {
        final CheckSumCreator aCSC  = getCheckSumCreator();
        final Path aCheckSumFile    = aCSC.getCheckSumFile( fFileToDownload );

        try{
            final CheckSumInfo aCSILocal = aCSC.calculate( fFileToDownload );
            if( aCSILocal != null ){
                final Path aCheckSumFileInSandBox = getSandBoxPath( aCheckSumFile );
                final LocalRemotePathPair aPathPair_CheckSum = new LocalRemotePathPair(
                        aCheckSumFileInSandBox, aCheckSumFile );
                m_RemoteAccess.download( aPathPair_CheckSum );
                final CheckSumInfo aCSIRemote = CheckSumInfo.readFromFile( aCheckSumFileInSandBox );
                if( aCSILocal.getHash().equals( aCSIRemote.getHash() )){
                    sm_Log.info( "Lokale Datei ist identisch mit Server-Version: "+ fFileToDownload);
                    return;
                }
            }
            final Path aFileToDownloadInSandBox = getSandBoxPath( fFileToDownload );
            final Path aFileToDownloadInSandBox_Cry = Paths.get( aFileToDownloadInSandBox.toString()+".cry" );
            final Path aFileToDownload_Cry          = Paths.get( fFileToDownload+".cry" );
            final LocalRemotePathPair aPathPair_DataFiles = new LocalRemotePathPair(
                    aFileToDownloadInSandBox_Cry, aFileToDownload_Cry );
            m_RemoteAccess.download( aPathPair_DataFiles );
            final Path aDataFileInSandBox_Decrypted = getCryptUtils().decrypt( aFileToDownloadInSandBox_Cry );
            Files.copy( aDataFileInSandBox_Decrypted, fFileToDownload, StandardCopyOption.REPLACE_EXISTING);
            sm_Log.info("Datei heruntergeladen: "+fFileToDownload);
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
    }

    @Override
    public void upload(final Path fFileToUpload) throws Exception
    {
        final ICryptUtils aCryptUtils = getCryptUtils();
        if( aCryptUtils == null ){
            sm_Log.warn( "Kein Verschlüsselungsobjekt! Hochladen nicht möglich!" );
            return;
        }
        final Path aLocalFile_RelativeToRoot = getRootFolder().resolve( fFileToUpload ).normalize();
        if(Files.isDirectory( aLocalFile_RelativeToRoot )){
            throw new UnsupportedOperationException("Noch nicht implementiert!");
        }
        uploadFile( aLocalFile_RelativeToRoot );
    }
    private void uploadFile(final Path fFileToUpload) throws Exception
    {
        final ICryptUtils aCryptUtils = getCryptUtils();
        final CheckSumCreator aCSC = getCheckSumCreator();

        final Path aFileToUploadInSandBox = copyFileToSandBox( fFileToUpload );
        final CheckSumInfo aCSI  = aCSC.calculate( aFileToUploadInSandBox );
        final Path aFileToUploadInSandBox_Encrypted = aCryptUtils.encrypt( aFileToUploadInSandBox );
        final Path aRemotePath_DataFile = Paths.get( fFileToUpload.toString()+"."+FilenameUtils.getExtension( aFileToUploadInSandBox_Encrypted.toString() ) );

        final LocalRemotePathPair aPathPair_Files = new LocalRemotePathPair(
                aFileToUploadInSandBox_Encrypted, aRemotePath_DataFile );
        m_RemoteAccess.upload( aPathPair_Files );

        final Path aCheckSumFile = aCSC.write( aFileToUploadInSandBox, aCSI );
        final Path aRemotePath_CheckSumFile = fFileToUpload.getParent().resolve( aCheckSumFile.getFileName() );
        final LocalRemotePathPair aPathPair_CheckSum = new LocalRemotePathPair(
                aCheckSumFile, aRemotePath_CheckSumFile );
        m_RemoteAccess.upload( aPathPair_CheckSum );
    }

    private static Path initSandBox( final Path fSandBox )
    {
        if( Files.exists( fSandBox )){
            return fSandBox;
        }
        try{
            Files.createDirectories( fSandBox );
            return fSandBox;
        }catch( final IOException fEx ){
            sm_Log.warn("Exception: ", fEx );
            return null;
        }
    }
    private Path copyFileToSandBox( final Path fFile ) throws IOException
    {
        final Path aFileInSandBox = Files.copy( fFile, getSandBoxPath( fFile ), StandardCopyOption.REPLACE_EXISTING );
        return aFileInSandBox;
    }

    private Path getSandBoxPath( final Path fFile )
    {
        final Path aPath = getSandBox().resolve( fFile.getFileName() );
        return aPath;
    }

}

// ############################################################################
