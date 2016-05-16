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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.inout.Writer;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.settings.IAppSettings;
import teambaltic.adhelper.model.settings.IUserSettings;
import teambaltic.adhelper.utils.FileUtils;
import teambaltic.adhelper.utils.InvoicingPeriodFolderFilter;

// ############################################################################
public class PeriodDataController implements IPeriodDataController
{
    private static final Logger sm_Log = Logger.getLogger(PeriodDataController.class);

    private static final PeriodDataComparator PERIOD_COMPARATOR = new PeriodDataComparator();

    // ------------------------------------------------------------------------
    private final IAppSettings m_AppSettings;
    private IAppSettings getAppSettings(){ return m_AppSettings; }
    private Path getRootFolder(){ return getAppSettings().getFolder_Data(); }
    private String getFileName_BaseData(){return getAppSettings().getFileName_BaseData(); }
    private String getFileName_Finished(){ return getAppSettings().getFileName_Finished(); }
    private String getFileName_Uploaded(){ return getAppSettings().getFileName_Uploaded(); }
    private String getFileName_WorkEvents(){return getAppSettings().getFileName_WorkEvents(); }
    private String getFileName_Balances(){return getAppSettings().getFileName_Balances(); }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IUserSettings m_UserSettings;
    private IUserSettings getUserSettings(){ return m_UserSettings; }
    private String getUserInfo(){ return getUserSettings().getDecoratedEMail(); }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private PeriodData m_ActivePeriod;
    @Override
    public PeriodData getActivePeriod(){ return m_ActivePeriod;}
    private void setActivePeriod( final PeriodData fActivePeriod ){
        if( m_ActivePeriod != null && m_ActivePeriod != fActivePeriod ){
            m_ActivePeriod.setActive( false );
        }
        m_ActivePeriod = fActivePeriod;
        m_ActivePeriod.setActive( true );
    }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final List<PeriodData> m_PeriodDataList;
    @Override
    public List<PeriodData> getPeriodDataList(final EPeriodDataSelector fSelector){
        final List<PeriodData> aFilteredList = new ArrayList<>();
        switch( fSelector ){
            case ALL:
                return m_PeriodDataList;

            case FINISHED:
                for( final PeriodData aPeriodData : m_PeriodDataList ){
                    if( isFinished( aPeriodData.getPeriod() ) ){
                        aFilteredList.add( aPeriodData );
                    }
                }
                break;

            case NOT_FINISHED:
                for( final PeriodData aPeriodData : m_PeriodDataList ){
                    if( !isFinished( aPeriodData.getPeriod() ) ){
                        aFilteredList.add( aPeriodData );
                    }
                }
                break;

            case UPLOADED:
                for( final PeriodData aPeriodData : m_PeriodDataList ){
                    if( isUploaded( aPeriodData.getPeriod() ) ){
                        aFilteredList.add( aPeriodData );
                    }
                }
                break;

            case UPLOADABLE:
                for( final PeriodData aPeriodData : getPeriodDataList(EPeriodDataSelector.FINISHED) ){
                    if( !isUploaded( aPeriodData.getPeriod() ) ){
                        aFilteredList.add( aPeriodData );
                    }
                }
                break;

            default:
                break;
        }
        return aFilteredList;
    }
    public PeriodData getPeriodData( final IPeriod fPeriod ){
        for( final PeriodData aPeriodData : m_PeriodDataList ){
            if( aPeriodData.getPeriod().equals( fPeriod )){
                return aPeriodData;
            }
        }
        return null;
    }

    @Override
    public PeriodData getPeriodData( final LocalDate fDate ){
        for( final PeriodData aPeriodData : m_PeriodDataList ){
            if( aPeriodData.getPeriod().isWithinMyPeriod( fDate )){
                return aPeriodData;
            }
        }
        return null;
    }
    // ------------------------------------------------------------------------

    public PeriodDataController( final IAppSettings fAppSettings, final IUserSettings fUserSettings )
    {
        m_AppSettings       = fAppSettings;
        m_UserSettings      = fUserSettings;
        m_PeriodDataList    = new ArrayList<>();
    }

    @Override
    public void init()
    {
        final File[] aDataFolders = findDataFolders( getRootFolder() );
        for( final File aDataFolder : aDataFolders ){
            final PeriodData aPeriodData = new PeriodData( aDataFolder.toPath() );
            m_PeriodDataList.add( aPeriodData );
        }
        Collections.sort( m_PeriodDataList, PERIOD_COMPARATOR );
        final PeriodData aNewestPeriodData = getNewestPeriodData();
        if( !isFinished( aNewestPeriodData ) ){
            setActivePeriod( aNewestPeriodData );
        }
    }

    @Override
    public void createNewPeriod() throws IOException
    {
        final PeriodData aNewestPeriodData = getNewestPeriodData();
        if( !isFinished( aNewestPeriodData ) ){
            setActivePeriod( aNewestPeriodData );
            return;
        }
        final IPeriod aNextPeriod = aNewestPeriodData.getPeriod().createSuccessor();
        final Path aNextPeriodFolder = getRootFolder().resolve( aNextPeriod.toString() );
        sm_Log.info("Erstelle neues Datenverzeichnis: "+aNextPeriodFolder);
        final PeriodData aNewlyCreatedPeriodData = new PeriodData( aNextPeriodFolder );
        m_PeriodDataList.add( aNewlyCreatedPeriodData );
        setActivePeriod( aNewlyCreatedPeriodData );
        populateNewPeriodFolder( getAppSettings(), aNewlyCreatedPeriodData);
    }

