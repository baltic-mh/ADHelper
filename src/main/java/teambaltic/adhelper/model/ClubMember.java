/**
 * ClubMember.java
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

// ############################################################################
public class ClubMember implements IClubMember
{
    // ------------------------------------------------------------------------
    private final int m_ID;
    @Override
    public int getID(){ return m_ID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_LinkID;
    @Override
    public int getLinkID(){ return m_LinkID; }
    public void setLinkID( final int fLinkID ){ m_LinkID = fLinkID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private String m_Name;
    @Override
    public String getName(){ return m_Name; }
    public void setName( final String fName ){ m_Name = fName; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private long m_MemberSince;
    @Override
    public long getMemberSince(){ return m_MemberSince; }
    public void setMemberSince( final long fNewVal ){ m_MemberSince = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private long m_MemberUntil;
    @Override
    public long getMemberUntil(){ return m_MemberUntil; }
    public void setMemberUntil( final long fNewVal ){ m_MemberUntil = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private EMemberKind m_MemberKind;
    @Override
    public EMemberKind getMemberKind(){ return m_MemberKind; }
    public void setMemberKind( final EMemberKind fNewVal ){ m_MemberKind = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private Long m_ManagementMemberSince;
    @Override
    public Long getManagementMemberSince(){ return m_ManagementMemberSince; }
    public void setManagementMemberSince( final Long fNewVal ){ m_ManagementMemberSince = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private Long m_FreedFromDutySince;
    @Override
    public Long getFreedFromDutySince(){ return m_FreedFromDutySince; }
    public void setFreedFromDutySince( final Long fNewVal ){ m_FreedFromDutySince = fNewVal; }
    // ------------------------------------------------------------------------

    public ClubMember(final int fID)
    {
        m_ID = fID;
        m_MemberKind = EMemberKind.NORMAL;
    }

    @Override
    public String toString()
    {
        return String.format( "%d: %s", getID(), getName() );
    }
}

// ############################################################################
