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
    private int m_Value_Original;
    public int getValue_Original(){return m_Value_Original; }
    public void setValue_Original( final int fValue_Original ){ m_Value_Original = fValue_Original; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private LocalDate m_ValidFrom;
    public LocalDate getValidFrom(){ return m_ValidFrom; }
    public void setValidFrom( final LocalDate fValidFrom ){ m_ValidFrom = fValidFrom; }
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

    public Balance( final int fMemberID )
    {
        m_MemberID       = fMemberID;
    }

    public Balance(final int fMemberID, final IPeriod fPeriod, final int fValue_Original)
    {
        this(fMemberID);
        setValidFrom( fPeriod.getStart() );
        setValues(fValue_Original);
    }
    public void setValues( final int fStartValue )
    {
        m_Value_Original = fStartValue;
        setValue_Charged( fStartValue );
        setValue_ChargedAndAdjusted( fStartValue );
    }

    @Override
    public String toString()
    {
        return String.format("%d (%s) => %5.2f/%5.2f/%5.2f", getID(), getValidFrom(),
                getValue_Original() / 100.0f, getValue_Charged() / 100.0f, getValue_ChargedAndAdjusted() / 100.0f);
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
