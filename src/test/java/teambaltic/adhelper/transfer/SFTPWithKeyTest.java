/**
 * SFTPWithKeyTest.java
 *
 * Created on 01.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.transfer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.remoteaccess.LocalRemotePathPair;
import teambaltic.adhelper.remoteaccess.SFTPWithKey;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class SFTPWithKeyTest
{
    private static final Logger sm_Log = Logger.getLogger(SFTPWithKeyTest.class);

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
    public void test_List()
    {
        final SFTPWithKey aSFTPWithKey = new SFTPWithKey( "Test-Daten", "syniphos", 5022, "Test", new File("./Daten/Einstellungen/ssh/id_rsa"));

        final Path aRemotePath = Paths.get( "SubDir" );
        try{
            final List<URL> aURLs = aSFTPWithKey.list( aRemotePath );
            assertNotNull("list", aURLs);
            for( final URL aURL : aURLs ){
                sm_Log.info( "List: File found:"+aURL );
            }
            assertEquals( "URLs.size", 4, aURLs.size() );
        }catch( final Exception fEx ){
            fail("Exception: "+fEx );
        }
    }

    @Test
    public void test_ListByExtension()
    {
        final SFTPWithKey aSFTPWithKey = new SFTPWithKey( "Test-Daten", "syniphos", 5022, "Test", new File("./Daten/Einstellungen/ssh/id_rsa"));

        final Path aRemotePath = Paths.get( "SubDir" );
        try{
            final List<URL> aURLs = aSFTPWithKey.list( aRemotePath, "md5" );
            assertNotNull("list", aURLs);
            for( final URL aURL : aURLs ){
                sm_Log.info( "List md5: File found:"+aURL );
                assertTrue("Endswith md5", aURL.toString().toLowerCase().endsWith( "md5" ));
            }
            assertEquals( "URLs.size", 2, aURLs.size() );

        }catch( final Exception fEx ){
            fail("Exception: "+fEx );
        }
    }

    @Test
    public void test_Download()
    {
        final SFTPWithKey aSFTPWithKey = new SFTPWithKey( "Test-Daten", "syniphos", 5022, "Test", new File("./Daten/Einstellungen/ssh/id_rsa"));
        final Path aLocalPath = Paths.get("misc/SandBox/File-Downloaded.txt");
        try{
            Files.delete( aLocalPath );
        }catch( final IOException fEx ){
            /*Ignored*/
        }
        final Path aRemotePath = Paths.get( "Anmerkungen.txt" );
        try{
            aSFTPWithKey.download( new LocalRemotePathPair( aLocalPath, aRemotePath ) );
            assertTrue("Downloaded file exists", Files.exists( aLocalPath ));
        }catch( final Exception fEx ){
            fail("Exception: "+fEx );
        }
    }

    @Test
    public void test_Upload()
    {
        final SFTPWithKey aSFTPWithKey = new SFTPWithKey( "Test-Daten", "syniphos", 5022, "Test", new File("./Daten/Einstellungen/ssh/id_rsa"));
        final Path aLocalPath = Paths.get("README");

        final Path aRemotePath = Paths.get( "README" );
        try{
            final boolean aExistsOnServer = aSFTPWithKey.exists( aRemotePath );
            if(aExistsOnServer){
                try{
                    aSFTPWithKey.delete( aRemotePath );
                }catch( final Exception fEx ){
                    /*Da kommt zwar ne Exception, die Datei ist aber trotzdem gelöscht*/
                }
            }
            assertFalse("Remote file exists1", aSFTPWithKey.exists( aRemotePath ));
            aSFTPWithKey.upload(  new LocalRemotePathPair( aLocalPath, aRemotePath ) );
            assertTrue("Uploaded file exists", aSFTPWithKey.exists( aRemotePath ));
        }catch( final Exception fEx ){
            fail("Exception: "+fEx );
        }
    }

}

// ############################################################################
