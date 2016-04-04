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
import static org.junit.Assert.fail;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.ClubMember;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.settings.ClubSettings;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class DutyCalculatorTest
{
    private static final Logger sm_Log = Logger.getLogger(DutyCalculatorTest.class);
    private static ClubSettings CLUBSETTINGS;
    private static FreeFromDutyCalculator sm_FFDCalculator;

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
        try{
            CLUBSETTINGS = new ClubSettings( Paths.get( "Daten/Einstellungen/VereinsDaten.prop") );
            sm_FFDCalculator = new FreeFromDutyCalculator( CLUBSETTINGS );
        }catch( final Exception fEx ){
            fail("Exception: "+ fEx.getMessage() );
        }
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
        final Halfyear aInvoicingPeriod = new Halfyear( 2000, EPart.FIRST );
        final DutyCalculator aDC = new DutyCalculator( aInvoicingPeriod, CLUBSETTINGS );
        final FreeFromDutyCalculator aFFDCalculator = new FreeFromDutyCalculator( CLUBSETTINGS );

        final int aMemberID = 1;
        final InfoForSingleMember aInfo = new InfoForSingleMember( aMemberID );
        final ClubMember aMember1 = new ClubMember(aMemberID);
        aInfo.setMember( aMember1 );
        aMember1.setBirthday( LocalDate.of( 1930, 1, 1 ) );

        final FreeFromDutySet aFreeFromDutySet = new FreeFromDutySet( aMemberID );
        aInfo.setFreeFromDutySet( aFreeFromDutySet );
        aFFDCalculator.populateFFDSetFromMemberData( aFreeFromDutySet, aInvoicingPeriod, aMember1 );
        final Collection<FreeFromDuty> aEffectiveItems = DutyCalculator.getEffectiveFreeFromDutyItems( aInvoicingPeriod, aInfo.getFreeFromDutyItems() );
        assertEquals("TOO_OLD1", 1, aEffectiveItems.size());
        int aIDX = 0;
        for( final FreeFromDuty aFreeFromDuty : aEffectiveItems ){
            if( aIDX++ == 1 ){
                fail("Zu viele Gründe!");
            }
            sm_Log.info( aFreeFromDuty );
            assertEquals( "1", FreeFromDuty.REASON.TOO_OLD, aFreeFromDuty.getReason() );
        }
        final List<Month> aMonthsDue = DutyCalculator.getMonthsDue( aInvoicingPeriod, aEffectiveItems );
        assertEquals( "TOO_OLD1_Monate", aMonthsDue.size() );

        aMemberID = 2;
        final ClubMember aMember2 = new ClubMember( aMemberID );
        aMember2.setBirthday( LocalDate.of( 1940, 2, 1 ) );

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
    public void test_NORMAL_MEMBER()
    {
        final Halfyear aInvoicingPeriod = new Halfyear( 2000, EPart.FIRST );
        final DutyCalculator aDC = new DutyCalculator( aInvoicingPeriod, CLUBSETTINGS  );

        final ClubMember aMember3 = new ClubMember(3);
        aMember3.setBirthday( LocalDate.of( 1950, 3, 1 ) );

        final FreeFromDuty aFreeFromDuty3 = aDC.isFreeFromDuty( aMember3 );
        sm_Log.info( aFreeFromDuty3 );
        assertNull( "3", aFreeFromDuty3 );

        final int aDutyHours3 = aDC.calculateHoursToWork( aFreeFromDuty3 );
        sm_Log.info( "Plichtstunden 3: "+aDutyHours3 );
        assertEquals("Plichtstunden 3", 300, aDutyHours3);

        // Der MittelMann tritt zum ersten April aus:
        aMember3.setMemberUntil( LocalDate.of( 2000, 4, 1 ) );
        final FreeFromDuty aFreeFromDuty3a = aDC.isFreeFromDuty( aMember3 );
        sm_Log.info( aFreeFromDuty3a );
        assertEquals( "3a", FreeFromDuty.REASON.NO_LONGER_MEMBER, aFreeFromDuty3a.getReason() );

        // Ein neues Mitglied tritt ein:
        final ClubMember aMember33 = new ClubMember(33);
        aMember33.setBirthday( LocalDate.of( 1958, 4, 9 ) );
        aMember33.setMemberFrom( LocalDate.of( 1999, 1, 1 ) );
        final FreeFromDuty aFreeFromDuty33 = aDC.isFreeFromDuty( aMember33 );
        sm_Log.info( aFreeFromDuty33 );
        assertNull( "33", aFreeFromDuty33 );

        final int aDutyHours33 = aDC.calculateHoursToWork( aFreeFromDuty33 );
        sm_Log.info( "Plichtstunden 33: "+aDutyHours33 );
        assertEquals("Plichtstunden 33", 300, aDutyHours33 );

        // Ein neues Mitglied tritt ein:
        final ClubMember aMember34 = new ClubMember(34);
        aMember34.setBirthday( LocalDate.of( 1958, 4, 9 ) );
        aMember34.setMemberFrom( LocalDate.of( 1999, 9, 1 ) );
        final FreeFromDuty aFreeFromDuty34 = aDC.isFreeFromDuty( aMember34 );
        sm_Log.info( aFreeFromDuty34 );
        assertEquals( "34", FreeFromDuty.REASON.DUTY_NOT_YET_EFFECTIVE, aFreeFromDuty34.getReason() );

        final int aDutyHours34 = aDC.calculateHoursToWork( aFreeFromDuty34 );
        sm_Log.info( "Plichtstunden 34: "+aDutyHours34 );
        assertEquals("Plichtstunden 34", 200, aDutyHours34 );

        // Ein neues Mitglied tritt ein:
        final ClubMember aMember333 = new ClubMember(333);
        aMember333.setBirthday( LocalDate.of( 1958, 4, 9 ) );
        aMember333.setMemberFrom( LocalDate.of( 2000, 1, 1 ) );
        final FreeFromDuty aFreeFromDuty333 = aDC.isFreeFromDuty( aMember333 );
        sm_Log.info( aFreeFromDuty333 );
        assertEquals( "333", FreeFromDuty.REASON.DUTY_NOT_YET_EFFECTIVE, aFreeFromDuty333.getReason() );

        final int aDutyHours333 = aDC.calculateHoursToWork( aFreeFromDuty333 );
        sm_Log.info( "Plichtstunden 333: "+aDutyHours333 );
        assertEquals("Plichtstunden 333", 0, aDutyHours333 );

    }

    @Test
    public void test_TOO_YOUNG()
    {
        final Halfyear aInvoicingPeriod = new Halfyear( 2000, EPart.FIRST );
        final DutyCalculator aDC = new DutyCalculator( aInvoicingPeriod, CLUBSETTINGS  );

        final ClubMember aMember4 = new ClubMember(4);
        aMember4.setBirthday( LocalDate.of( 1984, 3, 31 ) );
        final ClubMember aMember5 = new ClubMember(5);
        aMember5.setBirthday( LocalDate.of( 1990, 4, 30 ) );

        final FreeFromDuty aFreeFromDuty4 = aDC.isFreeFromDuty( aMember4 );
        sm_Log.info( aFreeFromDuty4 );
        assertEquals( "4", FreeFromDuty.REASON.TOO_YOUNG, aFreeFromDuty4.getReason() );

        final int aDutyHours4 = aDC.calculateHoursToWork( aFreeFromDuty4 );
        sm_Log.info( "Plichtstunden 4: "+aDutyHours4 );
        assertEquals("Plichtstunden 4", 150, aDutyHours4 );

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

        final int aDutyHours4a = aDC.calculateHoursToWork( aFreeFromDuty4a );
        sm_Log.info( "Plichtstunden 4a: "+aDutyHours4a );
        assertEquals("Plichtstunden 4a", 100, aDutyHours4a );

        // Das junge Kücken tritt zum ersten April aus:
        aMember5.setMemberUntil( LocalDate.of( 2000, 4, 1 ) );
        final FreeFromDuty aFreeFromDuty5a = aDC.isFreeFromDuty( aMember5 );
        sm_Log.info( aFreeFromDuty5a );
        assertEquals( "5a", FreeFromDuty.REASON.TOO_YOUNG, aFreeFromDuty5a.getReason() );

        final int aDutyHours5a = aDC.calculateHoursToWork( aFreeFromDuty5a );
        sm_Log.info( "Plichtstunden 5a: "+aDutyHours5a );
        assertEquals("Plichtstunden 5a", 0, aDutyHours5a );

    }

//    @Test
//    public void test_Austritt()
//    {
//        final Halfyear aInvoicingPeriod = new Halfyear( Year.of( 2000 ), EPart.FIRST );
//        final DutyCalculator aDC = new DutyCalculator( aInvoicingPeriod, GPs  );
//
//        final ClubMember aMember = new ClubMember(4);
//        aMember.setBirthday( LocalDate.of( 1984, 3, 31 ) );
//    }
}

// ############################################################################
