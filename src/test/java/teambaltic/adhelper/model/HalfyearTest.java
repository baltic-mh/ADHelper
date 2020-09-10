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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
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
    public void testBasic()
    {
        assertEquals( 2016, HY.getYear() );
        assertEquals( EPart.FIRST, HY.getPart() );
        assertEquals( sm_RefDate1, HY.getStart() );
        assertEquals( sm_RefDate2, HY.getEnd() );
    }

    @Test
    public void testConstructor_LocalDate()
    {
        final Halfyear aHY = new Halfyear( LocalDate.of(2016, Month.FEBRUARY, 29) );
        assertEquals( sm_RefDate1, aHY.getStart() );
        assertEquals( sm_RefDate2, aHY.getEnd() );
    }

    @Test
    public void testConstructor_Year_Part()
    {
        final Halfyear aHY = new Halfyear( Year.of(2016), EPart.FIRST );
        assertEquals( sm_RefDate1, aHY.getStart() );
        assertEquals( sm_RefDate2, aHY.getEnd() );
    }

    @Test
    public void testCreate()
    {
        assertNull( Halfyear.create( null ) );
        assertNull( Halfyear.create( "" ) );
        assertNull( Halfyear.create( "Unsinn" ) );
        assertEquals( HY, Halfyear.create("2016-01-01 - 2016-06-30") );
        final Halfyear aSecondHY = new Halfyear( Year.of(2016), EPart.SECOND );
        assertEquals( aSecondHY, Halfyear.create("2016-07-15 - 2016-12-15") );
    }

    @Test
    public void testNeigbours()
    {
        assertEquals( new Halfyear( 2015, EPart.SECOND ), HY.createPredeccessor() );
        assertEquals( new Halfyear( 2016, EPart.SECOND ), HY.createSuccessor() );
        final Halfyear aSecondHY = new Halfyear( Year.of(2016), EPart.SECOND );
        assertEquals( new Halfyear( 2016, EPart.FIRST ), aSecondHY.createPredeccessor() );
        assertEquals( new Halfyear( 2017, EPart.FIRST ), aSecondHY.createSuccessor() );
    }

    @Test
    public void testIsBeforeMyEnd()
    {
        assertTrue(HY.isBeforeMyEnd( sm_RefDate1 ) );
        assertTrue(HY.isBeforeMyEnd( sm_RefDate2 ) );
        assertTrue(HY.isBeforeMyEnd( sm_RefDate1_PreviousDay ) );
        assertFalse(HY.isBeforeMyEnd( sm_RefDate2_NextDay ) );

    }

    @Test
    public void testIsAfterMyStart()
    {
        assertTrue(HY.isAfterMyStart( null ) );
        assertTrue(HY.isAfterMyStart( sm_RefDate1 ) );
        assertTrue(HY.isAfterMyStart( sm_RefDate2 ) );
        assertFalse(HY.isAfterMyStart( sm_RefDate1_PreviousDay ) );
        assertTrue(HY.isAfterMyStart( sm_RefDate2_NextDay ) );
    }

    @Test
    public void testIsBeforeMyStart()
    {
        assertTrue(HY.isBeforeMyStart( null ) );
        assertFalse(HY.isBeforeMyStart( sm_RefDate1 ) );
        assertFalse(HY.isBeforeMyStart( sm_RefDate2 ) );
        assertTrue(HY.isBeforeMyStart( sm_RefDate1_PreviousDay ) );
        assertFalse(HY.isBeforeMyStart( sm_RefDate2_NextDay ) );
    }

    @Test
    public void testIsWithinMyPeriod()
    {
        assertTrue(HY.isWithinMyPeriod( (LocalDate)null ) );
        assertTrue(HY.isWithinMyPeriod( (IPeriod)null ) );

        assertTrue(HY.isWithinMyPeriod( sm_RefDate1 ) );
        assertTrue(HY.isWithinMyPeriod( sm_RefDate2 ) );
        assertFalse(HY.isWithinMyPeriod( sm_RefDate1_PreviousDay ) );
        assertFalse(HY.isWithinMyPeriod( sm_RefDate2_NextDay ) );

        assertTrue( HY.isWithinMyPeriod( new Halfyear( 2016, EPart.FIRST)));
        assertFalse( HY.isWithinMyPeriod( new Halfyear( 2016, EPart.SECOND)));

        final FreeFromDuty aOtherPeriod = new FreeFromDuty( 0, null);
        assertTrue( HY.isWithinMyPeriod( aOtherPeriod));
        // Start vor Beginn der Periode - Ende == null
        aOtherPeriod.setFrom(sm_RefDate1_PreviousDay);
        assertTrue( HY.isWithinMyPeriod( aOtherPeriod));
        // Start == null - Ende vor Beginn der Periode
        aOtherPeriod.setFrom(null);
        aOtherPeriod.setUntil(sm_RefDate1_PreviousDay);
        assertFalse( HY.isWithinMyPeriod( aOtherPeriod));
        // Start und Ende vor Beginn der Periode
        aOtherPeriod.setFrom(sm_RefDate1_PreviousDay);
        assertFalse( HY.isWithinMyPeriod( aOtherPeriod));
        // Start == null - Ende innerhalb der Periode
        aOtherPeriod.setFrom(null);
        aOtherPeriod.setUntil(sm_RefDate2);
        assertTrue( HY.isWithinMyPeriod( aOtherPeriod));
        // Start und Ende innerhalb der Periode
        aOtherPeriod.setFrom(sm_RefDate1);
        aOtherPeriod.setUntil(sm_RefDate2);
        assertTrue( HY.isWithinMyPeriod( aOtherPeriod));
        // Start == null - Ende außerhalb der Periode
        aOtherPeriod.setFrom(null);
        aOtherPeriod.setUntil(sm_RefDate2_NextDay);
        assertTrue( HY.isWithinMyPeriod( aOtherPeriod));
        // Start vor der Periode - Ende außerhalb der Periode
        aOtherPeriod.setFrom( sm_RefDate1_PreviousDay );
        aOtherPeriod.setUntil(sm_RefDate2_NextDay);
        assertTrue( HY.isWithinMyPeriod( aOtherPeriod));
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.simple().forClass(Halfyear.class).verify();
    }
}

// ############################################################################
