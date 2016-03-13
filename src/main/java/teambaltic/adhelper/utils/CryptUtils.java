/**
 * CryptUtils.java
 *
 * Created on 07.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

// ############################################################################
/**
 * Im ersten Ansatz hat diese Verschlüsselung nur funktioniert, wenn man weniger
 * als 245 Bytes zu verschlüsseln hat :-/
 *
 * Ich habe das Thema nun erst mal nach hinten gestellt und geben als verschlüsselte
 * Datei einfach die Original-Datei zurück.
 *
 * Einge ganz gute Quelle scheint mir folgendes zu sein:
 * http://www.torsten-horn.de/techdocs/java-crypto.htm
 */
/**
 * http://codeartisan.blogspot.de/2009/05/public-key-cryptography-in-java.html
 *
 * Creating the keypair.
 *  We are going to create a keypair, saving it in openssl's preferred PEM format.
 *  PEM formats are ASCII and hence easy to email around as needed.
 *  However, we will need to save the keys in the binary DER format so Java can read them.
 *  Without further ado, here is the magical incantation for creating the keys we'll use:
 *      # generate a 2048-bit RSA private key
 *      $ openssl genrsa -out private_key.pem 2048
 *
 *      # convert private Key to PKCS#8 format (so Java can read it)
 *      $ openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem \
 *      -out private_key.der -nocrypt
 *
 *      # output public key portion in DER format (so Java can read it)
 *      $ openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der
 *
 *  You keep private_key.pem around for reference, but you hand the DER versions to your Java programs.
 */
public class CryptUtils implements ICryptUtils
{
    private static final Logger sm_Log = Logger.getLogger(CryptUtils.class);

    public static final String ALGORITHM = "RSA";

    // ------------------------------------------------------------------------
    private final File m_PublicKeyFile;
    private File getPublicKeyFile(){ return m_PublicKeyFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final File m_PrivateKeyFile;
    private File getPrivateKeyFile(){ return m_PrivateKeyFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Cipher m_Cipher;
    private Cipher getCipher(){ return m_Cipher; }
    // ------------------------------------------------------------------------

    public CryptUtils(final File fPrivateKeyFile, final File fPublicKeyFile) throws Exception
    {
        // get an RSA cipher object
        m_Cipher = initCipher();
        m_PrivateKeyFile = fPrivateKeyFile;
        m_PublicKeyFile  = fPublicKeyFile;
    }

    private static Cipher initCipher() throws Exception
    {
        final Cipher aCipher = Cipher.getInstance( ALGORITHM );
        return aCipher;
    }
    /**
     * @param fFile
     * @return TargetPath
     * @throws Exception
     */
    @Override
    public Path encrypt( final Path fFile ) throws Exception
    {
        final Path aPath_Cry = Paths.get( fFile.toString()+".cry" );
        Files.copy( fFile, aPath_Cry, StandardCopyOption.REPLACE_EXISTING );
        return aPath_Cry;
    }

    @Override
    public Path decrypt( final Path fFile ) throws Exception
    {
        final String aFilenameWithout_Cry = FilenameUtils.getBaseName( fFile.toFile().getPath() );
        final Path aPath_Decry = Paths.get( fFile.getParent().toString(), aFilenameWithout_Cry );
        Files.copy( fFile, aPath_Decry, StandardCopyOption.REPLACE_EXISTING );
        return aPath_Decry;

//        final byte[] aData_Cry = Files.readAllBytes( fFile );
//        final byte[] aData     = decrypt( aData_Cry );
//        final String aFilenameWithout_Cry = FilenameUtils.getBaseName( fFile.toFile().getPath() );
//        final Path aPath_Decry = Paths.get( aFilenameWithout_Cry );
//        Files.write(aPath_Decry, aData );
//        return aPath_Decry;
    }
    /**
     * Encrypt the plain text using public key.
     *
     * @param fText
     *            : original plain text
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public byte[] encrypt(final String fText)
    {
        final byte[] aToEncrypt = fText.getBytes();
        return encrypt( aToEncrypt );

    }
    public byte[] encrypt( final byte[] fText )
    {
        byte[] cipherText = null;
        try {
            final Key aKey = getPublicKey( getPublicKeyFile() );
            // encrypt the plain text using the public key
            m_Cipher.init( Cipher.ENCRYPT_MODE, aKey );
            cipherText = m_Cipher.doFinal( fText );
        } catch (final Exception fEx) {
            sm_Log.warn("Problem:", fEx);
        }
        return cipherText;
    }

    /**
     * Decrypt text using private key.
     *
     * @param text
     *            :encrypted text
     * @return plain text
     * @throws java.lang.Exception
     */
    public byte[] decrypt(final byte[] text)
    {
        byte[] decryptedText = null;
        try {
            final PrivateKey aKey = getPrivateKey( getPrivateKeyFile() );
            getCipher().init( Cipher.DECRYPT_MODE, aKey );
            // decrypt the text using the private key
            decryptedText = getCipher().doFinal( text );

        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        return decryptedText;
    }

    private static PublicKey getPublicKey(final File fPublicKeyFile)
            throws Exception
    {

        final FileInputStream fis = new FileInputStream( fPublicKeyFile );
        final DataInputStream dis = new DataInputStream( fis );
        final byte[] keyBytes = new byte[(int) fPublicKeyFile.length()];
        dis.readFully( keyBytes );
        dis.close();

        final X509EncodedKeySpec spec = new X509EncodedKeySpec( keyBytes );
        final KeyFactory kf = KeyFactory.getInstance( "RSA" );
        return kf.generatePublic( spec );
    }

    private static PrivateKey getPrivateKey(final File fPrivateKeyFile)
            throws Exception
    {
        final FileInputStream fis = new FileInputStream( fPrivateKeyFile );
        final DataInputStream dis = new DataInputStream( fis );
        final byte[] keyBytes = new byte[(int) fPrivateKeyFile.length()];
        dis.readFully( keyBytes );
        dis.close();

        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( keyBytes );
        final KeyFactory kf = KeyFactory.getInstance( "RSA" );
        return kf.generatePrivate( spec );
    }

}

// ############################################################################
