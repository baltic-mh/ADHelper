/**
 * UserData.java
 *
 * Created on 02.03.2016
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
public class RemoteAccessSettings extends ASettings<IRemoteAccessSettings.EKey>
    implements IRemoteAccessSettings
{
    public RemoteAccessSettings(final Path fSettingsFile) throws Exception
    {
        super();
        setStringValue( EKey.PROTOCOL, "sftp" );
        init(fSettingsFile);
    }

    @Override
    protected EKey[] getKeyValues(){ return EKey.values(); }

    @Override
    public String getServerName()
    {
        return getStringValue(EKey.SERVERNAME);
    }

    @Override
    public String getUserName()
    {
        return getStringValue(EKey.USERNAME);
    }

    @Override
    public int getPort()
    {
        return getIntValue( EKey.PORT );
    }

    @Override
    public String getRemoteRootDir()
    {
        return getStringValue(EKey.REMOTEROOTDIR);
    }

    @Override
    public String getKeyFile()
    {
        return getStringValue(EKey.KEYFILE);
    }

    @Override
    public String getProtocol()
    {
        return getStringValue(EKey.PROTOCOL);
    }

}
// ############################################################################
