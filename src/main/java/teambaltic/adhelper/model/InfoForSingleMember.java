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

import java.util.Collection;

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
    private FreeFromDutySet m_FreeFromDutySet;
    public FreeFromDutySet getFreeFromDutySet(){ return m_FreeFromDutySet; }
    public Collection<FreeFromDuty> getFreeFromDutyItems(){ return m_FreeFromDutySet.getFreeFromDutyItems(); }
    public void setFreeFromDutySet( final FreeFromDutySet fNewVal ){ m_FreeFromDutySet = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    public int getBalance(){ return getDutyCharge().getBalance_ChargedAndAdjusted(); }
    public void setBalance( final int fValue ){ setDutyCharge( new DutyCharge( getID(), fValue) ); }
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

    @Override
    public String toString()
    {
        return getMember().toString();
    }
}

// ############################################################################
