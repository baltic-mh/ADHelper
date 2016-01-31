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
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.IClubMember;

// ############################################################################
/**
 * @author Mathias
 *
 *  Berechnet die Anzahl der zu erbringenden Arbeitsstunden pro Halbjahr
 *
 *  Das DueDate ist das Ende des zur Berechnung stehenden Halbjahres.
 */
public class DutyCalculator
{
    private static final long MIN_AGE_FOR_DUTY = 16L;
    private static final long MAX_AGE_FOR_DUTY = 60L;

    private static final float DUTYHOURS_PER_HALFYEAR = 3.0f;

    // ------------------------------------------------------------------------
    private final Halfyear m_InvoicingPeriod;
    public Halfyear getInvoicingPeriod(){ return m_InvoicingPeriod; }
    // ------------------------------------------------------------------------

    public DutyCalculator( final Halfyear fInvoicingPeriod )
    {
        m_InvoicingPeriod = fInvoicingPeriod;
    }

    public FreeFromDuty isFreeFromDuty( final IClubMember fMember )
    {
        final int aMemberID = fMember.getID();
        final FreeFromDuty aFreeByNoLongerMember = getFreeFromDutyByNoLongerMember(
                fMember, aMemberID );

        final LocalDate aBirthday  = fMember.getBirthday();
        final LocalDate aFreeByAgeFrom = aBirthday.plusYears( MAX_AGE_FOR_DUTY );
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

        final LocalDate aFreeByAgeUntil = aBirthday.plusYears( MIN_AGE_FOR_DUTY );
        if( aFreeByAgeUntil.compareTo( getInvoicingPeriod().getStart() ) > 0 ) {
            final FreeFromDuty aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.TOO_YOUNG );
            aFreeFromDuty.setUntil( aFreeByAgeUntil );
            if( aFreeByNoLongerMember != null
                    && aFreeByNoLongerMember.getFrom().compareTo( aFreeByAgeUntil ) > 0 ){
                aFreeFromDuty.setFrom( aFreeByNoLongerMember.getFrom() );
            }
            return aFreeFromDuty;
        }

        if( aFreeByNoLongerMember != null ){
            final FreeFromDuty aFreeFromDuty = new FreeFromDuty( aMemberID, REASON.NO_LONGER_MEMBER );
            aFreeFromDuty.setFrom( aFreeByNoLongerMember.getFrom() );
            return aFreeFromDuty;
        }
        return null;
    }

    private FreeFromDuty getFreeFromDutyByNoLongerMember( final IClubMember fMember, final int aMemberID )
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

    public float calculate( final FreeFromDuty fFreeFromDuty )
    {
        if( fFreeFromDuty == null ){
            return DUTYHOURS_PER_HALFYEAR;
        }
        final LocalDate aFreeFrom = fFreeFromDuty.getFrom();
        final LocalDate aFreeUntil = fFreeFromDuty.getUntil();
        final float aHoursToWork = calcDuty( getInvoicingPeriod(), aFreeFrom, aFreeUntil );
        return aHoursToWork;
    }

    private static float calcDuty(
            final Halfyear fInvoicingPeriod,
            final LocalDate fFreeFrom,
            final LocalDate fFreeUntil )
    {
        final LocalDate aInvoicingPeriodStart = fInvoicingPeriod.getStart();
        if( fFreeFrom != null && fFreeFrom.compareTo( aInvoicingPeriodStart ) <= 0 ){
            return 0.0f;
        }
        final LocalDate aInvoicingPeriodEnd = fInvoicingPeriod.getEnd();
        if( fFreeUntil != null && fFreeUntil.compareTo( aInvoicingPeriodEnd ) >= 0 ){
            return 0.0f;
        }

        int aNumMonthsDuty = 6;
        if( fFreeUntil != null
                && fFreeUntil.compareTo( aInvoicingPeriodStart ) >= 0
                && fFreeUntil.compareTo( aInvoicingPeriodEnd   ) <= 0
                ){
            // Befreiung liegt innerhalb des Abrechungszeitraumes
            final int aFreeUntilMonth  = fFreeUntil.getMonth().getValue();
            final int aInvoicingPeriodStartMonth = aInvoicingPeriodStart.getMonth().getValue();
            aNumMonthsDuty -= aFreeUntilMonth - aInvoicingPeriodStartMonth+1;
        }
        if( fFreeFrom != null
                && fFreeFrom.compareTo( aInvoicingPeriodStart ) >= 0
                && fFreeFrom.compareTo( aInvoicingPeriodEnd   ) <= 0
          ){
            // Befreiung liegt innerhalb des Abrechungszeitraumes
            final int aFreeFromMonth    = fFreeFrom.getMonth().getValue();
            final int aInvoicingPeriodEndMonth = aInvoicingPeriodEnd.getMonth().getValue();
            aNumMonthsDuty -= aInvoicingPeriodEndMonth - aFreeFromMonth+1;
        }
        return DUTYHOURS_PER_HALFYEAR * aNumMonthsDuty / 6;
    }
}

// ############################################################################
