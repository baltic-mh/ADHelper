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

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.controller.ListProvider;
import teambaltic.adhelper.controller.WorkEventReader;
import teambaltic.adhelper.model.InfoForSingleMember;
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
        final ListProvider<InfoForSingleMember> aInfoListProvider = new ListProvider<>();
        try{
            aReader.read( aInfoListProvider );
            for( final InfoForSingleMember aSingleInfo : aInfoListProvider.getAll() ){
                final WorkEventsAttended aWEAForMember = aSingleInfo.getWorkEventsAttended();
                sm_Log.info(String.format( "Arbeitsdienste für %d: %s", aWEAForMember.getID(), aWEAForMember) );
            }
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            fEx.printStackTrace();
            fail("Exception: "+fEx.getMessage());
        }
    }

}

// ############################################################################
