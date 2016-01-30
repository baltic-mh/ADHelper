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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

// ############################################################################
public final class ParseUtils
{
    private static final Logger sm_Log = Logger.getLogger(ParseUtils.class);

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

    public static Date getDate( final String fValue )
    {
        try{
            final DateFormat df = new SimpleDateFormat( "yyyy" );
            final Date result = df.parse( fValue );
            return result;
        }catch( final ParseException pe ){
            sm_Log.warn( "Not a number: " + fValue );
            return null;
        }
    }

}

// ############################################################################
