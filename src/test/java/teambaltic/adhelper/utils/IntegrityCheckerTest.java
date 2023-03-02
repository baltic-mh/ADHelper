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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.utils.DifferingLine.EDiffType;

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
    public void test_makeCSVLine() {
        final List<String> aColumnNames   = new ArrayList<>();
        aColumnNames.add("C1");
        aColumnNames.add("C3");
        final Map<String, String> aAttributes = new HashMap<>();
        aAttributes.put("C1", "V1");
        aAttributes.put("C2", "V2");
        aAttributes.put("C3", "V3");
        aAttributes.put("C4", "V4");

        final String aCSVLine = IntegrityChecker.makeCSVLine(aColumnNames, aAttributes);
        assertEquals("V1;V3", aCSVLine);
    }

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

    @Test
    public void test_compareWithObsoleteColumns() {
        try {
            final File aBaseDataFile1 = new File("misc/TestResources/IntegrityChecker/BaisDaten-SPG4-1-NurWeber.csv");
            final File aBaseDataFile2 = new File("misc/TestResources/IntegrityChecker/BaisDaten-SPG4-1-NurWeber-MitUeberfluessigerSpalte.csv");
            final FileComparisonResult aResult = IntegrityChecker.compare(aBaseDataFile1, aBaseDataFile2);
            final List<DifferingLine> aDifferingLines = aResult.getDifferingLines();
            assertEquals( 0, aDifferingLines.size() );
        } catch ( final Exception fEx ) {
            fail(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName(), fEx.getMessage() ) );

        }
    }

    @Test
    public void test_compare() {
        try {
            final File aBaseDataFile1 = new File("misc/TestResources/IntegrityChecker/BaisDaten-SPG4-1-complete.csv");
            final File aBaseDataFile2 = new File("misc/TestResources/IntegrityChecker/BaisDaten-SPG4-1-complete-mitModifikationen.csv");
            final FileComparisonResult aResult = IntegrityChecker.compare(aBaseDataFile1, aBaseDataFile2);
            final List<DifferingLine> aDifferingLines = aResult.getDifferingLines();
            assertEquals( 6, aDifferingLines.size() );
            assertEquals( EDiffType.DELETED, aDifferingLines.get(0).getType() );
            assertEquals( EDiffType.DELETED, aDifferingLines.get(1).getType() );
            assertEquals( EDiffType.MODIFIED, aDifferingLines.get(2).getType() );
            assertEquals( EDiffType.MODIFIED, aDifferingLines.get(3).getType() );
            assertEquals( EDiffType.ADDED, aDifferingLines.get(4).getType() );
            assertEquals( EDiffType.ADDED, aDifferingLines.get(5).getType() );
        } catch ( final Exception fEx ) {
            fEx.printStackTrace( System.err );
            fail(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName(), fEx.getMessage() ) );


        }
    }
}

// ############################################################################
