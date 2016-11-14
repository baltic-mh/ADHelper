/**
 * ClubSettings.java
 *
 * Created on 01.02.2016
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
public class ClubSettings extends ASettings<IClubSettings.EKey>
    implements IClubSettings
{
    public ClubSettings(final Path fSettingsFile) throws Exception
    {
        super();
        init(fSettingsFile);
    }

    @Override
    protected EKey[] getKeyValues(){ return EKey.values(); }

    @Override
    public int getMinAgeForDuty()
    {
        return getIntValue( EKey.MIN_AGE_FOR_DUTY );
    }

    @Override
    public int getMaxAgeForDuty()
    {
        return getIntValue( EKey.MAX_AGE_FOR_DUTY );
    }

    @Override
    public int getProtectionTime()
    {
        return getIntValue( EKey.PROTECTION_TIME );
    }

    @Override
    public int getMonthsPerPeriod()
    {
        return getIntValue( EKey.MONTHS_PER_PERIOD );
    }

    @Override
    public int getDutyHoursPerPeriod()
    {
        return getHourValue( EKey.DUTYHOURS_PER_PERIOD );
    }

}

// ############################################################################
