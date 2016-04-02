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

    public static ITransferController initTransferController(final AllSettings fAllSettings)
    {
        IRemoteAccess aRA = null;
        final IRemoteAccessSettings aRASettings = fAllSettings.getRemoteAccessSettings();
        if( aRASettings == null ){
            return null;
        }
        try{
            aRA = new RemoteAccess( aRASettings );
            aRA.init();
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
        }

        final IAppSettings aAppSettings = fAllSettings.getAppSettings();
        final Path aFile_Crypt_Priv = aAppSettings.getFile_Crypt( IAppSettings.EKey.FILENAME_CRYPT_PRIV );
        final Path aFile_Crypt_Publ = aAppSettings.getFile_Crypt( IAppSettings.EKey.FILENAME_CRYPT_PUBL );
        ICryptUtils aCryptUtils = null;
        try{
            aCryptUtils = new CryptUtils( aFile_Crypt_Priv.toFile(), aFile_Crypt_Publ.toFile() );
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
        final ISingletonWatcher aSW = initSingletonWatcher( fAllSettings, aRA);
        final ITransferController aTC = new TransferController(
                fAllSettings, aCryptUtils, aRA, aSW);
        return aTC;
    }

    public static ISingletonWatcher initSingletonWatcher(
            final AllSettings fAllSettings,
            final IRemoteAccess fRemoteAccess)
    {
        final IUserSettings aUserSettings = fAllSettings.getUserSettings();
        final String aInfo = aUserSettings.getDecoratedEMail();
        final int aCycleTime = fAllSettings.getAppSettings().getCycleTime_SingletonWatcher();
        final SingletonWatcher aSW = new SingletonWatcher(aInfo, aCycleTime, fRemoteAccess);
        return aSW;
    }

    public static ADH_DataProvider initDataProvider() throws Exception
    {
        final ADH_DataProvider aDataProvider = new ADH_DataProvider(AllSettings.INSTANCE);
        aDataProvider.init();
        return aDataProvider;
    }

}

// ############################################################################
