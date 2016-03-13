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

    public static CheckSumInfo readFromFile()
    {
        return null;
    }
}

// ############################################################################
