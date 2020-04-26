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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.inout.AdjustmentReader;
import teambaltic.adhelper.inout.BalanceReader;
import teambaltic.adhelper.inout.BaseDataReader;
import teambaltic.adhelper.inout.WorkEventReader;
import teambaltic.adhelper.inout.Writer;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.BalanceHistory;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.model.settings.IAllSettings;
import teambaltic.adhelper.model.settings.IClubSettings;
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
    private PeriodData m_PeriodData;
    public  PeriodData getPeriodData(){ return m_PeriodData; }
    public IPeriod getPeriod(){ return m_PeriodData == null ? null : m_PeriodData.getPeriod();}
    void setPeriodData(final PeriodData fPeriodData ){ m_PeriodData = fPeriodData ; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IPeriodDataController m_PDC;
    public IPeriodDataController getPDC(){ return m_PDC; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private File m_BaseDataFile;
    public File getBaseDataFile(){ return m_BaseDataFile; }
    private void setBaseDataFile( final File fBaseDataFile ){ m_BaseDataFile = fBaseDataFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private List<IClubMember> m_Members;
    public List<IClubMember> getMembers(){ return m_Members; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final ChargeManager m_ChargeManager;
    private ChargeManager getChargeManager(){ return m_ChargeManager; }
    // ------------------------------------------------------------------------

    /**
     * Sucht aus der Gesamtmenge aller Daten nur die heraus, die über den
     * kompletten Zeitraum der angegebenen Periode noch Mitglied sind.
     * @param fPeriod
     * @return
     */
    public List<InfoForSingleMember> getAllWithEffectiveMemberShip(final IPeriod fPeriod)
    {
        final List<InfoForSingleMember> aAllWithinPeriod = new ArrayList<>();
        for( final InfoForSingleMember aInfoForSingleMember : getAll() ){
            final IClubMember aThisMember = aInfoForSingleMember.getMember();
            final boolean aIsMembershipEffective = isMembershipEffective( aThisMember, fPeriod );
            if( aIsMembershipEffective ){
                aAllWithinPeriod.add( aInfoForSingleMember );
            }
        }
        return aAllWithinPeriod;
    }

    static boolean isMembershipEffective( final IClubMember fMember, final IPeriod fPeriod )
    {
        final LocalDate aMemberUntil = fMember.getMemberUntil();
        final boolean aIsMembershipEffective = aMemberUntil == null || fPeriod.isAfterMyStart( aMemberUntil );
        return aIsMembershipEffective;
    }

    public ADH_DataProvider( final IPeriodDataController fPDC, final IAllSettings fSettings ) throws Exception
    {
        m_PDC           = fPDC;
        m_AllSettings   = fSettings;
        m_ChargeManager = new ChargeManager( getClubSettings() );
    }

    public void init( final PeriodData fPeriodData ) throws Exception
    {
        init( fPeriodData, 0 );
    }
    public void init( final PeriodData fPeriodData, final int fOnlyID ) throws Exception
    {
        if( fPeriodData.equals( m_PeriodData ) ){
            return;
        }
        m_PeriodData = fPeriodData;
        final IPeriod aPeriod = fPeriodData.getPeriod();
        if( aPeriod == null ){
            return;
        }
        sm_Log.info( "Einlesen der Daten für Zeitraum: "+fPeriodData );

        // Die Daten werden immer aus dem Verzeichnis des Abrechnungszeitraumes gelesen
        readBaseData   ( getPDC().getFile_BaseData( fPeriodData ), aPeriod, fOnlyID );
        readWorkEvents ( getPDC().getFile_WorkEvents ( fPeriodData ) );
        readAdjustments( getPDC().getFile_Adjustments( fPeriodData ) );
        readBalances( fPeriodData );

        populateFreeFromDutySets( aPeriod );
        joinRelatives();
        balance( aPeriod );

    }

    private void readBalances(final PeriodData fPeriodData) throws Exception
    {
        Path aFileToReadFrom = getPDC().getFile_BalanceHistory( fPeriodData );
        final boolean aOld = !Files.exists( aFileToReadFrom );
        if( aOld ){
            aFileToReadFrom = getPDC().getFile_Balances( fPeriodData );
            aFileToReadFrom = FileUtils.getPathWithPostfixAppended( aFileToReadFrom, "_old" );
        }
        readBalances( aFileToReadFrom, aOld );
        if( aOld ){
            final Path aFile_BalanceHistories = Writer.writeToFile_BalanceHistories( this, fPeriodData.getFolder() );
            // Die Datei soll nicht älter sein, als die Datei, aus der die Daten gelesen wurden:
            aFile_BalanceHistories.toFile().setLastModified( aFileToReadFrom.toFile().lastModified() );
        }
    }
    public void readBaseData( final Path fFileToReadFrom, final IPeriod fPeriod, final int fOnlyID ) throws Exception
    {
        clear();
        setBaseDataFile( fFileToReadFrom.toFile() );
        final BaseDataReader aReader = new BaseDataReader( getBaseDataFile() );
        m_Members = aReader.read( this, fPeriod, fOnlyID );
        Collections.sort( m_Members );
    }

    public void readWorkEvents( final Path fFileToReadFrom ) throws Exception
    {
        final WorkEventReader aReader = new WorkEventReader( fFileToReadFrom.toFile() );
        aReader.read( this );
    }

    public void readAdjustments( final Path fFileToReadFrom ) throws Exception
    {
        if( !Files.exists( fFileToReadFrom )){
            return;
        }
        final AdjustmentReader aReader = new AdjustmentReader( fFileToReadFrom.toFile() );
        aReader.read( this );
    }

    public void readBalances( final Path fFileToReadFrom, final boolean fOld ) throws Exception
    {
        final BalanceReader aReader = new BalanceReader( fFileToReadFrom.toFile(), fOld );
        aReader.read( this );
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
            final InfoForSingleMember aLinkedToMember = get( aLinkID );
            if( aLinkedToMember == null ){
                sm_Log.error( String.format( "%s: Die LinkID %d existiert nicht als Mitgliedsnummer!",
                        aMember, aLinkID) );
                continue;
            }
            if( sm_Log.isDebugEnabled() ){
                sm_Log.debug( "Verbinde : "+aMember.getName() +" => "+aLinkedToMember.toString() );
            }
            aLinkedToMember.addRelative( aSingleInfo );

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

    public void balance(final IPeriod fPeriod)
    {
        final IPeriod aNextPeriod = fPeriod.createSuccessor();
        for( final InfoForSingleMember aSingleInfo : getAll() ){
            balance( fPeriod, aSingleInfo, aNextPeriod );
        }
    }
    private void balance(
            final IPeriod               fPeriod,
            final InfoForSingleMember   fMemberInfo,
            final IPeriod               fNextPeriod )
    {
        getChargeManager().balance( fPeriod, fMemberInfo );
        final Balance aBalanceOfThisPeriod = fMemberInfo.getBalance( fPeriod );
        final int aValue_ChargedAndAdjusted = aBalanceOfThisPeriod.getValue_ChargedAndAdjusted();
        final Balance aStartBalanceForNextPeriod = new Balance(
              fMemberInfo.getID(), fNextPeriod, aValue_ChargedAndAdjusted );
        fMemberInfo.addBalance( aStartBalanceForNextPeriod, true );
    }

    public IClubMember getMember( final int fMemberID )
    {
        final InfoForSingleMember aInfo = get( fMemberID );
        return aInfo == null ? null : aInfo.getMember();
    }

    public BalanceHistory getBalanceHistory( final int fMemberID )
    {
        final InfoForSingleMember aInfo = get( fMemberID );
        return aInfo.getBalanceHistory();
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

    public void writeToFiles() throws IOException
    {
        final Path fOutputFolder = getPeriodData().getFolder();
        final Writer aWriter = new Writer( this, getClubSettings() );
        aWriter.writeFiles( fOutputFolder );
    }

    public void writeToFile_WorkEvents()
    {
        Writer.writeToFile_WorkEvents( this, getPeriodData().getFolder() );
    }

    public void writeToFile_Adjustments()
    {
        Writer.writeToFile_Adjustments( this, getPeriodData().getFolder() );
    }

    public boolean isMemberInCurrentPeriod( final int fMemberID )
    {
        final IPeriod aCurrentPeriod = getPeriod();
        final InfoForSingleMember aInfoForSingleMember = get( fMemberID );
        final IClubMember aMember = aInfoForSingleMember.getMember();
        final LocalDate aMemberUntil = aMember.getMemberUntil();
        if( aMemberUntil != null && aCurrentPeriod.isBeforeMyStart( aMemberUntil ) ){
            return false;
        }
        return true;
    }

}

// ############################################################################
