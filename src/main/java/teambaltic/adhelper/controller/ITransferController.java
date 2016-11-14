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
import java.util.List;

import teambaltic.adhelper.model.ERole;

// ############################################################################
public interface ITransferController extends IShutdownListener
{
    void start() throws Exception;
    boolean isConnected();
    /**
     * @param fFileToUpload
     * @return Pfad zur intern erzeugten Check-Summen-Datei
     * @throws Exception
     */
    Path upload ( Path fFileToUpload ) throws Exception;
    /**
     * @param fFileToDownload
     * @return Pfad zur heruntergeladenen Check-Summen-Datei - null nichts heruntergeladen wurde
     */
    Path download( Path fFileToDownload );
    boolean uploadPeriodData() throws Exception;
    void updateBaseDataFromServer( Path fFile_BaseData, ERole fRole ) throws Exception;
    List<Path> updatePeriodDataFromServer() throws Exception;

    void setPeriodDataController( IPeriodDataController fPDC );
    boolean isActivePeriodModifiedLocally();
}

// ############################################################################
