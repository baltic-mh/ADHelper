/**
 * TransferController.java
 *
 * Created on 08.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import teambaltic.adhelper.remoteaccess.IRemoteAccess;
import teambaltic.adhelper.remoteaccess.LocalRemotePathPair;
import teambaltic.adhelper.utils.CheckSumCreator;
import teambaltic.adhelper.utils.CheckSumCreator.Type;
import teambaltic.adhelper.utils.CryptUtils;

// ############################################################################
public class TransferController
{
    private static final Logger sm_Log = Logger.getLogger(TransferController.class);

    private final CheckSumCreator   m_CheckSumCreator;
    private final CryptUtils        m_CryptUtils;
    private final IRemoteAccess     m_RemoteAccess;

    // ------------------------------------------------------------------------
    private final Path m_SandBox;
    private Path getSandBox(){ return m_SandBox; }
    // ------------------------------------------------------------------------

    public TransferController(
            final Path fSandBox,
            final File fPrivateKeyFile,
            final File fPublicKeyFile,
            final IRemoteAccess fRemoteAccess)
    {
        m_CheckSumCreator = new CheckSumCreator( Type.MD5 );
        m_CryptUtils = new CryptUtils(fPrivateKeyFile, fPublicKeyFile);
        m_SandBox = fSandBox;
        m_RemoteAccess = fRemoteAccess;
    }

    public void upload(final LocalRemotePathPair fPathPair)
    {
        final Path aLocal = fPathPair.getLocal();
        try{
            final Path aTarget = Files.copy( aLocal, Paths.get( getSandBox().toString(), aLocal.toFile().getName() ) );
            final Path aLocal_CheckSum = m_CheckSumCreator.process( aTarget );
            final Path aLocal_Crypted = m_CryptUtils.encrypt( aLocal );
//            m_RemoteAccess.upload( aPathPair_Crypted );
//            m_RemoteAccess.upload( aPathPair_CheckSum );
            // MD5-Datei und verschlüsselte Quelldatei hochladen.
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
    }
}

// ############################################################################
