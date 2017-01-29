/**
 * BalanceHistoryTest.java
 *
 * Created on 22.01.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class BalanceHistoryTest
{
    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        System.setProperty( "log4jfilename", "UnitTest.log" );
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

    // ########################################################################
    // TESTS
    // ########################################################################

    @Test
    public void test()
    {
        final int aMemberID = 4711;
        final BalanceHistory aTestObject = new BalanceHistory( aMemberID );

        populate( aMemberID, aTestObject, 5 );
        final Balance aNewestValue = aTestObject.getValue( new Halfyear(2017, 7 ) );

        assertEquals("NewestValue", 54, aNewestValue.getValue_Original());
    }

    // ########################################################################
    // HILFSMETHODEN
    // ########################################################################

    private static void populate( final int fMemberID, final BalanceHistory fBalanceHistory, final int fNumElements )
    {
        for( int aIdx = 0; aIdx < fNumElements; aIdx++ ){
            final IPeriod aPeriod = new Halfyear( 2017-aIdx, 7 );
            final Balance aBalance = new Balance( fMemberID, aPeriod, 54-aIdx );
            fBalanceHistory.addBalance( aBalance );
        }
    }

}

// ############################################################################
