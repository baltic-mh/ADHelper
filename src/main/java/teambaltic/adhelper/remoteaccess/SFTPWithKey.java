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
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;


//############################################################################
public class SFTPWithKey implements IRemoteAccess
{
//    private static final Logger sm_Log = Logger.getLogger(SFTPWithKey.class);

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

    // ------------------------------------------------------------------------
    private StandardFileSystemManager m_FS_Manager;
    private StandardFileSystemManager getFS_Manager(){ return m_FS_Manager; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_PathPrefix;
    private String getPathPrefix(){ return m_PathPrefix; }
    // ------------------------------------------------------------------------

    public SFTPWithKey(
            final String fRemoteRootDir,
            final String fServer,
            final int    fPort,
            final String fUser,
            final File fKeyFile )
    {
        m_RemoteRootDir = fRemoteRootDir;
        m_Server    = fServer;
        m_Port      = fPort;
        m_User      = fUser;
        m_KeyFile   = fKeyFile;

        m_PathPrefix = createPathPrefix(getUser(), getServer(), getPort(), getRemoteRootDir());
    }

    @Override
    public void init() throws Exception
    {
        m_FS_Manager = initFileSystemManager();
    }

    @Override
    public void close()
    {
        final StandardFileSystemManager aFS_Manager = getFS_Manager();
        if( aFS_Manager == null ){
            return;
        }
        synchronized( aFS_Manager ){
            aFS_Manager.close();
        }
    }

    private static StandardFileSystemManager initFileSystemManager() throws FileSystemException
    {
        final StandardFileSystemManager aFS_Manager = new StandardFileSystemManager();
//        final File aTempDir = new File(System.getProperty("java.io.tmpdir"));
//        sm_Log.info("Tempdir is: "+aTempDir);
//        aFS_Manager.setTemporaryFileStore( new DefaultFileReplicator( aTempDir) );
//        aFS_Manager.setLogger( null );

        // Initializes the file manager
//        aFS_Manager.init();

        return aFS_Manager;
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
        synchronized( aFS_Manager ){
            try{
                aFS_Manager.init();
                for( final LocalRemotePathPair aPathPair : fPathPairs ){
                    final FileObject aLocalObj = getLocalFileObject( aPathPair.getLocal(), aFS_Manager );
                    final FileObject aRemoteObj = getRemoteFileObject( aPathPair.getRemote(), aFS_Manager );
                    // upload der Datei
                    aRemoteObj.copyFrom( aLocalObj, Selectors.SELECT_SELF );
                }
            }finally{
                aFS_Manager.close();
            }
        }
    }

    @Override
    public boolean download( final LocalRemotePathPair fPathPair ) throws Exception
    {
        final List<LocalRemotePathPair> aList = new ArrayList<>();
        aList.add( fPathPair );
        return download( aList );
    }
    @Override
    public boolean download( final List<LocalRemotePathPair> fPathPairs ) throws Exception
    {
        final StandardFileSystemManager aFS_Manager = getFS_Manager();

        synchronized( aFS_Manager ){
            try{
                aFS_Manager.init();
                boolean aDownLoaded = true;
                for( final LocalRemotePathPair aPathPair : fPathPairs ){
                    final FileObject aRemoteObj = getRemoteFileObject( aPathPair.getRemote(), aFS_Manager );
                    if( aRemoteObj.exists() ){
                        //download der Datei
                        final FileObject aLocalObj = getLocalFileObject( aPathPair.getLocal(), aFS_Manager );
                        aLocalObj.copyFrom( aRemoteObj, Selectors.SELECT_SELF );
                    } else {
                        // Ein kranker Apfel verdirbt den ganzen Korb :-/
                        aDownLoaded = false;
                    }
                }
                return aDownLoaded;
            }finally{
                aFS_Manager.close();
            }
        }

    }

    @Override
    public List<String> list( final Path fRemotePath ) throws Exception
    {
        return list( fRemotePath, (FileSelector)null );
    }
    @Override
    public List<String> list( final Path fRemotePath, final String fExt ) throws Exception
    {
        FileSelector selector = null;
        if( fExt != null && !"".equals( fExt )){
            selector = createExtensionSelector(fExt);
        }
        return list( fRemotePath, selector );

    }
    @Override
    public List<String> listFolders( final Path fRemotePath ) throws Exception
    {
        final FileSelector selector = createFolderSelector( getPathPrefix() );
        return list( fRemotePath, selector );

    }
    private List<String> list( final Path fRemotePath, final FileSelector fSelector ) throws Exception
    {
        final StandardFileSystemManager aFS_Manager = getFS_Manager();

        synchronized( aFS_Manager ){
            try{
                aFS_Manager.init();
                final FileObject aRemoteObj = getRemoteFileObject( fRemotePath, aFS_Manager );
                List<FileObject> aSelected = new ArrayList<>();

                if( fSelector != null ){
                    aRemoteObj.findFiles( fSelector, false, aSelected );
                }else{
                    final FileObject[] aChildren = aRemoteObj.getChildren();
                    aSelected = Arrays.asList( aChildren );
                }
                final List<String> aRelativePaths = new ArrayList<>( aSelected.size() );
                for( final FileObject aFileObject : aSelected ){
                    final URL aURL = aFileObject.getURL();
                    final String aRelativePath = makeRelative( getPathPrefix(), aURL );
                    aRelativePaths.add( aRelativePath );
                }
                return aRelativePaths;
            }finally{
                aFS_Manager.close();
            }
        }

    }

    @Override
    public void delete( final Path fRemotePath ) throws Exception
    {
        final StandardFileSystemManager aFS_Manager = getFS_Manager();

        synchronized( aFS_Manager ){
            try{
                aFS_Manager.init();
                final FileObject aRemoteObj = getRemoteFileObject( fRemotePath, aFS_Manager );
                aRemoteObj.delete();
            }finally{
                aFS_Manager.close();
            }
        }

    }

    @Override
    public boolean exists( final Path fRemotePath ) throws Exception
    {
        final StandardFileSystemManager aFS_Manager = getFS_Manager();

        synchronized( aFS_Manager ){
            try{
                aFS_Manager.init();
                final FileObject aRemoteObj = getRemoteFileObject( fRemotePath, aFS_Manager );
                return aRemoteObj.exists();
            }finally{
                aFS_Manager.close();
            }
        }

    }

    private FileObject getRemoteFileObject( final Path fRemotePath,
            final FileSystemManager fFS_Manager )
            throws FileSystemException
    {
        final String aConnectionString = createConnectionString(
                getPathPrefix(), fRemotePath.toString());
        final FileSystemOptions aDefaultOptions = createDefaultOptions( getKeyFile() );
        final FileObject aRemoteObj = fFS_Manager.resolveFile(
                aConnectionString, aDefaultOptions);
        return aRemoteObj;
    }

    private static FileObject getLocalFileObject( final Path fLocalPath, final FileSystemManager fFS_Manager )
            throws FileSystemException
    {
        final String aAbsolutePath = fLocalPath.toFile().getAbsolutePath();
        final String aAbsolutePathNormalized = FilenameUtils.normalize( aAbsolutePath );
        final FileObject localFile = fFS_Manager.resolveFile(aAbsolutePathNormalized);
        return localFile;
    }

    private static String createConnectionString( final String fPathPrefix, final String fRemotePath )
    {
        return String.format( "%s/%s", fPathPrefix, fRemotePath );
    }
    private static String createPathPrefix(
            final String fUser, final String fServer, final int fPort, final String fRemoteRootDir)
    {
        final String aPrefix = String.format( "sftp://%s@%s:%d/%s/",
                fUser, fServer, fPort, fRemoteRootDir );
        return aPrefix;
    }

    private static FileSystemOptions createDefaultOptions(final File fKeyFile) throws FileSystemException{

        //create options for sftp
        final FileSystemOptions options = new FileSystemOptions();
        //ssh key
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(options, "no");
        // set root directory to user home
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, true);
        //timeout
        SftpFileSystemConfigBuilder.getInstance().setTimeout(options, 10000);
        SftpFileSystemConfigBuilder.getInstance().setIdentities(options, new File[] {fKeyFile});

        return options;
    }

