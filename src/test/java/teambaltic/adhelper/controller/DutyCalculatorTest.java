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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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

    private InfoForSingleMember m_Info;
    private ClubMember m_Member;
    private FreeFromDutySet m_FreeFromDutySet;

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
        try{
            CLUBSETTINGS = new ClubSettings( Paths.get( "misc/TestResources/VereinsDaten.properties") );
            sm_FFDCalculator = new FreeFromDutyCalculator( CLUBSETTINGS );
        }catch( final Exception fEx ){
            fail("Exception: "+ fEx.getMessage() );
        }
    }

    @Before
    public void initBeforeEachTest()
    {
        m_Info   = new InfoForSingleMember( ID );
        m_Member = new ClubMember(ID);
        m_Info.setMember( m_Member );
        m_FreeFromDutySet = new FreeFromDutySet( ID );
        m_Info.setFreeFromDutySet( m_FreeFromDutySet );
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
        m_Member.setBirthday( LocalDate.of( 1930, 1, 1 ) );
        check( aTestName, 0, REASON.TOO_OLD );

    }

    @Test
    public void test_ZuAltUndAustrittImMaerz()
    {
        final String aTestName = "ZuAltUndAustrittImMaerz";
        m_Member.setBirthday( LocalDate.of( 1930, 1, 1 ) );
        // Der ganz alte Opa tritt zum ersten März aus:
        m_Member.setMemberUntil( LocalDate.of( 2000, 3, 1 ) );

        check( aTestName, 0, REASON.TOO_OLD, REASON.NO_LONGER_MEMBER );

    }

    @Test
    public void test_ErstImMaerzZuAlt()
    {
        final String aTestName = "ErstImMaerzZuAlt";
        m_Member.setBirthday( LocalDate.of( 1940, 3, 31 ) );

        check( aTestName, 2, REASON.TOO_OLD );

    }

    @Test
    public void test_ErstImMaerzZuAltAberAustrittImFebruar()
    {
        final String aTestName = "ErstImMaerzZuAltAberAustrittImFebruar";
        m_Member.setBirthday( LocalDate.of( 1940, 3, 31 ) );
        // (bevor er in den Genuss kommt, alt genug zu sein, ist er ausgetreten :-/
        m_Member.setMemberUntil( LocalDate.of( 2000, 1, 29 ) );

        check( aTestName, 1, REASON.NO_LONGER_MEMBER, REASON.TOO_OLD );

    }

    @Test
    public void test_NORMAL_MEMBER()
    {
        final String aTestName = "NORMAL_MEMBER";
        m_Member.setBirthday( LocalDate.of( 1950, 3, 1 ) );

        check( aTestName, 6 );
    }

    @Test
    public void test_AusTrittImApril()
    {
        final String aTestName = "AustrittImApril";
        m_Member.setBirthday( LocalDate.of( 1950, 3, 1 ) );
        // Der MittelMann tritt zum ersten April aus:
        m_Member.setMemberUntil( LocalDate.of( 2000, 3, 31 ) );
        check( aTestName, 3, REASON.NO_LONGER_MEMBER );
    }

    @Test
    public void test_EintrittImOktoberImVorjahr()
    {
        final String aTestName = "EintrittImOktoberImVorjahr";
        m_Member.setBirthday( LocalDate.of( 1950, 3, 1 ) );
        m_Member.setMemberFrom( LocalDate.of( 1999, 10, 1 ) );
        check( aTestName, 3, REASON.DUTY_NOT_YET_EFFECTIVE );
    }

    @Test
    public void test_EintrittImJanuar()
    {
        final String aTestName = "EintrittImJanuar";
        m_Member.setBirthday( LocalDate.of( 1950, 3, 1 ) );
        m_Member.setMemberFrom( LocalDate.of( 2000, 1, 1 ) );
        check( aTestName, 0, REASON.DUTY_NOT_YET_EFFECTIVE );
    }

    @Test
    public void test_TOO_YOUNG()
    {
        final String aTestName = "viel zu Jung";
        m_Member.setBirthday( LocalDate.of( 1990, 4, 30 ) );
        check( aTestName, 0, REASON.TOO_YOUNG );
    }

    @Test
    public void test_ZuJungBisEndeMaerz()
    {
        final String aTestName = "ZuJungBisEndeMaerz";
        m_Member.setBirthday( LocalDate.of( 1984, 3, 31 ) );
        check( aTestName, 3, REASON.TOO_YOUNG );
    }

    @Test
    public void test_ZuJungBisEndeMaerzAusTrittImJuni()
    {
        final String aTestName = "ZuJungBisEndeMaerzAusTrittImJuni";
        m_Member.setBirthday( LocalDate.of( 1984, 3, 31 ) );
        m_Member.setMemberUntil( LocalDate.of( 2000, 5, 31 ) );
        // Das ältere Kücken tritt zum ersten Juni aus:
        // er wird am 1.4. arbeitsdienstpflichtig und steigt zum 1.6. aus.
        // Also ist er für die Monate 1-3 nicht dienstpflichtig,
        // für die Monate 4 und 5 dienstpflichtig
        // und für Monat 6 wieder nicht dienstpflichtig.
        // Also 2 von 6 Monaten (- sind 1/3 = eine Stunde!)
        // ACHTUNG: Das ist nur theoretischer Natur!
        //          Offiziell ist ein Austritt nur zum Ende eines
        //          Quartals möglich! Das wird im Programm aber NICHT
        //          überprüft! Das ist Sache der Mitgliederverwaltung!
        check( aTestName, 2, REASON.TOO_YOUNG, REASON.NO_LONGER_MEMBER );
    }

    private void check( final String fTestName, final int fExp_MonthsDue, final REASON ... fExp_Reasons )
    {
        sm_FFDCalculator.populateFFDSetFromMemberData( m_FreeFromDutySet, sm_InvoicingPeriod, m_Member );

        final Collection<FreeFromDuty> aEffectiveItems = m_Info.getFreeFromDutyItems(sm_InvoicingPeriod);
        assertEquals(fTestName+": EffectiveFFDs.size", fExp_Reasons.length, aEffectiveItems.size());
        final List<REASON> aReasonList = Arrays.asList( fExp_Reasons );
        for( final Iterator<FreeFromDuty> aIterator = aEffectiveItems.iterator(); aIterator.hasNext(); ){
            final FreeFromDuty aFreeFromDuty = aIterator.next();
            final REASON aReason = aFreeFromDuty.getReason();
            assertTrue( fTestName+": FFD "+aReason, aReasonList.contains( aReason ));
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
