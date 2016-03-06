/**
 * IUploader.java
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

import java.nio.file.Path;
import java.util.List;

// ############################################################################
public interface IRemoteAccess
{
    void upload  ( final LocalRemotePathPair fPathPair ) throws Exception;
    void upload  ( final List<LocalRemotePathPair> fPathPairs ) throws Exception;

    void download( final LocalRemotePathPair fPathPair ) throws Exception;
    void download( final List<LocalRemotePathPair> fPathPairs ) throws Exception;

    void delete  ( final Path fRemotePath ) throws Exception;
    boolean exists( final Path fRemotePath ) throws Exception;
}

// ############################################################################
