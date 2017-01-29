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
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.inout.BaseDataReader;
import teambaltic.adhelper.inout.DetailsReporter;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.ClubSettings;
import teambaltic.adhelper.model.settings.IAppSettings.EKey;
import teambaltic.adhelper.model.settings.IClubSettings;
import teambaltic.adhelper.utils.FileUtils;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class ADH_DataProviderTest
{
    private static final Logger sm_Log = Logger.getLogger(ADH_DataProviderTest.class);

    private static final List<String>COLUMNNAMES = Arrays.asList( new String[]{
            "Mitglieds_Nr", "Verknüpfung", "Geburtsdatum", "Eintritt", "Austritt",
            "Vorname", "Nachname", "AD-Frei.Grund", "AD-Frei.von", "AD-Frei.bis",
            "Guthaben Arbeitsstunden nach Q2 2014", "Beitragsart_1",
            "Anrede", "Straße", "Plz", "Ort"} );

    private static final Path PATH_CLUBSETTINGS  = Paths.get("misc/TestResources/VereinsDaten.properties");

    private static final Halfyear INVOICINGPERIOD = new Halfyear( 2014, EPart.SECOND );

    private static IClubSettings CLUBSETTINGS;

    private static FreeFromDutyCalculator sm_FFDCalculator;

    private static ADH_DataProvider CHEF;


    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        System.setProperty( EKey.FOLDERNAME_ROOT.name(), "misc/TestResources");
        Log4J.initLog4J();
        try{
            AllSettings.INSTANCE.init();
            CLUBSETTINGS = new ClubSettings( PATH_CLUBSETTINGS );
            sm_FFDCalculator = new FreeFromDutyCalculator( CLUBSETTINGS );
        }catch( final Exception fEx ){
            fail("Exception: "+ fEx.getMessage() );
        }
        CHEF = init();
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
        try{
            readData( CHEF );
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
            fail(fEx.getMessage());
        }

        for( final InfoForSingleMember aInfo : CHEF.getAll() ){
            final IClubMember aMember = aInfo.getMember();
            sm_Log.info("Mitglied: "+aMember);
            final Collection<FreeFromDuty> aFreeFromDutyItems = aInfo.getFreeFromDutyItems();
            if( aFreeFromDutyItems.size() > 0 ){
                final StringBuffer aSB = new StringBuffer( "\tAD-Befreiung: " );
                for( final FreeFromDuty aFreeFromDuty : aFreeFromDutyItems ){
                    aSB.append( "  | "+aFreeFromDuty );
                }
                sm_Log.info( aSB.toString() );
            }
            final WorkEventsAttended aWorkEventsAttended = aInfo.getWorkEventsAttended();
            if( aWorkEventsAttended != null ){
                final StringBuffer aSB = new StringBuffer( "\tAD-Teilnahmen: " );
                for( final WorkEvent aWE : aWorkEventsAttended.getWorkEvents() ){
                    aSB.append( String.format( " | %s: %5.2f", aWE.getDate(), aWE.getHours()/100.0f ) );
                }
                sm_Log.info( aSB.toString() );
            }
        }
    }

    @Test
    public void test_Calculate()
    {
        CHEF.calculateDutyCharges( INVOICINGPERIOD );
        CHEF.joinRelatives();
        CHEF.balanceRelatives( INVOICINGPERIOD );
        DetailsReporter.report( CHEF, Paths.get( "." ) );

    }

    @Test
    public void test_MerleWeber() throws Exception
    {
        final String aLineForSingleMember = "10412;10174;01.01.1970;01.04.2014;31.12.2099;Merle;Weber;;;;;ERW o. Boot, o. Spind;Frau;Alter Kieler Weg 2;24161;Altenholz";

        final InfoForSingleMember aMemberInfo = testSingleMember( aLineForSingleMember, 0 );
        final int aHoursToPayTotal = aMemberInfo.getDutyCharge().getHoursToPayTotal();

        assertEquals("Merle muss 1,5h zahlen!", 150, aHoursToPayTotal);

    }

    @Test
    public void test_LukasBal() throws Exception
    {
        final String aLineForSingleMember = "10242;;01.01.1970;01.07.2005;31.12.2099;Lukas;Bal;SUSTAINING;01.01.2016;;13,5;Fördermitglied;Herrn;Wilhelmshavener Str. 28;24105;Kiel";

        final InfoForSingleMember aMemberInfo = testSingleMember( aLineForSingleMember, 1350 );
        final int aHoursToPayTotal = aMemberInfo.getDutyCharge().getHoursToPayTotal();

        assertEquals("Lukas muss 0 Stunden zahlen!", 0, aHoursToPayTotal);
        final int aBalance = aMemberInfo.getBalance( INVOICINGPERIOD ).getValue_ChargedAndAdjusted();
        assertEquals("... weil er noch 10,5 Stunden Guthaben hat!", 1050, aBalance);

    }

    // ########################################################################
    // PRIVATE PROPERTY
    // ########################################################################

    private static ADH_DataProvider init()
    {
        try{
            final ADH_DataProvider aChef = new ADH_DataProvider(null, AllSettings.INSTANCE);
            final Path aPeriodFolder = Paths.get( "misc/TestResources/PeriodDataControllerTest/Daten", INVOICINGPERIOD.toString() );
            final PeriodData aPeriodData = new PeriodData( aPeriodFolder );
            aChef.setPeriodData( aPeriodData );
            return aChef;
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
            fail( fEx.getMessage() );
        }
        return null;
    }

    private static void readData( final ADH_DataProvider fChef ) throws Exception
    {
        final Path aBaseInfoFile  = Paths.get("misc/TestResources/Tabellen/BasisDaten.csv");
        final Path aWorkEventFile = Paths.get("misc/TestResources/Tabellen/Arbeitsdienste1.csv");
        try{
            fChef.readBaseData( aBaseInfoFile, 0 );
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
            fail( fEx.getMessage() );
        }
        fChef.readWorkEvents( aWorkEventFile );
    }

    private static InfoForSingleMember testSingleMember( final String fLineForSingleMember, final int fGuthaben ) throws Exception
    {
        CHEF.clear();
        final File aFile = new File("Dummy");
        final BaseDataReader aReader = new BaseDataReader( aFile );

        final Map<String, String> aAttributes = FileUtils.makeMap( COLUMNNAMES, fLineForSingleMember );
        final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
        final int aID = Integer.parseInt( aIDString );

        final InfoForSingleMember aMemberInfo = new InfoForSingleMember(aID);
        aReader.populateInfoForSingleMember( aMemberInfo, aAttributes );
        final Balance aBalance = new Balance( aID, INVOICINGPERIOD, fGuthaben );
        aMemberInfo.addBalance( aBalance );

        CHEF.add( aMemberInfo );

        final FreeFromDutySet aFFDSet = aMemberInfo.getFreeFromDutySet();
        sm_FFDCalculator.populateFFDSetFromMemberData(
                aFFDSet, INVOICINGPERIOD, aMemberInfo.getMember() );

        CHEF.calculateDutyCharges( INVOICINGPERIOD );
        DetailsReporter.report( CHEF, Paths.get( "." ) );
        return aMemberInfo;
    }

}

// ############################################################################
