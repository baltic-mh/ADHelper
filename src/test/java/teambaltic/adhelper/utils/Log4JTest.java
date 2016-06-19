/**
 * Log4JTest.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
//########################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// ############################################################################
public class Log4JTest
{
    private static final Logger sm_Log = Logger.getLogger(Log4JTest.class);

    private static final String PROPKEY_Log4jFileName = "log4jfilename";
    private static final String Log4jFileName = "Log4J-UnitTest.log";

    private final Path m_UninitializedLogFile = Paths.get( ".log" );
    private final Path m_InitializedLogFile = Paths.get( Log4jFileName );

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
    }

    @Before
    public void initBeforeEachTest()
    {
        delete( m_UninitializedLogFile );
        delete( m_InitializedLogFile );
        sm_Log.removeAllAppenders();
    }

    @After
    public void cleanupAfterEachTest()
    {
        sm_Log.removeAllAppenders();
        Log4J.reset();
        delete( m_UninitializedLogFile );
        delete( m_InitializedLogFile );
    }

    // ########################################################################
    // TESTS
    // ########################################################################
    //TODO Test aktivieren
//    @Test
    public void test_UninitializedAppName()
    {
        final String aMethodName = TestUtils.getMethodName();
        System.clearProperty(PROPKEY_Log4jFileName);
        // Diese Ausgabe erscheint nirgendwo!
        final long aStartTime = TestUtils.logMethodStart( aMethodName );
        Log4J.initLog4J();
        sm_Log.info( aMethodName+": Logging initialisiert!" );
        assertTrue( aMethodName+": LogDatei ohne Namen", Files.exists( m_UninitializedLogFile ) );
        TestUtils.logMethodEnd( aStartTime, aMethodName );
    }

    @Test
    public void test_InitializedAppName()
    {
        final String aMethodName = TestUtils.getMethodName();
        // Diese Ausgabe erscheint nirgendwo!
        final long aStartTime = TestUtils.logMethodStart( aMethodName );
        System.setProperty(PROPKEY_Log4jFileName, Log4jFileName);
        Log4J.initLog4J();
        sm_Log.info( aMethodName+": Logging initialisiert!" );
        assertTrue( aMethodName+": LogDatei ohne Namen", Files.exists( m_InitializedLogFile ) );
        TestUtils.logMethodEnd( aStartTime, aMethodName );
    }

    // ########################################################################
    // HILFSMETHODEN
    // ########################################################################
    private static void delete(final Path fFile)
    {
        if( !Files.exists( fFile )){
            return;
        }
        try{
            Files.delete( fFile );
        }catch( final IOException fEx ){
            fail("Konnte Datei nicht löschen: " +fEx.getMessage() );
        }
    }

}
// ############################################################################
