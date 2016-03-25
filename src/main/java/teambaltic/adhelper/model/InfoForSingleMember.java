/**
 * InfoForSingleMember.java
 *
 * Created on 06.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

// ############################################################################
public class InfoForSingleMember implements IIdentifiedItem<InfoForSingleMember>
{

    // ------------------------------------------------------------------------
    private final int m_ID;
    @Override
    public int getID(){ return m_ID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private IClubMember m_Member;
    public IClubMember getMember(){ return m_Member; }
    public void setMember( final IClubMember fMember ){ m_Member = fMember; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private FreeFromDuty m_FreeFromDuty;
    public FreeFromDuty getFreeFromDuty(){ return m_FreeFromDuty; }
    public void setFreeFromDuty( final FreeFromDuty fNewVal ){ m_FreeFromDuty = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private Balance m_Balance;
    public Balance getBalance(){ return m_Balance; }
    public void setBalance( final Balance fBalance ){ m_Balance = fBalance; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private WorkEventsAttended m_WorkEventsAttended;
    public WorkEventsAttended getWorkEventsAttended(){ return m_WorkEventsAttended; }
    public void setWorkEventsAttended( final WorkEventsAttended fNewVal ){ m_WorkEventsAttended = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private DutyCharge m_DutyCharge;
    public DutyCharge getDutyCharge(){ return m_DutyCharge; }
    public void setDutyCharge( final DutyCharge fDutyCharge ){ m_DutyCharge = fDutyCharge; }
    // ------------------------------------------------------------------------

    public InfoForSingleMember(final int fID)
    {
        m_ID = fID;
    }

    @Override
    public int compareTo( final InfoForSingleMember fOther )
    {
        final IClubMember aThisMember   = getMember();
        final IClubMember aOtherMember  = fOther.getMember();
        return aThisMember.compareTo( aOtherMember );
    }


}

// ############################################################################
