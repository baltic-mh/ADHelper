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
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.naming.OperationNotSupportedException;

// ############################################################################
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
    public static final String ALGORITHM = "RSA";

    // ------------------------------------------------------------------------
    private final File m_PublicKeyFile;
    private File getPublicKeyFile(){ return m_PublicKeyFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final File m_PrivateKeyFile;
    private File getPrivateKeyFile(){ return m_PrivateKeyFile; }
    // ------------------------------------------------------------------------

    public CryptUtils(final File fPrivateKeyFile, final File fPublicKeyFile)
    {
        m_PrivateKeyFile = fPrivateKeyFile;
        m_PublicKeyFile  = fPublicKeyFile;
    }

    /**
     * @param fFile
     * @return TargetPath
     * @throws Exception
     */
    @Override
    public Path encrypt( final Path fFile ) throws Exception
    {
        // TODO
        throw new OperationNotSupportedException( "Noch nicht implementiert!" );
    }
    @Override
    public Path decrypt( final Path fFile ) throws Exception
    {
        // TODO
        throw new OperationNotSupportedException( "Noch nicht implementiert!" );
    }
    /**
     * Encrypt the plain text using public key.
     *
     * @param text
     *            : original plain text
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    @Override
    public byte[] encrypt(final String text)
    {
        byte[] cipherText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance( ALGORITHM );
            final Key aPublicKey = getPublicKey( getPublicKeyFile() );
            // encrypt the plain text using the public key
            cipher.init( Cipher.ENCRYPT_MODE, aPublicKey );
            cipherText = cipher.doFinal( text.getBytes() );
        } catch (final Exception e) {
            e.printStackTrace();
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
    @Override
    public String decrypt(final byte[] text)
    {
        byte[] decryptedText = null;
        try {
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance( ALGORITHM );
            final PrivateKey aPublicKey = getPrivateKey( getPrivateKeyFile() );
            // decrypt the text using the private key
            cipher.init( Cipher.DECRYPT_MODE, aPublicKey );
            decryptedText = cipher.doFinal( text );

        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        return new String( decryptedText );
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
