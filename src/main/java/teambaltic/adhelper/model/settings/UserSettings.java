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

import java.nio.file.Files;
import java.nio.file.Path;

import teambaltic.adhelper.model.ERole;

// ############################################################################
public class UserSettings extends ASettings<IUserSettings.EKey>
    implements IUserSettings
{
    public UserSettings(final Path fSettingsFile) throws Exception
    {
        super();
        if( !Files.exists( fSettingsFile )){
            Files.createFile( fSettingsFile );
        }
        init(fSettingsFile);
    }

    @Override
    protected EKey[] getKeyValues(){ return EKey.values(); }

    @Override
    public String getName()
    {
        return getStringValue(EKey.NAME);
    }

    @Override
    public String getEMail()
    {
        return getStringValue(EKey.EMAIL);
    }

    @Override
    public String getDecoratedEMail()
    {
        return String.format("%s <%s>",getName(), getEMail());
    }

    @Override
    public ERole getRole()
    {
        final String aRoleStringValue = getStringValue(EKey.ROLE);
        try{
            final ERole aRole = ERole.valueOf( aRoleStringValue );
            return aRole;
        }catch( final Exception fEx ){
            return null;
        }
    }
}
// ############################################################################
