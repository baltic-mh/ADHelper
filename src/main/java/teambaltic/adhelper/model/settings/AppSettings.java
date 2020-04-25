/**
 * ApplicationProperties.java
 *
 * Created on 14.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model.settings;

import java.nio.file.Path;
import java.nio.file.Paths;

// ############################################################################
public class AppSettings extends ASettings<IAppSettings.EKey>
    implements IAppSettings
{
    public AppSettings() throws Exception
    {
        this(System.getProperty( EKey.FOLDERNAME_ROOT.name(), "."));
    }
    public AppSettings(final String fFolderName_Root) throws Exception
    {
        super();
        init( Paths.get( fFolderName_Root, "Einstellungen", "AppSettings.properties" ));
        // Sicher ist sicher!
        setStringValue( EKey.FOLDERNAME_ROOT, fFolderName_Root );
    }
    @Override
    protected EKey[] getKeyValues(){ return EKey.values(); }

    @Override
    public String getFolderName_Root()
    {
        return getStringValue( EKey.FOLDERNAME_ROOT );
    }
    @Override
    public Path getFolder_Root()
    {
        return Paths.get( getFolderName_Root() ).normalize();
    }
    @Override
    public String getFolderName_Data()
    {
        return getStringValue( EKey.FOLDERNAME_DATA );
    }
    @Override
    public Path getFolder_Data()
    {
        return getFolder( getFolderName_Data() );
    }
    @Override
    public Path getFile_RootBaseData()
    {
        return getFile( getFolderName_Data(), getFileName_BaseData() );
    }
    @Override
    public String getFolderName_Settings()
    {
        return getStringValue( EKey.FOLDERNAME_SETTINGS );
    }
    @Override
    public Path getFolder_Settings()
    {
        return getFolder( getFolderName_Settings() );
    }

    @Override
    public String getFolderName_Secrets()
    {
        final Path aFullFolderName = Paths.get(
                getFolderName_Settings(),
                getStringValue( EKey.FOLDERNAME_SECRETS ) ).normalize();
        return aFullFolderName.toString();
    }
    @Override
    public Path getFolder_Secrets()
    {
        return getFolder( getFolderName_Secrets() );
    }
    @Override
    public String getFolderName_SandBox()
    {
        return getStringValue( EKey.FOLDERNAME_SANDBOX );
    }
    @Override
    public Path getFolder_SandBox()
    {
        return getFolder( getFolderName_SandBox() );
    }
    @Override
    public String getFileName_BaseData()
    {
        return getStringValue( EKey.FILENAME_BASEINFO );
    }
    @Override
    public String getFileName_WorkEvents()
    {
        return getStringValue( EKey.FILENAME_WORKEVENTS );
    }
    @Override
    public String getFileName_Adjustments()
    {
        return getStringValue( EKey.FILENAME_ADJUSTMENTS );
    }
    @Override
    public String getFileName_Balances()
    {
        return getStringValue( EKey.FILENAME_BALANCES );
    }
    @Override
    public String getFileName_BalanceHistory()
    {
        return getStringValue( EKey.FILENAME_BALANCEHISTORY );
    }
    @Override
    public String getFileName_UserSettings()
    {
        return getStringValue( EKey.FILENAME_USERDATA );
    }
    @Override
    public Path getFile_UserSettings()
    {
        return getFile( getFolderName_Settings(), getFileName_UserSettings() );
    }
    @Override
    public String getFileName_ClubData()
    {
        return getStringValue( EKey.FILENAME_CLUBDATA );
    }
    @Override
    public Path getFile_ClubData()
    {
        return getFile( getFolderName_Settings(), getFileName_ClubData() );
    }
    @Override
    public String getFileName_RemoteAccessSettings()
    {
        return getStringValue( EKey.FILENAME_REMOTEACCESSDATA );
    }
    @Override
    public Path getFile_RemoteAccessSettings()
    {
        return getFile( getFolderName_Settings(), getFileName_RemoteAccessSettings() );
    }
    @Override
    public Path getFile_UISettings()
    {
        return getFile( getFolderName_Settings(), getFileName_UISettings() );
    }
    @Override
    public String getFileName_UISettings()
    {
        return getStringValue( EKey.FILENAME_UISETTINGS );
    }
    @Override
    public void saveSettings()
    {
        // TODO Auto-generated method stub

    }
    @Override
    public String getFileName_Finished()
    {
        return getStringValue( EKey.FILENAME_FINISHED );
    }
    @Override
    public String getFileName_Uploaded()
    {
        return getStringValue( EKey.FILENAME_UPLOADED );
    }

    @Override
    public int getCycleTime_SingletonWatcher()
    {
        return getIntValue( EKey.CYCLETIME_SINGLETONWATCHER );
    }
    @Override
    public int getMaxNum_ObsoleteFolders()
    {
        return getIntValue( EKey.MAXNUM_KEEPOBSOLETEFOLDERS );
    }
    @Override
    public String getFileName_Crypt( final EKey fPrivOrPub )
    {
        if(    !EKey.FILENAME_CRYPT_PRIV.equals( fPrivOrPub )
            && !EKey.FILENAME_CRYPT_PUBL.equals( fPrivOrPub ) ){
            throw new IllegalArgumentException("Unzulässiger Parameterwert "+fPrivOrPub);
        }
        return getStringValue( fPrivOrPub );
    }
    @Override
    public Path getFile_Crypt( final EKey fPrivOrPub )
    {
        return getFile( getFolderName_Secrets(), getFileName_Crypt( fPrivOrPub ) );
    }

    private Path getFile( final String fFolderName, final String fFileName )
    {
        final Path aPath = Paths.get( getFolderName_Root(), fFolderName, fFileName );
        return aPath;
    }

    private Path getFolder( final String fFolderName )
    {
        final Path aPath = Paths.get( getFolderName_Root(), fFolderName ).normalize();
        return aPath;
    }
}

// ############################################################################
