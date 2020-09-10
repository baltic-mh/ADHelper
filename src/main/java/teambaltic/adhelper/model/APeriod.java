/**
 * AInvoicingPeriod.java
 *
 * Created on 04.02.2016
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
public abstract class APeriod implements IPeriod
{
    // ------------------------------------------------------------------------
    private LocalDate m_Start;
    @Override
    public LocalDate getStart(){ return m_Start; }
    public void setStart(final LocalDate fStart) { m_Start = fStart; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private LocalDate m_End;
    @Override
    public LocalDate getEnd(){ return m_End; }
    public void setEnd(final LocalDate fEnd) { m_End = fEnd; }
    // ------------------------------------------------------------------------

    public APeriod()
    {
    }

    public APeriod(final LocalDate fStart, final LocalDate fEnd)
    {
        m_Start = fStart;
        m_End   = fEnd;
    }

    @Override
    public boolean isAfterMyStart(  final LocalDate fDate )
    {
        if( fDate == null ) {
            return true;
        }
        final LocalDate aMyStart = getStart();
        if( aMyStart == null ){
            return true;
        }
        final int aComparedTo = aMyStart.compareTo( fDate );
        return aComparedTo <= 0;
    }

    @Override
    public boolean isBeforeMyEnd(  final LocalDate fDate )
    {
        if( fDate == null ) {
            return true;
        }
        final LocalDate aMyEnd = getEnd();
        if( aMyEnd == null ){
            return true;
        }
        final int aComparedTo = aMyEnd.compareTo( fDate );
        return aComparedTo >= 0;
    }

    @Override
    public boolean isBeforeMyStart(  final LocalDate fDate )
    {
        if( fDate == null ) {
            return true;
        }
        final LocalDate aMyStart = getStart();
        if( aMyStart == null ){
            return true;
        }
        final int aComparedTo = aMyStart.compareTo( fDate );
        return aComparedTo > 0;
    }

    @Override
    public boolean isWithinMyPeriod( final LocalDate fDate )
    {
        return isAfterMyStart( fDate ) && isBeforeMyEnd( fDate );
    }

    /**
     * Liefert true, wenn die andere Periode diese ganz oder teilweise überdeckt!
     */
    @Override
    public boolean isWithinMyPeriod( final IPeriod fOther )
    {
        if( fOther == null ) {
            return true;
        }
        final LocalDate aOtherStart = fOther.getStart();
        final LocalDate aOtherEnd   = fOther.getEnd();
        if( aOtherStart == null ){
            if( aOtherEnd == null ){
                return true;
            }
            return isAfterMyStart( aOtherEnd );
        }
        if( aOtherEnd == null ){
            return isBeforeMyEnd( aOtherStart );
        }

        if( isBeforeMyEnd( aOtherStart ) && isAfterMyStart( aOtherEnd ) ){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((m_End == null) ? 0 : m_End.hashCode());
        result = prime * result + ((m_Start == null) ? 0 : m_Start.hashCode());
        return result;
    }
    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        final APeriod other = (APeriod) obj;
        if ( m_End == null ) {
            if ( other.m_End != null )
                return false;
        } else if ( !m_End.equals(other.m_End) )
            return false;
        if ( m_Start == null ) {
            if ( other.m_Start != null )
                return false;
        } else if ( !m_Start.equals(other.m_Start) )
            return false;
        return true;
    }
}

// ############################################################################
