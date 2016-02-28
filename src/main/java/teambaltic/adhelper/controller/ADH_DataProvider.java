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
import java.nio.file.Path;
import java.util.Collection;

import org.apache.log4j.Logger;

import teambaltic.adhelper.inout.BalanceReader;
import teambaltic.adhelper.inout.BaseInfoReader;
import teambaltic.adhelper.inout.Exporter;
import teambaltic.adhelper.model.ApplicationProperties;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.GlobalParameters;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class ADH_DataProvider extends ListProvider<InfoForSingleMember>
{
    private static final Logger sm_Log = Logger.getLogger(ADH_DataProvider.class);

    private final GlobalParameters m_GPs;

    // ------------------------------------------------------------------------
    private File m_BaseInfoFile;
    public File getBaseInfoFile(){ return m_BaseInfoFile; }
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
    private Collection<IClubMember> m_Members;
    public Collection<IClubMember> getMembers(){ return m_Members; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private ChargeCalculator m_ChargeCalculator;
    public ChargeCalculator getChargeCalculator(){ return m_ChargeCalculator; }
    public IPeriod getInvoicingPeriod(){ return m_ChargeCalculator == null ? null : m_ChargeCalculator.getInvoicingPeriod();}
    // ------------------------------------------------------------------------

    public ADH_DataProvider() throws Exception
    {
        m_GPs = new GlobalParameters( ApplicationProperties.INSTANCE.getDataFolderName() );
    }

    public void init()
    {
        final String aDataFoldername = ApplicationProperties.INSTANCE.getDataFolderName();
        // Bestimme das Verzeichnis mit den neuesten Abrechnungsdaten
        final File aFolderOfNewestInvoicingPeriod = FileUtils.determineNewestInvoicingPeriodFolder( new File(aDataFoldername) );
        // Bestimme daraus den folgenden Abrechnungszeitraum:
         final Halfyear aLatestProcessed = Halfyear.create( aFolderOfNewestInvoicingPeriod.getName() );

         final IPeriod aInvoicingPeriod = Halfyear.next( aLatestProcessed );
        m_ChargeCalculator = createChargeCalculator( aInvoicingPeriod );

        // Das BaseInfoFile liegt immer im Verzeichnis "Daten"
        readBaseInfo( new File(aDataFoldername, ApplicationProperties.INSTANCE.getFileName_BaseInfo() ) );
        // Das WorkEventFile liegt immer im Verzeichnis mit den neuesten Abrechnungsdaten
        readWorkEvents( new File(aFolderOfNewestInvoicingPeriod, ApplicationProperties.INSTANCE.getFileName_WorkEvents() ) );
        // Das BalanceFile liegt immer im Verzeichnis mit den neuesten Abrechnungsdaten
        readBalances( new File(aFolderOfNewestInvoicingPeriod, ApplicationProperties.INSTANCE.getFileName_Balances() ) );

        joinRelatives();

        calculateDutyCharges( aInvoicingPeriod );
        balanceRelatives();

    }

    public void readBaseInfo( final File fFileToReadFrom )
    {
        clear();
        m_BaseInfoFile = fFileToReadFrom;
        final BaseInfoReader aReader = new BaseInfoReader( m_BaseInfoFile );
        try{
            m_Members = aReader.read( this );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
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

    public void readBalances( final File fFileToReadFrom )
    {
        m_BalanceFile = fFileToReadFrom;
        final BalanceReader aReader = new BalanceReader( fFileToReadFrom );
        try{
            aReader.read( this );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: "+ fEx.getMessage() );
        }
    }

    public void calculateDutyCharges(final IPeriod fInvoicingPeriod)
    {
        final DutyCalculator aDC = m_ChargeCalculator.getDutyCalculator();

        for( final InfoForSingleMember aSingleInfo : getAll() ){
            final IClubMember aMember = aSingleInfo.getMember();
            FreeFromDuty aFreeFromDuty = aSingleInfo.getFreeFromDuty();
            // TODO Wenn jemand schon eine AD-Befreiung eingetragen hat,
            //      diese aber erst in ferner Zukunft wirksam wird UND
            //      für den aktuellen Abrechnungszeitraum eine andere
            //      Befreiung gelten würde (z.B. "zu jung"), würde das hier
            //      momentan nicht berücksichtig werden!
            if( aFreeFromDuty == null ){
                aFreeFromDuty = aDC.isFreeFromDuty( aMember );
                aSingleInfo.setFreeFromDuty( aFreeFromDuty );
            }
            final WorkEventsAttended aWorkEventsAttended = aSingleInfo.getWorkEventsAttended();
            final DutyCharge aDutyCharge = aSingleInfo.getDutyCharge();
            m_ChargeCalculator.calculate( aDutyCharge, aWorkEventsAttended, aFreeFromDuty );
        }
    }
    private ChargeCalculator createChargeCalculator( final IPeriod fInvoicingPeriod )
    {
        final ChargeCalculator aChargeCalculator = new ChargeCalculator( fInvoicingPeriod, m_GPs );
        return aChargeCalculator;
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

    public void export(final Path fOutputFolder)
    {
        final Exporter aExporter = new Exporter( this );
        aExporter.export( fOutputFolder );
    }

}

// ############################################################################
