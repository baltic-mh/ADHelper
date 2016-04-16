/**
 * RowFilter_Text.java
 *
 * Created on 12.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import javax.swing.RowFilter;

import teambaltic.adhelper.gui.model.TBLModel_WorkEvents;

// ############################################################################
public class RowFilter_Text extends RowFilter<TBLModel_WorkEvents, Object>
{
    // ------------------------------------------------------------------------
    private RowFilter<TBLModel_WorkEvents, Object> m_RowFilter;
    private RowFilter<TBLModel_WorkEvents, Object> getRowFilter(){ return m_RowFilter; }
    // ------------------------------------------------------------------------

    public RowFilter_Text()
    {
    }

    @Override
    public boolean include(final javax.swing.RowFilter.Entry<? extends TBLModel_WorkEvents, ? extends Object> fEntry)
    {
        if( getRowFilter() == null ){
            return true;
        }
        return getRowFilter().include( fEntry );
    }

    public void setRegExp(final String fRegexp)
    {
        try {
            m_RowFilter = RowFilter.regexFilter("(?i)"+fRegexp, 2);
        } catch (final java.util.regex.PatternSyntaxException e) {
            //If current expression doesn't parse, don't update.
            return;
        }
    }

    public void clear()
    {
        m_RowFilter = null;
    }
}

// ############################################################################
