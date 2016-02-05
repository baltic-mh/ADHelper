/**
 * FileUtils.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw�rmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ############################################################################
public final class FileUtils
{
    private FileUtils(){/**/}

    public static String readFirstLine( final File fFile )
    {
        String aLine = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader( new FileReader( fFile ) );
            aLine = in.readLine();
        } catch ( final IOException e ) {
            e.printStackTrace();
        } finally {
            if( in != null ) {
                try {
                    in.close();
                } catch ( final IOException e ){/**/}
            }
        }
        return aLine;
    }

    public static List<String> readAllLines( final File fFile )
    {
        return readAllLines( fFile, 0 );
    }
    public static List<String> readAllLines( final File fFile, final int fSkipLines )
    {
        int aLinesRead = 0;
        final List<String> aLines = new ArrayList<>();
        BufferedReader in = null;
        try {
            in = new BufferedReader( new FileReader( fFile ) );
            String aLine;
            while( ( aLine = in.readLine() ) != null ){
                aLinesRead++;
                if( aLinesRead > fSkipLines ){
                    aLines.add( aLine );
                }
            }
        } catch ( final IOException e ) {
            e.printStackTrace();
        } finally {
            if( in != null ) {
                try {
                    in.close();
                } catch ( final IOException e ){/**/}
            }
        }
        return aLines;
    }

    public static List<String> readColumnNames( final File fFile )
    {
        final String aFirstLine = FileUtils.readFirstLine( fFile );
        final String[] aColumnNames = aFirstLine.split( ";" );
        final List<String> aAsList = Arrays.asList( aColumnNames );
        return aAsList;
    }

    public static Map<String, String> makeMap(
            final List<String> fColumnNames, final String fSingleLine )
    {
        final Map<String, String> aMap = new HashMap<>();
        final String[] aSplit = fSingleLine.split( ";" );
        for( int aIdx = 0; aIdx < aSplit.length; aIdx++ ){
            final String aString = aSplit[aIdx];
            aMap.put( fColumnNames.get( aIdx ), aString );
        }
        return aMap;
    }

}

// ############################################################################
