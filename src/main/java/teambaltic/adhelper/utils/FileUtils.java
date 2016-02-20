/**
 * FileUtils.java
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

    public static File determineNewestInvoicingPeriodFolder( final File fDataFolder )
    {
        final File[] aChildren = fDataFolder.listFiles( new InvoicingPeriodFolderFilter() );
        if( aChildren.length == 0 ){
            return null;
        }
        if( aChildren.length == 1 ){
            return aChildren[0];
        }
        File aResult = null;
        final int aMostRecentYear  = 0;
        final int aMostRecentMonth = 0;
        for( int aIdx = 0; aIdx < aChildren.length; aIdx++ ){
            final String[] aParts = aChildren[aIdx].getName().split( InvoicingPeriodFolderFilter.sm_SplitRegex );
            final int aYear  = Integer.parseInt( aParts[0] );
            final int aMonth = Integer.parseInt( aParts[1] );
            if( aYear > aMostRecentYear ){
                aResult = aChildren[aIdx];
            } else if ( aYear == aMostRecentYear && aMonth > aMostRecentMonth ){
                aResult = aChildren[aIdx];
            }
        }
        return aResult;
    }

    public static void checkFile( final File fFile ) throws Exception
    {
        if( !fFile.exists() ){
            throw new Exception("File does not exist: "+fFile.getPath());
        }
        if( !fFile.isFile() ){
            throw new Exception("File is no regular file: "+fFile.getPath());
        }
        if( !fFile.canRead() ){
            throw new Exception("Cannot read file: "+fFile.getPath());
        }
    }
}

// ############################################################################
