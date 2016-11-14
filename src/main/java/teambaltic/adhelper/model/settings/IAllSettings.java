/**
 * IAllSettings.java
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

// ############################################################################
public interface IAllSettings
{
    IAppSettings  getAppSettings();
    IClubSettings getClubSettings();
    IUserSettings getUserSettings();
    IRemoteAccessSettings getRemoteAccessSettings();
}

// ############################################################################
