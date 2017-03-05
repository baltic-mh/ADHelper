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
public class TBLModel_Participation extends DefaultTableModel
{
    // Der Stundenwert muss immer in der vierten Spalte stehen:
    public static final int COLUMN_IDX_HOURS = 3;

    private static final long serialVersionUID = 3040033628089631180L;

    private final Class<?>[] m_COLUMNCLASSES;
    private final Integer [] m_EDITABLECOLUMNS;

    // ------------------------------------------------------------------------
    private final boolean m_ReadOnly;
    public boolean isReadOnly(){ return m_ReadOnly; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private boolean m_Dirty;
    public boolean isDirty(){ return m_Dirty; }
    private void setDirty( final boolean fDirty ){ m_Dirty = fDirty; }
    // ------------------------------------------------------------------------

    public TBLModel_Participation(
            final String[] fCOLUMNHEADERS,
            final Class<?>[] fCOLUMNCLASSES,
            final Integer [] fEDITABLECOLUMNS,
            final Object[][] fData, final boolean fReadOnly)
    {
        super( fData, fCOLUMNHEADERS);
        m_COLUMNCLASSES     = fCOLUMNCLASSES;
        m_EDITABLECOLUMNS   = fEDITABLECOLUMNS;
        m_ReadOnly          = fReadOnly;
    }

    @Override
    public Class<?> getColumnClass(final int fColIdx) {
        if( fColIdx < 0 || fColIdx >= m_COLUMNCLASSES.length ){
            return null;
        }
        return m_COLUMNCLASSES[fColIdx];
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
        if( isReadOnly() ){
            return false;
        }
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        for( final Integer aEDITABLECOLUMN : m_EDITABLECOLUMNS ){
            if( aEDITABLECOLUMN.intValue() == col ){
                return true;
            }
        }
        return false;
    }

    @Override
    public void setValueAt(final Object fNewValue, final int fRow, final int fCol)
    {
        if( fCol == COLUMN_IDX_HOURS ){
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

    public Double getHours( final int aIdx )
    {
        final Double aHoursValue = (Double) getValueAt( aIdx, COLUMN_IDX_HOURS );
        return aHoursValue;
    }

}

// ############################################################################
