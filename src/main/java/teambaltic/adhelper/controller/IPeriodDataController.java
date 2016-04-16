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

import java.util.List;

import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.PeriodData;

// ############################################################################
public interface IPeriodDataController
{
    void init( boolean fCreateNewPeriod );

    List<PeriodData> getPeriodDataList();
    PeriodData getNewestPeriodData();

    boolean isFinished( IPeriod fPeriod );
    boolean isFinished( PeriodData fPeriodData );

    boolean isUploaded( IPeriod fPeriod );
    boolean isUploaded( PeriodData fPeriodData );
}

// ############################################################################
