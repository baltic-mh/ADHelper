/**
 * DutyCalculator.java
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

import java.time.LocalDate;

import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDuty.REASON;
import teambaltic.adhelper.model.GlobalParameters;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IInvoicingPeriod;

// ############################################################################
/**
 * @author Mathias
 *
 *  Berechnet die Anzahl der zu erbringenden Arbeitsstunden pro Abbuchungszeitraum
 *
 */
public class DutyCalculator
{
    // ------------------------------------------------------------------------
    private final IInvoicingPeriod m_InvoicingPeriod;
    public IInvoicingPeriod getInvoicingPeriod(){ return m_InvoicingPeriod; }
    // ------------------------------------------------------------------------

    private final GlobalParameters m_GPs;
    private GlobalParameters getGPs(){ return m_GPs; }

    public DutyCalculator(
            final IInvoicingPeriod fInvoicingPeriod,
            final GlobalParameters fGlobalParameters)
    {
        m_InvoicingPeriod   = fInvoicingPeriod;
        m_GPs               = fGlobalParameters;
    }

    public FreeFromDuty isFreeFromDuty( final IClubMember fMember )
    {
        final int aMemberID = fMember.getID();
        final FreeFromDuty aFreeByNoLongerMember = getFreeFromDutyByNoLongerMember(
                fMember, aMemberID );
        final FreeFromDuty aFreeByNotYetMember = getFreeFromDutyByNotYetMember(
                fMember, aMemberID );

        final LocalDate aBirthday  = fMember.getBirthday();
        final LocalDate aFreeByAgeFrom = aBirthday.plusYears( getGPs().getMaxAgeForDuty() );
        if( aFreeByAgeFrom.compareTo( getInvoicingPeriod().getEnd() ) < 0 ) {
            final FreeFromDuty aFreeFromDuty;
            if( aFreeByNoLongerMember != null
                    && aFreeByNoLongerMember.getFrom().compareTo( aFreeByAgeFrom ) < 0 ){
                aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.NO_LONGER_MEMBER );
                aFreeFromDuty.setFrom( aFreeByNoLongerMember.getFrom() );
            } else {
                aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.TOO_OLD );
                aFreeFromDuty.setFrom( aFreeByAgeFrom );
            }
            return aFreeFromDuty;
        }

        final LocalDate aFreeByAgeUntil = aBirthday.plusYears( getGPs().getMinAgeForDuty() );
        if( aFreeByAgeUntil.compareTo( getInvoicingPeriod().getStart() ) > 0 ) {
            final FreeFromDuty aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.TOO_YOUNG );
            aFreeFromDuty.setUntil( aFreeByAgeUntil );
            if( aFreeByNoLongerMember != null
                    && aFreeByNoLongerMember.getFrom().compareTo( aFreeByAgeUntil ) > 0 ){
                aFreeFromDuty.setFrom( aFreeByNoLongerMember.getFrom() );
            }
            return aFreeFromDuty;
        }

        // Normale Austritte - ohne Faxen!
        if( aFreeByNoLongerMember != null ){
            return aFreeByNoLongerMember;
        }

        // Normale Eintritte - ohne Faxen!
        if( aFreeByNotYetMember != null ){
            return aFreeByNotYetMember;
        }

        return null;
    }

    private FreeFromDuty getFreeFromDutyByNoLongerMember(
            final IClubMember fMember, final int aMemberID )
    {
        final LocalDate aMemberUntil = fMember.getMemberUntil();
        if( aMemberUntil == null ){
            return null;
        }
        if( aMemberUntil.compareTo( getInvoicingPeriod().getEnd() ) < 0 ) {
            final FreeFromDuty aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.NO_LONGER_MEMBER );
            aFreeFromDuty.setFrom( aMemberUntil );
            return aFreeFromDuty;
        }
        return null;
    }

    private FreeFromDuty getFreeFromDutyByNotYetMember(
            final IClubMember fMember, final int aMemberID )
    {
        final LocalDate aMemberFrom = fMember.getMemberFrom();
        if( aMemberFrom == null ){
            return null;
        }
        final LocalDate aFreeUntil = aMemberFrom.plusMonths( getGPs().getProtectedTime()-1 );
        if( aFreeUntil.compareTo( getInvoicingPeriod().getStart() ) > 0 ) {
            final FreeFromDuty aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.NOT_YET_MEMBER );
            aFreeFromDuty.setUntil( aFreeUntil );
            return aFreeFromDuty;
        }
        return null;
    }

    public int calculate( final FreeFromDuty fFreeFromDuty )
    {
        if( fFreeFromDuty == null ){
            return getGPs().getDutyHoursPerInvoicePeriod();
        }
        final LocalDate aFreeFrom = fFreeFromDuty.getFrom();
        final LocalDate aFreeUntil = fFreeFromDuty.getUntil();
        final int aHoursToWork = calcDuty( getGPs(), getInvoicingPeriod(), aFreeFrom, aFreeUntil );
        return aHoursToWork;
    }

    private static int calcDuty(
            final GlobalParameters fGPs,
            final IInvoicingPeriod fInvoicingPeriod,
            final LocalDate fFreeFrom,
            final LocalDate fFreeUntil )
    {
        final LocalDate aInvoicingPeriodStart = fInvoicingPeriod.getStart();
        if( fFreeFrom != null && fFreeFrom.compareTo( aInvoicingPeriodStart ) <= 0 ){
            return 0;
        }
        final LocalDate aInvoicingPeriodEnd = fInvoicingPeriod.getEnd();
        if( fFreeUntil != null && fFreeUntil.compareTo( aInvoicingPeriodEnd ) >= 0 ){
            return 0;
        }

        int aNumMonthsInPeriod =
                      aInvoicingPeriodEnd.getMonthValue()
                    - aInvoicingPeriodStart.getMonthValue()+1;
        if( fFreeUntil != null
                && fFreeUntil.compareTo( aInvoicingPeriodStart ) >= 0
                && fFreeUntil.compareTo( aInvoicingPeriodEnd   ) <= 0
                ){
            // Befreiung liegt innerhalb des Abrechungszeitraumes
            final int aFreeUntilMonth  = fFreeUntil.getMonth().getValue();
            final int aInvoicingPeriodStartMonth = aInvoicingPeriodStart.getMonth().getValue();
            aNumMonthsInPeriod -= aFreeUntilMonth - aInvoicingPeriodStartMonth+1;
        }
        if( fFreeFrom != null
                && fFreeFrom.compareTo( aInvoicingPeriodStart ) >= 0
                && fFreeFrom.compareTo( aInvoicingPeriodEnd   ) <= 0
          ){
            // Befreiung liegt innerhalb des Abrechungszeitraumes
            final int aFreeFromMonth    = fFreeFrom.getMonth().getValue();
            final int aInvoicingPeriodEndMonth = aInvoicingPeriodEnd.getMonth().getValue();
            aNumMonthsInPeriod -= aInvoicingPeriodEndMonth - aFreeFromMonth+1;
        }
        return fGPs.getDutyHoursPerInvoicePeriod() * aNumMonthsInPeriod / 6;
    }

}

// ############################################################################
