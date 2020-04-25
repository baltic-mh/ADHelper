/**
 * CompareUtilsTest.java
 *
 * Created on 25.04.2020
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2020 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// ############################################################################
public class CompareUtilsTest {

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
    public void test() {
        final List<String> aPeriodFolderList = new ArrayList<>();
        aPeriodFolderList.add("5\\Daten\\2018-01-01 - 2018-06-30.zip.cry");
        aPeriodFolderList.add("3\\Daten\\2019-01-01 - 2019-06-30.zip.cry");
        aPeriodFolderList.add("1\\Daten\\2020-01-01 - 2020-06-30.zip.cry");
        aPeriodFolderList.add("2\\Daten\\2019-07-01 - 2019-12-31.zip.cry");
        aPeriodFolderList.add("4\\Daten\\2018-07-01 - 2018-12-31.zip.cry");
        aPeriodFolderList.add("0\\Daten\\2021-07-01 - 2021-12-31.zip.cry");
        CompareUtils.sortPeriodFolderList(aPeriodFolderList);
        int aIDX = 0;
        for ( final String aPeriodFolder : aPeriodFolderList ) {
            assertTrue(aPeriodFolder.startsWith(String.valueOf(aIDX++)));
        }
    }

}
// ############################################################################
