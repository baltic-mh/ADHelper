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

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.controller.ListProvider;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IClubMember;
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
        final File aFile = new File("misc/TestResources/Tabellen/BasisDaten.csv");
        final BaseDataReader aReader = new BaseDataReader( aFile );
        final ListProvider<InfoForSingleMember> aInfoListProvider = new ListProvider<>();
        try{
            aReader.read(  aInfoListProvider );
            final Collection<InfoForSingleMember> aInfoList = aInfoListProvider.getAll();
            for( final InfoForSingleMember aInfo : aInfoList ){
                final IClubMember aMember = aInfo.getMember();
                final StringBuffer aSB = new StringBuffer( "Mitglied: "+aMember );
                final Balance aBalance = aInfo.getBalance();
                if( aBalance != null ){
                    aSB.append( String.format( " | Guthaben: %5.2fh",aBalance.getValue()/100.0f) );
                }
                final FreeFromDuty aFreeFromDuty = aInfo.getFreeFromDuty();
                if( aFreeFromDuty != null ){
                    aSB.append( " | AD-Befreiung: "+aFreeFromDuty );
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
