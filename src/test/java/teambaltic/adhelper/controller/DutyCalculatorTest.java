/**
 * DutyCalculatorTest.java
 *
 * Created on 31.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.Year;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.ClubMember;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class DutyCalculatorTest
{
    private static final Logger sm_Log = Logger.getLogger(DutyCalculatorTest.class);

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
    public void test_TOO_OLD()
    {
        final Halfyear aInvoicingPeriod = new Halfyear( Year.of( 2000 ), EPart.FIRST );
        final DutyCalculator aDC = new DutyCalculator( aInvoicingPeriod );

        final ClubMember aMember1 = new ClubMember(1);
        aMember1.setBirthday( LocalDate.of( 1930, 1, 1 ) );
        final ClubMember aMember2 = new ClubMember(2);
        aMember2.setBirthday( LocalDate.of( 1940, 2, 1 ) );

        final FreeFromDuty aFreeFromDuty1 = aDC.isFreeFromDuty( aMember1 );
        sm_Log.info( aFreeFromDuty1 );
        assertEquals( "1", FreeFromDuty.REASON.TOO_OLD, aFreeFromDuty1.getReason() );
        final FreeFromDuty aFreeFromDuty2 = aDC.isFreeFromDuty( aMember2 );
        sm_Log.info( aFreeFromDuty2 );
        assertEquals( "2", FreeFromDuty.REASON.TOO_OLD, aFreeFromDuty2.getReason() );

        // Der ganz alte Opa tritt zum ersten März aus:
        aMember1.setMemberUntil( LocalDate.of( 2000, 3, 1 ) );
        final FreeFromDuty aFreeFromDuty1a = aDC.isFreeFromDuty( aMember1 );
        sm_Log.info( aFreeFromDuty1a );
        assertEquals( "1a", FreeFromDuty.REASON.TOO_OLD, aFreeFromDuty1a.getReason() );

        // Der nicht ganz alte Opa tritt zum ersten Januar aus:
        // (bevor er in den Genuss kommt, alt genug zu sein, ist er ausgetreten :-/
        aMember2.setMemberUntil( LocalDate.of( 2000, 1, 1 ) );
        final FreeFromDuty aFreeFromDuty2a = aDC.isFreeFromDuty( aMember2 );
        sm_Log.info( aFreeFromDuty2a );
        assertEquals( "2a", FreeFromDuty.REASON.NO_LONGER_MEMBER, aFreeFromDuty2a.getReason() );
    }

    @Test
    public void test_MIDDLE_AGE()
    {
        final Halfyear aInvoicingPeriod = new Halfyear( Year.of( 2000 ), EPart.FIRST );
        final DutyCalculator aDC = new DutyCalculator( aInvoicingPeriod );

        final ClubMember aMember3 = new ClubMember(3);
        aMember3.setBirthday( LocalDate.of( 1950, 3, 1 ) );

        final FreeFromDuty aFreeFromDuty3 = aDC.isFreeFromDuty( aMember3 );
        sm_Log.info( aFreeFromDuty3 );
        assertNull( "3", aFreeFromDuty3 );

        // Der MittelMann tritt zum ersten April aus:
        aMember3.setMemberUntil( LocalDate.of( 2000, 4, 1 ) );
        final FreeFromDuty aFreeFromDuty3a = aDC.isFreeFromDuty( aMember3 );
        sm_Log.info( aFreeFromDuty3a );
        assertEquals( "3a", FreeFromDuty.REASON.NO_LONGER_MEMBER, aFreeFromDuty3a.getReason() );
    }

    @Test
    public void test_TOO_YOUNG()
    {
        final Halfyear aInvoicingPeriod = new Halfyear( Year.of( 2000 ), EPart.FIRST );
        final DutyCalculator aDC = new DutyCalculator( aInvoicingPeriod );

        final ClubMember aMember4 = new ClubMember(4);
        aMember4.setBirthday( LocalDate.of( 1984, 3, 31 ) );
        final ClubMember aMember5 = new ClubMember(5);
        aMember5.setBirthday( LocalDate.of( 1990, 4, 30 ) );

        final FreeFromDuty aFreeFromDuty4 = aDC.isFreeFromDuty( aMember4 );
        sm_Log.info( aFreeFromDuty4 );
        assertEquals( "4", FreeFromDuty.REASON.TOO_YOUNG, aFreeFromDuty4.getReason() );

        final float aDutyHours4 = aDC.calculate( aFreeFromDuty4 );
        sm_Log.info( "Plichtstunden 4: "+aDutyHours4 );
        assertEquals("Plichtstunden 4", 1.5f, aDutyHours4, 0.001f);

        final FreeFromDuty aFreeFromDuty5 = aDC.isFreeFromDuty( aMember5 );
        sm_Log.info( aFreeFromDuty5 );
        assertEquals( "5", FreeFromDuty.REASON.TOO_YOUNG, aFreeFromDuty5.getReason() );

        // Das ältere Kücken tritt zum ersten Juni aus:
        // er wird am 1.4. arbeitsdienstpflichtig und steigt zum 1.6. aus.
        // Also ist er für die Monate 1-3 nicht dienstpflichtig,
        // für die Monate 4 und 5 dienstpflichtig
        // und für Monat 6 wieder nicht dienstpflichtig.
        // Also 2 von 6 Monaten - sind 1/3 = eine Stunde!
        aMember4.setMemberUntil( LocalDate.of( 2000, 6, 1 ) );
        final FreeFromDuty aFreeFromDuty4a = aDC.isFreeFromDuty( aMember4 );
        sm_Log.info( aFreeFromDuty4a );
        assertEquals( "4a", FreeFromDuty.REASON.TOO_YOUNG, aFreeFromDuty4a.getReason() );

        final float aDutyHours4a = aDC.calculate( aFreeFromDuty4a );
        sm_Log.info( "Plichtstunden 4a: "+aDutyHours4a );
        assertEquals("Plichtstunden 4a", 1.0f, aDutyHours4a, 0.001f);

        // Das junge Kücken tritt zum ersten April aus:
        aMember5.setMemberUntil( LocalDate.of( 2000, 4, 1 ) );
        final FreeFromDuty aFreeFromDuty5a = aDC.isFreeFromDuty( aMember5 );
        sm_Log.info( aFreeFromDuty5a );
        assertEquals( "5a", FreeFromDuty.REASON.TOO_YOUNG, aFreeFromDuty5a.getReason() );

        final float aDutyHours5a = aDC.calculate( aFreeFromDuty5a );
        sm_Log.info( "Plichtstunden 5a: "+aDutyHours5a );
        assertEquals("Plichtstunden 5a", 0.0f, aDutyHours5a, 0.001f);

    }

}

// ############################################################################
