/**
 * FreeFromDutyCalculator.java
 *
 * Created on 04.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDuty.REASON;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.settings.IClubSettings;

// ############################################################################
public class FreeFromDutyCalculator
{
    // ------------------------------------------------------------------------
    private final IClubSettings m_ClubSettings;
    private IClubSettings getClubSettings(){ return m_ClubSettings; }
    // ------------------------------------------------------------------------

    public FreeFromDutyCalculator(final IClubSettings fClubSettings)
    {
        m_ClubSettings = fClubSettings;
    }

    public void populateFFDSetFromMemberData(
            final FreeFromDutySet fFreeFromDutySet,
            final IPeriod fInvoicingPeriod,
            final IClubMember fMember )
    {
        fFreeFromDutySet.addItem( createFFD_TooYoung( fInvoicingPeriod, fMember ) );
        fFreeFromDutySet.addItem( createFFD_TooOld  ( fInvoicingPeriod, fMember ) );
        fFreeFromDutySet.addItem( createFFD_NoLongerMember( fInvoicingPeriod, fMember ) );
        fFreeFromDutySet.addItem( createFFD_DutyNotYetEffective( fInvoicingPeriod, fMember ) );
    }

    private FreeFromDuty createFFD_TooYoung( final IPeriod fInvoicingPeriod, final IClubMember fMember )
    {
        final LocalDate aStart = fInvoicingPeriod.getStart();
        final LocalDate aBirthday = fMember.getBirthday();
        final int aMinAgeForDuty = getClubSettings().getMinAgeForDuty();
        final int aAge = aStart.getYear() - aBirthday.getYear();
        if( aAge - aMinAgeForDuty >= 3 ){
            // Wir hoffen, dass wir nie mehr als drei Jahre mit der Aberechnung
            // in Rückstand geraten!
            return null;
        }
        final LocalDate aFreeByAgeUntil = aBirthday.plusYears( aMinAgeForDuty );
        final FreeFromDuty aFFD = new FreeFromDuty( fMember.getID(), REASON.TOO_YOUNG );
        aFFD.setUntil( aFreeByAgeUntil );

        return aFFD;
    }

    private FreeFromDuty createFFD_TooOld( final IPeriod fInvoicingPeriod, final IClubMember fMember )
    {
        final LocalDate aStart = fInvoicingPeriod.getStart();
        final LocalDate aBirthday = fMember.getBirthday();
        final int aMaxAgeForDuty = getClubSettings().getMaxAgeForDuty();
        final int aAge = aStart.getYear() - aBirthday.getYear();
        if( aMaxAgeForDuty - aAge >= 3 ){
            // Wir hoffen, dass wir nie mehr als drei Jahre mit der Abrechnung
            // in Rückstand geraten!
            return null;
        }
        final FreeFromDuty aFFD = new FreeFromDuty( fMember.getID(), REASON.TOO_OLD );
        final LocalDate aFreeByAgeFrom = aBirthday.plusYears( aMaxAgeForDuty );
        aFFD.setFrom( aFreeByAgeFrom );

        return aFFD;
    }

    private static FreeFromDuty createFFD_NoLongerMember(
            final IPeriod fInvoicingPeriod, final IClubMember fMember )
    {
        final LocalDate aMemberUntil = fMember.getMemberUntil();
        if( aMemberUntil == null ){
            return null;
        }
        final int aMemberID = fMember.getID();
        final FreeFromDuty aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.NO_LONGER_MEMBER );
        aFreeFromDuty.setFrom( aMemberUntil.with( TemporalAdjusters.firstDayOfNextMonth() ) );
        return aFreeFromDuty;
    }

    private FreeFromDuty createFFD_DutyNotYetEffective(
            final IPeriod fInvoicingPeriod, final IClubMember fMember )
    {
        final LocalDate aMemberFrom = fMember.getMemberFrom();
        if( aMemberFrom == null ){
            return null;
        }
        final int aMemberID = fMember.getID();
        final long aProtectedTime = getClubSettings().getProtectionTime();
        final LocalDate aFreeUntil = aMemberFrom.plusMonths( aProtectedTime ).minusDays( 1 );
        final FreeFromDuty aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.DUTY_NOT_YET_EFFECTIVE );
        aFreeFromDuty.setUntil( aFreeUntil );
        return aFreeFromDuty;
    }

}

// ############################################################################
