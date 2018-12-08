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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

// ############################################################################
public class CryptUtilsTest
{
    private static final Logger sm_Log            = Logger.getLogger( CryptUtilsTest.class );

    private static final String sm_TestFolderName = "misc/TestResources/CryptUtils";
    private static final File   sm_PrivateKeyFile = new File( sm_TestFolderName, "private_key.der" );
    private static final File   sm_PublicKeyFile  = new File( sm_TestFolderName, "public_key.der" );

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

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
            final Path aMyTestFolder = testFolder.newFolder().toPath();
            final String FileToEncrypt_Name = "BasisDaten.csv";
            final Path aFileOrig = Paths.get( sm_TestFolderName, FileToEncrypt_Name );
            final Path aFileToEncrypt = aMyTestFolder.resolve( FileToEncrypt_Name );

            Files.copy( aFileOrig, aFileToEncrypt, StandardCopyOption.REPLACE_EXISTING );
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

    @Test
    public void testEncrypt_File_2017_II()
    {
        try{
            final Path aMyTestFolder = testFolder.newFolder().toPath();
            final String FileToEncrypt_Name = "2017-07-01 - 2017-12-31.zip";
            final Path aFileOrig = Paths.get( sm_TestFolderName, FileToEncrypt_Name );
            final Path aFileToEncrypt = aMyTestFolder.resolve( FileToEncrypt_Name );

            Files.copy( aFileOrig, aFileToEncrypt, StandardCopyOption.REPLACE_EXISTING );
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

    @Test
    public void testDencrypt_File_2017_II()
    {
        try{
            final Path aMyTestFolder = testFolder.newFolder().toPath();
            final String FileToDecrypt_Name = "2017-07-01 - 2017-12-31.zip.cry";
            final Path aFileOrig = Paths.get( sm_TestFolderName, FileToDecrypt_Name );
            final Path aFileToDecrypt = aMyTestFolder.resolve( FileToDecrypt_Name );
            Files.copy( aFileOrig, aFileToDecrypt, StandardCopyOption.REPLACE_EXISTING );

            final Path aFileDecrypted = CRYPTUTILS.decrypt( aFileToDecrypt );
            final Path aFileEncrypted = CRYPTUTILS.encrypt( aFileDecrypted );

            final byte[] aBytesToDecrypt = Files.readAllBytes( aFileToDecrypt );
            final byte[] aBytesEncrypted = Files.readAllBytes( aFileEncrypted );
            assertEquals("Decrypted-Encrypted length", aBytesToDecrypt.length, aBytesEncrypted.length);
            for( int aIdx = 0; aIdx < aBytesEncrypted.length; aIdx++ ){
                assertEquals("Decrypted-Encrypted IDX "+aIdx, aBytesToDecrypt[aIdx], aBytesEncrypted[aIdx]);
            }
        }catch( final Exception fEx ){
            sm_Log.error( "Exception: ", fEx );
            fail( fEx.getMessage() );
        }
    }

}

