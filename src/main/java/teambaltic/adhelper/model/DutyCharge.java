package teambaltic.adhelper.model;

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

// ############################################################################
public class DutyCharge implements IIdentifiedItem<DutyCharge>
{
    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
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

    public DutyCharge( final int fMemberID, final Balance fBalance )
    {
        m_MemberID = fMemberID;
    }

    @Override
    public String toString()
    {
        return String.format( "Pflicht: %5.2f | Gearbeitet: %5.2f | Zu zahlen: %5.2f",
                getHoursDue()/100.0f, getHoursWorked()/100.0f, getHoursToPay()/100.0f );
    }

    @Override
    public int compareTo( final DutyCharge fOther )
    {
        final int aThisValue  = getHoursToPayTotal();
        final int aOtherValue = fOther.getHoursToPayTotal();
        if( aThisValue < aOtherValue ){
            return -1;
        }
        if( aThisValue > aOtherValue ){
            return 1;
        }
        return 0;
    }
}

// ############################################################################
