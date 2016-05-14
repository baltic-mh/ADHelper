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
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.inout.BalanceReader;
import teambaltic.adhelper.inout.BaseDataReader;
import teambaltic.adhelper.inout.WorkEventReader;
import teambaltic.adhelper.inout.Writer;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.model.settings.IAllSettings;
import teambaltic.adhelper.model.settings.IClubSettings;

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
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IPeriodDataController m_PDC;
    public IPeriodDataController getPDC(){ return m_PDC; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private File m_BaseDataFile;
    public File getBaseDataFile(){ return m_BaseDataFile; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private List<IClubMember> m_Members;
    public List<IClubMember> getMembers(){ return m_Members; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private ChargeCalculator m_ChargeCalculator;
    public void setChargeCalculator( final ChargeCalculator fNewVal ){ m_ChargeCalculator = fNewVal; }
    public ChargeCalculator getChargeCalculator(){ return m_ChargeCalculator; }
    // ------------------------------------------------------------------------

    public ADH_DataProvider( final IPeriodDataController fPDC, final IAllSettings fSettings ) throws Exception
    {
        m_PDC = fPDC;
        m_AllSettings = fSettings;
    }

    public void init( final PeriodData fPeriodData ) throws Exception
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
        m_ChargeCalculator = createChargeCalculator( aPeriod );

        // Die Daten werden immer aus dem Verzeichnis des Abrechnungszeitraumes gelesen
        readBaseData( getPDC().getFile_BaseData( fPeriodData ) );
        readWorkEvents( getPDC().getFile_WorkEvents( fPeriodData ) );
        readBalances( getPDC().getFile_Balances( fPeriodData ), !getPDC().isFinished( fPeriodData ) );

        populateFreeFromDutySets( aPeriod );
        joinRelatives();

        calculateDutyCharges( aPeriod );
        balanceRelatives();

    }

    public void readBaseData( final Path fFileToReadFrom ) throws Exception
    {
        clear();
        m_BaseDataFile = fFileToReadFrom.toFile();
        final BaseDataReader aReader = new BaseDataReader( m_BaseDataFile );
        m_Members = aReader.read( this );
        Collections.sort( m_Members );
    }

    public void readWorkEvents( final Path fFileToReadFrom )
    {
        final WorkEventReader aReader = new WorkEventReader( fFileToReadFrom.toFile() );
        try{
            aReader.read( this );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
    }

    public void readBalances( final Path fFileToReadFrom, final boolean fTakePreviousBalanceValues )
    {
        final BalanceReader aReader = new BalanceReader( fFileToReadFrom.toFile(), fTakePreviousBalanceValues );
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

    public void writeToFiles() throws IOException
    {
        final Path fOutputFolder = getPeriodData().getFolder();
        final Writer aWriter = new Writer( this );
        aWriter.writeFiles( fOutputFolder );
    }

    public void writeToFile_WorkEvents()
    {
        Writer.writeToFile_WorkEvents( this, getPeriodData().getFolder() );
    }

}

// ############################################################################
