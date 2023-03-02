/**
 * FileUtilsTest.java
 *
 * Created on 16.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

// ############################################################################
public class FileUtilsTest
{
	@Rule
    public final TemporaryFolder TEMP = new TemporaryFolder();

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################

    @BeforeClass
    public static void initOnceBeforeStart()
    {
        TestUtils.initLog4J();
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
    public void test_cleanupFolder() throws IOException
    {
        final Path aFolderToCleanup = TEMP.newFolder("FolderToCleanup").toPath();
        for (int aIDX = 0; aIDX < 10; aIDX++) {
            aFolderToCleanup.resolve("File"+aIDX).toFile().createNewFile();
        }
        FileUtils.cleanupFolder(aFolderToCleanup, 2);
        final String[] aRetainedFiles = aFolderToCleanup.toFile().list();
        assertEquals( 2, aRetainedFiles.length );
        assertEquals( "File8", aRetainedFiles[0]);
        assertEquals( "File9", aRetainedFiles[1]);
    }

    @Test
    public void test_makeMap()
    {
        final List<String> aActualColumnNames   = new ArrayList<>();
        aActualColumnNames.add("C1");
        aActualColumnNames.add("C2");
        aActualColumnNames.add("C3");
        aActualColumnNames.add("C4");
        final String aSingleLine = "v1;v2;v3;v4";
        final Map<String, String> aMap = FileUtils.makeMap( aActualColumnNames, aSingleLine);
        assertEquals(4, aMap.size() );
    }
}

// ############################################################################
