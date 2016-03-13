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

    public AppSettings()
    {
        this(System.getProperty( "adhelper.folder.data", "."));
    }
    public AppSettings(final String fFolderName_Root)
    {
        setStringValue( EKey.FOLDERNAME_ROOT,       fFolderName_Root );
        setStringValue( EKey.FOLDERNAME_DATA,       "Daten" );
        setStringValue( EKey.FOLDERNAME_SETTINGS,   "Einstellungen" );
        setStringValue( EKey.FOLDERNAME_SANDBOX,    "Sandkiste" );
        setStringValue( EKey.FOLDERNAME_SECRETS,    "ssh" );

        setStringValue( EKey.FILENAME_BASEINFO,     "BasisDaten.csv" );
        setStringValue( EKey.FILENAME_WORKEVENTS,   "Arbeitsdienste.csv" );
        setStringValue( EKey.FILENAME_BALANCES,     "Guthaben.csv" );
        setStringValue( EKey.FILENAME_USERDATA,     "BenutzerDaten.prop" );
        setStringValue( EKey.FILENAME_CLUBDATA,     "VereinsDaten.prop" );
        setStringValue( EKey.FILENAME_REMOTEACCESSDATA, "ServerZugangsDaten.prop" );

        setStringValue( EKey.FILENAME_CRYPT_PRIV,   "private_key.der" );
        setStringValue( EKey.FILENAME_CRYPT_PUBL,   "public_key.der" );

        setIntValue( EKey.CYCLETIME_SINGLETONWATCHER, 5000 );

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
        return getFolder( getFolderName_Root() );
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
        return getStringValue( EKey.FOLDERNAME_SECRETS );
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
    public String getFileName_BaseInfo()
    {
        return getStringValue( EKey.FILENAME_BASEINFO );
    }
    @Override
    public String getFileName_WorkEvents()
    {
        return getStringValue( EKey.FILENAME_WORKEVENTS );
    }
    @Override
    public String getFileName_Balances()
    {
        return getStringValue( EKey.FILENAME_BALANCES );
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
        final String aFolderName = getFolderName_Settings();
        final String aFileName   = getStringValue( EKey.FILENAME_REMOTEACCESSDATA);
        return getFile( aFolderName, aFileName );
    }

    @Override
    public int getCycleTime_SingletonWatcher()
    {
        return getIntValue( EKey.CYCLETIME_SINGLETONWATCHER );
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
        final Path aPath = Paths.get( getFolderName_Root(), fFolderName );
        return aPath;
    }
}

// ############################################################################
