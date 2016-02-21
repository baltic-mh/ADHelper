/**
 * SFTPUpload.java
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
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

// ############################################################################
public class SFTPUpload
{
    public static void main(final String[] args) {
        final SFTPUpload aUpLoader = new SFTPUpload();
        final boolean aSuccess = aUpLoader.startFTP( "Anmerkungen.txt" );
        System.out.println( "Success: "+aSuccess );
    }
    Properties props = new Properties();

    public SFTPUpload()
    {
        props.setProperty( "serverAddress", "diskstation" );
        props.setProperty( "userId", "mathias" );
        props.setProperty( "password", "stummel2");
        props.setProperty( "remoteDirectory", "FTP-Daten/" );
        props.setProperty( "localDirectory", "./" );
    }

    public boolean startFTP(final String fileToFTP)
    {

        final StandardFileSystemManager manager = new StandardFileSystemManager();

        try{

            final String serverAddress = props.getProperty( "serverAddress" ).trim();
            final String userId = props.getProperty( "userId" ).trim();
            final String password = props.getProperty( "password" ).trim();
            final String remoteDirectory = props.getProperty( "remoteDirectory" ).trim();
            final String localDirectory = props.getProperty( "localDirectory" ).trim();

            // check if the file exists
            final String filepath = localDirectory + fileToFTP;
            final File file = new File( filepath );
            if( !file.exists() )
                throw new RuntimeException( "Error. Local file not found" );

            // Initializes the file manager
            manager.init();

            // Setup our SFTP configuration
            final FileSystemOptions opts = new FileSystemOptions();
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking( opts, "no" );
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot( opts, true );
            SftpFileSystemConfigBuilder.getInstance().setTimeout( opts, 10000 );

            // Create the SFTP URI using the host name, userid, password, remote
            // path and file name
            final String sftpUri = "sftp://" + userId + ":" + password + "@" + serverAddress + "/" + remoteDirectory
                    + fileToFTP;

            // Create local file object
            final FileObject localFile = manager.resolveFile( file.getAbsolutePath() );

            // Create remote file object
            final FileObject remoteFile = manager.resolveFile( sftpUri, opts );

            // Copy local file to sftp server
            remoteFile.copyFrom( localFile, Selectors.SELECT_SELF );
            System.out.println( "File upload successful" );

        }catch( final Exception ex ){
            ex.printStackTrace();
            return false;
        }finally{
            manager.close();
        }

        return true;
    }


      }

// ############################################################################
