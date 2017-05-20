/**
 * PersonalReportTest.java
 *
 * Created on 01.05.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.concatenatedReport;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.dynamicreports.jasper.builder.JasperConcatenatedReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.Exporters;
import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.controller.InitHelper;
import teambaltic.adhelper.controller.ReferenzTest;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings.EKey;
import teambaltic.adhelper.utils.TestUtils;

// ############################################################################
public class PersonalReportTest
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
    public void test()
    {
        // Mit diesem Wert kann man steuern, ob alle Daten eingelesen und
        // bearbeitet werden oder nur für ein einzelnes Mitglied.
        // Wenn aOnlyID == 0 ist, werden alle Daten eingelesen, sonst nur
        // das durch die ID angegebene Mitglied.
        final int aOnlyID = 0;//10174;
        try{
            DATAPROVIDER.init( ACTIVEPERIOD, aOnlyID );
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
            fail( "Mist: "+fEx.getMessage() );
        }

        final JasperConcatenatedReportBuilder aReportBuilder = concatenatedReport().continuousPageNumbering();

        final List<InfoForSingleMember> aAll = DATAPROVIDER.getAll();
        sm_Log.info( String.format( "Bearbeite %d Datensätze...", aAll.size()) );
        for( final InfoForSingleMember aInfoForSingleMember : aAll ){
            sm_Log.info( String.format( "Bearbeite Mitglied '%s'...", aInfoForSingleMember) );

            aReportBuilder.concatenate(
                    (new PersonalReport(ACTIVEPERIOD.getPeriod(), aInfoForSingleMember)).build())
            ;

        }

        final String aPDFFileName = "MyFirstReport.pdf";
        final File aPDFFile = new File( aPDFFileName );
        if( aPDFFile.exists() ){
            final boolean aDeleted = aPDFFile.delete();
            if( !aDeleted ){
                fail("Konnte Datei nicht löschen");
            }
        }
        sm_Log.info( "Erzeuge PDF-Report..." );
        try {
            final long aStartTime = System.currentTimeMillis();
            aReportBuilder.toPdf(Exporters.pdfExporter(aPDFFileName));
            final long aTimeLapsed = System.currentTimeMillis() - aStartTime;
            final String aFormatDurationWords = DurationFormatUtils.formatDurationWords( aTimeLapsed, true, true );
            sm_Log.info( String.format( "PDF-Erzeugung hat %s gedauert", aFormatDurationWords ) );
        } catch (final Throwable e) {
            fail(e.getMessage());
        }
    }

//    @Test
//    public void test()
//    {
//        final Halfyear aPeriod = new Halfyear( 2016, EPart.FIRST );
//        final InfoForSingleMember aInfo = createInfoForSingleMember( aPeriod );
//        final String aPDFFileName = "MyFirstReport.pdf";
//        final File aPDFFile = new File( aPDFFileName );
//        if( aPDFFile.exists() ){
//            aPDFFile.delete();
//        }
//        try {
//            concatenatedReport()
//            .continuousPageNumbering()
//            .concatenate(
//                      (new PersonalReport(aPeriod, aInfo)).build(),
//                      (new PersonalReport(aPeriod, aInfo)).build())
//            .toPdf(Exporters.pdfExporter(aPDFFileName));
//        } catch (final DRException e) {
//            e.printStackTrace();
//        }
//    }

    // ########################################################################
    // HILFSMETHODEN
    // ########################################################################
//    private static InfoForSingleMember createInfoForSingleMember(final IPeriod fPeriod)
//    {
//        final InfoForSingleMember aInfo = new InfoForSingleMember( ID );
//        final ClubMember aMember = new ClubMember(ID);
//        aMember.setName( "Mathias Weber" );
//        aInfo.setMember( aMember );
//        final FreeFromDutySet aFreeFromDutySet = new FreeFromDutySet( ID );
//        final FreeFromDuty aFFD = new FreeFromDuty( ID, REASON.MANAGEMENT );
//        aFFD.setFrom( LocalDate.of( 2016, 4, 1 ) );
//        aFFD.setUntil( LocalDate.of( 2016, 5, 1 ) );
//        aFreeFromDutySet.addItem( aFFD );
//        aInfo.setFreeFromDutySet( aFreeFromDutySet );
//
//        final WorkEventsAttended aWEA = new WorkEventsAttended( ID );
//        aInfo.setWorkEventsAttended( aWEA );
//        final WorkEvent aWE1 = new WorkEvent( ID );
//        aWE1.setDate( LocalDate.of( 2016, 4, 9 ) );
//        aWE1.setHours( 250 );
//        aWEA.add( aWE1 );
//        final WorkEvent aWE2 = new WorkEvent( ID );
//        aWE2.setDate( LocalDate.of( 2016, 5, 17 ) );
//        aWE2.setHours( 600 );
//        aWEA.add( aWE2 );
//
//        final BalanceHistory aBalanceHistory = new BalanceHistory( ID );
//        aInfo.setBalanceHistory( aBalanceHistory );
//        final Balance aBalance = new Balance( ID, fPeriod, 7600 );
//        aBalance.setValue_Charged( 7500 );
//        aBalance.setValue_ChargedAndAdjusted( 6050 );
//        aBalanceHistory.addBalance( aBalance );
//        IPeriod aPredeccessor = fPeriod.createPredeccessor();
//        Balance aBalance2 = new Balance( ID, aPredeccessor, 12000 );
//        aBalanceHistory.addBalance( aBalance2 );
//        aPredeccessor = aPredeccessor.createPredeccessor();
//        aBalance2 = new Balance( ID, aPredeccessor, 20000 );
//        aBalanceHistory.addBalance( aBalance2 );
//
//        final DutyCharge aDutyCharge = new DutyCharge( ID );
//        aDutyCharge.setHoursWorked( 100 );
//        aDutyCharge.setHoursToPay ( 0 );
//        aInfo.setDutyCharge( aDutyCharge );
//
//        final CreditHoursGranted aCreditHoursGranted = new CreditHoursGranted( ID );
//        final CreditHours aCreditHours = new CreditHours( ID );
//        aCreditHours.setDate( LocalDate.of( 2016, 1, 1 ) );
//        aCreditHours.setHours( 300 );
//        aCreditHours.setComment( "War'n Versähen! Es tut mir so unsäglich leid und es soll auch nicht wieder vorkommen!" );
//        aCreditHoursGranted.add( aCreditHours );
//        aInfo.setCreditHoursGranted( aCreditHoursGranted );
//        return aInfo;
//    }

}

// ############################################################################
