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

import java.util.Collection;
import java.util.List;

import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.model.settings.IClubSettings;

// ############################################################################
/**
 * @author Mathias
 *
 *  Berechnet die zu bezahlende Arbeitsstunden pro Abbuchungszeitraum
 *
 *  Das DueDate ist das Ende des zur Berechnung stehenden Halbjahres.
 */
public class ChargeCalculator
{
    // ------------------------------------------------------------------------
    private final DutyCalculator m_DutyCalculator;
    public DutyCalculator getDutyCalculator(){ return m_DutyCalculator; }
    // ------------------------------------------------------------------------

    public ChargeCalculator(final IPeriod fPeriod, final IClubSettings fClubSettings)
    {
        m_DutyCalculator = new DutyCalculator( fPeriod, fClubSettings );
    }

    public IPeriod getPeriod()
    {
        return getDutyCalculator().getPeriod();
    }

    public DutyCharge calculate(final InfoForSingleMember fMemberInfo)
    {
        final WorkEventsAttended aWEA = fMemberInfo.getWorkEventsAttended();
        final IPeriod aPeriod = getDutyCalculator().getPeriod();
        final int aHoursWorked = aWEA == null ? 0 : aWEA.getTotalHoursWorked( aPeriod );

        final DutyCharge aCharge = fMemberInfo.getDutyCharge();
        aCharge.setHoursWorked( aHoursWorked );

        final Collection<FreeFromDuty> aFreeFromDutyItems = fMemberInfo.getFreeFromDutyItems();
        final int aHoursDue = getDutyCalculator().calculateHoursToWork( aFreeFromDutyItems );
        aCharge.setHoursDue( aHoursDue );

        final int aBalanceValue = aCharge.getBalance_Original();
        int aBalanceCharged = aBalanceValue + aHoursWorked - aHoursDue;
        if( aBalanceCharged < 0 ){
            aBalanceCharged = 0;
        }
        aCharge.setBalance_Charged( aBalanceCharged );
        aCharge.setBalance_ChargedAndAdjusted( aBalanceCharged );

        int aHoursToPay = aHoursDue - aHoursWorked - aBalanceValue;
        if( aHoursToPay < 0 ){
            aHoursToPay = 0;
        }
        aCharge.setHoursToPay( aHoursToPay );

        return aCharge;
    }

    public void balance( final DutyCharge fCharge )
    {
        final List <DutyCharge> aAllCharges = fCharge.getAllDutyCharges();
        int aHoursToPayTotal  = 0;
        int aBalanceTotal     = 0;
        for( final DutyCharge aCharge : aAllCharges ){
            aHoursToPayTotal += aCharge.getHoursToPay();
            aBalanceTotal    += aCharge.getBalance_Charged();
        }
        while( aBalanceTotal > 0 && aHoursToPayTotal > 0 ){
            for( final DutyCharge aCharge : aAllCharges ){
                int aChargedAndAdjusted = aCharge.getBalance_ChargedAndAdjusted();
                if( aChargedAndAdjusted <= 0 ){
                    continue;
                }
                if( aChargedAndAdjusted < 100 ){
                    aBalanceTotal       -= aChargedAndAdjusted;
                    aHoursToPayTotal    -= aChargedAndAdjusted;
                    aChargedAndAdjusted  = 0;
                } else if( aHoursToPayTotal < 100 ){
                    aBalanceTotal       -= aHoursToPayTotal;
                    aChargedAndAdjusted -= aHoursToPayTotal;
                    aHoursToPayTotal     = 0;
                } else {
                    aBalanceTotal       -= 100;
                    aHoursToPayTotal    -= 100;
                    aChargedAndAdjusted -= 100;
                }
                aCharge.setBalance_ChargedAndAdjusted( aChargedAndAdjusted );
                if(aHoursToPayTotal == 0){
                    break;
                }

            }
        }
        fCharge.setHoursToPayTotal( aHoursToPayTotal );
    }

}

// ############################################################################
