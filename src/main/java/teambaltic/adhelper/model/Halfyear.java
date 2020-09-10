/**
 * Halfyear.java
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
import java.time.Year;
import java.time.format.DateTimeFormatter;

import org.apache.log4j.Logger;

import teambaltic.adhelper.utils.InvoicingPeriodFolderFilter;

// ############################################################################
public class Halfyear extends APeriod
{
    private static final Logger sm_Log = Logger.getLogger(Halfyear.class);

    private static final DateTimeFormatter FORM = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public enum EPart { FIRST, SECOND; }

    // ------------------------------------------------------------------------
    private final int m_Year;
    public int getYear(){ return m_Year; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final EPart m_Part;
    public EPart getPart(){ return m_Part; }
    // ------------------------------------------------------------------------

    public Halfyear(final LocalDate fStartDate)
    {
        this( fStartDate.getYear(), fStartDate.getMonthValue() );
    }
    public Halfyear(final int fYear, final int fMonth )
    {
        this( fYear, fMonth > 6 ? EPart.SECOND : EPart.FIRST );
    }
    public Halfyear(final Year fYear, final EPart fPart)
    {
        this( fYear.getValue(), fPart );
    }
    public Halfyear(final int fYear, final EPart fPart)
    {
        super( calcStart( fYear, fPart ), calcEnd( fYear, fPart) );
        m_Year = fYear;
        m_Part = fPart;
    }

    private static LocalDate calcStart( final int fYear, final EPart fPart )
    {
        final String aMonth = EPart.FIRST.equals( fPart ) ? "01" : "07";
        final String aDateText = String.format( "01.%s.%d", aMonth, fYear );
        return LocalDate.parse(aDateText, FORM);
    }

    private static LocalDate calcEnd( final int fYear, final EPart fPart )
    {
        final String aDay   = EPart.FIRST.equals( fPart ) ? "30" : "31";
        final String aMonth = EPart.FIRST.equals( fPart ) ? "06" : "12";
        final String aDateText = String.format( "%s.%s.%d", aDay, aMonth, fYear );
        return LocalDate.parse(aDateText, FORM);
    }

    @Override
    public String toString()
    {
        return String.format( "%s - %s", getStart(), getEnd() );
    }

    public static Halfyear create(final String fString)
    {
        if( fString == null || fString.isEmpty() ) {
            return null;
        }
        final String[] aParts = fString.split( InvoicingPeriodFolderFilter.REGEX_SPLIT );
        int aYearInt;
        int aMonthInt;
        try {
            aYearInt = Integer.parseInt( aParts[0] );
            aMonthInt = Integer.parseInt( aParts[1] );
        } catch ( final NumberFormatException fEx ) {
            sm_Log.warn(String.format( "Kein korrektes Datumsformat: %s", fString));
            return null;

        }

        return new Halfyear( aYearInt, aMonthInt > 6 ? EPart.SECOND : EPart.FIRST );
    }

    public static Halfyear next( final Halfyear fPrevious )
    {
        if( EPart.FIRST.equals( fPrevious.getPart() ) ){
            return new Halfyear( fPrevious.getYear(), EPart.SECOND );
        }
        return new Halfyear( fPrevious.getYear()+1, EPart.FIRST );
    }

    public static Halfyear previous( final Halfyear fNext )
    {
        if( EPart.SECOND.equals( fNext.getPart() ) ){
            return new Halfyear( fNext.getYear(), EPart.FIRST );
        }
        return new Halfyear( fNext.getYear()-1, EPart.SECOND );
    }

    @Override
    public IPeriod createSuccessor()
    {
        return Halfyear.next( this );
    }

    @Override
    public IPeriod createPredeccessor()
    {
        return Halfyear.previous( this );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((m_Part == null) ? 0 : m_Part.hashCode());
        result = prime * result + m_Year;
        return result;
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( this == obj )
            return true;
        if ( !super.equals(obj) )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        final Halfyear other = (Halfyear) obj;
        if ( m_Part != other.m_Part )
            return false;
        if ( m_Year != other.m_Year )
            return false;
        return true;
    }

}

// ############################################################################
