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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

// ############################################################################
/**
 */
public class CryptUtils implements ICryptUtils
{
    private static final Logger sm_Log = Logger.getLogger(CryptUtils.class);

    private static final String ALGORITHM_ASYM = "RSA";
    private static final String ALGORITHM_SYM  = "AES";

    private static final String sm_Resource_KeyPriv ="crypt/private.key";
    private static final String sm_Resource_KeyPubl ="crypt/public.key";

    private final Base64 m_Base64;

    public CryptUtils(final File fPrivateKeyFile, final File fPublicKeyFile) throws Exception
    {
        m_Base64  = new Base64();
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
    private static void encrypt(
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
            final String aEncryptedBase64 = m_Base64.encodeToString( aEncrypted );
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
            final byte[] aTextEncrypted = m_Base64.decode( fTextEncryptedBase64 );
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

    private static PublicKey getPublicKey() throws Exception
    {
        final InputStream aIS = getResourceAsStream( sm_Resource_KeyPubl );
        final ObjectInputStream keyIn = new ObjectInputStream( aIS );
        PublicKey publicKey;
        try { publicKey = (PublicKey) keyIn.readObject(); } finally { keyIn.close(); }

        return publicKey;

    }

    private static PrivateKey getPrivateKey() throws Exception
    {
        final InputStream aIS = getResourceAsStream( sm_Resource_KeyPriv );

        final ObjectInputStream keyIn = new ObjectInputStream( aIS );
        PrivateKey Key;
        try { Key = (PrivateKey) keyIn.readObject(); } finally { keyIn.close(); }

        return Key;

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
     * Generiere privaten und oeffentlichen RSA-Schluessel
     *  (Streams werden mit close() geschlossen)
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

    public static void main(final String[] fArgs)
    {
        try{
            generateKeyPair( "private.key", "public.key", 2048);
        }catch( NoSuchAlgorithmException | IOException fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
    }

    private static InputStream getResourceAsStream( final String aResourceName )
    {
        InputStream aIS = CryptUtils.class.getResourceAsStream(aResourceName);
        if( aIS == null ){
            aIS = CryptUtils.class.getResourceAsStream("/"+aResourceName);
        }
        return aIS;
    }

}

// ############################################################################
