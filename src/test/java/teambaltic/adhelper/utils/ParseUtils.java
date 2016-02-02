/**
 * ParseUtils.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.log4j.Logger;

// ############################################################################
public final class ParseUtils
{
    private static final Logger sm_Log = Logger.getLogger(ParseUtils.class);

    private static final DateTimeFormatter FORM = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static Integer getInteger( final String fValue )
    {
        if( fValue == null || "".equals( fValue ) ){
            return null;
        }
        try{
            final int aValue = Integer.parseInt( fValue );
            return Integer.valueOf( aValue );
        }catch( final NumberFormatException fEx ){
            sm_Log.warn( "Not a number: " + fValue );
            return null;
        }
    }

    public static Long getLong( final String fValue )
    {
        if( fValue == null || "".equals( fValue ) ){
            return null;
        }
        try{
            final long aValue = Long.parseLong( fValue );
            return Long.valueOf( aValue );
        }catch( final NumberFormatException fEx ){
            sm_Log.warn( "Not a number: " + fValue );
            return null;
        }
    }

    public static LocalDate getDate( final String fDateText )
    {
        if( fDateText == null || "".equals( fDateText ) ){
            return null;
        }
        try{
            final LocalDate aResult = LocalDate.parse(fDateText, FORM);
            return aResult;
        }catch( final DateTimeParseException fEx ){
            sm_Log.warn( "Not a valid date: " + fDateText );
            return null;
        }
    }

}

// ############################################################################
