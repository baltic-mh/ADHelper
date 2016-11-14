/**
 * InitHelper.java
 *
 * Created on 12.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.nio.file.Path;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAllSettings;
import teambaltic.adhelper.model.settings.IAppSettings;
import teambaltic.adhelper.model.settings.IRemoteAccessSettings;
import teambaltic.adhelper.model.settings.IUserSettings;
import teambaltic.adhelper.remoteaccess.IRemoteAccess;
import teambaltic.adhelper.remoteaccess.RemoteAccess;
import teambaltic.adhelper.utils.CryptUtils;
import teambaltic.adhelper.utils.ICryptUtils;

// ############################################################################
public class InitHelper
{
    private static final Logger sm_Log = Logger.getLogger(InitHelper.class);

    // ------------------------------------------------------------------------
    private final IAllSettings m_AllSettings;
    private IAllSettings getAllSettings(){ return m_AllSettings; }
    private IAppSettings getAppSettings(){ return getAllSettings().getAppSettings(); }
    private IUserSettings getUserSettings(){ return getAllSettings().getUserSettings(); }
    private IRemoteAccessSettings getRASettings(){ return getAllSettings().getRemoteAccessSettings(); }
    // ------------------------------------------------------------------------

    public InitHelper(final AllSettings fAllSettings)
    {
        m_AllSettings = fAllSettings;
    }

    public ITransferController initTransferController()
            throws Exception
    {
        IRemoteAccess aRA = null;
        final IRemoteAccessSettings aRASettings = getRASettings();
        if( aRASettings == null ){
            throw new Exception( "Keine Server-Zugangsdaten gefunden! Das wird nix!");
        }

        sm_Log.info( "Server ist: "+aRASettings.getServerName() );
        final String aFolderName_Root = getAppSettings().getFolderName_Root();
        sm_Log.info( "Wurzelverzeichnis: "+aFolderName_Root );
        aRA = new RemoteAccess( aFolderName_Root, aRASettings );
        aRA.init();

        final IAppSettings aAppSettings = getAppSettings();
        final Path aFile_Crypt_Priv = aAppSettings.getFile_Crypt( IAppSettings.EKey.FILENAME_CRYPT_PRIV );
        final Path aFile_Crypt_Publ = aAppSettings.getFile_Crypt( IAppSettings.EKey.FILENAME_CRYPT_PUBL );
        final ICryptUtils aCryptUtils = new CryptUtils( aFile_Crypt_Priv.toFile(), aFile_Crypt_Publ.toFile() );
        final ISingletonWatcher aSW = initSingletonWatcher( aRA);
        final ITransferController aTC = new TransferController(
                getAllSettings(), aCryptUtils, aRA, aSW);
        return aTC;
    }

    private ISingletonWatcher initSingletonWatcher( final IRemoteAccess fRemoteAccess )
    {
        final String aInfo = getUserSettings().getDecoratedEMail();
        final int aCycleTime = getAppSettings().getCycleTime_SingletonWatcher();
        final SingletonWatcher aSW = new SingletonWatcher(aInfo, aCycleTime, fRemoteAccess);
        return aSW;
    }

    public ADH_DataProvider initDataProvider(final IPeriodDataController fPDC) throws Exception
    {
        final ADH_DataProvider aDataProvider = new ADH_DataProvider(fPDC, AllSettings.INSTANCE);
        return aDataProvider;
    }

    public IPeriodDataController initPeriodDataController()
    {
        final PeriodDataController aPDC = new PeriodDataController( getAppSettings(), getUserSettings() );
        aPDC.init();
        return aPDC;
    }

}

// ############################################################################
