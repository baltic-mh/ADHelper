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

import java.util.List;

import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IInvoicingPeriod;
import teambaltic.adhelper.model.WorkEventsAttended;

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
    private DutyCalculator getDC(){ return m_DutyCalculator; }
    // ------------------------------------------------------------------------

    public ChargeCalculator(final DutyCalculator fDutyCalculator)
    {
        m_DutyCalculator = fDutyCalculator;
    }

    public IInvoicingPeriod getInvoicingPeriod()
    {
        return getDC().getInvoicingPeriod();
    }

    public DutyCharge calculate(
            final IClubMember        fMember,
            final Balance            fBalance,
            final WorkEventsAttended fWorkEventsAttended,
            final FreeFromDuty       fFreeFromDuty)
    {
        final int aMemberID = fMember.getID();
        final IInvoicingPeriod aInvoicingPeriod = getDC().getInvoicingPeriod();
        final int aBalanceValue = getBalanceValue( fBalance );
        final DutyCharge aCharge = new DutyCharge(aMemberID, aBalanceValue );
        final int aHoursWorked = fWorkEventsAttended == null ? 0 : fWorkEventsAttended.getTotalHoursWorked( aInvoicingPeriod );
        aCharge.setHoursWorked( aHoursWorked );

        final int aHoursDue = getDC().calculateHoursToWork( fFreeFromDuty );
        aCharge.setHoursDue( aHoursDue );

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
                if( aChargedAndAdjusted > 0 ){
                    if( aChargedAndAdjusted < 100 ){
                        aBalanceTotal       -= aChargedAndAdjusted;
                        aHoursToPayTotal    -= aChargedAndAdjusted;
                        aChargedAndAdjusted  = 0;
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
        }
        fCharge.setHoursToPayTotal( aHoursToPayTotal );
    }

    private static int getBalanceValue( final Balance fBalance )
    {
        if( fBalance == null ){
            return 0;
        }
        return fBalance.getValue();
    }

}

// ############################################################################
