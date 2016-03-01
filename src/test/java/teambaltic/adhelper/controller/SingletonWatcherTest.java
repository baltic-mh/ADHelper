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
import static org.junit.Assert.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import teambaltic.adhelper.remoteaccess.IRemoteAccess;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class SingletonWatcherTest
{
//    private static final Logger sm_Log = Logger.getLogger(SingletonWatcherTest.class);

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
        final MyRemoteAccess aRemoteAccess = new MyRemoteAccess();
        final SingletonWatcher aBC = new SingletonWatcher( "Ich war's!", 1000L, aRemoteAccess );
        aBC.start();
        try{ Thread.sleep( 5000L ); }catch( final InterruptedException fEx ){/**/}
        assertEquals("UploadCnt", 5, aRemoteAccess.getCnt_Upload());

        final boolean aAmIAlone = aBC.amIAlone();
        assertFalse("AmIAlone", aAmIAlone);
        aBC.stop();
        assertEquals("DeleteCnt", 1, aRemoteAccess.getCnt_Delete());
    }

    // ########################################################################
    // SUPPORT AREA
    // ########################################################################

    private class MyRemoteAccess implements IRemoteAccess
    {
        private int m_Cnt_Upload;
        public int getCnt_Upload(){ return m_Cnt_Upload; }

        private int m_Cnt_Download;
        public int getCnt_Download(){ return m_Cnt_Download; }

        private int m_Cnt_Delete;
        public int getCnt_Delete(){ return m_Cnt_Delete; }

        private final Path aMyRemotePath = Paths.get( "misc", "SandBox", "BusyFile.txt" );

        @Override
        public void upload( final Path fLocalPath, final Path fRemotePath ) throws Exception
        {
            m_Cnt_Upload++;
            Files.copy( fLocalPath, aMyRemotePath, StandardCopyOption.REPLACE_EXISTING );
        }

        @Override
        public void download( final Path fRemotePath, final Path fLocalPath ) throws Exception
        {
            m_Cnt_Download++;
            Files.copy( aMyRemotePath, fLocalPath, StandardCopyOption.REPLACE_EXISTING );
        }

        @Override
        public void delete( final Path fRemotePath ) throws Exception
        {
            m_Cnt_Delete++;
            Files.delete( aMyRemotePath );
        }

        @Override
        public boolean exists( final Path fRemotePath ) throws Exception
        {
            return Files.exists( aMyRemotePath );
        }

    }
}

// ############################################################################
