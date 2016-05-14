/**
 * Duty.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.time.LocalDate;

// ############################################################################
public class WorkEvent implements IIdentifiedItem<WorkEvent>
{
    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private LocalDate m_Date;
    public LocalDate getDate(){ return m_Date; }
    public void setDate( final LocalDate fDate ){ m_Date = fDate; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_Hours;
    public int getHours(){ return m_Hours; }
    public void setHours( final int fHours ) { m_Hours = fHours; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private LocalDate m_Cleared;
    public LocalDate getCleared(){ return m_Cleared; }
    public void setCleared( final LocalDate fCleared ){ m_Cleared = fCleared; }
    // ------------------------------------------------------------------------

    public WorkEvent( final int fMemberID )
    {
        m_MemberID = fMemberID;
    }

    @Override
    public int compareTo( final WorkEvent fOther )
    {
        return getDate().compareTo( fOther.getDate() );
    }

    @Override
    public String toString()
    {
        final String aString = String.format("%d: %s - %5.2f", getID(), getDate().toString(), getHours()/100.0f);
        return aString;
    }
}

// ############################################################################
