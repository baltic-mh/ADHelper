/**
 * ReferenzTest.java
 *
 * Created on 23.01.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings.EKey;
import teambaltic.adhelper.utils.FileUtils;
import teambaltic.adhelper.utils.TestUtils;

// ############################################################################
public class ReferenzTest
{
    private static final Logger sm_Log = Logger.getLogger(ReferenzTest.class);
    private static final Integer ZERO = Integer.valueOf( 0 );

    private static final String FOLDERNAME_ROOT = "misc/TestResources/ReferenzDaten";

    private static ADH_DataProvider DATAPROVIDER;
    private static PeriodData ACTIVEPERIOD;

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        System.setProperty( EKey.FOLDERNAME_ROOT.name(), FOLDERNAME_ROOT);
        System.setProperty( "log4j.debug", "false" );
        TestUtils.initLog4J();
        try{
            AllSettings.INSTANCE.init();
            final InitHelper aInitHelper = new InitHelper(AllSettings.INSTANCE);
            final IPeriodDataController aPDC = aInitHelper.initPeriodDataController();
            ACTIVEPERIOD = aPDC.getActivePeriod();
            DATAPROVIDER = new ADH_DataProvider(aPDC, AllSettings.INSTANCE);
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
    public void test() throws Exception
    {
        // Mit diesem Wert kann man steuern, ob alle Daten eingelesen und
        // bearbeitet werden oder nur für ein einzelnes Mitglied.
        // Wenn aOnlyID == 0 ist, werden alle Daten eingelesen, sonst nur
        // das durch die ID angegebene Mitglied.
        final int aOnlyID = 0;//10131;
        DATAPROVIDER.init( ACTIVEPERIOD, aOnlyID );

//        Writer.writeToFile_BalanceHistories( DATAPROVIDER, ACTIVEPERIOD.getFolder() );

        final Map<Integer, Integer> aBalances_Ref = readBalances( ACTIVEPERIOD.getFolder() );
        final Map<Integer, Integer> aCharges_Ref  = readCharges ( ACTIVEPERIOD.getFolder() );

        final List<InfoForSingleMember> aAll = DATAPROVIDER.getAll();
        sm_Log.info( String.format( "Überprüfe %d Datensätze...", aAll.size()) );
        for( final InfoForSingleMember aInfoForSingleMember : aAll ){
            sm_Log.info( String.format( "Überprüfe Mitglied '%s'...", aInfoForSingleMember) );
            compareCharges ( aInfoForSingleMember, aCharges_Ref );
            compareBalances( aInfoForSingleMember, aBalances_Ref, ACTIVEPERIOD.getPeriod() );
            // Da die Guthaben-III-Werte (belastet und ausgeglichen) verglichen werden
            // müssen auch die Guthaben der Folgeperiode passen:
            compareBalances( aInfoForSingleMember, aBalances_Ref, ACTIVEPERIOD.getPeriod().createSuccessor() );

        }

    }

    // ########################################################################
    // PRIVATE PROPERTY
    // ########################################################################

    private static Map<Integer, Integer> readBalances( final Path fFolder )
    {
        final Map<Integer, Integer> aGuthaben = new HashMap<>();

        final Path aFile = fFolder.resolve( "Guthaben.csv" );
        final List<String> aAllLines = FileUtils.readAllLines( aFile, 1 );
        for( final String aThisLine : aAllLines ){
            final String[] aParts = aThisLine.split( ";" );
            final Integer aMemberID = Integer.valueOf( aParts[0] );
            final Float aFloatValue = Float.valueOf( aParts[2].replace( ',', '.' ) );
            final Integer aIntValue = Integer.valueOf( (int) ( aFloatValue.floatValue()* 100.0f ) );
            aGuthaben.put( aMemberID, aIntValue );
        }
        return aGuthaben;
    }

    private static Map<Integer, Integer> readCharges( final Path fFolder )
    {
        final Map<Integer, Integer> aCharges = new HashMap<>();

        final Path aFile = fFolder.resolve( "ZuZahlendeStunden.csv" );
        final List<String> aAllLines = FileUtils.readAllLines( aFile, 1 );
        for( final String aThisLine : aAllLines ){
            final String[] aParts = aThisLine.split( ";" );
            final Float aFloatValue = Float.valueOf( aParts[2].replace( ',', '.' ) );
            final Integer aIntValue = Integer.valueOf( (int) ( aFloatValue.floatValue()* 100.0f ) );
            aCharges.put( Integer.valueOf( aParts[0] ), aIntValue );
        }
        return aCharges;
    }

    private static void compareBalances(
            final InfoForSingleMember fInfoForSingleMember,
            final Map<Integer, Integer> fBalances_Ref, final IPeriod fPeriod)
    {
        final int aID = fInfoForSingleMember.getID();
        final Balance aBalance = fInfoForSingleMember.getBalance( fPeriod );
        final Integer aBalance_Ref = fBalances_Ref.get( aID );
        if( aBalance == null && aBalance_Ref == null ){
            return;
        }
        if( aBalance_Ref == null){
            assertEquals(fInfoForSingleMember.toString()+": Balance", ZERO, Integer.valueOf(aBalance.getValue_ChargedAndAdjusted()));
        } else if ( aBalance == null) {
            assertEquals(fInfoForSingleMember.toString()+": Balance", aBalance_Ref, Integer.valueOf(0));
        } else {
            assertEquals(fInfoForSingleMember.toString()+": Balance", aBalance_Ref, Integer.valueOf(aBalance.getValue_ChargedAndAdjusted()));
        }
    }

    private static void compareCharges(
            final InfoForSingleMember fInfoForSingleMember,
            final Map<Integer, Integer> fCharges_Ref)
    {
        final int aID = fInfoForSingleMember.getID();
        final IClubMember aMember = fInfoForSingleMember.getMember();
        final int aLinkID = aMember.getLinkID();
        final int aCharge = aLinkID != 0 ? 0 : fInfoForSingleMember.getDutyCharge().getHoursToPayTotal();
        final Integer aCharge_Ref = fCharges_Ref.get( aID );
        if( aCharge_Ref == null){
            assertEquals(fInfoForSingleMember.toString()+": Charge", ZERO, Integer.valueOf( aCharge ));
        } else {
            assertEquals(fInfoForSingleMember.toString()+": Charge", aCharge_Ref, Integer.valueOf(aCharge));
        }
    }

}

// ############################################################################
