/**
 * GlobalParameters.java
 *
 * Created on 01.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.util.HashMap;
import java.util.Map;

// ############################################################################
public class GlobalParameters implements IGlobalParameters
{
    private final Map<EKey, Integer> m_IntegerValues;
    // Alle Stundenwerte werden in 100stel Stunden angegeben!
    private final Map<EKey, Integer> m_HourValues;

    public GlobalParameters()
    {
        m_IntegerValues = new HashMap<>();
        m_HourValues    = new HashMap<>();
        init();
    }

    private void init()
    {
        m_IntegerValues.put( EKey.PROTECTION_TIME, Integer.valueOf( 6 ) );
        m_IntegerValues.put( EKey.MIN_AGE_FOR_DUTY, Integer.valueOf( 16 ) );
        m_IntegerValues.put( EKey.MAX_AGE_FOR_DUTY, Integer.valueOf( 60 ) );
        m_IntegerValues.put( EKey.MONTHS_PER_INVOICEPERIOD, Integer.valueOf( 6 ) );

        m_HourValues.put( EKey.DUTYHOURS_PER_INVOICEPERIOD, Integer.valueOf( 300 ) );
    }

    public int getProtectedTime()
    {
        return m_IntegerValues.get( EKey.PROTECTION_TIME );
    }

    public int getMinAgeForDuty()
    {
        return m_IntegerValues.get( EKey.MIN_AGE_FOR_DUTY );
    }

    public int getMaxAgeForDuty()
    {
        return m_IntegerValues.get( EKey.MAX_AGE_FOR_DUTY );
    }

    public int getMonthsPerInvoicePeriod()
    {
        return m_IntegerValues.get( EKey.MONTHS_PER_INVOICEPERIOD );
    }

    public int getDutyHoursPerInvoicePeriod()
    {
        return m_HourValues.get( EKey.DUTYHOURS_PER_INVOICEPERIOD );
    }
}

// ############################################################################
