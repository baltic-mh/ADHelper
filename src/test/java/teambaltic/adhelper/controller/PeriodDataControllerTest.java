/**
 * PeriodDataControllerTest.java
 *
 * Created on 07.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import teambaltic.adhelper.model.PeriodData;

// ############################################################################
public class PeriodDataControllerTest
{

    @Test
    public void test_getPeriods()
    {
        final Path aRootFolder = Paths.get( "misc/TestResources/PeriodDataControllerTest" );
        final PeriodDataController aPDC = new PeriodDataController( aRootFolder, "Abgeschlossen.txt", "Hochgeladen.txt" );
        aPDC.init( true );
        final List<PeriodData> aPeriods = aPDC.getPeriodDataList();
        assertEquals("Periods.size()", 3, aPeriods.size());
    }

    @Test
    public void test_getNewestPeriod()
    {
        final Path aRootFolder = Paths.get( "misc/TestResources/PeriodDataControllerTest" );
        final PeriodDataController aPDC = new PeriodDataController( aRootFolder, "Abgeschlossen.txt", "Hochgeladen.txt" );
        aPDC.init( true );
        final PeriodData aPeriod = aPDC.getNewestPeriodData();
        assertEquals("NewestPeriods.size()", "2015-01-01 - 2015-06-30", aPeriod.toString());
    }

}

// ############################################################################
