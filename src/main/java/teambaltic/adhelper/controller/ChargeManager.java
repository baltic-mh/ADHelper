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
import teambaltic.adhelper.model.CreditHours;
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
public class ChargeManager
{
    // ------------------------------------------------------------------------
    private final DutyCalculator m_DutyCalculator;
    public DutyCalculator getDutyCalculator(){ return m_DutyCalculator; }
    // ------------------------------------------------------------------------

    public ChargeManager( final IClubSettings fClubSettings )
    {
        m_DutyCalculator = new DutyCalculator( fClubSettings );
    }

    public DutyCharge createDutyCharge(
            final int           fMemberID,
            final IPeriod       fPeriod,
            final Balance       fBalance,
            final int           fHoursWorked,
            final CreditHours   fCreditHours,
            final Collection<FreeFromDuty> fFreeFromDutyItems )
    {
        final DutyCharge aCharge = new DutyCharge( fMemberID );

        aCharge.setHoursWorked( fHoursWorked );

        final int aHoursDue = getDutyCalculator().calculateHoursToWork( fFreeFromDutyItems, fPeriod );
        aCharge.setHoursDue( aHoursDue );

        final int aBalanceValue = fBalance.getValue_Original();

        final int aCreditHours = getCreditHoursValue( fCreditHours );

        int aHoursToPay = aHoursDue - fHoursWorked - aBalanceValue - aCreditHours;
        if( aHoursToPay < 0 ){
            aHoursToPay = 0;
        }
        aCharge.setHoursToPay( aHoursToPay );

        return aCharge;
    }

    public void balance(final IPeriod fPeriod, final InfoForSingleMember fMemberInfo )
    {
        final List<InfoForSingleMember> aAllRelatives = fMemberInfo.getAllRelatives();
        int aHoursToPayTotal  = 0;
        int aBalanceTotal     = 0;
        for( final InfoForSingleMember aInfoForThisMember : aAllRelatives ){
            final DutyCharge aCharge = createDutyCharge( fPeriod, aInfoForThisMember );
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
        final DutyCharge aDutyCharge = fMemberInfo.getDutyCharge();
        aDutyCharge.setHoursToPayTotal( aHoursToPayTotal );
    }

    private DutyCharge createDutyCharge( final IPeriod fPeriod, final InfoForSingleMember fMemberInfo )
    {
        Balance aBalance = fMemberInfo.getBalance( fPeriod );
        final int aID = fMemberInfo.getID();
        if(  aBalance == null ){
            aBalance = new Balance( aID, fPeriod, 0);
            fMemberInfo.addBalance( aBalance );
        }
        final int aHoursWorked = getHoursWorked( fMemberInfo, fPeriod );
        final CreditHours aCreditHours = fMemberInfo.getCreditHours( fPeriod );
        final Collection<FreeFromDuty> aFreeFromDutyItems = fMemberInfo.getFreeFromDutyItems( fPeriod );
        final DutyCharge aDutyCharge = createDutyCharge(
                aID, fPeriod, aBalance, aHoursWorked, aCreditHours, aFreeFromDutyItems );
        fMemberInfo.setDutyCharge( aDutyCharge );
        chargeBalance( fPeriod, aBalance, aDutyCharge, aCreditHours );
        return aDutyCharge;
    }

    private static void chargeBalance(
            final IPeriod       fPeriod,
            final Balance       fBalance,
            final DutyCharge    fCharge,
            final CreditHours   fCreditHours)
    {
        final int aBalanceValue = fBalance.getValue_Original();
        final int aHoursWorked  = fCharge.getHoursWorked();
        final int aHoursDue     = fCharge.getHoursDue();
        final int aCreditHours  = getCreditHoursValue( fCreditHours );
        int aBalanceCharged = aBalanceValue + aCreditHours + aHoursWorked - aHoursDue;
        if( aBalanceCharged < 0 ){
            aBalanceCharged = 0;
        }
        fBalance.setValue_Charged( aBalanceCharged );
        fBalance.setValue_ChargedAndAdjusted( aBalanceCharged );
    }

    private static int getCreditHoursValue( final CreditHours fCreditHours )
    {
        final int aCreditHours  = fCreditHours == null ? 0 : fCreditHours.getHours();
        return aCreditHours;
    }

    public static int getHoursWorked( final InfoForSingleMember fMemberInfo, final IPeriod fPeriod )
    {
        final WorkEventsAttended aWEA = fMemberInfo.getWorkEventsAttended();
        final int aHoursWorked = aWEA == null ? 0 : aWEA.getTotalHoursWorked( fPeriod );
        return aHoursWorked;
    }

}

// ############################################################################
