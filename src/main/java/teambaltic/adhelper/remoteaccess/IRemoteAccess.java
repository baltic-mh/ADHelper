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

// ############################################################################
public interface IRemoteAccess
{
    void upload  ( final Path fLocalPath, final Path fRemotePath ) throws Exception;
    void download( final Path fRemotePath, final Path fLocalPath ) throws Exception;
    void delete  ( final Path fRemotePath ) throws Exception;
    boolean exists( final Path fRemotePath ) throws Exception;
}

// ############################################################################
