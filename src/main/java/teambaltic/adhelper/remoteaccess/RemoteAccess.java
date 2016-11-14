/**
 * TransferController.java
 *
 * Created on 29.02.2016
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.log4j.Logger;

import teambaltic.adhelper.model.settings.IRemoteAccessSettings;

// ############################################################################
public class RemoteAccess implements IRemoteAccess
{
    private static final Logger sm_Log = Logger.getLogger(RemoteAccess.class);

    private final IRemoteAccess m_RemoteEngine;

    public RemoteAccess(final String fFolderRootName, final IRemoteAccessSettings fRASettings) throws Exception
    {
        m_RemoteEngine = createRemoteEngine(fFolderRootName, fRASettings);
    }

    @Override
    public void init() throws Exception
    {
        m_RemoteEngine.init();
    }

    @Override
    public void close()
    {
        m_RemoteEngine.close();
    }

    @Override
    public List<String> list( final Path fRemotePath ) throws Exception
    {
        return list( fRemotePath, null );
    }

    @Override
    public List<String> list( final Path fRemotePath, final String fExt ) throws Exception
    {
        return m_RemoteEngine.list( fRemotePath, fExt );
    }

    @Override
    public List<String> listFolders( final Path fRemotePath ) throws Exception
    {
        return m_RemoteEngine.listFolders( fRemotePath );
    }

    @Override
    public boolean exists( final Path fRemotePath ) throws Exception
    {
        return m_RemoteEngine.exists( fRemotePath );
    }

    @Override
    public void delete( final Path fRemotePath ) throws Exception
    {
        try{
            m_RemoteEngine.delete( fRemotePath );
        }catch( final Exception fEx ){
            if( !m_RemoteEngine.exists( fRemotePath ) ){
                // Was wollen wir mehr!
            } else {
                throw fEx;
            }
        }
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
        m_RemoteEngine.upload( fPathPairs );
    }

    @Override
    public boolean download( final LocalRemotePathPair fPathPair ) throws Exception
    {
        final List<LocalRemotePathPair> aList = new ArrayList<>();
        aList.add( fPathPair );
        return m_RemoteEngine.download( aList );
    }
    @Override
    public boolean download( final List<LocalRemotePathPair> fPathPairs ) throws Exception
    {
        return m_RemoteEngine.download( fPathPairs );
    }

    private static IRemoteAccess createRemoteEngine(
            final String fFolderRootName, final IRemoteAccessSettings fRASettings)
            throws Exception
    {
        final String aProtocol = fRASettings.getProtocol();
        IRemoteAccess aRemoteEngine = null;
        switch( aProtocol ){
            case "sftp":
                aRemoteEngine = createRemoteEngine_SFTP( fFolderRootName, fRASettings );
                break;

            default:
                sm_Log.error("Unknown protocol for remote access: "+aProtocol);
                break;
        }
        return aRemoteEngine;
    }

    private static IRemoteAccess createRemoteEngine_SFTP(
            final String fFolderRootName,
            final IRemoteAccessSettings fSettings )
                    throws FileSystemException
    {
        final String aServer        = fSettings.getServerName();
        final int aPort             = fSettings.getPort();
        final String aUser          = fSettings.getUserName();
        final String aKeyFileName   = fSettings.getKeyFile();
        final File aKeyFile = Paths.get( fFolderRootName, aKeyFileName ).toFile();
        final SFTPWithKey aRemoteEngine = new SFTPWithKey( aServer, aPort, aUser, aKeyFile);
        return aRemoteEngine;
    }

}

// ############################################################################
