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

import teambaltic.adhelper.model.Balance;
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

    public ChargeCalculator( final IClubSettings fClubSettings )
    {
        m_DutyCalculator = new DutyCalculator( fClubSettings );
    }

    public DutyCharge calculate( final InfoForSingleMember fMemberInfo, final IPeriod fPeriod )
    {
        final int aMemberID = fMemberInfo.getID();
        final DutyCharge aCharge = new DutyCharge( aMemberID );

        final WorkEventsAttended aWEA = fMemberInfo.getWorkEventsAttended();
        final int aHoursWorked = aWEA == null ? 0 : aWEA.getTotalHoursWorked( fPeriod );

        aCharge.setHoursWorked( aHoursWorked );

        final Collection<FreeFromDuty> aFreeFromDutyItems = fMemberInfo.getFreeFromDutyItems();
        final int aHoursDue = getDutyCalculator().calculateHoursToWork( aFreeFromDutyItems, fPeriod );
        aCharge.setHoursDue( aHoursDue );

        Balance aBalance = fMemberInfo.getBalance( fPeriod );
        if(  aBalance == null ){
            aBalance = new Balance( aMemberID, fPeriod, 0);
            fMemberInfo.addBalance( aBalance );
        }

        final int aBalanceValue = aBalance.getValue_Original();
        int aBalanceCharged = aBalanceValue + aHoursWorked - aHoursDue;
        if( aBalanceCharged < 0 ){
            aBalanceCharged = 0;
        }
        aBalance.setValue_Charged( aBalanceCharged );
        aBalance.setValue_ChargedAndAdjusted( aBalanceCharged );

        int aHoursToPay = aHoursDue - aHoursWorked - aBalanceValue;
        if( aHoursToPay < 0 ){
            aHoursToPay = 0;
        }
        aCharge.setHoursToPay( aHoursToPay );

        return aCharge;
    }

    public void balance( final InfoForSingleMember fMemberInfo, final IPeriod fPeriod )
    {
        final List <InfoForSingleMember> aAllRelatives = fMemberInfo.getAllRelatives();
        int aHoursToPayTotal  = 0;
        int aBalanceTotal     = 0;
        for( final InfoForSingleMember aInfoForThisMember : aAllRelatives ){
            final DutyCharge aCharge = aInfoForThisMember.getDutyCharge();
            final Balance aBalance = aInfoForThisMember.getBalance( fPeriod );
            aHoursToPayTotal += aCharge.getHoursToPay();
            aBalanceTotal    += aBalance.getValue_Charged();
        }
        while( aBalanceTotal > 0 && aHoursToPayTotal > 0 ){
            for( final InfoForSingleMember aInfoForThisMember : aAllRelatives ){
                final Balance aBalance = aInfoForThisMember.getBalance( fPeriod );
                int aChargedAndAdjusted = aBalance.getValue_ChargedAndAdjusted();
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
                aBalance.setValue_ChargedAndAdjusted( aChargedAndAdjusted );
                if(aHoursToPayTotal == 0){
                    break;
                }

            }
        }
        fMemberInfo.getDutyCharge().setHoursToPayTotal( aHoursToPayTotal );
    }

}

// ############################################################################
