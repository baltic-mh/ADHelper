/**
 * PeriodData.java
 *
 * Created on 07.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.nio.file.Path;

// ############################################################################
public class PeriodData
{
    // ------------------------------------------------------------------------
    private final Path m_Folder;
    public Path getFolder(){ return m_Folder; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IPeriod m_Period;
    public IPeriod getPeriod(){ return m_Period; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private boolean m_Active;
    public boolean isActive(){ return m_Active; }
    public void setActive( final boolean fActive ){ m_Active = fActive; }
    // ------------------------------------------------------------------------

    public PeriodData( final Path fFolder )
    {
        m_Folder = fFolder;
        m_Period = fFolder == null ? null : Halfyear.create( fFolder.getFileName().toString() );
    }

    @Override
    public String toString()
    {
        final String aPeriodString = getPeriod().toString();
        return aPeriodString + (isActive() ? "*" : "");
    }
}

// ############################################################################
