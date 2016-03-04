/**
 * AllSettings.java
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
import java.nio.file.Paths;

// ############################################################################
public enum AllSettings implements IAllSettings
{
    INSTANCE;

    // ------------------------------------------------------------------------
    private IAppSettings m_AppSettings;
    @Override
    public IAppSettings getAppSettings(){ return m_AppSettings; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private IClubSettings m_ClubSettings;
    @Override
    public IClubSettings getClubSettings(){ return m_ClubSettings; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private IUserSettings m_UserSettings;
    @Override
    public IUserSettings getUserSettings(){ return m_UserSettings; }
    // ------------------------------------------------------------------------

    public void init() throws Exception
    {
        m_AppSettings = new AppSettings();
        final Path aPath_Settings = getPath_Settings();

        final String aFileName_ClubSettings = m_AppSettings.getStringValue( IAppSettings.EKey.FILENAME_CLUBDATA );
        final Path aClubFile = Paths.get( aPath_Settings.toString(), aFileName_ClubSettings );
        m_ClubSettings = new ClubSettings( aClubFile );

        final String aFileName_UserSettings = m_AppSettings.getStringValue( IAppSettings.EKey.FILENAME_USERDATA );
        final Path aUserFile = Paths.get( aPath_Settings.toString(), aFileName_UserSettings );
        m_UserSettings = new UserSettings( aUserFile );

    }

    private Path getPath_Settings()
    {
        final String aFolderName_Data = m_AppSettings.getStringValue( IAppSettings.EKey.FOLDERNAME_DATA );
        final String aFolderName_Settings = m_AppSettings.getStringValue( IAppSettings.EKey.FOLDERNAME_SETTINGS );
        final Path aPath_Settings = Paths.get( aFolderName_Data, aFolderName_Settings );
        return aPath_Settings;
    }

}

// ############################################################################
