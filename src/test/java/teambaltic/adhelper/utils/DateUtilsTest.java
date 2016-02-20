/**
 * DateUtilsTest.java
 *
 * Created on 20.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;

// ############################################################################
public class DateUtilsTest
{
    @Test
    public void test()
    {
        final Halfyear aPeriodToCover = new Halfyear( 2014, EPart.FIRST);
        final FreeFromDuty aCoveringPeriod = new FreeFromDuty( 0, null );
        // Kein Anfang und kein Ende - Totale Abdeckung!
        int aCoverageInMonths = DateUtils.getCoverageInMonths( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang und kein Ende", 6, aCoverageInMonths);

        // Anfang nach dem Ende - 0 Abdeckung
        aCoveringPeriod.setFrom( LocalDate.of( 2015, 1, 1 ) );
        aCoverageInMonths = DateUtils.getCoverageInMonths( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang nach dem Ende - 0 Abdeckung", 0, aCoverageInMonths);

        // Wieder kein Anfang und kein Ende - Totale Abdeckung!
        aCoveringPeriod.setFrom( null );
        aCoverageInMonths = DateUtils.getCoverageInMonths( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang und kein Ende2", 6, aCoverageInMonths);

        // Ende vor dem Start - 0 Abdeckung
        aCoveringPeriod.setUntil( LocalDate.of( 2013, 1, 1 ) );
        aCoverageInMonths = DateUtils.getCoverageInMonths( aCoveringPeriod, aPeriodToCover );
        assertEquals("Ende vor dem Start - 0 Abdeckung", 0, aCoverageInMonths);

        // Wieder kein Anfang und kein Ende - Totale Abdeckung!
        aCoveringPeriod.setUntil( null );
        aCoverageInMonths = DateUtils.getCoverageInMonths( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang und kein Ende3", 6, aCoverageInMonths);

        // Anfang vor dem Anfang - kein Ende - totale Abdeckung
        aCoveringPeriod.setFrom( LocalDate.of( 2013, 1, 1 ) );
        aCoverageInMonths = DateUtils.getCoverageInMonths( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang vor dem Anfang - kein Ende - totale Abdeckung", 6, aCoverageInMonths);

        // Kein Anfang - Ende nach dem Ende - totale Abdeckung
        aCoveringPeriod.setFrom( null );
        aCoveringPeriod.setUntil( LocalDate.of( 2015, 1, 1 ) );
        aCoverageInMonths = DateUtils.getCoverageInMonths( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang - Ende nach dem Ende - totale Abdeckung", 6, aCoverageInMonths);

        // Anfang auf Anfang - Ende auf Ende - totale Abdeckung
        aCoveringPeriod.setFrom( aPeriodToCover.getStart() );
        aCoveringPeriod.setUntil( aPeriodToCover.getEnd() );
        aCoverageInMonths = DateUtils.getCoverageInMonths( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang auf Anfang - Ende auf Ende - totale Abdeckung", 6, aCoverageInMonths);

        // Anfang auf Anfang+1 - Ende auf Ende - 5 Monate Abdeckung
        aCoveringPeriod.setFrom( aPeriodToCover.getStart().plusMonths( 1 ) );
        aCoveringPeriod.setUntil( aPeriodToCover.getEnd() );
        aCoverageInMonths = DateUtils.getCoverageInMonths( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang auf Anfang+1 - Ende auf Ende - 5 Monate Abdeckung", 5, aCoverageInMonths);

    }

}

// ############################################################################
