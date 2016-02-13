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
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.inout.BaseInfoReader;
import teambaltic.adhelper.inout.WorkEventReader;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.GlobalParameters;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IInvoicingPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.utils.DateUtils;

// ############################################################################
public class ADH_DataProvider extends ListProvider<InfoForSingleMember>
{
    private static final Logger sm_Log = Logger.getLogger(ADH_DataProvider.class);

    private final GlobalParameters m_GPs;

    private ChargeCalculator m_ChargeCalculator;

    // ------------------------------------------------------------------------
    private Collection<IClubMember> m_Members;
    public Collection<IClubMember> getMembers(){ return m_Members; }
    // ------------------------------------------------------------------------

    public ADH_DataProvider()
    {
        m_GPs = new GlobalParameters();
    }

    public void readBaseInfo( final File fFileToReadFrom )
    {
        clear();
        final BaseInfoReader aReader = new BaseInfoReader( fFileToReadFrom );
        try{
            m_Members = aReader.read( this );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
    }

    public void readWorkEvents( final File fFileToReadFrom )
    {
        final WorkEventReader aReader = new WorkEventReader( fFileToReadFrom );
        try{
            aReader.read( this );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
    }

    public void calculateDutyCharges(final IInvoicingPeriod fInvoicingPeriod)
    {
        final DutyCalculator aDC = new DutyCalculator( fInvoicingPeriod, m_GPs );
        m_ChargeCalculator = new ChargeCalculator( aDC );

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

    public void reportCharges()
    {
        sm_Log.info( "########################################################################" );
        final IInvoicingPeriod aInvoicingPeriod = m_ChargeCalculator.getInvoicingPeriod();
        sm_Log.info( "Abrechnungszeitraum: "+aInvoicingPeriod );
        for( final InfoForSingleMember aSingleInfo : getAll() ){
            reportCharge( aSingleInfo, true );
        }

    }

    public void reportCharge( final InfoForSingleMember fSingleInfo )
    {
        reportCharge( fSingleInfo, false );
    }
    public void reportCharge( final InfoForSingleMember fSingleInfo, final boolean fOnlyPayers )
    {
        final DutyCharge aCharge = fSingleInfo.getDutyCharge();
        final List<DutyCharge> aAllDutyCharges = aCharge.getAllDutyCharges();
        final FreeFromDuty aFreeFromDuty = fSingleInfo.getFreeFromDuty();
        final IClubMember aMember = fSingleInfo.getMember();
        if( fOnlyPayers && aMember.getLinkID() != 0 ){
            return;
        }
        sm_Log.info( "==========================================================================" );
        if( isFreeFromDutyEffective( aCharge, aFreeFromDuty ) ){
            sm_Log.info( aMember.getName() + ": AD befreit "+aFreeFromDuty);
        } else {
            sm_Log.info( String.format("%-27s  %6s %6s %6s %6s %6s %6s",
                    "Name", "Guth.", "Gearb.", "Pflicht", "Guth.II", "Zu zahl", "Gut.III" ));
            sm_Log.info( "--------------------------------------------------------------------------" );
            for( final DutyCharge aC : aAllDutyCharges ){
                final IClubMember aRelatedMember = getMember( aC.getMemberID() );
                sm_Log.info( String.format("%-27s %6.2f %6.2f   %6.2f  %6.2f  %6.2f  %6.2f",
                        aRelatedMember.getName(),
                        aC.getBalance_Original()/100.0,
                        aC.getHoursWorked()/100.0,
                        aC.getHoursDue()/100.0,
                        aC.getBalance_Charged()/100.0,
                        aC.getHoursToPay()/100.0,
                        aC.getBalance_ChargedAndAdjusted()/100.0
                        ) );
            }
            sm_Log.info( "--------------------------------------------------------------------------" );
            sm_Log.info(String.format( "Verbleibende Stunden zu zahlen:   %7.2f",
                    aCharge.getHoursToPayTotal()/100.0));
        }
    }

    private boolean isFreeFromDutyEffective(
            final DutyCharge fCharge,
            final FreeFromDuty fFreeFromDuty )
    {
        if( fFreeFromDuty == null ){
            return false;
        }
        if( fCharge.getAllDutyCharges().size() > 1 ){
            return false;
        }
        if( fCharge.getHoursToPayTotal() > 0 ){
            return false;
        }
        final IInvoicingPeriod aIP = m_ChargeCalculator.getInvoicingPeriod();
        return DateUtils.coversFreeFromDuty_InvoicingPeriod( fFreeFromDuty, aIP );
    }

    private IClubMember getMember( final int fMemberID )
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

}

// ############################################################################
