/**
 * ZipUtilsTest.java
 *
 * Created on 29.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// ############################################################################
public class ZipUtilsTest
{
    private static final Logger sm_Log = Logger.getLogger(ZipUtilsTest.class);

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################

    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
    }
    @AfterClass
    public static void shutdownWhenFinished()
    {
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
            /*final Path aZipped = */ZipUtils.zip( Paths.get( "Daten/2014-07-01 - 2014-12-31" ) );
//            final Path aZipped = ZipUtils.zip( Paths.get( "Daten" ) );
            ZipUtils.unzip( Paths.get( "Daten/2014-07-01 - 2014-12-31.zip" ) );
        }catch( final Exception fEx ){
            sm_Log.error("Exception: ", fEx );
            fail("Exception: "+ fEx.getMessage() );
        }
    }

}

// ############################################################################
