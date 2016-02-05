/**
 * WorkEventReaderTest.java
 *
 * Created on 04.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class WorkEventReaderTest
{
    private static final Logger sm_Log = Logger.getLogger(WorkEventReaderTest.class);

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
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
        final File aFile = new File("misc/TestResources/Tabellen/Arbeitsdienste1.csv");
        final WorkEventReader aReader = new WorkEventReader( aFile );
        try{
            aReader.read( );
            final Collection<WorkEventsAttended> aWEA = aReader.getWorkEventsAttendedList();
            for( final WorkEventsAttended aWEAForMember : aWEA ){
                sm_Log.info(aWEAForMember);
            }
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            fEx.printStackTrace();
            fail("Exception: "+fEx.getMessage());
        }
    }

}

// ############################################################################
