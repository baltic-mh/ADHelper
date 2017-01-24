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
public class Balance implements IIdentifiedItem<Balance>
{
//    private static final Logger sm_Log = Logger.getLogger(Balance.class);

    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final int m_Value_Original;
    public int getValue_Original(){return m_Value_Original; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final LocalDate m_ValidFrom;
    public LocalDate getValidFrom(){ return m_ValidFrom; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_Value_Charged;
    public int getValue_Charged(){ return m_Value_Charged; }
    public void setValue_Charged( final int fNewVal ){ m_Value_Charged = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_Value_ChargedAndAdjusted;
    public int getValue_ChargedAndAdjusted(){ return m_Value_ChargedAndAdjusted; }
    public void setValue_ChargedAndAdjusted( final int fNewVal ){ m_Value_ChargedAndAdjusted = fNewVal; }
    // ------------------------------------------------------------------------

    public Balance(final int fMemberID, final IPeriod fPeriod, final int fValue_Original)
    {
        this(fMemberID, fPeriod.getStart(), fValue_Original);
    }
    public Balance( final int fMemberID, final LocalDate fValidFrom, final int fValue_Original )
    {
        m_MemberID       = fMemberID;
        m_Value_Original = fValue_Original;
        m_ValidFrom      = fValidFrom;
        setValue_Charged( fValue_Original );
        setValue_ChargedAndAdjusted( fValue_Original );
    }

    @Override
    public String toString()
    {
        return String.format("%5.1f", getValue_Original() / 100.0f);
    }

    @Override
    public int compareTo( final Balance fOther )
    {
        final int aThisValue = getValue_ChargedAndAdjusted();
        final int aOtherValue = fOther.getValue_ChargedAndAdjusted();
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
