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

import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.inout.Writer;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings.EKey;
import teambaltic.adhelper.utils.TestUtils;

// ############################################################################
public class BalanceHistoryTest
{
    private static final Logger sm_Log = Logger.getLogger(BalanceHistoryTest.class);

    private static final String FOLDERNAME_ROOT = "misc/TestResources/TestDaten1";

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
        try{
            DATAPROVIDER.init( ACTIVEPERIOD, 0 );
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
            fail( "Mist: "+fEx.getMessage() );
        }

        Writer.writeToFile_BalanceHistories( DATAPROVIDER, Paths.get( FOLDERNAME_ROOT ) );
    }

    // ########################################################################
    // PRIVATE PROPERTY
    // ########################################################################

}

// ############################################################################
