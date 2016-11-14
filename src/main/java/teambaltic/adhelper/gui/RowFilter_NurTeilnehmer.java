/**
 * RowFilter_NurTeilnehmer.java
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
public class RowFilter_NurTeilnehmer extends RowFilter<TBLModel_WorkEvents, Integer>
{
    // ------------------------------------------------------------------------
    private boolean m_Enabled;
    public boolean isEnabled(){ return m_Enabled; }
    public void setEnabled( final boolean fSet ){ m_Enabled = fSet; }
    // ------------------------------------------------------------------------

    public RowFilter_NurTeilnehmer()
    {

    }

    @Override
    public boolean include(final RowFilter.Entry<? extends TBLModel_WorkEvents, ? extends Integer> fEntry)
    {
        if( !isEnabled() ){
            return true;
        }
        Object aValue = fEntry.getValue( 0 );
        if( aValue instanceof Boolean ){
            final boolean aWarDabei = ((Boolean)aValue).booleanValue();
            if( aWarDabei ){
                return true;
            }
        }
        aValue = fEntry.getValue( 3 );
        if( aValue instanceof Double ){
            final boolean aHatGearbeitet = ((Double)aValue).doubleValue() > 0.0;
            if( aHatGearbeitet ){
                return true;
            }
        }
        return false;
    }

}

// ############################################################################
