/**
 * DutyCharge.java
 *
 * Created on 31.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

// ############################################################################
public class DutyCharge implements IIdentifiedItem
{
    private static final Logger sm_Log = Logger.getLogger(DutyCharge.class);

    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final int m_Balance_Original;
    public int getBalance_Original(){ return m_Balance_Original; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_Balance_Charged;
    public int getBalance_Charged(){ return m_Balance_Charged; }
    public void setBalance_Charged( final int fNewVal ){ m_Balance_Charged = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_Balance_ChargedAndAdjusted;
    public int getBalance_ChargedAndAdjusted(){ return m_Balance_ChargedAndAdjusted; }
    public void setBalance_ChargedAndAdjusted( final int fNewVal ){ m_Balance_ChargedAndAdjusted = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_HoursWorked;
    public int getHoursWorked(){ return m_HoursWorked; }
    public void setHoursWorked( final int fNewVal ){ m_HoursWorked = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_HoursDue;
    public int getHoursDue(){ return m_HoursDue; }
    public void setHoursDue( final int fNewVal ){ m_HoursDue = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_HoursToPay;
    public int getHoursToPay(){ return m_HoursToPay; }
    public void setHoursToPay( final int fNewVal ){ m_HoursToPay = m_HoursToPayTotal = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_HoursToPayTotal;
    public int getHoursToPayTotal(){ return m_HoursToPayTotal; }
    public void setHoursToPayTotal( final int fNewVal ){ m_HoursToPayTotal = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Map<Integer, DutyCharge> m_ChargeOfRelatives;
    public List<DutyCharge> getAllDutyCharges()
    {
        final List <DutyCharge> aAllItems = new ArrayList<>();
        aAllItems.add( this );
        aAllItems.addAll( m_ChargeOfRelatives.values() );
        return aAllItems;
    }
    // ------------------------------------------------------------------------

    public DutyCharge( final int fMemberID, final int fBalance )
    {
        m_MemberID = fMemberID;
        m_Balance_Original  = fBalance;
        m_ChargeOfRelatives = new HashMap<>();
    }

    public void addRelative( final DutyCharge fItem )
    {
        final int aRelativeID = fItem.getMemberID();
        synchronized( m_ChargeOfRelatives ){
            final Integer aIntegerKey = Integer.valueOf( aRelativeID );
            if( m_ChargeOfRelatives.containsKey( aIntegerKey ) ){
                sm_Log.warn( String.format("%d: Charge from id %d already included! Will be ignored!",
                        getMemberID(), aRelativeID ) );
                return;
            }
            m_ChargeOfRelatives.put( aIntegerKey, fItem );
        }
    }

    @Override
    public String toString()
    {
        return String.format( "Guthaben: %5.2f | Pflicht: %5.2f | Gearbeitet: %5.2f | Zu zahlen: %5.2f",
                getBalance_Original()/100.0f, getHoursDue()/100.0f, getHoursWorked()/100.0f, getHoursToPay()/100.0f );
    }
}

// ############################################################################
