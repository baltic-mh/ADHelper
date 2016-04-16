/**
 * PeriodDataController.java
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.utils.FileUtils;
import teambaltic.adhelper.utils.InvoicingPeriodFolderFilter;

// ############################################################################
public class PeriodDataController implements IPeriodDataController
{
    private static final Logger sm_Log = Logger.getLogger(PeriodDataController.class);

    private static final PeriodDataComparator PERIOD_COMPARATOR = new PeriodDataComparator();

    // ------------------------------------------------------------------------
    private final Path m_RootFolder;
    private Path getRootFolder(){ return m_RootFolder; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_FinishedFileName;
    private String getFinishedFileName(){ return m_FinishedFileName; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_UploadedFileName;
    private String getUploadedFileName(){ return m_UploadedFileName; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Map<IPeriod, PeriodData> m_PeriodDataMap;
    @Override
    public List<PeriodData> getPeriodDataList(){
        final ArrayList<PeriodData> aList = new ArrayList<>( m_PeriodDataMap.values() );
        Collections.sort( aList, PERIOD_COMPARATOR );
        return aList;
    }
    public PeriodData getPeriodData( final IPeriod fPeriod ){ return m_PeriodDataMap.get( fPeriod );}
    // ------------------------------------------------------------------------

    public PeriodDataController(
            final Path fRootFolder,
            final String fFinishedFileName,
            final String fUploadedFileName)
    {
        m_RootFolder = fRootFolder;
        m_FinishedFileName = fFinishedFileName;
        m_UploadedFileName = fUploadedFileName;
        m_PeriodDataMap = new HashMap<>();
    }

    @Override
    public void init(final boolean fCreateNewPeriod)
    {
        final File[] aDataFolders = findDataFolders( getRootFolder() );
        for( final File aDataFolder : aDataFolders ){
            final PeriodData aPeriodData = new PeriodData( aDataFolder.toPath() );
            m_PeriodDataMap.put( aPeriodData.getPeriod(), aPeriodData );
        }
        final PeriodData aNewestPeriodData = getNewestPeriodData();
        if( fCreateNewPeriod && isFinished( aNewestPeriodData ) ){
            final IPeriod aNextPeriod = aNewestPeriodData.getPeriod().createSuccessor();
            final Path aNextPeriodFolder = getRootFolder().resolve( aNextPeriod.toString() );
            sm_Log.info("Alle Datenverzeichnisse sind abgeschlossen. Erstelle neues: "+aNextPeriodFolder);
            final PeriodData aPeriodData = new PeriodData( aNextPeriodFolder );
            m_PeriodDataMap.put( aPeriodData.getPeriod(), aPeriodData );
        }
    }

    @Override
    public PeriodData getNewestPeriodData()
    {
        final List<PeriodData> aList = getPeriodDataList();
        Collections.sort( aList, PERIOD_COMPARATOR );
        final PeriodData aNewest = aList.get( aList.size()-1 );
        return aNewest;
    }

    @Override
    public boolean isFinished( final IPeriod fPeriod )
    {
        final PeriodData aPD = getPeriodData( fPeriod );
        return isFinished( aPD );
    }

    @Override
    public boolean isFinished( final PeriodData fPeriodData )
    {
        final String aFinishedFileName = getFinishedFileName();
        return exists( fPeriodData.getFolder(), aFinishedFileName );
    }

    @Override
    public boolean isUploaded( final IPeriod fPeriod )
    {
        final PeriodData aPD = getPeriodData( fPeriod );
        return isUploaded( aPD );
    }

    @Override
    public boolean isUploaded( final PeriodData fPeriodData )
    {
        return exists( fPeriodData.getFolder(), getUploadedFileName() );
    }

    private static boolean exists( final Path fFolder, final String aFileName )
    {
        final Path aFile = fFolder.resolve( aFileName );
        final boolean aFileExists = Files.exists( aFile );
        return aFileExists;
    }

    public Path getFolder( final IPeriod fPeriod )
    {
        final PeriodData aPeriodData = m_PeriodDataMap.get( fPeriod );
        return aPeriodData == null ? null : aPeriodData.getFolder();
    }

    private static File[] findDataFolders(final Path fRootFolder)
    {
        final InvoicingPeriodFolderFilter aFilter = InvoicingPeriodFolderFilter.createFilter_All();
        return FileUtils.getChildFolders( fRootFolder, aFilter );
    }

    private static class PeriodDataComparator implements Comparator<PeriodData>
    {
        @Override
        public int compare( final PeriodData fPD1, final PeriodData fPD2 )
        {
            final IPeriod aPeriod1 = fPD1.getPeriod();
            final IPeriod aPeriod2 = fPD2.getPeriod();
            final LocalDate aStart1 = aPeriod1.getStart();
            final LocalDate aStart2 = aPeriod2.getStart();

            if( aStart1 == null ) {
                if( aStart2 == null){
                    return 0;
                }
                return -1;
            }

            if( aStart2 == null ){
                return 1;
            }

            return aStart1.compareTo( aStart2 );

        }

    }
}

// ############################################################################
