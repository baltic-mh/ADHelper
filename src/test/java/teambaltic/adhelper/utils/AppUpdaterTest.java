/**
 * AppUpdaterTest.java
 *
 * Created on 14.11.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// ############################################################################
public class AppUpdaterTest
{
    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        System.setProperty( "log4jfilename", "JUnit.log" );
        Log4J.initLog4J();
    }

    @Before
    public void initBeforeEachTest()
    {
    }

    @After
    public void cleanupAfterEachTest()
    {
    }

    @AfterClass
    public static void cleanupAfterShutdown()
    {
        Log4J.reset();
    }

    // ########################################################################
    // TESTS
    // ########################################################################

    @Test
    public void test()
    {
        final String aMultiLineString = "# Diese Datei beinhaltet die Adresse, an der die freigegebenen Releases\r\n"
+"# zum Download bereitstehen. \r\n"
+"# Alle Zeilen in dieser Datei, die mit # beginnen, werden ignoriert.\r\n"
+"# Die Adresse dieser Datei selbst ist:\r\n"
+"# https://raw.githubusercontent.com/baltic-mh/ADHelper/master/Update.url\r\n"
+"# Die folgende Adresse wird vom Programm ADHelper ausgelesen und für die\r\n"
+"# automatische Aktualisierung benutzt:\r\n"
+"  http://syniphos.i234.me/~kvk/Download/ADHelper/releases.xml  ";
        final List<String> aNonCommentLines = AppUpdater.getNonCommentLines( aMultiLineString );
        assertEquals("Anzahl Zeilen", 1, aNonCommentLines.size());
        assertEquals("Zeile", "http://syniphos.i234.me/~kvk/Download/ADHelper/releases.xml", aNonCommentLines.get(0) );
    }

}

// ############################################################################
