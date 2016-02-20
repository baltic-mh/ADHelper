/**
 * AD_DataProviderTest.java
 *
 * Created on 05.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.inout.BaseInfoReader;
import teambaltic.adhelper.inout.DetailsReporter;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.utils.FileUtils;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class ADH_DataProviderTest
{
    private static final Logger sm_Log = Logger.getLogger(ADH_DataProviderTest.class);

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
    public void test_Read()
    {
        final ADH_DataProvider aChef = init();

        for( final InfoForSingleMember aInfo : aChef.getAll() ){
            final IClubMember aMember = aInfo.getMember();
            sm_Log.info("Mitglied: "+aMember);
            final Balance aBalance = aInfo.getBalance();
            if( aBalance != null ){
                sm_Log.info("\tGuthaben: "+aBalance);
            }
            final FreeFromDuty aFreeFromDuty = aInfo.getFreeFromDuty();
            if( aFreeFromDuty != null ){
                sm_Log.info("\tAD-Befreiung: "+aFreeFromDuty);
            }
            final WorkEventsAttended aWorkEventsAttended = aInfo.getWorkEventsAttended();
            if( aWorkEventsAttended != null ){
                sm_Log.info("\tAD-Teilnahmen: "+aWorkEventsAttended);
            }
        }
    }

    @Test
    public void test_Calculate()
    {
        final ADH_DataProvider aChef = init();

        final Halfyear aInvoicingPeriod = new Halfyear( 2014, EPart.SECOND );

        aChef.calculateDutyCharges( aInvoicingPeriod );
        aChef.joinRelatives();
        aChef.balanceRelatives();
        DetailsReporter.report( aChef, Paths.get( "." ) );

    }

    @Test
    public void test_MerleWeber() throws Exception
    {
        final File aFile = new File("misc/TestResources/Tabellen/Mitglieder.csv");
        final BaseInfoReader aReader = new BaseInfoReader( aFile );

        final List<String>aColumnNames = FileUtils.readColumnNames( aFile );

        final String aLineForSingleMember = "10412;10174;01.01.1970;01.04.2014;31.12.2099;Merle;Weber;;;;;ERW o. Boot, o. Spind;Frau;Alter Kieler Weg 2;24161;Altenholz";
        final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aLineForSingleMember );
        final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
        final int aID = Integer.parseInt( aIDString );

        final InfoForSingleMember aInfo = new InfoForSingleMember(aID);
        aReader.populateInfoForSingleMember( aInfo, aAttributes );

        final ADH_DataProvider aChef = new ADH_DataProvider();
        aChef.add( aInfo );

        final Halfyear aInvoicingPeriod = new Halfyear( 2014, EPart.SECOND );
        aChef.calculateDutyCharges( aInvoicingPeriod );
        final int aHoursToPayTotal = aInfo.getDutyCharge().getHoursToPayTotal();
        assertEquals("Merle muss 1,5h zahlen!", 150, aHoursToPayTotal);

        DetailsReporter.report( aChef, Paths.get( "." ) );

    }

    @Test
    public void test_LukasBal() throws Exception
    {
        final File aFile = new File("misc/TestResources/Tabellen/Mitglieder.csv");
        final BaseInfoReader aReader = new BaseInfoReader( aFile );

        final List<String>aColumnNames = FileUtils.readColumnNames( aFile );

        final String aLineForSingleMember = "10242;;01.01.1970;01.07.2005;31.12.2099;Lukas;Bal;SUSTAINING;01.01.2016;;13,5;Fördermitglied;Herrn;Wilhelmshavener Str. 28;24105;Kiel";

        final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aLineForSingleMember );
        final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
        final int aID = Integer.parseInt( aIDString );

        final InfoForSingleMember aInfo = new InfoForSingleMember(aID);
        aReader.populateInfoForSingleMember( aInfo, aAttributes );

        final ADH_DataProvider aChef = new ADH_DataProvider();
        aChef.add( aInfo );

        final Halfyear aInvoicingPeriod = new Halfyear( 2014, EPart.SECOND );
        aChef.calculateDutyCharges( aInvoicingPeriod );
        final DutyCharge aDutyCharge = aInfo.getDutyCharge();
        final int aHoursToPayTotal = aDutyCharge.getHoursToPayTotal();
        assertEquals("Lukas muss 0 Stunden zahlen!", 0, aHoursToPayTotal);
        final int aBalance = aDutyCharge.getBalance_ChargedAndAdjusted();
        assertEquals("... weil er noch 10,5 Stunden Guthaben hat!", 1050, aBalance);

        DetailsReporter.report( aChef, Paths.get( "." ) );

    }

    // ########################################################################
    // TESTS
    // ########################################################################

    private static ADH_DataProvider init()
    {
        final File aBaseInfoFile  = new File("misc/TestResources/Tabellen/Mitglieder.csv");
        final File aWorkEventFile = new File("misc/TestResources/Tabellen/Arbeitsdienste1.csv");
        final ADH_DataProvider aChef = new ADH_DataProvider();
        aChef.readBaseInfo( aBaseInfoFile );
        aChef.readWorkEvents( aWorkEventFile );
        return aChef;
    }

}

// ############################################################################
