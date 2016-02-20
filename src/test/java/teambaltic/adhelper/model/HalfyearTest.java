/**
 * HalfyearTest.java
 *
 * Created on 31.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class HalfyearTest
{
    private static final Logger sm_Log = Logger.getLogger(HalfyearTest.class);

    private static final LocalDate sm_RefDate1 = LocalDate.of(2016, Month.JANUARY, 1);
    private static final LocalDate sm_RefDate2 = LocalDate.of(2016, Month.JUNE, 30);
    private static final LocalDate sm_RefDate1_PreviousDay = sm_RefDate1.minusDays( 1 );
    private static final LocalDate sm_RefDate2_NextDay = sm_RefDate2.plusDays( 1 );

    private static final Halfyear HY = new Halfyear( 2016, EPart.FIRST );

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
        sm_Log.info( "HalfYear: "+HY );
        sm_Log.info("RefDate1: "+sm_RefDate1);
        sm_Log.info("RefDate2: "+sm_RefDate2);
        sm_Log.info( "Previous day: "+sm_RefDate1_PreviousDay );
        sm_Log.info( "Next day: "+sm_RefDate2_NextDay );
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
    public void testIsBeforeMyEnd()
    {

        final boolean aBeforeMyEnd1 = HY.isBeforeEnd( sm_RefDate1 );
        assertTrue("BeforeMyEnd1", aBeforeMyEnd1 );
        final boolean aBeforeMyEnd2 = HY.isBeforeEnd( sm_RefDate2 );
        assertTrue("BeforeMyEnd2", aBeforeMyEnd2 );
        final boolean aBeforeMyEnd3 = HY.isBeforeEnd( sm_RefDate1_PreviousDay );
        assertTrue("BeforeMyEnd3", aBeforeMyEnd3 );
        final boolean aBeforeMyEnd4 = HY.isBeforeEnd( sm_RefDate2_NextDay );
        assertFalse("BeforeMyEnd4", aBeforeMyEnd4 );

    }

    @Test
    public void testIsAfterMyStart()
    {
        final boolean aAfterMyStart1 = HY.isAfterStart( sm_RefDate1 );
        assertTrue("AfterMyStart1", aAfterMyStart1 );
        final boolean aAfterMyStart2 = HY.isAfterStart( sm_RefDate2 );
        assertTrue("AfterMyStart2", aAfterMyStart2 );
        final boolean aAfterMyStart3 = HY.isAfterStart( sm_RefDate1_PreviousDay );
        assertFalse("AfterMyStart3", aAfterMyStart3 );
        final boolean aAfterMyStart4 = HY.isAfterStart( sm_RefDate2_NextDay );
        assertTrue("AfterMyStart4", aAfterMyStart4 );
    }

    @Test
    public void testIsWithinMyBounds()
    {
        final boolean aWithinMyBounds1 = HY.isWithinPeriod( sm_RefDate1 );
        assertTrue("WithinMyBounds1", aWithinMyBounds1 );
        final boolean aWithinMyBounds2 = HY.isWithinPeriod( sm_RefDate2 );
        assertTrue("WithinMyBounds2", aWithinMyBounds2 );
        final boolean aWithinMyBounds3 = HY.isWithinPeriod( sm_RefDate1_PreviousDay );
        assertFalse("WithinMyBounds3", aWithinMyBounds3 );
        final boolean aWithinMyBounds4 = HY.isWithinPeriod( sm_RefDate2_NextDay );
        assertFalse("WithinMyBounds4", aWithinMyBounds4 );
    }
}

// ############################################################################
