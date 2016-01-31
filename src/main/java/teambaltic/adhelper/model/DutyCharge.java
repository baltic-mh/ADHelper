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

// ############################################################################
public class DutyCharge
{
    // ------------------------------------------------------------------------
    private final int m_MemberID;
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final float m_Balance_Original;
    public float getBalance_Original(){ return m_Balance_Original; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private float m_Balance_AfterPayment;
    public float getBalance_AfterPayment(){ return m_Balance_AfterPayment; }
    public void setBalance_AfterPayment( final float fNewVal ){ m_Balance_AfterPayment = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private float m_Balance_AfterPaymentAndAdjustment;
    public float getBalance_AfterPaymentAndAdjustment(){ return m_Balance_AfterPaymentAndAdjustment; }
    public void setBalance_AfterPaymentAndAdjustment( final float fNewVal ){ m_Balance_AfterPaymentAndAdjustment = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private float m_HoursWorked;
    public float getHoursWorked(){ return m_HoursWorked; }
    public void setHoursWorked( final float fNewVal ){ m_HoursWorked = fNewVal; }
    // ------------------------------------------------------------------------

    public DutyCharge( final int fMemberID, final float fBalance )
    {
        m_MemberID = fMemberID;
        m_Balance_Original  = fBalance;
    }
}

// ############################################################################
