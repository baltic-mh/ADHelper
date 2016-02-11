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

import java.time.LocalDate;

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
    private LocalDate m_Birthday;
    @Override
    public LocalDate getBirthday(){ return m_Birthday; }
    public void setBirthday( final LocalDate fNewVal ){ m_Birthday = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private LocalDate m_MemberFrom;
    @Override
    public LocalDate getMemberFrom(){ return m_MemberFrom; }
    public void setMemberFrom( final LocalDate fNewVal ){ m_MemberFrom = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private LocalDate m_MemberUntil;
    @Override
    public LocalDate getMemberUntil(){ return m_MemberUntil; }
    public void setMemberUntil( final LocalDate fNewVal ){ m_MemberUntil = fNewVal; }
    // ------------------------------------------------------------------------

    public ClubMember(final int fID)
    {
        m_ID = fID;
    }

    @Override
    public String toString()
    {
        return String.format( "%s (%d)", getName(), getID() );
    }
}

// ############################################################################
