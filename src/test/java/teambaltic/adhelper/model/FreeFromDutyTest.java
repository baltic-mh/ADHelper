/**
 * FreeFromDutyTest.java
 *
 * Created on 04.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.FreeFromDuty.REASON;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class FreeFromDutyTest
{
    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
    }

    @Before
    public void initBeforeEachTest()
    {
    }

    @After
    public void cleanupAfterEachTest()
    {
    }

    // ########################################################################
    // TESTS
    // ########################################################################

    @Test
    public void test_isWithinMyPeriod()
    {
        final FreeFromDuty aFFD = new FreeFromDuty(42, REASON.INDIVIDUALREASON);
        assertNotNull("FFD", aFFD);

        final LocalDate aEvent = LocalDate.of( 2000, Month.FEBRUARY, 15 );
        boolean aWithinMyPeriod = aFFD.isWithinMyPeriod( aEvent );
        assertTrue("Beide Enden offen", aWithinMyPeriod);

        aFFD.setUntil( LocalDate.of( 2000, Month.MAY, 15 ) );
        aWithinMyPeriod = aFFD.isWithinMyPeriod( aEvent );
        assertTrue("Anfang offen, Ende nach Event", aWithinMyPeriod);

        aFFD.setFrom( LocalDate.of( 2000, Month.FEBRUARY, 15 ) );
        aWithinMyPeriod = aFFD.isWithinMyPeriod( aEvent );
        assertTrue("Anfang auf Event, Ende nach Event", aWithinMyPeriod);

        aFFD.setFrom( LocalDate.of( 2000, Month.FEBRUARY, 16 ) );
        aWithinMyPeriod = aFFD.isWithinMyPeriod( aEvent );
        assertTrue("Anfang ein Tag nach Event im selben Monat, Ende nach Event", aWithinMyPeriod);

        aFFD.setFrom( LocalDate.of( 2000, Month.MARCH, 16 ) );
        aWithinMyPeriod = aFFD.isWithinMyPeriod( aEvent );
        assertFalse("Anfang ein Monat nach Event, Ende nach Event", aWithinMyPeriod);

        aFFD.setFrom( null );
        aWithinMyPeriod = aFFD.isWithinMyPeriod( aEvent );
        assertTrue("Anfang wieder offen, Ende nach Event", aWithinMyPeriod);

        final LocalDate aEvent2 = LocalDate.of( 2000, Month.MAY, 31 );
        aWithinMyPeriod = aFFD.isWithinMyPeriod( aEvent2 );
        assertTrue("Anfang offen, Event2: 31. Mai (nach Ende im selben Monat)", aWithinMyPeriod);

        final LocalDate aEvent3 = aEvent2.plusDays( 1 );
        aWithinMyPeriod = aFFD.isWithinMyPeriod( aEvent3 );
        assertFalse("Anfang offen, Event3: einen Tag nach Event2 - also nächster Monat", aWithinMyPeriod);

    }


}

// ############################################################################