    private void populateNewPeriodFolder(
            final IAppSettings fAppSettings,
            final PeriodData fNewlyCreatedPeriodData )
                    throws IOException
    {
        final Path aFolder_Data = fAppSettings.getFolder_Data();
        final String aFileName_BaseData = fAppSettings.getFileName_BaseData();
        final File aBaseDataFile = aFolder_Data.resolve( aFileName_BaseData ).toFile();
        final PeriodData aPredecessor = getPredecessor( fNewlyCreatedPeriodData );

        copyBasicFilesToFolder(aBaseDataFile, fNewlyCreatedPeriodData.getFolder(), aPredecessor.getFolder(),
                fAppSettings.getFileName_WorkEvents(), fAppSettings.getFileName_Balances());
    }

    private static void copyBasicFilesToFolder(
            final File fBaseDataFile,
            final Path fPeriodDataFolder,
            final Path fPredecessorFolder,
            final String fFileName_WorkEvents,
            final String fFileName_Balances )
                    throws IOException
    {
        if( !Files.exists( fPeriodDataFolder )){
            Files.createDirectories( fPeriodDataFolder );
        }

        FileUtils.copyFileToFolder( fBaseDataFile, fPeriodDataFolder );

        final File aWEF = fPredecessorFolder.resolve( fFileName_WorkEvents ).toFile();
        FileUtils.copyFileToFolder( aWEF, fPeriodDataFolder );
        final String aNewName_WEF = FileUtils.getFileNameWithPostfixAppended( aWEF, "_old" );
        FileUtils.copyFileToFolder( aWEF, fPeriodDataFolder, aNewName_WEF );

        final File aBalancesFile = fPredecessorFolder.resolve( fFileName_Balances ).toFile();
        final Path aShiftedBalanceFile = FileUtils.copyFileToFolder( aBalancesFile, fPeriodDataFolder );
        final String aNewName_BF = FileUtils.getFileNameWithPostfixAppended( aBalancesFile, "_old" );
        FileUtils.copyFileToFolder( aBalancesFile, fPeriodDataFolder, aNewName_BF );
        Writer.shiftBalanceValues( aShiftedBalanceFile.toFile() );
    }

    private PeriodData getNewestPeriodData()
    {
        final List<PeriodData> aList = getPeriodDataList( EPeriodDataSelector.ALL );
        final PeriodData aNewest = aList.get( aList.size()-1 );
        return aNewest;
    }

    @Override
    public PeriodData getSuccessor( final IPeriod fPeriod )
    {
        final PeriodData aPD = getPeriodData( fPeriod );
        return getSuccessor( aPD );
    }

    @Override
    public PeriodData getSuccessor( final PeriodData fPeriodData )
    {
        final int aIndex = m_PeriodDataList.indexOf( fPeriodData );
        if( aIndex == m_PeriodDataList.size() -1 ){
            return null;
        }
        return m_PeriodDataList.get( aIndex+1 );
    }

    @Override
    public PeriodData getPredecessor( final IPeriod fPeriod )
    {
        final PeriodData aPD = getPeriodData( fPeriod );
        return getPredecessor( aPD );
    }

    @Override
    public PeriodData getPredecessor( final PeriodData fPeriodData )
    {
        final int aIndex = m_PeriodDataList.indexOf( fPeriodData );
        if( aIndex <= 0 ){
            return null;
        }
        return m_PeriodDataList.get( aIndex-1 );
    }

    @Override
    public boolean isFinished( final LocalDate fDate )
    {
        if( fDate == null ){
            return false;
        }
        final PeriodData aPD = getPeriodData( fDate );
        return isFinished( aPD );
    }

    @Override
    public boolean isFinished( final IPeriod fPeriod )
    {
        if( fPeriod == null ){
            return false;
        }
        final PeriodData aPD = getPeriodData( fPeriod );
        return isFinished( aPD );
    }

    @Override
    public boolean isFinished( final PeriodData fPeriodData )
    {
        if( fPeriodData == null ){
            return false;
        }
        final String aFinishedFileName = getFileName_Finished();
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
        return exists( fPeriodData.getFolder(), getFileName_Uploaded() );
    }

    private static boolean exists( final Path fFolder, final String aFileName )
    {
        final Path aFile = fFolder.resolve( aFileName );
        final boolean aFileExists = Files.exists( aFile );
        return aFileExists;
    }

    public Path getFolder( final IPeriod fPeriod )
    {
        final PeriodData aPeriodData = getPeriodData( fPeriod );
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

    @Override
    public Path getFile_BaseData( final PeriodData fPeriodData )
    {
        return fPeriodData.getFolder().resolve( getFileName_BaseData() );
    }
    @Override
    public Path getFile_WorkEvents( final PeriodData fPeriodData )
    {
        return fPeriodData.getFolder().resolve( getFileName_WorkEvents() );
    }
    @Override
    public Path getFile_Balances( final PeriodData fPeriodData )
    {
        return fPeriodData.getFolder().resolve( getFileName_Balances() );
    }

    @Override
    public boolean isActivePeriodFinished()
    {
        return isFinished( getActivePeriod() );
    }
    @Override
    public void setActivePeriodToFinished() throws IOException
    {
        final PeriodData aActivePeriod = getActivePeriod();
        if( isFinished( aActivePeriod ) ){
            sm_Log.warn( "Periode ist schon abgeschlossen: "+ aActivePeriod);
            return;
        }
        setFinished( aActivePeriod );
    }
    private void setFinished( final PeriodData fPeriodData ) throws IOException
    {
        sm_Log.info( "Periode wird abgeschlossen: "+fPeriodData );
        FileUtils.writeFinishedFile( fPeriodData.getFolder().resolve( getFileName_Finished() ), getUserInfo() );
    }
}

// ############################################################################
