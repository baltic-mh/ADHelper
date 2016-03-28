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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

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

    static final String ALGORITHM_ASYM = "RSA";
    static final String ALGORITHM_SYM  = "AES";

    // ------------------------------------------------------------------------
    private final File m_PublicKeyFile;
    private File getPublicKeyFile(){ return m_PublicKeyFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final File m_PrivateKeyFile;
    private File getPrivateKeyFile(){ return m_PrivateKeyFile; }
    // ------------------------------------------------------------------------

    private final BASE64Encoder m_Base64Encoder;
    private final BASE64Decoder m_Base64Decoder;

    public CryptUtils(final File fPrivateKeyFile, final File fPublicKeyFile) throws Exception
    {
        m_Base64Encoder  = new BASE64Encoder();
        m_Base64Decoder  = new BASE64Decoder();
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
        final Path aFile_Encrypted = Paths.get( fFile.toString()+".cry" );

        final InputStream      aInStream   = new FileInputStream( fFile.toFile() );
        final FileOutputStream aOutStream  = new FileOutputStream( aFile_Encrypted.toFile() );

        encrypt( aInStream, aOutStream );
        return aFile_Encrypted;
    }

    /** Verschluesseln (Streams werden mit close() geschlossen) */
    private void encrypt(
            final InputStream inpStream,
            final OutputStream encryptedOutStream )
            throws Exception
    {
        try{
            final KeyGenerator keyGen = KeyGenerator.getInstance( ALGORITHM_SYM );
            keyGen.init( Math.min( 256, Cipher.getMaxAllowedKeyLength( ALGORITHM_SYM ) ) );
            final SecretKey symKey = keyGen.generateKey();
            final Key publicKey = getPublicKey();

            Cipher cipher = Cipher.getInstance( ALGORITHM_ASYM );
            cipher.init( Cipher.WRAP_MODE, publicKey );
            final byte[] wrappedKey = cipher.wrap( symKey );

            final DataOutputStream out = new DataOutputStream( encryptedOutStream );
            try{
                out.writeInt( wrappedKey.length );
                out.write( wrappedKey );
                cipher = Cipher.getInstance( ALGORITHM_SYM );
                cipher.init( Cipher.ENCRYPT_MODE, symKey );
                transform( inpStream, out, cipher );
            }finally{
                out.close();
            }
        }finally{
            inpStream.close();
            encryptedOutStream.close();
        }
    }

    @Override
    public Path decrypt( final Path fFile ) throws Exception
    {
        final String aFilenameWithout_Cry = FilenameUtils.getBaseName( fFile.toFile().getPath() );
        final Path aFile_Decrypted = Paths.get( fFile.getParent().toString(), aFilenameWithout_Cry );

        final InputStream      aInStream   = new FileInputStream( fFile.toFile() );
        final FileOutputStream aOutStream  = new FileOutputStream( aFile_Decrypted.toFile() );
        decrypt( aInStream, aOutStream );

        return aFile_Decrypted;
    }

    /** Entschluesseln (Streams werden mit close() geschlossen) */
    public void decrypt(
            final InputStream encryptedInpStream,
            final OutputStream decryptedOutStream)
          throws Exception
    {
        try{
            final DataInputStream in = new DataInputStream( encryptedInpStream );
            try{
                final int length = in.readInt();
                final byte[] wrappedKey = new byte[length];
                in.read( wrappedKey, 0, length );

                final Key privateKey = getPrivateKey();

                Cipher cipher = Cipher.getInstance( ALGORITHM_ASYM );
                cipher.init( Cipher.UNWRAP_MODE, privateKey );
                final Key symKey = cipher.unwrap( wrappedKey, ALGORITHM_SYM, Cipher.SECRET_KEY );

                cipher = Cipher.getInstance( ALGORITHM_SYM );
                cipher.init( Cipher.DECRYPT_MODE, symKey );
                transform( in, decryptedOutStream, cipher );
            }finally{
                in.close();
            }
        }finally{
            encryptedInpStream.close();
            decryptedOutStream.close();
        }
    }

    /**
     * Encrypt the plain text using public key.
     *
     * @param fText
     *            : original plain text
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public String encrypt( final String fText )
    {
        try {
            final Key aKey = getPublicKey();
            // encrypt the plain text using the public key
            final Cipher aCipher = Cipher.getInstance( ALGORITHM_ASYM );
            aCipher.init( Cipher.ENCRYPT_MODE, aKey );
            final byte[] aEncrypted = aCipher.doFinal( fText.getBytes() );
            // bytes zu Base64-String konvertieren
            final String aEncryptedBase64 = m_Base64Encoder.encode( aEncrypted );
            return aEncryptedBase64;
        } catch (final Exception fEx) {
            sm_Log.warn("Problem:", fEx);
            return null;
        }
    }

    /**
     * Decrypt text using private key.
     *
     * @param fTextEncryptedBase64
     *            :encrypted text
     * @return plain text
     * @throws java.lang.Exception
     */
    public String decrypt(final String fTextEncryptedBase64)
    {

        try {
            // BASE64 String zu Byte-Array
            final byte[] aTextEncrypted = m_Base64Decoder.decodeBuffer( fTextEncryptedBase64 );
            final PrivateKey aKey = getPrivateKey();
            final Cipher aCipher = Cipher.getInstance( ALGORITHM_ASYM );
            aCipher.init( Cipher.DECRYPT_MODE, aKey );
            // decrypt the text using the private key
            final byte[] aTextDecrypted_Bytes = aCipher.doFinal( aTextEncrypted );
            final String aTextDecrypted_String = new String(aTextDecrypted_Bytes);
            return aTextDecrypted_String;
        } catch (final Exception fEx) {
            sm_Log.warn("Problem:", fEx);
            return null;
        }

    }

    private PublicKey getPublicKey() throws Exception
    {
        return getPublicKey( getPublicKeyFile() );
    }

    private PrivateKey getPrivateKey() throws Exception
    {
        return getPrivateKey( getPrivateKeyFile() );
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

    private static void transform( final InputStream in, final OutputStream out, final Cipher cipher )
            throws IOException, GeneralSecurityException
    {
        final int blockSize = cipher.getBlockSize();
        final byte[] input = new byte[blockSize];
        final byte[] output = new byte[cipher.getOutputSize( blockSize )];
        int len;
        while( ( len = in.read( input ) ) == blockSize ){
            final int outLength = cipher.update( input, 0, blockSize, output );
            out.write( output, 0, outLength );
        }
        out.write( ( len > 0 ) ? cipher.doFinal( input, 0, len ) : cipher.doFinal() );
    }

    /** Generiere privaten und oeffentlichen RSA-Schluessel */
    public static void generateKeyPair( final String privateKeyFile, final String publicKeyFile, final int rsaKeySize )
            throws NoSuchAlgorithmException, IOException
    {
        generateKeyPair( new FileOutputStream( privateKeyFile ), new FileOutputStream( publicKeyFile ), rsaKeySize );
    }

    /**
     * Generiere privaten und oeffentlichen RSA-Schluessel (Streams werden mit
     * close() geschlossen)
     */
    public static void generateKeyPair( final OutputStream privateKeyFile, final OutputStream publicKeyFile, final int rsaKeySize )
            throws NoSuchAlgorithmException, IOException
    {
        try{
            final KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance( ALGORITHM_ASYM );
            keyPairGen.initialize( rsaKeySize );
            final KeyPair keyPair = keyPairGen.generateKeyPair();
            ObjectOutputStream out = new ObjectOutputStream( publicKeyFile );
            try{
                out.writeObject( keyPair.getPublic() );
            }finally{
                out.close();
            }
            out = new ObjectOutputStream( privateKeyFile );
            try{
                out.writeObject( keyPair.getPrivate() );
            }finally{
                out.close();
            }
        }finally{
            privateKeyFile.close();
            publicKeyFile.close();
        }
    }

}

// ############################################################################
