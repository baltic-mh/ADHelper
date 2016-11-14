/**
 * SingletonWatcherTest.java
 *
 * Created on 01.03.2016
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.remoteaccess.IRemoteAccess;
import teambaltic.adhelper.remoteaccess.LocalRemotePathPair;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class SingletonWatcherTest
{
//    private static final Logger sm_Log = Logger.getLogger(SingletonWatcherTest.class);
    private static final Path sm_FremderMann = Paths.get( "misc", "TestResources", "SingletonWatcher", "BusyFile.txt" );
    private static final Path sm_LocalPath   = Paths.get( "misc", "SandBox", "BusyFile.txt" );

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
        // Zur Sicherheit machen wir die Bahn frei:
        try{
            Files.deleteIfExists( sm_LocalPath );
        }catch( final IOException fEx ){
            fail( "Tut mir leid: "+fEx.getMessage());
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
    public void test()
    {
        final MyRemoteAccess aRemoteAccess = new MyRemoteAccess();
        final SingletonWatcher aSW = new SingletonWatcher( "Ich war's!", 1000L, aRemoteAccess );

        // Zuerst ist keiner da:
        String aRemoteInfo = aSW.getRemoteInfo();
        assertNull("RemoteInfo1", aRemoteInfo);
        int aCnt_Download   = aRemoteAccess.getCnt_Download();
        assertEquals("DownloadCnt", 1, aCnt_Download);

        // Dann kommt ein fremder Mann;
        try{
            Files.copy( sm_FremderMann, sm_LocalPath, StandardCopyOption.REPLACE_EXISTING );
            aRemoteInfo = aSW.getRemoteInfo();
            aCnt_Download   = aRemoteAccess.getCnt_Download();
            assertEquals("DownloadCnt", 2, aCnt_Download);
            assertNotNull("RemoteInfo2", aRemoteInfo);
        }catch( final IOException fEx ){
            fail( "Tut mir leid: "+fEx.getMessage());
        }

        // Und nun sind wir selbst da:
        try{
            aSW.start();
            fail( "Start - obwohl eine andere Instanz läuft!");
        }catch( final Exception fEx1 ){
            // Alles chicko!
            aCnt_Download   = aRemoteAccess.getCnt_Download();
            assertEquals("DownloadCnt", 3, aCnt_Download);
        }
        // Jetzt machen wir die Bahn wieder frei:
        try{
            Files.delete( sm_LocalPath );
        }catch( final IOException fEx ){
            fail( "Tut mir leid: "+fEx.getMessage() );
        }
        // ... und legen erneut los:
        try{
            aSW.start();
            aCnt_Download   = aRemoteAccess.getCnt_Download();
            assertEquals("DownloadCnt", 4, aCnt_Download);
            // Alles chicko!
        }catch( final Exception fEx1 ){
            fail( "Start geht nicht - obwohl KEINE andere Instanz läuft!");
        }

        try{ Thread.sleep( 5000L ); }catch( final InterruptedException fEx ){/**/}
        final int aCnt_Upload     = aRemoteAccess.getCnt_Upload();
        assertTrue("UploadCnt="+aCnt_Upload, aCnt_Upload >= 5 );

        // Bei getRemoteInfo wird über das RemoteAccess-Objekt nach einer BusyDatei
        // (auf dem Server) gefragt. Das RemoteAccess-Objekt dieses Tests
        // liefert die vom SingletonWatcher selbst erzeugte Datei zurück!
        // Daher ist man hier NICHT ALLEINE - obwohl nur ein Objekt existiert ;-)
        aRemoteInfo = aSW.getRemoteInfo();
        assertNotNull("RemoteInfo3", aRemoteInfo);
        aCnt_Download = aRemoteAccess.getCnt_Download();
        assertEquals("DownloadCnt", 5, aCnt_Download);
        aSW.stop();
        assertEquals("DeleteCnt", 1, aRemoteAccess.getCnt_Delete());
    }

    // ########################################################################
    // SUPPORT AREA
    // ########################################################################

    private class MyRemoteAccess implements IRemoteAccess
    {
        // Dieses Objekt ist nicht wirklich "Remote"!
        private final Path m_MyRemotePath = sm_LocalPath;

        private int m_Cnt_Upload;
        public int getCnt_Upload(){ return m_Cnt_Upload; }

        private int m_Cnt_Download;
        public int getCnt_Download(){ return m_Cnt_Download; }

        private int m_Cnt_Delete;
        public int getCnt_Delete(){ return m_Cnt_Delete; }

        @Override
        public void upload( final LocalRemotePathPair fPathPair ) throws Exception
        {
            m_Cnt_Upload++;
            Files.copy( fPathPair.getLocal(), m_MyRemotePath , StandardCopyOption.REPLACE_EXISTING );
        }

        @Override
        public boolean download( final LocalRemotePathPair fPathPair ) throws Exception
        {
            m_Cnt_Download++;
            Files.copy( m_MyRemotePath, fPathPair.getLocal(), StandardCopyOption.REPLACE_EXISTING );
            return true;
        }

        @Override
        public void delete( final Path fRemotePath ) throws Exception
        {
            m_Cnt_Delete++;
            Files.delete( m_MyRemotePath );
        }

        @Override
        public boolean exists( final Path fRemotePath ) throws Exception
        {
            return Files.exists( m_MyRemotePath );
        }

        @Override
        public void upload( final List<LocalRemotePathPair> fPathPairs ) throws Exception{}
        @Override
        public boolean download( final List<LocalRemotePathPair> fPathPairs ) throws Exception{ return false; }
        @Override
        public List<String> list( final Path fRemotePath ) throws Exception{ return null; }
        @Override
        public List<String> list( final Path fRemotePath, final String fExt ) throws Exception{ return null; }
        @Override
        public List<String> listFolders( final Path fRemotePath ) throws Exception{ return null; }
        @Override
        public void init() throws Exception{}
        @Override
        public void close(){}
    }
}

// ############################################################################
