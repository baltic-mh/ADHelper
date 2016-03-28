/**
 * CryptUtilsTest.java
 *
 * Created on 28.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

// ############################################################################
public class CryptUtilsTest
{
    private static final Logger sm_Log            = Logger.getLogger( CryptUtilsTest.class );

    private static final String sm_TestFolderName = "misc/TestResources/CryptUtils";
    private static final File   sm_PrivateKeyFile = new File( sm_TestFolderName, "private_key.der" );
    private static final File   sm_PublicKeyFile  = new File( sm_TestFolderName, "public_key.der" );

    private static CryptUtils   CRYPTUTILS;

    // ########################################################################
    // INITIALISIERUNG
    // ########################################################################
    @BeforeClass
    public static void initOnceBeforeStart()
    {
        Log4J.initLog4J();
        try{
            CRYPTUTILS = new CryptUtils( sm_PrivateKeyFile, sm_PublicKeyFile );
        }catch( final Exception fEx ){
            sm_Log.error( "Exception: ", fEx );
            fail( fEx.getMessage() );
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

    @AfterClass
    public static void cleanupAfterShutdown()
    {
        Log4J.reset();
    }

    // ########################################################################
    // TESTS
    // ########################################################################

    @Test
    public void testEncryptDecrypt_String()
    {
        try{
            final String aTextToEncrypt = "Text, der verschlüsselt werden soll";
            final String aTextEncrypted = CRYPTUTILS.encrypt( aTextToEncrypt );
            final String aTextDecrypted = CRYPTUTILS.decrypt( aTextEncrypted );
            assertEquals( "Encrypted-Decrypted", aTextToEncrypt, aTextDecrypted );
        }catch( final Exception fEx ){
            sm_Log.error( "Exception: ", fEx );
            fail( fEx.getMessage() );
        }
    }

    @Test
    public void testEncryptDecrypt_File()
    {
        try{
            final Path aFileToEncrypt = Paths.get( sm_TestFolderName, "BasisDaten.csv" );
            final Path aFileOrig = Paths.get( sm_TestFolderName, "BasisDaten-orig.csv" );
            ;
            Files.copy( aFileToEncrypt, aFileOrig, StandardCopyOption.REPLACE_EXISTING );
            final Path aFileEncrypted = CRYPTUTILS.encrypt( aFileToEncrypt );
            final Path aFileDecrypted = CRYPTUTILS.decrypt( aFileEncrypted );
            final byte[] aBytesToEncrypt = Files.readAllBytes( aFileToEncrypt );
            final byte[] aBytesDecrypted = Files.readAllBytes( aFileDecrypted );
            assertEquals("Encrypted-Decrypted length", aBytesToEncrypt.length, aBytesDecrypted.length);
            for( int aIdx = 0; aIdx < aBytesDecrypted.length; aIdx++ ){
                assertEquals("Encrypted-Decrypted IDX "+aIdx, aBytesToEncrypt[aIdx], aBytesDecrypted[aIdx]);
            }
        }catch( final Exception fEx ){
            sm_Log.error( "Exception: ", fEx );
            fail( fEx.getMessage() );
        }
    }

}

