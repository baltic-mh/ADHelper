/**
 * DifferingLine.java
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
public class DifferingLine {

    public enum EDiffType {
        ADDED,
        MODIFIED,
        DELETED;
    }

    private final LineInfo m_LineInfo_Ref;
    public LineInfo getLineInfo_Ref() { return m_LineInfo_Ref; }

    private final LineInfo m_LineInfo_New;
    public LineInfo getLineInfo_New() { return m_LineInfo_New; }

    private final EDiffType m_Type;
    public EDiffType getType() { return m_Type; }

    public DifferingLine(final LineInfo fLineInfo_Ref, final LineInfo fLineInfo_New, final EDiffType fType) {
        m_LineInfo_Ref = fLineInfo_Ref;
        m_LineInfo_New = fLineInfo_New;
        m_Type   = fType;
    }

    @Override
    public String toString() {
        return String.format("%s: %s - %s", getType(), getLineInfo_Ref(), getLineInfo_New() );
    }
}

// ############################################################################
