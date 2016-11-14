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
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;

// ############################################################################
public class DateUtilsTest
{
    @Test
    public void test_getCoverageInMonths()
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

    @Test
    public void test_getMonthsNotCovered()
    {
        final Halfyear aPeriodToCover = new Halfyear( 2014, EPart.FIRST);
        final FreeFromDuty aCoveringPeriod = new FreeFromDuty( 0, null );
        // Kein Anfang und kein Ende - Totale Abdeckung!
        List<Month> aMonthsNotCovered = DateUtils.getMonthsNotCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang und kein Ende", 0, aMonthsNotCovered.size());

        // Anfang nach dem Ende - 0 Abdeckung
        aCoveringPeriod.setFrom( LocalDate.of( 2015, 1, 1 ) );
        aMonthsNotCovered = DateUtils.getMonthsNotCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang nach dem Ende - 0 Abdeckung", 6, aMonthsNotCovered.size());

        // Wieder kein Anfang und kein Ende - Totale Abdeckung!
        aCoveringPeriod.setFrom( null );
        aMonthsNotCovered = DateUtils.getMonthsNotCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang und kein Ende2", 0, aMonthsNotCovered.size());

        // Ende vor dem Start - 0 Abdeckung
        aCoveringPeriod.setUntil( LocalDate.of( 2013, 1, 1 ) );
        aMonthsNotCovered = DateUtils.getMonthsNotCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Ende vor dem Start - 0 Abdeckung", 6, aMonthsNotCovered.size());

        // Wieder kein Anfang und kein Ende - Totale Abdeckung!
        aCoveringPeriod.setUntil( null );
        aMonthsNotCovered = DateUtils.getMonthsNotCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang und kein Ende3", 0, aMonthsNotCovered.size());

        // Anfang vor dem Anfang - kein Ende - totale Abdeckung
        aCoveringPeriod.setFrom( LocalDate.of( 2013, 1, 1 ) );
        aMonthsNotCovered = DateUtils.getMonthsNotCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang vor dem Anfang - kein Ende - totale Abdeckung", 0, aMonthsNotCovered.size());

        // Kein Anfang - Ende nach dem Ende - totale Abdeckung
        aCoveringPeriod.setFrom( null );
        aCoveringPeriod.setUntil( LocalDate.of( 2015, 1, 1 ) );
        aMonthsNotCovered = DateUtils.getMonthsNotCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang - Ende nach dem Ende - totale Abdeckung", 0, aMonthsNotCovered.size());

        // Anfang auf Anfang - Ende auf Ende - totale Abdeckung
        aCoveringPeriod.setFrom( aPeriodToCover.getStart() );
        aCoveringPeriod.setUntil( aPeriodToCover.getEnd() );
        aMonthsNotCovered = DateUtils.getMonthsNotCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang auf Anfang - Ende auf Ende - totale Abdeckung", 0, aMonthsNotCovered.size());

        // Anfang auf Anfang+1 - Ende auf Ende - 5 Monate Abdeckung
        aCoveringPeriod.setFrom( aPeriodToCover.getStart().plusMonths( 1 ) );
        aCoveringPeriod.setUntil( aPeriodToCover.getEnd() );
        aMonthsNotCovered = DateUtils.getMonthsNotCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang auf Anfang+1 - Ende auf Ende - 5 Monate Abdeckung", 1, aMonthsNotCovered.size());
        final Month aMonth = aMonthsNotCovered.get( 0 );
        final String aDisplayName = aMonth.getDisplayName( TextStyle.FULL, Locale.GERMAN );
        assertEquals("Januar", "Januar", aDisplayName);

    }

    @Test
    public void test_getMonthsCovered()
    {
        final Halfyear aPeriodToCover = new Halfyear( 2014, EPart.FIRST);
        final FreeFromDuty aCoveringPeriod = new FreeFromDuty( 0, null );
        // Kein Anfang und kein Ende - Totale Abdeckung!
        List<Month> aMonthsCovered = DateUtils.getMonthsCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang und kein Ende", 6, aMonthsCovered.size());

        // Anfang nach dem Ende - 0 Abdeckung
        aCoveringPeriod.setFrom( LocalDate.of( 2015, 1, 1 ) );
        aMonthsCovered = DateUtils.getMonthsCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang nach dem Ende - 0 Abdeckung", 0, aMonthsCovered.size());

        // Wieder kein Anfang und kein Ende - Totale Abdeckung!
        aCoveringPeriod.setFrom( null );
        aMonthsCovered = DateUtils.getMonthsCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang und kein Ende2", 6, aMonthsCovered.size());

        // Ende vor dem Start - 0 Abdeckung
        aCoveringPeriod.setUntil( LocalDate.of( 2013, 1, 1 ) );
        aMonthsCovered = DateUtils.getMonthsCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Ende vor dem Start - 0 Abdeckung", 0, aMonthsCovered.size());

        // Wieder kein Anfang und kein Ende - Totale Abdeckung!
        aCoveringPeriod.setUntil( null );
        aMonthsCovered = DateUtils.getMonthsCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang und kein Ende3", 6, aMonthsCovered.size());

        // Anfang vor dem Anfang - kein Ende - totale Abdeckung
        aCoveringPeriod.setFrom( LocalDate.of( 2013, 1, 1 ) );
        aMonthsCovered = DateUtils.getMonthsCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang vor dem Anfang - kein Ende - totale Abdeckung", 6, aMonthsCovered.size());

        // Kein Anfang - Ende nach dem Ende - totale Abdeckung
        aCoveringPeriod.setFrom( null );
        aCoveringPeriod.setUntil( LocalDate.of( 2015, 1, 1 ) );
        aMonthsCovered = DateUtils.getMonthsCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Kein Anfang - Ende nach dem Ende - totale Abdeckung", 6, aMonthsCovered.size());

        // Anfang auf Anfang - Ende auf Ende - totale Abdeckung
        aCoveringPeriod.setFrom( aPeriodToCover.getStart() );
        aCoveringPeriod.setUntil( aPeriodToCover.getEnd() );
        aMonthsCovered = DateUtils.getMonthsCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang auf Anfang - Ende auf Ende - totale Abdeckung", 6, aMonthsCovered.size());

        // Anfang auf Anfang+1 - Ende auf Ende - 5 Monate Abdeckung
        aCoveringPeriod.setFrom( aPeriodToCover.getStart().plusMonths( 1 ) );
        aCoveringPeriod.setUntil( aPeriodToCover.getEnd() );
        aMonthsCovered = DateUtils.getMonthsCovered( aCoveringPeriod, aPeriodToCover );
        assertEquals("Anfang auf Anfang+1 - Ende auf Ende - 5 Monate Abdeckung", 5, aMonthsCovered.size());
        final Month aMonth = aMonthsCovered.get( 0 );
        final String aDisplayName = aMonth.getDisplayName( TextStyle.FULL, Locale.GERMAN );
        assertEquals("Februar", "Februar", aDisplayName);

    }
}

// ############################################################################
