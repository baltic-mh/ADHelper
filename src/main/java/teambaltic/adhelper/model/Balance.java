/**
 * Balance.java
 *
 * Created on 03.02.2016
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
public class Balance implements IIdentifiedItem
{
//    private static final Logger sm_Log = Logger.getLogger(Balance.class);

    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final int m_Value;
    public int getValue(){return m_Value; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private LocalDate m_ValidOn;
    public LocalDate getValidOn(){ return m_ValidOn; }
    public void setValidOn( final LocalDate fNewVal ){ m_ValidOn = fNewVal; }
    // ------------------------------------------------------------------------

    public Balance( final int fMemberID, final int fValue )
    {
        m_MemberID  = fMemberID;
        m_Value     = fValue;
    }

    @Override
    public String toString()
    {
        return String.format("%5.1f", getValue() / 100.0f);
    }
}

// ############################################################################
