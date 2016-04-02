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

import teambaltic.adhelper.model.ERole;

// ############################################################################
public interface ITransferController extends IShutdownListener
{
    void start() throws Exception;
    boolean isConnected();
    void upload  ( Path fFileToUpload ) throws Exception;
    boolean download( Path fFileToDownload );
    boolean uploadBillingData() throws Exception;
    void updateBaseDataFromServer( Path fFile_BaseData, ERole fRole ) throws Exception;
    void updateBillingDataFromServer() throws Exception;
}

// ############################################################################
