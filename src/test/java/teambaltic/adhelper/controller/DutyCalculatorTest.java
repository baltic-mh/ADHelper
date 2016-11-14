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
import static org.junit.Assert.fail;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.ClubMember;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDuty.REASON;
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

    private static final int ID = 1;
    private static FreeFromDutyCalculator sm_FFDCalculator;
    private static final Halfyear sm_InvoicingPeriod = new Halfyear( 2000, EPart.FIRST );

    private InfoForSingleMember sm_Info;
    private ClubMember sm_Member;
    private FreeFromDutySet sm_FreeFromDutySet;

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
        sm_Info   = new InfoForSingleMember( ID );
        sm_Member = new ClubMember(ID);
        sm_Info.setMember( sm_Member );
        sm_FreeFromDutySet = new FreeFromDutySet( ID );
        sm_Info.setFreeFromDutySet( sm_FreeFromDutySet );
    }

    @After
    public void cleanupAfterEachTest()
    {
    }

    // ########################################################################
    // TESTS
    // ########################################################################

    @Test
    public void test_SimplyTooOld()
    {
        final String aTestName = "SimplyTooOld";
        sm_Member.setBirthday( LocalDate.of( 1930, 1, 1 ) );
        check( aTestName, 0, REASON.TOO_OLD, REASON.NO_LONGER_MEMBER );

    }

    @Test
    public void test_ZuAltUndAustrittImMaerz()
    {
        final String aTestName = "ZuAltUndAustrittImMaerz";
        sm_Member.setBirthday( LocalDate.of( 1930, 1, 1 ) );
        // Der ganz alte Opa tritt zum ersten März aus:
        sm_Member.setMemberUntil( LocalDate.of( 2000, 3, 1 ) );

        check( aTestName, 0, REASON.TOO_OLD, REASON.NO_LONGER_MEMBER );

    }

    @Test
    public void test_ErstImMaerzZuAlt()
    {
        final String aTestName = "ErstImMaerzZuAlt";
        sm_Member.setBirthday( LocalDate.of( 1940, 3, 31 ) );

        check( aTestName, 2, REASON.TOO_OLD );

    }

    @Test
    public void test_ErstImMaerzZuAltAberAustrittImFebruar()
    {
        final String aTestName = "ErstImMaerzZuAltAberAustrittImFebruar";
        sm_Member.setBirthday( LocalDate.of( 1940, 3, 31 ) );
        // (bevor er in den Genuss kommt, alt genug zu sein, ist er ausgetreten :-/
        sm_Member.setMemberUntil( LocalDate.of( 2000, 3, 1 ) );

        check( aTestName, 1, REASON.NO_LONGER_MEMBER, REASON.TOO_OLD );

    }

    @Test
    public void test_NORMAL_MEMBER()
    {
        final String aTestName = "NORMAL_MEMBER";
        sm_Member.setBirthday( LocalDate.of( 1950, 3, 1 ) );

        check( aTestName, 6 );
    }

    @Test
    public void test_AusTrittImApril()
    {
        final String aTestName = "AustrittImApril";
        sm_Member.setBirthday( LocalDate.of( 1950, 3, 1 ) );
        // Der MittelMann tritt zum ersten April aus:
        sm_Member.setMemberUntil( LocalDate.of( 2000, 4, 1 ) );
        check( aTestName, 3, REASON.NO_LONGER_MEMBER );
    }

    @Test
    public void test_EintrittImOktoberImVorjahr()
    {
        final String aTestName = "EintrittImOktoberImVorjahr";
        sm_Member.setBirthday( LocalDate.of( 1950, 3, 1 ) );
        sm_Member.setMemberFrom( LocalDate.of( 1999, 10, 1 ) );
        check( aTestName, 3, REASON.DUTY_NOT_YET_EFFECTIVE );
    }

    @Test
    public void test_EintrittImJanuar()
    {
        final String aTestName = "EintrittImJanuar";
        sm_Member.setBirthday( LocalDate.of( 1950, 3, 1 ) );
        sm_Member.setMemberFrom( LocalDate.of( 2000, 1, 1 ) );
        check( aTestName, 0, REASON.DUTY_NOT_YET_EFFECTIVE );
    }

    @Test
    public void test_TOO_YOUNG()
    {
        final String aTestName = "viel zu Jung";
        sm_Member.setBirthday( LocalDate.of( 1990, 4, 30 ) );
        check( aTestName, 0, REASON.TOO_YOUNG );
    }

    @Test
    public void test_ZuJungBisEndeMaerz()
    {
        final String aTestName = "ZuJungBisEndeMaerz";
        sm_Member.setBirthday( LocalDate.of( 1984, 3, 31 ) );
        check( aTestName, 3, REASON.TOO_YOUNG );
    }

    @Test
    public void test_ZuJungBisEndeMaerzAusTrittImJuni()
    {
        final String aTestName = "ZuJungBisEndeMaerzAusTrittImJuni";
        sm_Member.setBirthday( LocalDate.of( 1984, 3, 31 ) );
        sm_Member.setMemberUntil( LocalDate.of( 2000, 6, 1 ) );
        // Das ältere Kücken tritt zum ersten Juni aus:
        // er wird am 1.4. arbeitsdienstpflichtig und steigt zum 1.6. aus.
        // Also ist er für die Monate 1-3 nicht dienstpflichtig,
        // für die Monate 4 und 5 dienstpflichtig
        // und für Monat 6 wieder nicht dienstpflichtig.
        // Also 2 von 6 Monaten (- sind 1/3 = eine Stunde!)
        check( aTestName, 2, REASON.TOO_YOUNG, REASON.NO_LONGER_MEMBER );
    }

    private void check( final String fTestName, final int fExp_MonthsDue, final REASON ... fExp_Reasons )
    {
        sm_FFDCalculator.populateFFDSetFromMemberData( sm_FreeFromDutySet, sm_InvoicingPeriod, sm_Member );

        final List<FreeFromDuty> aEffectiveItems = DutyCalculator.getEffectiveFreeFromDutyItems( sm_InvoicingPeriod, sm_Info.getFreeFromDutyItems() );
        assertEquals(fTestName+": EffectiveFFDs.size", fExp_Reasons.length, aEffectiveItems.size());
        for( int aIDX = 0; aIDX < fExp_Reasons.length; aIDX++ ){
            final FreeFromDuty aFreeFromDuty = aEffectiveItems.get( aIDX );
            assertEquals( fTestName+": FFD "+aIDX, fExp_Reasons[aIDX], aFreeFromDuty.getReason() );
        }

        final List<Month> aMonthsDue = DutyCalculator.getMonthsDue( sm_InvoicingPeriod, aEffectiveItems );
        final StringBuffer aSB = new StringBuffer();
        for( final Month aMonth : aMonthsDue ){
            aSB.append( aMonth ).append( ", " );
        }
        sm_Log.info( fTestName+": MonthsDue: " + aSB.toString());
        assertEquals( fTestName+": MonthsDue ", fExp_MonthsDue, aMonthsDue.size() );
    }

}

// ############################################################################
