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

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.ClubMember;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDuty.REASON;
import teambaltic.adhelper.model.GlobalParameters;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IInvoicingPeriod;
import teambaltic.adhelper.utils.Log4J;
import teambaltic.adhelper.utils.TestUtils;

// ############################################################################
public class ChargeCalculatorTest
{
    private static final Logger sm_Log = Logger.getLogger(ChargeCalculatorTest.class);
    private static GlobalParameters GPs;

    private static ClubMember MHW;
    private static ClubMember MTW;
    private static ClubMember BJW;
    private static ClubMember MMW;

    private static FreeFromDuty MHW_FreeFromDuty;

    private static ListProvider<IClubMember> MemberListProvider;

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
        GPs = new GlobalParameters();
        MemberListProvider = new ListProvider<>();
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
        final IInvoicingPeriod aInvoicingPeriod = new Halfyear( Year.of( 2016 ), EPart.FIRST );
        final DutyCalculator aDC = new DutyCalculator( aInvoicingPeriod, GPs );

        final ChargeCalculator aCC = new ChargeCalculator( aDC );
        final DutyCharge aCharge_MHW = aCC.calculate( MHW, 8100,  300, MHW_FreeFromDuty );
        final DutyCharge aCharge_MTW = aCC.calculate( MTW, 0,  0, null );
        aCharge_MHW.addCharge( aCharge_MTW );
        final DutyCharge aCharge_BJW = aCC.calculate( BJW, 400,  200, null );
        aCharge_MHW.addCharge( aCharge_BJW );
        final DutyCharge aCharge_MMW = aCC.calculate( MMW, 100,  0, null );
        aCharge_MHW.addCharge( aCharge_MMW );

        final int aToPayTotal = aCC.balance( aCharge_MHW );
        final List<DutyCharge> aAllDutyCharges = aCharge_MHW.getAllDutyCharges();
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
        sm_Log.info(String.format( "Verbleibende Stunden zu zahlen: %5.1f",aToPayTotal/100.0));
        TestUtils.logMethodEnd( aStartTime, aMethodName );
    }

    // ########################################################################
    // SUPPORT AREA
    // ########################################################################

    private static void createFamilieWeber()
    {
        MHW = new ClubMember( 44 );
        MHW.setBirthday( LocalDate.of( 1958, 4, 9 ) );
        MHW.setName( "Mathias" );
        MemberListProvider.add( MHW );

        MHW_FreeFromDuty = new FreeFromDuty( MHW.getID(), REASON.MANAGEMENT );
        MHW_FreeFromDuty.setFrom( LocalDate.of( 2011, 2, 14 ) );

        MTW = new ClubMember( 46 );
        MTW.setBirthday( LocalDate.of( 1960, 3, 20 ) );
        MTW.setName( "Marie-Theres" );
        MemberListProvider.add( MTW );

        BJW = new ClubMember( 48 );
        BJW.setBirthday( LocalDate.of( 1993, 9, 26 ) );
        BJW.setName( "Birke" );
        MemberListProvider.add( BJW );

        MMW = new ClubMember( 50 );
        MMW.setBirthday( LocalDate.of( 1996, 9, 26 ) );
        MMW.setName( "Merle" );
        MemberListProvider.add( MMW );

    }
}

// ############################################################################
