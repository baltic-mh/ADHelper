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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import teambaltic.adhelper.controller.IPeriodDataController.EPeriodDataSelector;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.settings.AppSettings;
import teambaltic.adhelper.model.settings.UserSettings;

// ############################################################################
public class PeriodDataControllerTest
{

    @Test
    public void test_getPeriods()
    {
        try{
            final PeriodDataController aPDC = new PeriodDataController( new MyAppSettings(), new MyUserSettings() );
            aPDC.init();
            final List<PeriodData> aPeriods = aPDC.getPeriodDataList(EPeriodDataSelector.ALL);
            assertEquals("Periods.size()", 3, aPeriods.size());
        }catch( final Exception fEx ){
            fEx.printStackTrace();
            fail("Exception: "+fEx.getMessage() );
        }
    }

    @Test
    public void test_getActivePeriod()
    {
        try{
            final PeriodDataController aPDC = new PeriodDataController( new MyAppSettings(), new MyUserSettings() );
            aPDC.init();
            final PeriodData aPeriod = aPDC.getActivePeriod();
            assertNotNull( "ActivePeriod", aPeriod);
            assertEquals("ActivePeriod", "2015-01-01 - 2015-06-30*", aPeriod.toString());
        }catch( final Exception fEx ){
            fEx.printStackTrace();
            fail("Exception: "+fEx.getMessage() );
        }
    }

    private static class MyAppSettings extends AppSettings
    {

        public MyAppSettings() throws Exception
        {
            super("misc/TestResources/PeriodDataControllerTest");
        }

    }
    private static class MyUserSettings extends UserSettings
    {

        public MyUserSettings() throws Exception
        {
            super( Paths.get("kannweg.prop") );
        }

        @Override
        public String getDecoratedEMail()
        {
            return "TESTUSER@TESTSITE";
        }

    }
}

// ############################################################################
