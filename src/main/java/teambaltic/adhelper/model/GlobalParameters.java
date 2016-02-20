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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// ############################################################################
public class GlobalParameters implements IGlobalParameters
{
    private final Map<EKey, Integer> m_IntegerValues;
    // Alle Stundenwerte werden in 100stel Stunden angegeben!
    private final Map<EKey, Integer> m_HourValues;

    public GlobalParameters(final String fDataFoldername) throws Exception
    {
        m_IntegerValues = new HashMap<>();
        m_HourValues    = new HashMap<>();
        init(fDataFoldername);
    }

    private void init(final String fDataFoldername) throws FileNotFoundException, IOException
    {
        final Path aClubPropsFile = Paths.get(fDataFoldername, "Einstellungen", "Vereinsparameter.prop" );
        final Properties aClubProps = new Properties();
        aClubProps.load( new FileInputStream( aClubPropsFile.toFile() ) );

        storeIntegerValue( EKey.PROTECTION_TIME, aClubProps );
        storeIntegerValue( EKey.MIN_AGE_FOR_DUTY, aClubProps );
        storeIntegerValue( EKey.MAX_AGE_FOR_DUTY, aClubProps );
        storeIntegerValue( EKey.MONTHS_PER_INVOICEPERIOD, aClubProps );

        storeHourValue( EKey.DUTYHOURS_PER_INVOICEPERIOD, aClubProps );
    }

    private void storeIntegerValue( final EKey fKey, final Properties fProps )
    {
        m_IntegerValues.put( fKey, Integer.valueOf( fProps.getProperty( fKey.toString() ) ) );
    }

    private void storeHourValue( final EKey fKey, final Properties fProps )
    {
        final int aHoursInt = Integer.parseInt( fProps.getProperty( fKey.toString() ) );
        m_HourValues.put( fKey, Integer.valueOf( aHoursInt*100 ) );
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
