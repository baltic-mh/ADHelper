/**
 * TransferControllerTest.java
 *
 * Created on 13.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings.EKey;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class TransferControllerTest
{

    private static InitHelper sm_InitHelper;
    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
        System.setProperty( EKey.FOLDERNAME_ROOT.name(), "misc/TestResources/TransferController/Upload" );

        try{
            AllSettings.INSTANCE.init();
            sm_InitHelper = new InitHelper( AllSettings.INSTANCE );
        }catch( final Exception fEx ){
            fEx.printStackTrace();
            fail( fEx.getMessage() );
        }
//        // Zur Sicherheit machen wir die Bahn frei:
//        try{
//            Files.deleteIfExists( sm_LocalPath );
//        }catch( final IOException fEx ){
//            fail( "Tut mir leid: "+fEx.getMessage());
//        }

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


//    @Test
    public void test_Upload()
    {
        ITransferController aTC;
        try{
            aTC = sm_InitHelper.initTransferController();
            assertNotNull("TC.init", aTC);
            final Path aLocalFile = Paths.get( "Daten", "BasisDaten.csv" );
            aTC.upload( aLocalFile );
        }catch( final Exception fEx ){
            fail("Exception: "+fEx.getMessage() );
        }

    }

//    @Test
    public void test_Download()
    {
        ITransferController aTC;
        try{
            aTC = sm_InitHelper.initTransferController();
            assertNotNull("TC.init", aTC);

            final Path aLocalFile = Paths.get( "Daten", "BasisDaten.csv" );
            aTC.download( aLocalFile );
        }catch( final Exception fEx ){
            fail("Exception: "+fEx.getMessage() );
        }
    }

}

// ############################################################################
