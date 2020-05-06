/**
 * ColumnNamesMappingTest.java
 *
 * Created on 06.05.2020
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2020 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model.settings;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class ColumnNamesMappingTest {

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart() {
        Log4J.initLog4J("log4j-test.properties");
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
    public void test() {
        final List<String> aInStrings = new ArrayList<>();
        aInStrings.add("Mitglieds_Nr");
        aInStrings.add("MitgliedID");
        aInStrings.add("Eintritt_Datum");
        aInStrings.add("Eintritt");
        aInStrings.add("Austritt_Datum");
        aInStrings.add("Austritt");
        aInStrings.add("Zusatzfeld_02");
        aInStrings.add("AD-Frei.Grund");
        aInStrings.add("Zusatzfeld_03");
        aInStrings.add("AD-Frei.von");
        aInStrings.add("Zusatzfeld_04");
        aInStrings.add("AD-Frei.bis");
        aInStrings.add("Geburtsdatum");
        aInStrings.add("MURKS");

        final List<String> aOutStrings = ColumnNamesMapping.INSTANCE.map(aInStrings);
        assertEquals("Mitglieds_Nr", aOutStrings.get(0));
        assertEquals("Mitglieds_Nr", aOutStrings.get(1));
        assertEquals("Eintritt", aOutStrings.get(2));
        assertEquals("Eintritt", aOutStrings.get(3));
        assertEquals("Austritt", aOutStrings.get(4));
        assertEquals("Austritt", aOutStrings.get(5));
        assertEquals("AD-Frei.Grund", aOutStrings.get(6));
        assertEquals("AD-Frei.Grund", aOutStrings.get(7));
        assertEquals("AD-Frei.von", aOutStrings.get(8));
        assertEquals("AD-Frei.von", aOutStrings.get(9));
        assertEquals("AD-Frei.bis", aOutStrings.get(10));
        assertEquals("AD-Frei.bis", aOutStrings.get(11));
        assertEquals("Geburtsdatum", aOutStrings.get(12));
        assertEquals("MURKS", aOutStrings.get(13));
    }

}

// ############################################################################
