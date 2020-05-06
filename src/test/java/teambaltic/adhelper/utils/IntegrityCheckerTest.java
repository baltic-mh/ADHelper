/**
 * IntegrityCheckerTest.java
 *
 * Created on 06.05.2020
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2020 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// ############################################################################
public class IntegrityCheckerTest {

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################

    @BeforeClass
    public static void initOnceBeforeStart() {
        TestUtils.initLog4J();
    }
    @AfterClass
    public static void shutdownWhenFinished() {
    }

    @Before
    public void initBeforeEachTest() {
    }

    @After
    public void cleanupAfterEachTest() {
    }

    // ########################################################################
    // TESTS
    // ########################################################################

    @Test
    public void test_checkBaseDataFile() {
        try {
            final File aBaseDataFile1 = new File("misc/TestResources/IntegrityChecker/BaisDaten-SPG4-1-NurWeber.csv");
            IntegrityChecker.checkBaseDataFile(aBaseDataFile1);
            final File aBaseDataFile2 = new File("misc/TestResources/IntegrityChecker/BaisDaten-SPG4-1-complete.csv");
            IntegrityChecker.checkBaseDataFile(aBaseDataFile2);
        } catch ( final Exception fEx ) {
            fail(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName(), fEx.getMessage() ) );

        }
    }

}

// ############################################################################