    private static String makeRelative( final String fPathPrefix, final URL fURL )
    {
        final String aExternalForm = fURL.toExternalForm();
        final String aLocalPath = aExternalForm.replaceFirst( fPathPrefix, "" );
        return aLocalPath;
    }

    private static FileSelector createFolderSelector(final String fRootFolder)
    {
        final FileSelector selector = new FileSelector() {

            private final String m_RootFolder = fRootFolder.replaceFirst( "/$", "" );
            @Override
            public boolean traverseDescendents( final FileSelectInfo fFileInfo ) throws Exception
            {
//                final FileObject aBaseFolder = fFileInfo.getBaseFolder();
//                final FileObject aFile = fFileInfo.getFile();
//                return aBaseFolder.equals( aFile );
                return fFileInfo.getDepth() == 0;
            }

            @Override
            public boolean includeFile( final FileSelectInfo fFileInfo ) throws Exception
            {
                final FileObject aFile = fFileInfo.getFile();
                final FileType aFileType = aFile.getType();
                if( !FileType.FOLDER.equals( aFileType ) ){
                    return false;
                }
                final FileName aFileName = aFile.getName();
                if(m_RootFolder.equals( aFileName.toString() )){
                    return false;
                }
                return true;
            }
        };
        return selector;
    }

    private static FileSelector createExtensionSelector(final String fExt)
    {
        final FileSelector selector = new FileSelector() {

            @Override
            public boolean traverseDescendents( final FileSelectInfo fFileInfo ) throws Exception
            {
//                final FileObject aBaseFolder = fFileInfo.getBaseFolder();
//                final FileObject aFile = fFileInfo.getFile();
//                return aBaseFolder.equals( aFile );
                return fFileInfo.getDepth() == 0;
            }

            @Override
            public boolean includeFile( final FileSelectInfo fFileInfo ) throws Exception
            {
                final FileName aFileName = fFileInfo.getFile().getName();
                final String aFriendlyURI = aFileName.getFriendlyURI();
                final boolean aMatches = aFriendlyURI.toLowerCase().endsWith( fExt.toLowerCase() );
                return aMatches;
            }
        };
        return selector;
    }

}
// ############################################################################
