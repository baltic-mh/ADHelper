/**
 * WorkedHoursSpinnerEditor.java
 *
 * Created on 23.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

// ############################################################################
public class HoursSpinnerEditor extends AbstractCellEditor implements TableCellEditor
{
    private static final long serialVersionUID = 3347552072237679344L;

    private final JSpinner spinner;

    public HoursSpinnerEditor()
    {
       spinner = new JSpinner(new SpinnerNumberModel(Double.valueOf( 0.0 ), Double.valueOf( 0.0 ), Double.valueOf( 10.0 ), Double.valueOf( 0.25 )));
       final JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "0.##");
       spinner.setEditor(editor);
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value,
                     final boolean isSelected, final int row, final int column)
    {
        spinner.setValue( value == null ? Double.valueOf(0.0) : value );
        return spinner;
    }

    @Override
    public Object getCellEditorValue() {
       final Object aValue = spinner.getValue();
       return aValue;
    }
}
// ############################################################################
