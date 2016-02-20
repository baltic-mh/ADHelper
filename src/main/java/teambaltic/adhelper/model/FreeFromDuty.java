/**
 * FreeFromDuty.java
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

import java.time.LocalDate;

// ############################################################################
public class FreeFromDuty extends APeriod implements IIdentifiedItem
{
    public enum REASON{
        // Aus Mitgliedsdaten berechnete Gründe:
        TOO_YOUNG,
        DUTY_NOT_YET_EFFECTIVE,
        TOO_OLD,
        NO_LONGER_MEMBER,
        // Explizit anzugebende Gründe:
        SUSTAINING("Fördermitglied"),
        HONORY("Ehrenmitglied"),
        MANAGEMENT,
        REMOTENESS,
        INDIVIDUALREASON;

        private final String m_StringRep;
        public String getStringRep(){ return m_StringRep == null ? name() : m_StringRep; }

        REASON()
        {
            this(null);
        }
        REASON(final String fStringRep)
        {
            m_StringRep = fStringRep;
        }
    }

    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final REASON m_Reason;
    public REASON getReason(){ return m_Reason; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private LocalDate m_From;
    public LocalDate getFrom(){ return m_From; }
    @Override
    public LocalDate getStart(){ return getFrom(); }
    public void setFrom( final LocalDate fFrom ){ m_From = fFrom; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private LocalDate m_Until;
    public LocalDate getUntil(){ return m_Until; }
    @Override
    public LocalDate getEnd(){ return getUntil(); }
    public void setUntil( final LocalDate fUntil ){ m_Until = fUntil; }
    // ------------------------------------------------------------------------

    public FreeFromDuty( final int fMemberID, final REASON fReason )
    {
        m_MemberID  = fMemberID;
        m_Reason    = fReason;
    }

    @Override
    public String toString()
    {
        final StringBuffer aSB = new StringBuffer( String.format( "%s ", getReason().getStringRep() ) );
        final LocalDate aFrom = getFrom();
        if( aFrom != null ){
            aSB.append( " von "+aFrom );
        }
        final LocalDate aUntil = getUntil();
        if( aUntil != null ){
            aSB.append( " bis "+aUntil );
        }
        return aSB.toString();
    }
}

// ############################################################################
