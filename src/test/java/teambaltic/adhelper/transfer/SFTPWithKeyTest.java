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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
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

    private static SFTPWithKey sm_SFTPWithKey;
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
        sm_SFTPWithKey = new SFTPWithKey( "Test-Daten", "syniphos", 5022, "Test", new File("./Einstellungen/ssh/id_rsa"));
        try{
            sm_SFTPWithKey.init();
        }catch( final Exception fEx ){
            sm_Log.error("Exception: ", fEx );
            fail(fEx.getMessage());
        }

    }
    @AfterClass
    public static void shutdownWhenFinished()
    {
        if( sm_SFTPWithKey != null ){
            sm_SFTPWithKey.close();
        }
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

        final Path aRemotePath = Paths.get( "SubDir" );
        try{
            final List<String> aPaths = sm_SFTPWithKey.list( aRemotePath );
            assertNotNull("list", aPaths);
            for( final String aPath : aPaths ){
                sm_Log.info( "List: File found: "+aPath );
            }
            assertEquals( "Paths.size", 4, aPaths.size() );
        }catch( final Exception fEx ){
            fail("Exception: "+fEx );
        }
    }

    @Test
    public void test_ListByExtension()
    {

        final Path aRemotePath = Paths.get( "SubDir" );
        try{
            final List<String> aPaths = sm_SFTPWithKey.list( aRemotePath, "md5" );
            assertNotNull("list", aPaths);
            for( final String aPath : aPaths ){
                sm_Log.info( "List md5: File found: "+aPath );
                assertTrue("Endswith md5", aPath.toString().toLowerCase().endsWith( "md5" ));
            }
            assertEquals( "Paths.size", 2, aPaths.size() );

        }catch( final Exception fEx ){
            fail("Exception: "+fEx );
        }
    }

    @Test
    public void test_ListFolder()
    {

        final Path aRemotePath = Paths.get( "." );
        try{
            final List<String> aPaths = sm_SFTPWithKey.listFolders( aRemotePath );
            assertNotNull("list", aPaths);
            for( final String aPath : aPaths ){
                sm_Log.info( "List : Folder found: "+aPath );
            }
            assertEquals( "Paths.size", 2, aPaths.size() );

        }catch( final Exception fEx ){
            fail("Exception: "+fEx );
        }
    }

    @Test
    public void test_Download()
    {
        final Path aLocalPath = Paths.get("misc/SandBox/File-Downloaded.txt");
        try{
            Files.delete( aLocalPath );
        }catch( final IOException fEx ){
            /*Ignored*/
        }
        final Path aRemotePath = Paths.get( "Anmerkungen.txt" );
        try{
            sm_SFTPWithKey.download( new LocalRemotePathPair( aLocalPath, aRemotePath ) );
            assertTrue("Downloaded file exists", Files.exists( aLocalPath ));
        }catch( final Exception fEx ){
            fail("Exception: "+fEx );
        }
    }

    @Test
    public void test_Upload()
    {
        final Path aLocalPath = Paths.get("README");

        final Path aRemotePath = Paths.get( "README" );
        try{
            final boolean aExistsOnServer = sm_SFTPWithKey.exists( aRemotePath );
            if(aExistsOnServer){
                try{
                    sm_SFTPWithKey.delete( aRemotePath );
                }catch( final Exception fEx ){
                    /*Da kommt zwar ne Exception, die Datei ist aber trotzdem gelöscht*/
                }
            }
            assertFalse("Remote file exists1", sm_SFTPWithKey.exists( aRemotePath ));
            sm_SFTPWithKey.upload(  new LocalRemotePathPair( aLocalPath, aRemotePath ) );
            assertTrue("Uploaded file exists", sm_SFTPWithKey.exists( aRemotePath ));
        }catch( final Exception fEx ){
            fail("Exception: "+fEx );
        }
    }

}

// ############################################################################
