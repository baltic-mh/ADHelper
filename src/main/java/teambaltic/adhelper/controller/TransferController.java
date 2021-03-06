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
import teambaltic.adhelper.model.settings.IAllSettings;
import teambaltic.adhelper.model.settings.IAppSettings;
import teambaltic.adhelper.model.settings.IUserSettings;
import teambaltic.adhelper.remoteaccess.IRemoteAccess;
import teambaltic.adhelper.remoteaccess.LocalRemotePathPair;
import teambaltic.adhelper.utils.CheckSumCreator;
import teambaltic.adhelper.utils.CheckSumCreator.Type;
import teambaltic.adhelper.utils.CompareUtils;
import teambaltic.adhelper.utils.FileUtils;
import teambaltic.adhelper.utils.ICryptUtils;
import teambaltic.adhelper.utils.ZipUtils;

// ############################################################################
public class TransferController implements ITransferController
{
    private static final Logger sm_Log = Logger.getLogger(TransferController.class);

    // ------------------------------------------------------------------------
    private final IAllSettings m_AllSettings;
    private IAllSettings getAllSettings(){ return m_AllSettings; }
    // ------------------------------------------------------------------------
    private IAppSettings getAppSettings(){ return getAllSettings().getAppSettings(); }
    // ------------------------------------------------------------------------
    private IUserSettings getUserSettings(){ return getAllSettings().getUserSettings(); }
    private String getUserInfo(){ return getUserSettings().getDecoratedEMail(); }
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

    // ------------------------------------------------------------------------
    private IPeriodDataController m_PDC;
    private IPeriodDataController getPDC(){ return m_PDC; }
    @Override
    public void setPeriodDataController( final IPeriodDataController fPDC ){ m_PDC = fPDC; }
    // ------------------------------------------------------------------------

    public TransferController(
            final IAllSettings      fAllSettings,
            final ICryptUtils       fCryptUtils,
            final IRemoteAccess     fRemoteAccess,
            final ISingletonWatcher fSingletonWatcher)
    {
        m_CheckSumCreator   = new CheckSumCreator( Type.MD5 );
        m_AllSettings       = fAllSettings;
        m_CryptUtils        = fCryptUtils;
        m_RemoteAccess      = fRemoteAccess;
        m_SingletonWatcher  = fSingletonWatcher;
        initSandBox( getSandBox() );
    }

