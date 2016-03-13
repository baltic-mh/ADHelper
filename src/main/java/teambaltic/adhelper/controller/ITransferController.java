/**
 * ITransferController.java
 *
 * Created on 12.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.nio.file.Path;

// ############################################################################
public interface ITransferController
{
    void start() throws Exception;
    void shutdown() throws Exception;
    boolean isConnected();
    void upload  ( Path fFileToUpload );
    void download( Path fFileToDownload );
}

// ############################################################################
