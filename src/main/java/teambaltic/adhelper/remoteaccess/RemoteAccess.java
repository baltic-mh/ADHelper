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
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.settings.IRemoteAccessSettings;

// ############################################################################
public class RemoteAccess implements IRemoteAccess
{
    private static final Logger sm_Log = Logger.getLogger(RemoteAccess.class);

    private final IRemoteAccess m_RemoteEngine;

    public RemoteAccess(final IRemoteAccessSettings fRASettings)
    {
        m_RemoteEngine = createRemoteEngine(fRASettings);
    }

    @Override
    public List<URL> list( final Path fRemotePath ) throws Exception
    {
        return list( fRemotePath, null );
    }

    @Override
    public List<URL> list( final Path fRemotePath, final String fExt ) throws Exception
    {
        return m_RemoteEngine.list( fRemotePath, fExt );
    }

    @Override
    public boolean exists( final Path fRemotePath ) throws Exception
    {
        return m_RemoteEngine.exists( fRemotePath );
    }

    @Override
    public void delete( final Path fRemotePath ) throws Exception
    {
        m_RemoteEngine.delete( fRemotePath );
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
    public void download( final LocalRemotePathPair fPathPair ) throws Exception
    {
        final List<LocalRemotePathPair> aList = new ArrayList<>();
        aList.add( fPathPair );
        m_RemoteEngine.download( aList );
    }
    @Override
    public void download( final List<LocalRemotePathPair> fPathPairs ) throws Exception
    {
        m_RemoteEngine.download( fPathPairs );
    }

    private static IRemoteAccess createRemoteEngine(final IRemoteAccessSettings fRASettings)
    {
        final String aProtocol = fRASettings.getProtocol();
        IRemoteAccess aRemoteEngine = null;
        switch( aProtocol ){
            case "sftp":
                aRemoteEngine = createRemoteEngine_SFTP( fRASettings );
                break;

            default:
                sm_Log.error("Unknown protocol for remote access: "+aProtocol);
                break;
        }
        return aRemoteEngine;
    }

    private static IRemoteAccess createRemoteEngine_SFTP( final IRemoteAccessSettings fSettings )
    {
        final String aServer        = fSettings.getServerName();
        final int aPort             = fSettings.getPort();
        final String aUser          = fSettings.getUserName();
        final String aRemoteRootDir = fSettings.getRemoteRootDir();
        final String aKeyFileName   = fSettings.getKeyFile();
        final File aKeyFile = new File(aKeyFileName);
        final SFTPWithKey aRemoteEngine = new SFTPWithKey(aRemoteRootDir, aServer, aPort, aUser, aKeyFile);
        return aRemoteEngine;
    }

}

// ############################################################################
