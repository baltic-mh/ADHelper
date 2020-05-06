/**
 * IAppSettings.java
 *
 * Created on 04.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model.settings;

import java.nio.file.Path;

import teambaltic.adhelper.model.EPropType;
import teambaltic.adhelper.model.IKey;

// ############################################################################
public interface IAppSettings extends ISettings<IAppSettings.EKey>
{
    public enum EKey implements IKey {
        FOLDERNAME_ROOT
       ,FOLDERNAME_DATA
       ,FOLDERNAME_SETTINGS
       ,FOLDERNAME_SECRETS
       ,FOLDERNAME_SANDBOX

       ,FILENAME_BASEINFO
       ,FILENAME_WORKEVENTS
       ,FILENAME_ADJUSTMENTS
       ,FILENAME_BALANCES
       ,FILENAME_BALANCEHISTORY
       ,FILENAME_USERDATA
       ,FILENAME_CLUBDATA
       ,FILENAME_FINISHED
       ,FILENAME_UPLOADED
       ,FILENAME_REMOTEACCESSDATA
       ,FILENAME_UISETTINGS
       ,FILENAME_COLUMNNAMESMAPPING

       ,FILENAME_CRYPT_PRIV
       ,FILENAME_CRYPT_PUBL

       ,CYCLETIME_SINGLETONWATCHER(EPropType.INTVALUE)
       ,MAXNUM_KEEPOBSOLETEFOLDERS(EPropType.INTVALUE)
       ,MAXNUM_PERIODSTOCONSIDER(EPropType.INTVALUE)

       ;

       // --------------------------------------------------------------------
       private final EPropType m_PropType;
       @Override
       public EPropType getPropType(){ return m_PropType; }
       // --------------------------------------------------------------------

       private EKey()
       {
           this(EPropType.STRINGVALUE);
       }

       private EKey(final EPropType fPropType)
       {
           m_PropType = fPropType;
       }
    }

    String getFolderName_Root();
    Path getFolder_Root();
    String getFolderName_Data();
    Path getFolder_Data();
    String getFolderName_Settings();
    Path getFolder_Settings();
    String getFolderName_Secrets();
    Path getFolder_Secrets();
    String getFolderName_SandBox();
    Path getFolder_SandBox();

    String getFileName_BaseData();
    Path getFile_RootBaseData();
    String getFileName_WorkEvents();
    String getFileName_Adjustments();
    String getFileName_Balances();
    String getFileName_BalanceHistory();
    String getFileName_UserSettings();
    Path getFile_UserSettings();
    String getFileName_ClubData();
    Path getFile_ClubData();
    String getFileName_RemoteAccessSettings();
    Path getFile_RemoteAccessSettings();
    String getFileName_UISettings();
    Path getFile_UISettings();
    String getFileName_Finished();
    String getFileName_Uploaded();
    String getFileName_ColumnNamesMapping();

    String getFileName_Crypt( EKey fPrivOrPub);
    Path getFile_Crypt( EKey fPrivOrPub );

    int getCycleTime_SingletonWatcher();
    int getMaxNum_ObsoleteFolders();
    int getMaxNum_PeriodsToConsider();

    void saveSettings();

}

// ############################################################################

