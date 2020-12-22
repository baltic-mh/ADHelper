/**
 * IClubSettings.java
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

import teambaltic.adhelper.model.EPropType;
import teambaltic.adhelper.model.IKey;
import teambaltic.adhelper.model.IPeriod;

// ############################################################################
public interface IClubSettings extends ISettings<IClubSettings.EKey>
{
    public enum EKey implements IKey {
         MIN_AGE_FOR_DUTY(EPropType.INTVALUE)
        ,MAX_AGE_FOR_DUTY(EPropType.INTVALUE)
        // Anzahl der Monate, die man nach Vereinseintritt vom AD befreit ist:
        ,PROTECTION_TIME(EPropType.INTVALUE)
        // Anzahl Pflichtarbeitsstunden pro Abrechnungszeitraum
        ,DUTYHOURS_PER_PERIOD(EPropType.HOURVALUE)
        // Anzahl Pflichtarbeitsstunden pro Abrechnungszeitraum
        ,HOURLY_RATE(EPropType.FLOATVALUE)
        ;

        // --------------------------------------------------------------------
        private final EPropType m_PropType;
        @Override
        public EPropType getPropType(){ return m_PropType; }
        // --------------------------------------------------------------------

        private EKey(final EPropType fPropType)
        {
            m_PropType = fPropType;
        }

    }

    int getMinAgeForDuty();
    int getMaxAgeForDuty();
    int getProtectionTime();
    int getDutyHoursPerPeriod(IPeriod fPeriod);
    float getHourlyRate();
}

// ############################################################################
