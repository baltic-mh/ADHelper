/**
 * FTPUpload.java
 *
 * Created on 20.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 * A program that demonstrates how to upload files from local computer
 * to a remote FTP server using Apache Commons Net API.
 * @author www.codejava.net
 */

// ############################################################################
public class FTPUpload
{
    public static void main( final String[] args )
    {
        final String server = "diskstation";
        final int port = 21;
        final String user = "mathias";
        final String pass = "stummel2";

        final FTPClient ftpClient = new FTPClient();
        try{

            ftpClient.connect( server, port );
            ftpClient.login( user, pass );
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType( FTP.BINARY_FILE_TYPE );

            // APPROACH #1: uploads first file using an InputStream
            final File firstLocalFile = new File( "Anmerkungen.txt" );

            final String firstRemoteFile = "/homes/Mathias/Anmerkungen.txt";
            InputStream inputStream = new FileInputStream( firstLocalFile );

            System.out.println( "Start uploading first file" );
            final boolean done = ftpClient.storeFile( firstRemoteFile, inputStream );
            inputStream.close();
            if( done ){
                System.out.println( "The first file is uploaded successfully." );
            }

            // APPROACH #2: uploads second file using an OutputStream
            final File secondLocalFile = new File( "Ausgabe.txt" );
            final String secondRemoteFile = "/homes/Mathias/Ausgabe.txt";
            inputStream = new FileInputStream( secondLocalFile );

            System.out.println( "Start uploading second file" );
            final OutputStream outputStream = ftpClient.storeFileStream( secondRemoteFile );
            final byte[] bytesIn = new byte[4096];
            int read = 0;

            while( ( read = inputStream.read( bytesIn ) ) != -1 ){
                outputStream.write( bytesIn, 0, read );
            }
            inputStream.close();
            outputStream.close();

            final boolean completed = ftpClient.completePendingCommand();
            if( completed ){
                System.out.println( "The second file is uploaded successfully." );
            }

        }catch( final IOException ex ){
            System.out.println( "Error: " + ex.getMessage() );
            ex.printStackTrace();
        }finally{
            try{
                if( ftpClient.isConnected() ){
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            }catch( final IOException ex ){
                ex.printStackTrace();
            }
        }
    }

}
// ############################################################################
