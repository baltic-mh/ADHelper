/**
 * FileComparisonResult.java
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// ############################################################################
public class FileComparisonResult {
    private final File m_Ref;
    public File getRef() { return m_Ref; }

    private final File m_New;
    public File getNew() { return m_New; }

    private final List<DifferingLine> m_DifferingLines;
    public List<DifferingLine> getDifferingLines() { return m_DifferingLines; }

    private final List<String> m_ColumnNames;
    public List<String> getColumnNames() { return m_ColumnNames; }

    public FileComparisonResult( final File fRef, final File fNew, final List<String> fColumnNames ) {
        m_Ref = fRef;
        m_New = fNew;
        m_ColumnNames = fColumnNames;
        m_DifferingLines = new ArrayList<>();
    }

    public void add( final DifferingLine fLine ) {
        m_DifferingLines.add(fLine);
    }

    public boolean filesDiffer() {
        return getDifferingLines().size() != 0;
    }
}

// ############################################################################
