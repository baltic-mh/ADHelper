/**
 * Duty.java
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
public class Participation extends AIdentifiedItem<Participation>
{
    // ------------------------------------------------------------------------
    private LocalDate m_Date;
    public LocalDate getDate(){ return m_Date; }
    public void setDate( final LocalDate fDate ){ m_Date = fDate; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private int m_Hours;
    public int getHours(){ return m_Hours; }
    public void setHours( final int fHours ) { m_Hours = fHours; }
    // ------------------------------------------------------------------------

    public Participation( final int fMemberID )
    {
        super( fMemberID );
    }

    @Override
    public int compareTo( final Participation fOther )
    {
        return getDate().compareTo( fOther.getDate() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( m_Date == null ) ? 0 : m_Date.hashCode() );
        result = prime * result + m_Hours;
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        final Participation other = (Participation) obj;
        if( m_Date == null ){
            if( other.m_Date != null )
                return false;
        }else if( !m_Date.equals( other.m_Date ) )
            return false;
        if( m_Hours != other.m_Hours )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        final String aString = String.format("%d: %s - %5.2f", getID(), getDate().toString(), getHours()/100.0f);
        return aString;
    }
}

// ############################################################################
