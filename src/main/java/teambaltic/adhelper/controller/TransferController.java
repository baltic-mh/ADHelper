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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import teambaltic.adhelper.model.CheckSumInfo;
import teambaltic.adhelper.model.ERole;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings;
import teambaltic.adhelper.model.settings.IUserSettings;
import teambaltic.adhelper.remoteaccess.IRemoteAccess;
import teambaltic.adhelper.remoteaccess.LocalRemotePathPair;
import teambaltic.adhelper.utils.CheckSumCreator;
import teambaltic.adhelper.utils.CheckSumCreator.Type;
import teambaltic.adhelper.utils.FileUtils;
import teambaltic.adhelper.utils.ICryptUtils;
import teambaltic.adhelper.utils.ZipUtils;

// ############################################################################
public class TransferController implements ITransferController
{
    private static final Logger sm_Log = Logger.getLogger(TransferController.class);

    // ------------------------------------------------------------------------
    private final AllSettings m_AllSettings;
    private AllSettings getAllSettings(){ return m_AllSettings; }
    // ------------------------------------------------------------------------
    private IAppSettings getAppSettings(){ return getAllSettings().getAppSettings(); }
    // ------------------------------------------------------------------------
    private IUserSettings getUserSettings(){ return getAllSettings().getUserSettings(); }
    // ------------------------------------------------------------------------
    private Path getRootFolder(){ return getAppSettings().getFolder_Root(); }
    // ------------------------------------------------------------------------
    private Path getSandBox(){ return getAppSettings().getFolder_SandBox(); }
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
            final AllSettings       fAllSettings,
            final ICryptUtils       fCryptUtils,
            final IRemoteAccess     fRemoteAccess,
            final ISingletonWatcher fSingletonWatcher )
    {
        m_CheckSumCreator   = new CheckSumCreator( Type.MD5 );
        m_AllSettings       = fAllSettings;
        m_CryptUtils        = fCryptUtils;
        m_RemoteAccess      = fRemoteAccess;
        m_SingletonWatcher  = fSingletonWatcher;
        initSandBox( getSandBox() );
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
        final File aSandbox = getSandBox().toFile();
        if( aSandbox.exists() ){
            sm_Log.info("Lösche Verzeichnis "+aSandbox);
            org.apache.commons.io.FileUtils.deleteQuietly( aSandbox );
        }
    }

    @Override
    public boolean isConnected()
    {
        return getSingletonWatcher().isConnected();
    }

    @Override
    public boolean download(final Path fFileToDownload)
    {
        final CheckSumCreator aCSC  = getCheckSumCreator();
        final Path aCheckSumFile    = aCSC.getCheckSumFile( fFileToDownload );

        try{
            final CheckSumInfo aCSILocal = aCSC.calculate( fFileToDownload );
            if( aCSILocal != null ){
                final Path aCheckSumFileInSandBox = getSandBoxPath( aCheckSumFile );
                final LocalRemotePathPair aPathPair_CheckSum = new LocalRemotePathPair(
                        aCheckSumFileInSandBox, aCheckSumFile );
                final boolean aSuccess = m_RemoteAccess.download( aPathPair_CheckSum );
                if( !aSuccess ){
                    sm_Log.warn( "Download fehlgeschlagen: "+fFileToDownload);
                    return false;
                }
                final CheckSumInfo aCSIRemote = CheckSumInfo.readFromFile( aCheckSumFileInSandBox );
                if( aCSILocal.getHash().equals( aCSIRemote.getHash() )){
                    sm_Log.info( "Lokale Datei ist identisch mit Server-Version: "+ fFileToDownload);
                    return false;
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
            return true;
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
            return false;
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

        final String aDecoratedEMail = getUserSettings().getDecoratedEMail();
        final Path aCheckSumFile = aCSC.write( aFileToUploadInSandBox, aCSI, aDecoratedEMail );
        final Path aRemotePath_CheckSumFile = fFileToUpload.getParent().resolve( aCheckSumFile.getFileName() );
        final LocalRemotePathPair aPathPair_CheckSum = new LocalRemotePathPair(
                aCheckSumFile, aRemotePath_CheckSumFile );
        m_RemoteAccess.upload( aPathPair_CheckSum );
        sm_Log.info("Datei hochgeladen: "+fFileToUpload);
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

    @Override
    public boolean uploadBillingData() throws Exception
    {
        final Path aDataFolder          = getAppSettings().getFolder_Data();
        final String aFileName_Finished = getAppSettings().getFileName_Finished();
        final String aFileName_Uploaded = getAppSettings().getFileName_Uploaded();
        final File[] aNotUploadedFolders = FileUtils.getFolders_NotUploaded( aDataFolder, aFileName_Finished , aFileName_Uploaded );
        for( final File aFolder : aNotUploadedFolders ){
            final Path aUploadInfoFile = aFolder.toPath().resolve( aFileName_Uploaded );
            final String aDecoratedEMail = getUserSettings().getDecoratedEMail();
            FileUtils.writeUploadInfo( aUploadInfoFile, aDecoratedEMail );
            final Path aZipped = ZipUtils.zip( aFolder.toPath() );
            upload( aZipped );
            Files.delete( aZipped );
        }
        return true;
    }

    @Override
    public void updateBaseDataFromServer(
            final Path fFile_BaseData,
            final ERole fRole )
                    throws Exception
    {
        Path aBackupCopy = null;
        if( Files.exists( fFile_BaseData ) ){
            if( ERole.MITGLIEDERWART.equals( fRole ) ){
                // Der Mitgliederwart ist der Erzeuger der Basisdaten-Datei!
                sm_Log.info( "MITGLIEDERWART: Basisdaten werden nicht vom Server geholt!" );
                return;
            }
            aBackupCopy = FileUtils.makeBackupCopy( fFile_BaseData );
        }
        try{
            final Path aPathOnServer = Paths.get( fFile_BaseData.toString()+".cry" );
            final boolean aExistsOnServer = m_RemoteAccess.exists( aPathOnServer );
            if( !aExistsOnServer ){
                sm_Log.warn("Datei existiert nicht auf dem Server: "+aPathOnServer );
                return;
            }
            final boolean aActuallyDownloaded = download(fFile_BaseData);
            if( !aActuallyDownloaded && aBackupCopy != null ){
                try{
                    Files.delete( aBackupCopy );
                }catch( final Exception fEx ){
                    sm_Log.warn("Exception: ", fEx );
                }
            }
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
        } finally {
            if( !Files.exists( fFile_BaseData )){
                throw new IllegalStateException("Keine Arme - keine Kekse! Datei mit BasisDaten nicht verfügbar!");
            };
        }
    }

    @Override
    public void updateBillingDataFromServer() throws Exception
    {
        final Path aDataFolder = getAppSettings().getFolder_Data();
        final String aFN_Finished = getAppSettings().getFileName_Finished();
        final String aFN_Uploaded = getAppSettings().getFileName_Uploaded();
        final File[] aFolders_AlreadyUploaded = FileUtils.getFinishedAndUploadedFolders( aDataFolder, aFN_Finished, aFN_Uploaded );

        final List<String> aRemoteZipFiles = m_RemoteAccess.list( aDataFolder, "zip.cry" );
        final List<String> aZipsToDownLoad = new ArrayList<>();
        aRemoteZipFiles.forEach( aRemoteZipFile -> {
            if( !isLocallyUptodate( aRemoteZipFile, aFolders_AlreadyUploaded ) ){
                aZipsToDownLoad.add( aRemoteZipFile );
            }
        } );
        if( aZipsToDownLoad.size() == 0 ){
            sm_Log.info( "Alle lokalen Daten sind aktuell!");
            return;
        }
        for( final String aZipToDownload : aZipsToDownLoad ){
            downloadDecryptAndUnzip(aZipToDownload);
        }
    }

    private static boolean isLocallyUptodate( final String fRemoteZipFile, final File[] fFolders_Uploaded)
    {
        if( fFolders_Uploaded == null || fFolders_Uploaded.length == 0 ){
            return false;
        }
        final String aLocalFolderPath = fRemoteZipFile.replaceFirst( "\\.zip\\.cry$", "" );
        final Path   aPathToMatch = Paths.get( aLocalFolderPath );
        for( final File aFolder : fFolders_Uploaded ){
            if( aPathToMatch.equals( aFolder.toPath() ) ){
                return true;
            }
        }
        return false;
    }

    private void downloadDecryptAndUnzip( final String fZipToDownload ) throws Exception
    {
        final String aFileName = fZipToDownload.replaceFirst( "\\.cry$", "" );
        final Path aFileToDownload = Paths.get( aFileName );
        final boolean aDownloaded = download( aFileToDownload );
        if( !aDownloaded ){
            sm_Log.error( "Download hat nicht geklappt: "+fZipToDownload );
            return;
        }
        ZipUtils.unzip( aFileToDownload );
        Files.delete( aFileToDownload );
    }
}

// ############################################################################
