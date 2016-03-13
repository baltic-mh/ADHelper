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
    void init() throws Exception;
    void close();

    List<String> list( Path fRemotePath ) throws Exception;
    List<String> list( Path fRemotePath, String fExt ) throws Exception;
    List<String> listFolders( Path fRemotePath ) throws Exception;

    void delete   ( Path fRemotePath ) throws Exception;
    boolean exists( Path fRemotePath ) throws Exception;

    void upload  ( LocalRemotePathPair fPathPair ) throws Exception;
    void upload  ( List<LocalRemotePathPair> fPathPairs ) throws Exception;

    void download( LocalRemotePathPair fPathPair ) throws Exception;
    void download( List<LocalRemotePathPair> fPathPairs ) throws Exception;

}

// ############################################################################
