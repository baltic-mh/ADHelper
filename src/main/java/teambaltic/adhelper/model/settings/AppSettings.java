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

// ############################################################################
public class AppSettings extends ASettings<IAppSettings.EKey>
    implements IAppSettings
{

    public AppSettings()
    {
        setStringValue( EKey.FOLDERNAME_DATA,       "Daten" );
        setStringValue( EKey.FOLDERNAME_SETTINGS,   "Einstellungen" );
        setStringValue( EKey.FILENAME_BASEINFO,     "BasisDaten.csv" );
        setStringValue( EKey.FILENAME_WORKEVENTS,   "Arbeitsdienste.csv" );
        setStringValue( EKey.FILENAME_BALANCES,     "Guthaben.csv" );
        setStringValue( EKey.FILENAME_USERDATA,     "BenutzerDaten.prop" );
        setStringValue( EKey.FILENAME_CLUBDATA,     "VereinsDaten.prop" );

    }
    @Override
    protected EKey[] getKeyValues(){ return EKey.values(); }

    @Override
    public String getFolderName_Data()
    {
        return getStringValue( EKey.FOLDERNAME_DATA );
    }
    @Override
    public String getFolderName_Settings()
    {
        return getStringValue( EKey.FOLDERNAME_SETTINGS );
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
    public String getFileName_UserData()
    {
        return getStringValue( EKey.FILENAME_USERDATA );
    }
    @Override
    public String getFileName_ClubData()
    {
        return getStringValue( EKey.FILENAME_CLUBDATA );
    }

}

// ############################################################################