    @Override
    public String start() throws Exception
    {
        return getSingletonWatcher().start();
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
    public Path download(final Path fFileToDownload)
    {
        final CheckSumCreator aCSC  = getCheckSumCreator();

        try{
            final Path aCheckSumFileFromServer = getCheckSumFromServer( aCSC, fFileToDownload );
            final CheckSumInfo aCSILocal = aCSC.calculate( fFileToDownload );
            if( areCheckSumsEqual( aCSILocal, aCheckSumFileFromServer) ){
                sm_Log.info( "Lokale Datei ist identisch mit Server-Version: "+ fFileToDownload);
                return null;
            }

            final Path aFileToDownloadInSandBox     = getSandBoxPath( fFileToDownload );
            final Path aFileToDownloadInSandBox_Cry = Paths.get( aFileToDownloadInSandBox.toString()+".cry" );
            final Path aFileToDownload_Cry          = Paths.get( fFileToDownload+".cry" );
            final LocalRemotePathPair aPathPair_DataFiles = new LocalRemotePathPair(
                    aFileToDownloadInSandBox_Cry, aFileToDownload_Cry );
            m_RemoteAccess.download( aPathPair_DataFiles );
            final Path aDataFileInSandBox_Decrypted = getCryptUtils().decrypt( aFileToDownloadInSandBox_Cry );
            Files.copy( aDataFileInSandBox_Decrypted, fFileToDownload, StandardCopyOption.REPLACE_EXISTING);
            sm_Log.info("Datei heruntergeladen: "+fFileToDownload);
            FileUtils.copyFileToFolder( aCheckSumFileFromServer, fFileToDownload.getParent() );
            return aCheckSumFileFromServer;
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
            return null;
        }
    }

    private Path getCheckSumFromServer(
            final CheckSumCreator fCSC,
            final Path fFileToDownload )
                    throws Exception
    {
        final Path aCheckSumFile    = fCSC.getCheckSumFile( fFileToDownload );
        final Path aCheckSumFileInSandBox = getSandBoxPath( aCheckSumFile );
        final LocalRemotePathPair aPathPair_CheckSum = new LocalRemotePathPair(
                aCheckSumFileInSandBox, aCheckSumFile );
        final boolean aSuccess = m_RemoteAccess.download( aPathPair_CheckSum );
        if( !aSuccess ){
            throw new Exception("Download der CheckSummen-Datei fehlgeschlagen: "+fFileToDownload);
        }
        return aCheckSumFileInSandBox;
    }

    private static boolean areCheckSumsEqual( final CheckSumInfo fCSI, final Path fCheckSumFile )
    {
        if( fCSI == null ){
            return false;
        }
        final CheckSumInfo aCSIFromFile = CheckSumInfo.readFromFile( fCheckSumFile );
        if( fCSI.getHash().equals( aCSIFromFile.getHash() )){
            return true;
        }
        return false;
    }

    @Override
    public Path upload(final Path fFileToUpload) throws Exception
    {
        final ICryptUtils aCryptUtils = getCryptUtils();
        if( aCryptUtils == null ){
            sm_Log.warn( "Kein Verschlüsselungsobjekt! Hochladen nicht möglich!" );
            return null;
        }
        final Path aLocalFile_RelativeToRoot = fFileToUpload.normalize();
        if(Files.isDirectory( aLocalFile_RelativeToRoot )){
            throw new UnsupportedOperationException("Noch nicht implementiert!");
        }
        return uploadFile( aLocalFile_RelativeToRoot );
    }

    private Path uploadFile(final Path fFileToUpload) throws Exception
    {
        final ICryptUtils aCryptUtils = getCryptUtils();
        final CheckSumCreator aCSC = getCheckSumCreator();

        final Path aFileInSandBoxToUpload = copyFileToSandBox( fFileToUpload );
        final CheckSumInfo aCSI  = aCSC.calculate( aFileInSandBoxToUpload );
        final Path aFileToUploadInSandBox_Encrypted = aCryptUtils.encrypt( aFileInSandBoxToUpload );
        final Path aRemotePath_DataFile = Paths.get( fFileToUpload.toString()+"."+FilenameUtils.getExtension( aFileToUploadInSandBox_Encrypted.toString() ) );

        final LocalRemotePathPair aPathPair_Files = new LocalRemotePathPair(
                aFileToUploadInSandBox_Encrypted, aRemotePath_DataFile );
        m_RemoteAccess.upload( aPathPair_Files );

        final Path aCheckSumFile = aCSC.write( aFileInSandBoxToUpload, aCSI, getUserInfo() );
        final Path aRemotePath_CheckSumFile = fFileToUpload.getParent().resolve( aCheckSumFile.getFileName() );
        final LocalRemotePathPair aPathPair_CheckSum = new LocalRemotePathPair(
                aCheckSumFile, aRemotePath_CheckSumFile );
        m_RemoteAccess.upload( aPathPair_CheckSum );
        sm_Log.info("Datei hochgeladen: "+fFileToUpload);

        return aCheckSumFile;
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
        aFileInSandBox.toFile().setLastModified( fFile.toFile().lastModified() );
        return aFileInSandBox;
    }

    private Path getSandBoxPath( final Path fFile )
    {
        final Path aPath = getSandBox().resolve( fFile.getFileName() );
        return aPath;
    }

    @Override
    public boolean isActivePeriodModifiedLocally()
    {
        final Path aActivePeriodFolder = getPDC().getActivePeriodFolder();
        if( aActivePeriodFolder == null ){
            return false;
        }
        final CheckSumCreator aCSC = getCheckSumCreator();
        final Path aZipFile = Paths.get(aActivePeriodFolder.toString()+".zip");
        final Path aLocalCheckSumFile = aCSC.getCheckSumFile( aZipFile );

        final String aFileName_Uploaded = getAppSettings().getFileName_Uploaded();
        final boolean aFolderDirty = FileUtils.isFolderDirty(
                aActivePeriodFolder, aFileName_Uploaded, aLocalCheckSumFile );
        if( sm_Log.isDebugEnabled() ){
            sm_Log.debug( String.format("Aktive Periode ist lokal %sverändert: %s",
                    aFolderDirty ? "" : "un", aActivePeriodFolder ));
        }
        return aFolderDirty;
    }

    @Override
    public boolean uploadPeriodData() throws Exception
    {
        if( !isActivePeriodModifiedLocally() ) {
            return false;
        }
        final Path aFolderToUpload = getPDC().getActivePeriodFolder();
        final String aFileName_Uploaded = getAppSettings().getFileName_Uploaded();
        // Damit die Datei mit der Upload-Info auch auf den Server kommt,
        // muss sie bereits VOR dem Zippen da hineingeschrieben werden.
        // Wenn dann das Upload schief geht, stimmt die Angabe natürlich nicht!
        final Path aUploadInfoFile = aFolderToUpload.resolve( aFileName_Uploaded );
        FileUtils.writeUploadInfo( aUploadInfoFile, getUserInfo() );
        final Path aZipped = ZipUtils.zip( aFolderToUpload );
        final Path aCheckSumFile = upload( aZipped );
        FileUtils.copyFileToFolder( aCheckSumFile, aFolderToUpload.getParent() );
        Files.delete( aZipped );

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
            final boolean aActuallyDownloaded = download(fFile_BaseData) != null;
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
    public List<Path> updatePeriodDataFromServer() throws Exception
    {
        final Path aDataFolder = getAppSettings().getFolder_Data();
        final List<String> aRemoteZipFiles_Crypted = m_RemoteAccess.list( aDataFolder, "zip.cry" );
        CompareUtils.sortPeriodFolderList(aRemoteZipFiles_Crypted);
        final List<String> aZipsToDownLoad = new ArrayList<>();
        final CheckSumCreator aCSC  = getCheckSumCreator();

        int aNumPeriodsConsidered = 0;
        final int aMaxNumPeriodsToConsider = getAppSettings().getMaxNum_PeriodsToConsider();
        final int aNumPeriodsOnServer = aRemoteZipFiles_Crypted.size();
        if( aNumPeriodsOnServer > aMaxNumPeriodsToConsider) {
            sm_Log.info(String.format("Es liegen %d Abrechnungszeiträume auf dem Server - es werden aber nur die neuesten %d betrachtet.", aNumPeriodsOnServer, aMaxNumPeriodsToConsider));
        }
        final List<Path> aFoldersKnownOnServer = new ArrayList<>();
        for( final String aRemoteZipFile_Crypted : aRemoteZipFiles_Crypted ){
            final Path aRemoteZipFilePath = getRemoteZipFilePath( aRemoteZipFile_Crypted );
            final Path aLocalFolderPath   = getLocalFolderPath( aRemoteZipFilePath );
            aFoldersKnownOnServer.add( aLocalFolderPath );
            aNumPeriodsConsidered++;
            if( aNumPeriodsConsidered > aMaxNumPeriodsToConsider ) {
            	continue;
            }
            if( !isLocallyUptodate( aCSC, aRemoteZipFilePath, aLocalFolderPath ) ){
                aZipsToDownLoad.add( aRemoteZipFile_Crypted );
            }
        }
        if( aZipsToDownLoad.size() == 0 ){
            sm_Log.info( "Alle lokalen Daten sind aktuell!");
        } else {
            for( final String aZipToDownload : aZipsToDownLoad ){
                sm_Log.info( "Download von Server: "+aZipToDownload);
                downloadDecryptAndUnzip(aZipToDownload);
            }
        }
        return aFoldersKnownOnServer;
    }

	private boolean isLocallyUptodate(
            final CheckSumCreator fCSC,
            final Path aRemoteZipFile,
            final Path aLocalFolderPath ) throws Exception
    {
        if( !Files.exists( aLocalFolderPath ) ){
            return false;
        }
        if( Files.exists( aLocalFolderPath.resolve( "INVALID" ) )) {
            sm_Log.warn( "Lokales Verzeichnis ist ungültig: "+ aLocalFolderPath );
            return false;
        }
        final Path aCheckSumFileFromServer = getCheckSumFromServer( fCSC, aRemoteZipFile );
        final Path aCheckSumFileLocal      = fCSC.getCheckSumFile( aRemoteZipFile );
        if( !Files.exists( aCheckSumFileLocal )){
            return false;
        }
        final CheckSumInfo aCSILocal  = CheckSumInfo.readFromFile( aCheckSumFileLocal );
        final boolean aEqualCheckSums = areCheckSumsEqual( aCSILocal, aCheckSumFileFromServer );
        if( aEqualCheckSums ){
            sm_Log.info( "Lokales Verzeichnis identisch mit Server-Version: "+ aLocalFolderPath);
            return true;
        }
        sm_Log.info( "Lokales Verzeichnis nicht identisch mit Server-Version: "+ aLocalFolderPath);
        return false;
    }

    private static Path getRemoteZipFilePath( final String fRemoteZipFile_Crypted )
    {
        final Path aZipFileToDownload = Paths.get( fRemoteZipFile_Crypted.replaceFirst( "\\.cry$", "" ) );
        return aZipFileToDownload;
    }
    private static Path getLocalFolderPath( final Path fRemoteZipFile )
    {
        final Path aLocalFolderPath = Paths.get( fRemoteZipFile.toString().replaceFirst( "\\.zip$", "" ) );
        return aLocalFolderPath;
    }

    private void downloadDecryptAndUnzip( final String fZipToDownload ) throws Exception
    {
        final String aFileName = fZipToDownload.replaceFirst( "\\.cry$", "" );
        final Path aFileToDownload = Paths.get( aFileName );
        Files.deleteIfExists( aFileToDownload );
        final boolean aDownloaded = download( aFileToDownload ) != null;
        if( !aDownloaded ){
            sm_Log.error( "Download hat nicht geklappt: "+fZipToDownload );
            return;
        }
        ZipUtils.unzip( aFileToDownload );
        final String aFolderName = aFileName.replaceFirst( "\\.zip$", "" );
        final Path aFileINVALID = Paths.get(aFolderName, "INVALID");
        Files.deleteIfExists( aFileINVALID );
        Files.delete( aFileToDownload );
    }

}

// ############################################################################
