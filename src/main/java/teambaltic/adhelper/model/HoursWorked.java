/**
 * Duty.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw�rmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

// ############################################################################
public class HoursWorked
{
    // ------------------------------------------------------------------------
    private final int m_MemberID;
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private long m_Date;
    public long getDate(){ return m_Date; }
    public void setDate( final long fDate ){ m_Date = fDate; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private float m_Hours;
    public float getHours(){ return m_Hours; }
    public void setHours( final float fHours ) { m_Hours = fHours; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private long m_Cleared;
    public long getCleared(){ return m_Cleared; }
    public void setCleared( final long fCleared ){ m_Cleared = fCleared; }
    // ------------------------------------------------------------------------

    public HoursWorked( final int fMemberID )
    {
        m_MemberID = fMemberID;
    }
}

// ############################################################################
