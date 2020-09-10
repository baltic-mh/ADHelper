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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

// ############################################################################
public class ZipUtilsTest
{
    private static final Logger sm_Log = Logger.getLogger(ZipUtilsTest.class);
    private final static Path SOURCE = Paths.get("misc/TestResources/ReferenzDaten/Daten/2019-07-01 - 2019-12-31");

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################

    @Rule
    public TemporaryFolder TEMP = new TemporaryFolder();

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
    public void test() throws IOException
    {
        final File aTestFolder= TEMP.newFolder("ZipUtilsTest");
        final Path aDatenFolder = aTestFolder.toPath().resolve("Daten");
        org.apache.commons.io.FileUtils.copyDirectory(SOURCE.toFile(), aDatenFolder.toFile() );
        final List<String> aList_Orig = Arrays.asList( aDatenFolder.toFile().list() );
        try{
            final Path aZipped = ZipUtils.zip( aDatenFolder );
            org.apache.commons.io.FileUtils.deleteQuietly( aDatenFolder.toFile() );
            ZipUtils.unzip( aZipped );
            final List<String> aList_AfterZip = Arrays.asList( aDatenFolder.toFile().list() );
            assertEquals( aList_Orig.size(), aList_AfterZip.size());
            for ( final String aString : aList_AfterZip ) {
                assertTrue( aList_Orig.contains(aString));
            }
        }catch( final Exception fEx ){
            sm_Log.error("Exception: ", fEx );
            fail("Exception: "+ fEx.getMessage() );
        }
    }

}

// ############################################################################
