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

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDuty.REASON;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.settings.IClubSettings;
import teambaltic.adhelper.utils.DateUtils;

// ############################################################################
/**
 * @author Mathias
 *
 *  Berechnet die Anzahl der zu erbringenden Arbeitsstunden pro Abbuchungszeitraum
 *
 */
public class DutyCalculator
{
    private static final Logger sm_Log = Logger.getLogger(DutyCalculator.class);

    // ------------------------------------------------------------------------
    private final IPeriod m_InvoicingPeriod;
    public IPeriod getInvoicingPeriod(){ return m_InvoicingPeriod; }
    // ------------------------------------------------------------------------

    private final IClubSettings m_ClubSettings;
    private IClubSettings getClubSettings(){ return m_ClubSettings; }

    public DutyCalculator(
            final IPeriod fInvoicingPeriod,
            final IClubSettings fClubSettings)
    {
        m_InvoicingPeriod   = fInvoicingPeriod;
        m_ClubSettings      = fClubSettings;
    }

    public FreeFromDuty isFreeFromDuty( final IClubMember fMember )
    {
        final int aMemberID = fMember.getID();
        final FreeFromDuty aFreeByNoLongerMember = getFreeFromDutyByNoLongerMember(
                fMember, aMemberID );
        final FreeFromDuty aFreeByNotYetMember = getFreeFromDutyByNotYetMember(
                fMember, aMemberID );

        final LocalDate aBirthday  = fMember.getBirthday();
        if( aBirthday == null ){
            sm_Log.warn("Kein Geburtsdatum gefunden für: "+fMember);
        } else {
            final LocalDate aFreeByAgeFrom = aBirthday.plusYears( getClubSettings().getMaxAgeForDuty() );
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

            final LocalDate aFreeByAgeUntil = aBirthday.plusYears( getClubSettings().getMinAgeForDuty() );
            if( aFreeByAgeUntil.compareTo( getInvoicingPeriod().getStart() ) > 0 ) {
                final FreeFromDuty aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.TOO_YOUNG );
                aFreeFromDuty.setUntil( aFreeByAgeUntil );
                if( aFreeByNoLongerMember != null
                        && aFreeByNoLongerMember.getFrom().compareTo( aFreeByAgeUntil ) > 0 ){
                    aFreeFromDuty.setFrom( aFreeByNoLongerMember.getFrom() );
                }
                return aFreeFromDuty;
            }
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
        final long aProtectedTime = getClubSettings().getProtectionTime();
        final LocalDate aFreeUntil = aMemberFrom.plusMonths( aProtectedTime ).minusDays( 1 );
        if( aFreeUntil.compareTo( getInvoicingPeriod().getStart() ) > 0 ) {
            final FreeFromDuty aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.DUTY_NOT_YET_EFFECTIVE );
            aFreeFromDuty.setUntil( aFreeUntil );
            return aFreeFromDuty;
        }
        return null;
    }

    public int calculateHoursToWork( final FreeFromDuty fFreeFromDuty )
    {
        if( fFreeFromDuty == null ){
            return getClubSettings().getDutyHoursPerInvoicePeriod();
        }
        final IPeriod aIP = getInvoicingPeriod();
        final int aMontsPerInvoicePeriod = getClubSettings().getMontsPerInvoicePeriod();
        if( DateUtils.getCoverageInMonths( fFreeFromDuty, aIP ) == aMontsPerInvoicePeriod ){
            return 0;
        }
        final LocalDate aFreeFrom = fFreeFromDuty.getFrom();
        final LocalDate aFreeUntil = fFreeFromDuty.getUntil();
        final int aHoursToWork = calcDuty( getClubSettings(), aIP, aFreeFrom, aFreeUntil );
        return aHoursToWork;
    }

    private static int calcDuty(
            final IClubSettings fClubSettings,
            final IPeriod fInvoicingPeriod,
            final LocalDate fFreeFrom,
            final LocalDate fFreeUntil )
    {
        final LocalDate aInvoicingPeriodStart = fInvoicingPeriod.getStart();
        final LocalDate aInvoicingPeriodEnd = fInvoicingPeriod.getEnd();

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
        return fClubSettings.getDutyHoursPerInvoicePeriod() * aNumMonthsInPeriod / 6;
    }

}

// ############################################################################
