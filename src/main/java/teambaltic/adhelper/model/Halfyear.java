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

import teambaltic.adhelper.utils.InvoicingPeriodFolderFilter;

// ############################################################################
public class Halfyear extends APeriod
{
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

    // ------------------------------------------------------------------------
    private final LocalDate m_Start;
    @Override
    public LocalDate getStart(){ return m_Start; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final LocalDate m_End;
    @Override
    public LocalDate getEnd(){ return m_End;}
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
        m_Year = fYear;
        m_Part = fPart;
        m_Start = calcStart( fYear, fPart );
        m_End   = calcEnd( fYear, fPart);
    }

    private static LocalDate calcStart( final int fYear, final EPart fPart )
    {
        final String aMonth = EPart.FIRST.equals( fPart ) ? "01" : "07";
        final String aDateText = String.format( "01.%s.%d", aMonth, fYear );
        final LocalDate aStartDate = LocalDate.parse(aDateText, FORM);
        return aStartDate;
    }

    private static LocalDate calcEnd( final int fYear, final EPart fPart )
    {
        final String aDay   = EPart.FIRST.equals( fPart ) ? "30" : "31";
        final String aMonth = EPart.FIRST.equals( fPart ) ? "06" : "12";
        final String aDateText = String.format( "%s.%s.%d", aDay, aMonth, fYear );
        final LocalDate aStartDate = LocalDate.parse(aDateText, FORM);
        return aStartDate;
    }

    @Override
    public String toString()
    {
        return String.format( "%s - %s", getStart(), getEnd() );
    }

    public static Halfyear create(final String fString)
    {
        final String[] aParts = fString.split( InvoicingPeriodFolderFilter.REGEX_SPLIT );
        final int aYearInt  = Integer.parseInt( aParts[0] );
        final int aMonthInt = Integer.parseInt( aParts[1] );

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
		int result = 1;
		result = prime * result + ((m_End == null) ? 0 : m_End.hashCode());
		result = prime * result + ((m_Part == null) ? 0 : m_Part.hashCode());
		result = prime * result + ((m_Start == null) ? 0 : m_Start.hashCode());
		result = prime * result + m_Year;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Halfyear other = (Halfyear) obj;
		if (m_End == null) {
			if (other.m_End != null)
				return false;
		} else if (!m_End.equals(other.m_End))
			return false;
		if (m_Part != other.m_Part)
			return false;
		if (m_Start == null) {
			if (other.m_Start != null)
				return false;
		} else if (!m_Start.equals(other.m_Start))
			return false;
		if (m_Year != other.m_Year)
			return false;
		return true;
	}

}

// ############################################################################
