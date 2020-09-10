/**
 * BaseInfoReaderTest.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.controller.ListProvider;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDuty.REASON;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class BaseInfoReaderTest
{
    private static final Logger sm_Log = Logger.getLogger(BaseInfoReaderTest.class);

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
        final File aFile = new File("misc/TestResources/ReferenzDaten/Daten/BasisDaten.csv");
        final BaseDataReader aReader = new BaseDataReader( aFile );
        final ListProvider<InfoForSingleMember> aInfoListProvider = new ListProvider<>();
        try{
            aReader.read(  aInfoListProvider, null, 0 );
            final Collection<InfoForSingleMember> aInfoList = aInfoListProvider.getAll();
            assertEquals( 4, aInfoList.size() );
            for( final InfoForSingleMember aInfo : aInfoList ){
                final IClubMember aMember = aInfo.getMember();
                final StringBuffer aSB = new StringBuffer( "Mitglied: "+aMember );
                final FreeFromDutySet aFreeFromDutySet = aInfo.getFreeFromDutySet();
                if( aFreeFromDutySet != null ){
                    aSB.append( " | AD-Befreiung: "+aFreeFromDutySet );
                    if( aMember.getID() == 10174 ) {
                        final IPeriod aPeriod = new Halfyear( 2020, EPart.FIRST);
                        final Collection<FreeFromDuty> aFreeFromDutyItems = aFreeFromDutySet.getFreeFromDutyItems(aPeriod );
                        assertEquals(1, aFreeFromDutyItems.size());
                        // Es gibt nur ein Element - aber an das kommen wir nur über eine Schleife ran :-|
                        aFreeFromDutyItems.forEach( it -> assertEquals( REASON.MANAGEMENT, it.getReason() ) );
                    }
                }
                sm_Log.info( aSB.toString() );
            }
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            fEx.printStackTrace();
            fail("Exception: "+fEx.getMessage());
        }
    }

}

// ############################################################################
