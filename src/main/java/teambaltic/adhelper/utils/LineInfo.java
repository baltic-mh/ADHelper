/**
 * LineInfo.java
 *
 * Created on 05.12.2021
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2021 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

// ############################################################################
public class LineInfo {

    private final int    m_LineNo;
    public int getLineNo() {return m_LineNo; }

    private final String m_Line;
    public String getLine() { return m_Line; }

    public LineInfo( final int fLineNo, final String fLine) {
        m_LineNo = fLineNo;
        m_Line   = fLine;
    }

    @Override
    public String toString() {
        return String.format("%d: %s", getLineNo(), getLine() );
    }

}

// ############################################################################
