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
import java.time.temporal.TemporalAdjusters;

// ############################################################################
public class FreeFromDuty extends APeriod implements IIdentifiedItem<FreeFromDuty>
{
    public enum REASON{
        // Aus Mitgliedsdaten berechnete Gründe:
        TOO_YOUNG,
        DUTY_NOT_YET_EFFECTIVE,
        TOO_OLD,
        NO_LONGER_MEMBER,
        // Explizit anzugebende Gründe:
        SUSTAINING("F\u00F6rdermitglied"),
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
    public int getID() { return m_MemberID; }
    public int getMemberID() { return getID(); }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final REASON m_Reason;
    public REASON getReason(){ return m_Reason; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    public LocalDate getFrom(){ return getStart(); }
    public void setFrom( final LocalDate fFrom ){ setStart( adjustToBeginningOfMonth( fFrom ) ); }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    public LocalDate getUntil(){ return getEnd(); }
    public void setUntil( final LocalDate fUntil ){ setEnd( adjustToEndOfMonth( fUntil ) ); }
    // ------------------------------------------------------------------------

    public FreeFromDuty( final int fMemberID, final REASON fReason )
    {
        m_MemberID  = fMemberID;
        m_Reason    = fReason;
    }

    @Override
    public String toString()
    {
        final StringBuffer aSB = new StringBuffer( String.format( "%s", getReason().getStringRep() ) );
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

    @Override
    public int compareTo( final FreeFromDuty fOther )
    {
        final int aThisValue = getID();
        final int aOtherValue = fOther.getID();
        if( aThisValue < aOtherValue ){
            return -1;
        }
        if( aThisValue > aOtherValue ){
            return 1;
        }
        return 0;
    }

    private static LocalDate adjustToBeginningOfMonth( final LocalDate fDate )
    {
        if( fDate == null ){
            return fDate;
        }
        final LocalDate aAdjusted = fDate.with(TemporalAdjusters.firstDayOfMonth());
        return aAdjusted;
    }
    private static LocalDate adjustToEndOfMonth( final LocalDate fDate )
    {
        if( fDate == null ){
            return fDate;
        }
        final LocalDate aAdjusted = fDate.with(TemporalAdjusters.lastDayOfMonth());
        return aAdjusted;
    }

    @Override
    public IPeriod createSuccessor()
    {
        return null;
    }
    @Override
    public IPeriod createPredeccessor()
    {
        return null;
    }
}

// ############################################################################
