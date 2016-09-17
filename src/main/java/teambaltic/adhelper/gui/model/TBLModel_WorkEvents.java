/**
 * TBLModel_WorkEvents.java
 *
 * Created on 12.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.model;

import javax.swing.table.DefaultTableModel;

// ############################################################################
public class TBLModel_WorkEvents extends DefaultTableModel
{
    private static final long serialVersionUID = 3040033628089631180L;

    private static final String[] COLUMNHEADERS = new String[] { "Teilnahme", "ID", "Name", "Stunden" };

    // ------------------------------------------------------------------------
    private final boolean m_ReadOnly;
    public boolean isReadOnly(){ return m_ReadOnly; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private boolean m_Dirty;
    public boolean isDirty(){ return m_Dirty; }
    private void setDirty( final boolean fDirty ){ m_Dirty = fDirty; }
    // ------------------------------------------------------------------------

    public TBLModel_WorkEvents(final Object[][] fData, final boolean fReadOnly)
    {
        super( fData, COLUMNHEADERS);
        m_ReadOnly = fReadOnly;
    }

    @Override
    public Class<?> getColumnClass(final int c) {
        switch (c) {
            case 0 :
                return Boolean.class;

            case 1 :
                return Integer.class;

            case 2 :
                return String.class;

            case 3 :
                return Float.class;

            default :
                return null;
        }
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
        if( isReadOnly() ){
            return false;
        }
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if ( col == 0 || col == 3 ) {
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt(final Object fNewValue, final int fRow, final int fCol)
    {
        if( fCol == 3 ){
            final Object aOldValue = getValueAt(fRow, fCol);
            if( !areValuesEqual( aOldValue, fNewValue ) ){
                setDirty( true );
            }
        }
        super.setValueAt( fNewValue, fRow, fCol );
    }

    private static boolean areValuesEqual( final Object fOldValue, final Object fNewValue )
    {
        // NewValue ist nie null!
        if( fOldValue == null ){
            if( ((Double)fNewValue).doubleValue() == 0.0 ){
                return true;
            }
        }
        return fNewValue.equals( fOldValue );
    }
}

// ############################################################################
