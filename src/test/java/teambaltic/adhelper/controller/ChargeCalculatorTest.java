/**
 * ChargeCalculatorTest.java
 *
 * Created on 01.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import static org.junit.Assert.fail;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.ClubMember;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDuty.REASON;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.settings.ClubSettings;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.utils.Log4J;
import teambaltic.adhelper.utils.TestUtils;

// ############################################################################
public class ChargeCalculatorTest
{
    private static final Logger sm_Log = Logger.getLogger(ChargeCalculatorTest.class);
    private static ClubSettings CLUBSETTINGS;

    private static ClubMember MHW;
    private static ClubMember MTW;
    private static ClubMember BJW;
    private static ClubMember MMW;

    private static FreeFromDuty MHW_FreeFromDuty;

    private static Balance MHW_Balance;
    private static Balance BJW_Balance;
    private static Balance MMW_Balance;

    private static WorkEventsAttended MHW_WorkEventsAttended;
    private static WorkEventsAttended BJW_WorkEventsAttended;

    private static ListProvider<WorkEventsAttended> WorkEventsAttendedListProvider;
    private static ListProvider<IClubMember> MemberListProvider;
    private static ListProvider<Balance>     BalanceListProvider;

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
        try{
            CLUBSETTINGS = new ClubSettings( Paths.get( "Daten/Einstellungen/Vereinsparameter.prop") );
        }catch( final Exception fEx ){
            fail("Exception: "+fEx.getMessage() );
        }
        MemberListProvider  = new ListProvider<>();
        BalanceListProvider = new ListProvider<>();
        WorkEventsAttendedListProvider = new ListProvider<>();
        createFamilieWeber();
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
    public void test()
    {
        final String aMethodName = TestUtils.getMethodName();
        final long aStartTime = TestUtils.logMethodStart( aMethodName );
        final IPeriod aInvoicingPeriod = new Halfyear( 2016, EPart.FIRST );

        final ChargeCalculator aCC = new ChargeCalculator( aInvoicingPeriod, CLUBSETTINGS );
        final DutyCharge aCharge_MHW = new DutyCharge(MHW.getID(), MHW_Balance.getValue());
        aCC.calculate( aCharge_MHW, MHW_WorkEventsAttended, MHW_FreeFromDuty );
        final DutyCharge aCharge_MTW = new DutyCharge(MTW.getID(), 0);
        aCC.calculate( aCharge_MTW, null, null );
        aCharge_MHW.addRelative( aCharge_MTW );
        final DutyCharge aCharge_BJW = new DutyCharge(BJW.getID(), BJW_Balance.getValue() );
        aCC.calculate( aCharge_BJW, BJW_WorkEventsAttended, null );
        aCharge_MHW.addRelative( aCharge_BJW );
        final DutyCharge aCharge_MMW = new DutyCharge(MMW.getID(), MMW_Balance.getValue() );
        aCC.calculate( aCharge_MMW, null, null );
        aCharge_MHW.addRelative( aCharge_MMW );

        aCC.balance( aCharge_MHW );
        reportCharge( aCharge_MHW );
        TestUtils.logMethodEnd( aStartTime, aMethodName );
    }

    // ########################################################################
    // SUPPORT AREA
    // ########################################################################

    private static void createFamilieWeber()
    {
        // ====================================================================
        // MATHIAS
        final int aID_MW = 44;
        MHW = new ClubMember( aID_MW );
        MHW.setBirthday( LocalDate.of( 1958, 4, 9 ) );
        MHW.setName( "Mathias" );
        MemberListProvider.add( MHW );

        MHW_FreeFromDuty = new FreeFromDuty( MHW.getID(), REASON.MANAGEMENT );
        MHW_FreeFromDuty.setFrom( LocalDate.of( 2011, 2, 14 ) );

        MHW_Balance = new Balance( aID_MW, 8100 );
        BalanceListProvider.add( MHW_Balance );

        MHW_WorkEventsAttended = new WorkEventsAttended( aID_MW );
        WorkEventsAttendedListProvider.add( MHW_WorkEventsAttended );
        final WorkEvent aWorkEvent1_MW = new WorkEvent( aID_MW );
        MHW_WorkEventsAttended.addWorkEvent( aWorkEvent1_MW );
        aWorkEvent1_MW.setHours( 100 );
        aWorkEvent1_MW.setDate( LocalDate.of( 2016, 3, 1 ) );
        final WorkEvent aWorkEvent2_MW = new WorkEvent( aID_MW );
        MHW_WorkEventsAttended.addWorkEvent( aWorkEvent2_MW );
        aWorkEvent2_MW.setHours( 100 );
        aWorkEvent2_MW.setDate( LocalDate.of( 2016, 4, 1 ) );

        // ====================================================================
        // MARIE-THERES
        MTW = new ClubMember( 46 );
        MTW.setBirthday( LocalDate.of( 1960, 3, 20 ) );
        MTW.setName( "Marie-Theres" );
        MemberListProvider.add( MTW );

        // ====================================================================
        // BIRKE
        final int aID_BJ = 48;
        BJW = new ClubMember( aID_BJ );
        BJW.setBirthday( LocalDate.of( 1993, 9, 26 ) );
        BJW.setName( "Birke" );
        MemberListProvider.add( BJW );

        BJW_Balance = new Balance( aID_BJ, 400 );
        BalanceListProvider.add( BJW_Balance );

        BJW_WorkEventsAttended = new WorkEventsAttended( aID_BJ );
        WorkEventsAttendedListProvider.add( BJW_WorkEventsAttended );
        final WorkEvent aWorkEvent1_BJ = new WorkEvent( aID_BJ );
        BJW_WorkEventsAttended.addWorkEvent( aWorkEvent1_BJ );
        aWorkEvent1_BJ.setHours( 200 );
        aWorkEvent1_BJ.setDate( LocalDate.of( 2016, 3, 1 ) );
        final WorkEvent aWorkEvent2_BJ = new WorkEvent( aID_BJ );
        BJW_WorkEventsAttended.addWorkEvent( aWorkEvent2_BJ );
        aWorkEvent2_BJ.setHours( 100 );
        aWorkEvent2_BJ.setDate( LocalDate.of( 2016, 4, 1 ) );

        // ====================================================================
        // MERLE
        final int aID_MM = 50;
        MMW = new ClubMember( aID_MM );
        MMW.setBirthday( LocalDate.of( 1996, 9, 26 ) );
        MMW.setName( "Merle" );
        MemberListProvider.add( MMW );

        MMW_Balance = new Balance( aID_MM, 100 );
        BalanceListProvider.add( MMW_Balance );

    }

    private static void reportCharge( final DutyCharge fCharge )
    {
        final List<DutyCharge> aAllDutyCharges = fCharge.getAllDutyCharges();
        sm_Log.info( String.format("%-20s  %5s %5s %5s %5s %5s %5s",
                "Name", "Guth.", "Gearb.", "Pflicht", "Guth.II", "Zu zahl", "Gut.III" ));
        for( final DutyCharge aC : aAllDutyCharges ){
            sm_Log.info( String.format("%-20s  %5.1f %5.1f    %5.1f   %5.1f   %5.1f   %5.1f",
                    MemberListProvider.get( aC.getMemberID() ).getName(),
                    aC.getBalance_Original()/100.0,
                    aC.getHoursWorked()/100.0,
                    aC.getHoursDue()/100.0,
                    aC.getBalance_Charged()/100.0,
                    aC.getHoursToPay()/100.0,
                    aC.getBalance_ChargedAndAdjusted()/100.0
                    ) );
        }
        sm_Log.info(String.format( "Verbleibende Stunden zu zahlen: %5.1f",
                fCharge.getHoursToPayTotal()/100.0));
    }

}

// ############################################################################
