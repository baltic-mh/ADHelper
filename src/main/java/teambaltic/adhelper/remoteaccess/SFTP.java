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
package teambaltic.adhelper.remoteaccess;

import java.io.File;
import java.util.Properties;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

// ############################################################################
public class SFTP
{
    public static void main(final String[] args)
    {
        final SFTP aSFTP = new SFTP( "syniphos", "Test" );
//        final boolean aSuccess = aSFTP.upload( "Anmerkungen.txt" );
        try{
            aSFTP.init();
            final boolean aSuccess = aSFTP.download( "Anmerkungen.txt" );
            System.out.println( "Success: "+aSuccess );
        }catch( final FileSystemException fEx ){
            fEx.printStackTrace( System.err );;
        }
    }
    Properties props = new Properties();

    // ------------------------------------------------------------------------
    private final StandardFileSystemManager m_FS_Manager;
    private StandardFileSystemManager getFS_Manager(){ return m_FS_Manager; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_Server;
    public String getServer(){ return m_Server; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_User;
    public String getUser(){ return m_User; }

    public SFTP(final String fServer, final String fUser)
    {
        m_FS_Manager = new StandardFileSystemManager();
        m_Server = fServer.trim();
        m_User   = fUser.trim();

        props.setProperty( "password", "Ew2Kkdhesl");
        props.setProperty( "remoteDirectory", "Test-Daten/" );
        props.setProperty( "localDirectory.up", "./" );
        props.setProperty( "localDirectory.down", "./FTPDownload/" );
    }

    public void init() throws FileSystemException
    {
        // Initializes the file manager
        m_FS_Manager.init();
    }

    public boolean download( final String fileToDownload )
    {
        final String serverAddress = getServer();
        final String userId = getUser();
        final String password = props.getProperty( "password" ).trim();
        final String remoteDirectory = props.getProperty( "remoteDirectory" ).trim();
        final String localDirectory = props.getProperty( "localDirectory.down" ).trim();

        try{


            // Setup our SFTP configuration
            final FileSystemOptions opts = new FileSystemOptions();
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking( opts, "no" );
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot( opts, true );
            SftpFileSystemConfigBuilder.getInstance().setTimeout( opts, 10000 );

            // Create the SFTP URI using the host name, userid, password, remote
            // path and file name
            final String sftpUri = "sftp://" + userId + ":" + password + "@" + serverAddress + "/" + remoteDirectory
                    + fileToDownload;

            // Create local file object
            final String filepath = localDirectory + fileToDownload;
            final File file = new File( filepath );
            final FileObject localFile = getFS_Manager().resolveFile( file.getAbsolutePath() );

            // Create remote file object
            final FileObject remoteFile = getFS_Manager().resolveFile( sftpUri, opts );

            // Copy local file to sftp server
            localFile.copyFrom( remoteFile, Selectors.SELECT_SELF );
            System.out.println( "File download successful" );

        }catch( final Exception ex ){
            ex.printStackTrace();
            return false;
        }finally{
            getFS_Manager().close();
        }

        return true;
    }
    public boolean upload(final String fileToFTP)
    {


        try{

            final String serverAddress = props.getProperty( "serverAddress" ).trim();
            final String userId = props.getProperty( "userId" ).trim();
            final String password = props.getProperty( "password" ).trim();
            final String remoteDirectory = props.getProperty( "remoteDirectory" ).trim();
            final String localDirectory = props.getProperty( "localDirectory.up" ).trim();

            // check if the file exists
            final String filepath = localDirectory + fileToFTP;
            final File file = new File( filepath );
            if( !file.exists() )
                throw new RuntimeException( "Error. Local file not found" );

            // Initializes the file manager
            getFS_Manager().init();

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
            final FileObject localFile = getFS_Manager().resolveFile( file.getAbsolutePath() );

            // Create remote file object
            final FileObject remoteFile = getFS_Manager().resolveFile( sftpUri, opts );

            // Copy local file to sftp server
            remoteFile.copyFrom( localFile, Selectors.SELECT_SELF );
            System.out.println( "File upload successful" );

        }catch( final Exception ex ){
            ex.printStackTrace();
            return false;
        }finally{
            getFS_Manager().close();
        }

        return true;
    }

}

// ############################################################################
