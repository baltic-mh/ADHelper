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

// ############################################################################
public class Halfyear extends AInvoicingPeriod
{
    private static final DateTimeFormatter FORM = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public enum EPart { FIRST, SECOND; }

    // ------------------------------------------------------------------------
    private final Year m_Year;
    public Year getYear(){ return m_Year; }
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

    public Halfyear(final Year fYear, final EPart fPart)
    {
        m_Year = fYear;
        m_Part = fPart;
        m_Start = calcStart( fYear, fPart);
        m_End   = calcEnd( fYear, fPart);
    }

    private static LocalDate calcStart( final Year fYear, final EPart fPart )
    {
        final String aMonth = EPart.FIRST.equals( fPart ) ? "01" : "07";
        final String aDateText = String.format( "01.%s.%d", aMonth, fYear.getValue() );
        final LocalDate aStartDate = LocalDate.parse(aDateText, FORM);
        return aStartDate;
    }

    private static LocalDate calcEnd( final Year fYear, final EPart fPart )
    {
        final String aDay   = EPart.FIRST.equals( fPart ) ? "30" : "31";
        final String aMonth = EPart.FIRST.equals( fPart ) ? "06" : "12";
        final String aDateText = String.format( "%s.%s.%d", aDay, aMonth, fYear.getValue() );
        final LocalDate aStartDate = LocalDate.parse(aDateText, FORM);
        return aStartDate;
    }

    @Override
    public String toString()
    {
        return String.format( "%s - %s", getStart(), getEnd() );
    }
}

// ############################################################################
