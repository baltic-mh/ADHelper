/**
 * IDataFolderController.java
 *
 * Created on 07.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.PeriodData;

// ############################################################################
public interface IPeriodDataController
{
    public enum EPeriodDataSelector {
        ALL,
        FINISHED,
        NOT_FINISHED,
        UPLOADED,
        UPLOADABLE;
    }

    void init();
    PeriodData createNewPeriod() throws IOException;

    List<PeriodData> getPeriodDataList( EPeriodDataSelector fSelector );
    PeriodData getActivePeriod();
    PeriodData getNewestPeriodData();
    PeriodData getPeriodData( LocalDate fDate );
    Path getActivePeriodFolder();
    void removeActivePeriodFolder() throws Exception;

    boolean isFinished( PeriodData fPeriodData );
    boolean isFinished( IPeriod fPeriod );
    boolean isFinished( LocalDate fDate );

    boolean isActivePeriodFinished();
    void setActivePeriodToFinished() throws IOException;

    boolean isUploaded( IPeriod fPeriod );
    boolean isUploaded( PeriodData fPeriodData );

    PeriodData getSuccessor( IPeriod fPeriod );
    PeriodData getSuccessor( PeriodData fPeriodData );

    PeriodData getPredecessor( IPeriod fPeriod );
    PeriodData getPredecessor( PeriodData fPeriodData );

    Path getFile_BaseData( PeriodData fPeriodData );
    Path getFile_WorkEvents( PeriodData fPeriodData );
    Path getFile_Balances( PeriodData fPeriodData );
    void removeDataFolderOrphans( List<Path> fPeriodFoldersKnownOnServer );

}

// ############################################################################
