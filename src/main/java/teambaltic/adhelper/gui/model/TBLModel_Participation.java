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

import teambaltic.adhelper.gui.UIUtils;

// ############################################################################
public class TBLModel_Participation extends DefaultTableModel
{
    private static final long serialVersionUID = 3040033628089631180L;

    // Solange die abgeleiteten Klassen dieselbe Reihenfolge der Spalten haben,
    // können diese Konstanten verwendet werden. Sonst müssen die Methoden
    // überschrieben werden.
    private static final int COLUMN_IDX_PARTICIPATIONFLAG = 0;
    public int getColIdx_Participationflag(){ return COLUMN_IDX_PARTICIPATIONFLAG; }

    private static final int COLUMN_IDX_ID = 1;
    public int getColIdx_ID(){ return COLUMN_IDX_ID; }

    private static final int COLUMN_IDX_NAME = 2;
    public int getColIdx_Name(){ return COLUMN_IDX_NAME; }

    private static final int COLUMN_IDX_HOURS  = 3;
    public int getColIdx_Hours(){ return COLUMN_IDX_HOURS; }

    private final Integer [] m_EDITABLECOLUMNS;

    // ------------------------------------------------------------------------
    private final Class<?>[] m_COLUMNCLASSES;
    public Class<?>[] getCOLUMNCLASSES(){ return m_COLUMNCLASSES; }
    // ------------------------------------------------------------------------

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
        Object aNewValue = fNewValue;
        if( fCol == getColIdx_Hours() ){
            final Double aNewHourValue = UIUtils.getDoubleValue( fNewValue );
            final Double aAdjustedHoursValue = adjustHoursValue( aNewHourValue );

            final Double aOldHourValue = (Double) getValueAt(fRow, fCol);
            if( !areValuesEqual( aOldHourValue, aAdjustedHoursValue ) ){
                setDirty( true );
            }
            aNewValue = aAdjustedHoursValue;
        }
        super.setValueAt( aNewValue, fRow, fCol );
    }

    private static boolean areValuesEqual( final Double fOldValue, final Double fNewValue )
    {
        // NewValue ist nie null!
        if( fOldValue == null ){
            if( fNewValue.doubleValue() == 0.0 ){
                return true;
            }
        }
        return fNewValue.equals( fOldValue );
    }

    public Double getHours( final int aIdx )
    {
        final Object aValue = getValueAt( aIdx, getColIdx_Hours() );
        final Double aHoursValue = UIUtils.getDoubleValue(aValue);
        return aHoursValue;
    }

    private Double adjustHoursValue( final Double fDoubleValue )
    {
        final double aMaxValue = getMaxHoursValue();
        if( fDoubleValue.doubleValue() > aMaxValue ){
            return Double.valueOf( aMaxValue );
        }
        if( fDoubleValue.doubleValue() <= 0.0 ){
            return Double.valueOf( 0.0 );
        }

        final int aNurVorkomma  = 100*fDoubleValue.intValue();
        final int aMitNachKomma = (int) Math.round(100.0*fDoubleValue.doubleValue());
        final int aNurNachKomma = aMitNachKomma - aNurVorkomma;
        final int aViertel = (aNurNachKomma +12) / 25;
        final int aAdjusted = aNurVorkomma + aViertel*25;
        return Double.valueOf( aAdjusted / 100.0  );
    }

    public double getMaxHoursValue()
    {
        final double aMaxValue = 10.0;
        return aMaxValue;
    }

}

// ############################################################################
