/**
 * CheckSumInfo.java
 *
 * Created on 12.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.nio.file.Path;
import java.util.List;

import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class CheckSumInfo
{
    // ------------------------------------------------------------------------
    private final String m_Hash;
    public String getHash(){ return m_Hash; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final long m_TimeStamp;
    public long getTimeStamp(){ return m_TimeStamp; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_FileName;
    public String getFileName(){ return m_FileName; }
    // ------------------------------------------------------------------------

    public CheckSumInfo(final long fTimeStamp, final String fHash, final String fFileName)
    {
        m_TimeStamp = fTimeStamp;
        m_Hash      = fHash;
        m_FileName  = fFileName;
    }

    public static CheckSumInfo readFromFile( final Path fCheckSumFile)
    {
        final List<String> aLines = FileUtils.readAllLines( fCheckSumFile.toFile(), 1 );
        String[] aParts = aLines.get( 0 ).split( ";" );
        final long aTS = Long.parseLong( aParts[0].replaceFirst( "^#", "" ) );
        final String aFileName = aParts[2];
        aParts = aLines.get( 1 ).split( " " );
        final String aHash = aParts[0];
        return new CheckSumInfo(aTS, aHash, aFileName);
    }
}

// ############################################################################
