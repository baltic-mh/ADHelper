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
    private IRemoteAccessSettings m_RemoteAccessSettings;
    @Override
    public IRemoteAccessSettings getRemoteAccessSettings(){ return m_RemoteAccessSettings; }
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

        final Path aClubFile = m_AppSettings.getFile_ClubData();
        m_ClubSettings = new ClubSettings( aClubFile );

        final Path aUserFile = m_AppSettings.getFile_UserSettings();
        m_UserSettings = new UserSettings( aUserFile );

        // Wenn es keine Zugangsdaten zum Server gibt, arbeiten wir eben lokal!
        final Path aRemoteAccessFile = m_AppSettings.getFile_RemoteAccessSettings();
        RemoteAccessSettings aRASettings = null;
        try{
            aRASettings = new RemoteAccessSettings( aRemoteAccessFile );
        }catch( final Exception fEx ){
        }
        m_RemoteAccessSettings = aRASettings;

    }

}

// ############################################################################
