/**
 * ADH_DataProvider.java
 *
 * Created on 30.01.2016
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
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.inout.BalanceReader;
import teambaltic.adhelper.inout.BaseDataReader;
import teambaltic.adhelper.inout.Exporter;
import teambaltic.adhelper.inout.WorkEventReader;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.model.settings.IAllSettings;
import teambaltic.adhelper.model.settings.IAppSettings;
import teambaltic.adhelper.model.settings.IClubSettings;
import teambaltic.adhelper.model.settings.IUserSettings;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class ADH_DataProvider extends ListProvider<InfoForSingleMember>
{
    private static final Logger sm_Log = Logger.getLogger(ADH_DataProvider.class);

    // ------------------------------------------------------------------------
    private final IAllSettings m_AllSettings;
    private IClubSettings getClubSettings(){ return m_AllSettings.getClubSettings(); }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private File m_BaseDataFile;
    public File getBaseDataFile(){ return m_BaseDataFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private File m_WorkEventFile;
    public File getWorkEventFile(){ return m_WorkEventFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private File m_BalanceFile;
    public File getBalanceFile(){ return m_BalanceFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private List<IClubMember> m_Members;
    public List<IClubMember> getMembers(){ return m_Members; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_UserInfo;
    private final String getUserInfo(){ return m_UserInfo; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private ChargeCalculator m_ChargeCalculator;
    public void setChargeCalculator( final ChargeCalculator fNewVal ){ m_ChargeCalculator = fNewVal; }
    public ChargeCalculator getChargeCalculator(){ return m_ChargeCalculator; }
    public IPeriod getInvoicingPeriod(){ return m_ChargeCalculator == null ? null : m_ChargeCalculator.getInvoicingPeriod();}
    // ------------------------------------------------------------------------

    public ADH_DataProvider(final IAllSettings fSettings) throws Exception
    {
        m_AllSettings = fSettings;
        final IUserSettings aUserSettings = m_AllSettings.getUserSettings();
        m_UserInfo = aUserSettings.getDecoratedEMail();
    }

    public void init() throws Exception
    {
        final IAppSettings aAppSettings = m_AllSettings.getAppSettings();
        // Bestimme das Verzeichnis mit den neuesten Abrechnungsdaten
        final Path aDataFolder = getDataFolder();
        final File aFolderOfNewestInvoicingPeriod =
                FileUtils.determineNewestInvoicingPeriodFolder( aDataFolder, getFinishedFileName() );
        // Bestimme daraus den folgenden Abrechnungszeitraum:
        final Halfyear aLatestProcessed = Halfyear.create( aFolderOfNewestInvoicingPeriod.getName() );
        final Path aOutputFolder = getOutputFolder( aLatestProcessed );
        final boolean aIsOutputFinished = isOutputFinished( aOutputFolder );
        final IPeriod aInvoicingPeriod = aIsOutputFinished
                ? Halfyear.next( aLatestProcessed )
                : aLatestProcessed;
        m_ChargeCalculator = createChargeCalculator( aInvoicingPeriod );

        readBaseData( aAppSettings.getFile_BaseData() );
        // Das WorkEventFile liegt immer im Verzeichnis mit den neuesten Abrechnungsdaten
        readWorkEvents( new File(aFolderOfNewestInvoicingPeriod, aAppSettings.getFileName_WorkEvents() ) );
        // Das BalanceFile liegt immer im Verzeichnis mit den neuesten Abrechnungsdaten
        readBalances( new File(aFolderOfNewestInvoicingPeriod, aAppSettings.getFileName_Balances() ), !aIsOutputFinished );

        populateFreeFromDutySets( aInvoicingPeriod );
        joinRelatives();

        calculateDutyCharges( aInvoicingPeriod );
        balanceRelatives();

        // Erst mal die Daten sichern:
        export( false );
    }

    public void readBaseData( final Path fFileToReadFrom ) throws Exception
    {
        clear();
        m_BaseDataFile = fFileToReadFrom.toFile();
        final BaseDataReader aReader = new BaseDataReader( m_BaseDataFile );
        m_Members = aReader.read( this );
        Collections.sort( m_Members );
    }

    public void readWorkEvents( final File fFileToReadFrom )
    {
        m_WorkEventFile = fFileToReadFrom;
        final WorkEventReader aReader = new WorkEventReader( fFileToReadFrom );
        try{
            aReader.read( this );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
    }

    public void readBalances( final File fFileToReadFrom, final boolean fTakePreviousBalanceValues )
    {
        m_BalanceFile = fFileToReadFrom;
        final BalanceReader aReader = new BalanceReader( fFileToReadFrom, fTakePreviousBalanceValues );
        try{
            aReader.read( this );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: "+ fEx.getMessage() );
        }
    }

    public void calculateDutyCharges(final IPeriod fInvoicingPeriod)
    {
        for( final InfoForSingleMember aSingleInfo : getAll() ){
            m_ChargeCalculator.calculate( aSingleInfo );
        }
    }

    public ChargeCalculator createChargeCalculator( final IPeriod fInvoicingPeriod )
    {
        final ChargeCalculator aChargeCalculator = new ChargeCalculator( fInvoicingPeriod, getClubSettings() );
        return aChargeCalculator;
    }

    private void populateFreeFromDutySets(final IPeriod fInvoicingPeriod)
    {
        final FreeFromDutyCalculator aFFDCalculator = new FreeFromDutyCalculator( getClubSettings() );
        for( final InfoForSingleMember aSingleInfo : getAll() ){
            final IClubMember aMember = aSingleInfo.getMember();
            final FreeFromDutySet aFFDSet = aSingleInfo.getFreeFromDutySet();
            aFFDCalculator.populateFFDSetFromMemberData( aFFDSet, fInvoicingPeriod, aMember );
        }
    }

    public void joinRelatives()
    {
        // Verbinden der Verwandten:
        for( final InfoForSingleMember aSingleInfo : getAll() ){
            final IClubMember aMember = aSingleInfo.getMember();
            final int aLinkID = aMember.getLinkID();
            if( aLinkID == 0 ){
                continue;
            }
            final IClubMember aLinkedToMember = getMember( aLinkID );
            if( aLinkedToMember == null ){
                sm_Log.error( String.format( "%s: Die LinkID %d existiert nicht als Mitgliedsnummer!",
                        aMember, aLinkID) );
                continue;
            }
            if( sm_Log.isDebugEnabled() ){
                sm_Log.debug( "Verbinde : "+aMember.getName() +" => "+aLinkedToMember.getName());
            }
            final DutyCharge aCharge = aSingleInfo.getDutyCharge();
            final DutyCharge aChargeToLinkTo = getDutyCharge( aLinkID );
            aChargeToLinkTo.addRelative( aCharge );

            final WorkEventsAttended aWEA = aSingleInfo.getWorkEventsAttended();
            if( aWEA == null ){
                continue;
            }
            WorkEventsAttended aWEAToLinkTo = getWorkEventsAttended( aLinkID );
            if( aWEAToLinkTo == null ){
                aWEAToLinkTo = new WorkEventsAttended( aLinkID );
            }
            aWEAToLinkTo.addRelative( aWEA );
        }
    }

    public void balanceRelatives()
    {
        for( final InfoForSingleMember aSingleInfo : getAll() ){
            final IClubMember aMember = aSingleInfo.getMember();
            if( aMember.getLinkID() != 0 ){
                continue;
            }
            final DutyCharge aCharge = aSingleInfo.getDutyCharge();
            m_ChargeCalculator.balance( aCharge );
        }
    }

    public IClubMember getMember( final int fMemberID )
    {
        final InfoForSingleMember aInfo = get( fMemberID );
        return aInfo.getMember();
    }

    private DutyCharge getDutyCharge( final int fMemberID )
    {
        final InfoForSingleMember aInfo = get( fMemberID );
        return aInfo.getDutyCharge();
    }

    private WorkEventsAttended getWorkEventsAttended( final int fMemberID )
    {
        final InfoForSingleMember aInfo = get( fMemberID );
        return aInfo.getWorkEventsAttended();
    }

    public String getMemberName( final int fID )
    {
        final InfoForSingleMember aInfoForSingleMember = get( fID );
        final IClubMember aMember = aInfoForSingleMember.getMember();
        return aMember.getName();
    }

    public void export( final boolean fSetFinished ) throws IOException
    {
        final Path fOutputFolder = getOutputFolder();
        final Exporter aExporter = new Exporter( this, getFinishedFileName() );
        aExporter.export( fOutputFolder, getUserInfo(), fSetFinished );
    }

    public Path getOutputFolder()
    {
        return getOutputFolder( getInvoicingPeriod() );
    }

    private Path getOutputFolder( final IPeriod fInvoicingPeriod )
    {
        final Path aOutputFolder = getDataFolder().resolve( fInvoicingPeriod.toString() );
        return aOutputFolder;
    }

    private Path getDataFolder()
    {
        final IAppSettings aAppSettings = m_AllSettings.getAppSettings();
        final Path aDataFolder = aAppSettings.getFolder_Data();
        return aDataFolder;
    }
    private String getFinishedFileName()
    {
        final IAppSettings aAppSettings = m_AllSettings.getAppSettings();
        return aAppSettings.getFileName_Finished();
    }
    private String getUploadedFileName()
    {
        final IAppSettings aAppSettings = m_AllSettings.getAppSettings();
        return aAppSettings.getFileName_Uploaded();
    }
    // ------------------------------------------------------------------------


    public boolean isOutputFinished()
    {
        return isOutputFinished( getOutputFolder() );

    }
    private boolean isOutputFinished(final Path fOutputFolder)
    {
        final String aFinishedFileName = getFinishedFileName();
        final Path aFinishedFile = fOutputFolder.resolve( aFinishedFileName );
        return Files.exists( aFinishedFile );
    }

    public void exportWorkEvents()
    {
        Exporter.exportWorkEvents( this, getOutputFolder() );
    }

    public File[] getNotUploadedFolders()
    {
        final File[] aFolders_NotUploaded = FileUtils.getFolders_NotUploaded( getDataFolder(), getFinishedFileName(), getUploadedFileName() );
        return aFolders_NotUploaded;
    }

}

// ############################################################################
