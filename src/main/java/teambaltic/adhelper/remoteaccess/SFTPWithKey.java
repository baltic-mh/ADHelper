/**
 * SFTPWithKey.java
 *
 * Created on 22.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.remoteaccess;


import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;


//############################################################################
public class SFTPWithKey implements IRemoteAccess
{
    // ------------------------------------------------------------------------
    private final StandardFileSystemManager m_FS_Manager;
    private StandardFileSystemManager getFS_Manager(){ return m_FS_Manager; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_RemoteRootDir;
    private String getRemoteRootDir(){ return m_RemoteRootDir; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_Server;
    public String getServer(){ return m_Server; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_User;
    public String getUser(){ return m_User; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final File m_KeyFile;
    private File getKeyFile(){ return m_KeyFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final int m_Port;
    private int getPort(){ return m_Port; }
    // ------------------------------------------------------------------------

    public SFTPWithKey(
            final String fRemoteRootDir,
            final String fServer,
            final int    fPort,
            final String fUser,
            final File fKeyFile )
    {
        m_FS_Manager = new StandardFileSystemManager();
        m_RemoteRootDir = fRemoteRootDir;
        m_Server    = fServer;
        m_Port      = fPort;
        m_User      = fUser;
        m_KeyFile   = fKeyFile;
    }

    @Override
    public void upload( final LocalRemotePathPair fPathPair ) throws Exception
    {
        final List<LocalRemotePathPair> aList = new ArrayList<>();
        aList.add( fPathPair );
        upload( aList );
    }
    @Override
    public void upload( final List<LocalRemotePathPair> fPathPairs ) throws Exception
    {
        final StandardFileSystemManager aFS_Manager = getFS_Manager();

        try {
            // Initializes the file manager
            getFS_Manager().init();

            for( final LocalRemotePathPair aPathPair : fPathPairs ){
                final FileObject aLocalObj = getLocalFileObject( aPathPair.getLocal(), aFS_Manager );
                final FileObject aRemoteObj = getRemoteFileObject( aPathPair.getRemote(), aFS_Manager );
                // upload der Datei
                aRemoteObj.copyFrom(aLocalObj, Selectors.SELECT_SELF);
            }


        }finally{
            aFS_Manager.close();
        }
    }

    @Override
    public void download( final LocalRemotePathPair fPathPair ) throws Exception
    {
        final List<LocalRemotePathPair> aList = new ArrayList<>();
        aList.add( fPathPair );
        download( aList );
    }
    @Override
    public void download( final List<LocalRemotePathPair> fPathPairs ) throws Exception
    {
        final StandardFileSystemManager aFS_Manager = getFS_Manager();

        try {
            // Initializes the file manager
            aFS_Manager.init();

            for( final LocalRemotePathPair aPathPair : fPathPairs ){
                final FileObject aLocalObj = getLocalFileObject( aPathPair.getLocal(), aFS_Manager );
                final FileObject aRemoteObj = getRemoteFileObject( aPathPair.getRemote(), aFS_Manager );
                //download der Datei
                aLocalObj.copyFrom(aRemoteObj, Selectors.SELECT_SELF);
            }

        }finally{
            aFS_Manager.close();
        }
    }

    @Override
    public void delete( final Path fRemotePath ) throws Exception
    {
        final StandardFileSystemManager aFS_Manager = getFS_Manager();

        try {
            // Initializes the file manager
            aFS_Manager.init();
            final FileObject aRemoteObj = getRemoteFileObject( fRemotePath, aFS_Manager );
            aRemoteObj.delete();

        }finally{
            aFS_Manager.close();
        }
    }

    @Override
    public boolean exists( final Path fRemotePath ) throws Exception
    {
        final StandardFileSystemManager aFS_Manager = getFS_Manager();

        try {
            // Initializes the file manager
            aFS_Manager.init();
            final FileObject aRemoteObj = getRemoteFileObject( fRemotePath, aFS_Manager );
            return aRemoteObj.exists();

        }finally{
            aFS_Manager.close();
        }
    }

    private FileObject getRemoteFileObject( final Path fRemotePath,
            final StandardFileSystemManager fFS_Manager )
            throws FileSystemException
    {
        final String aConnectionString = createConnectionString(
                fRemotePath.toString());
        final FileSystemOptions aDefaultOptions = createDefaultOptions( getKeyFile() );
        final FileObject aRemoteObj = fFS_Manager.resolveFile(
                aConnectionString, aDefaultOptions);
        return aRemoteObj;
    }

    private static FileObject getLocalFileObject( final Path fLocalPath, final StandardFileSystemManager fFS_Manager )
            throws FileSystemException
    {
        final FileObject localFile = fFS_Manager.resolveFile(fLocalPath.toFile().getAbsolutePath());
        return localFile;
    }

    private String createConnectionString(final String fRemotePath) {

        return String.format( "sftp://%s@%s:%d/%s/%s",
                getUser(), getServer(), getPort(), getRemoteRootDir(), fRemotePath );
    }

    private static FileSystemOptions createDefaultOptions(final File fKeyFile) throws FileSystemException{

        //create options for sftp
        final FileSystemOptions options = new FileSystemOptions();
        //ssh key
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
        //set root directory to user home
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, true);
        //timeout
        SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 10000);
        SftpFileSystemConfigBuilder.getInstance().setIdentities(options, new File[] {fKeyFile});

        return options;
    }

}
// ############################################################################
