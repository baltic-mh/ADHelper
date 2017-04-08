/**
 * HoursCellEditor.java
 *
 * Created on 08.04.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

// ############################################################################
public class HoursCellEditor extends AbstractCellEditor implements TableCellEditor
{
    private static final long serialVersionUID = 5806170681513849518L;

    private final JTextField    m_TextField;

    public HoursCellEditor()
    {
        super();
        m_TextField = new JTextField();
        m_TextField.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object fRawValue, final boolean isSelected,
            final int rowIndex, final int vColIndex)
    {
        if( fRawValue == null ){
            m_TextField.setText("0,00");
        } else {
            final Double aDoubleValue = UIUtils.getDoubleValue( fRawValue );
            m_TextField.setText( String.format("%.2f", aDoubleValue) );
        }

        return m_TextField;
    }

    @Override
    public Object getCellEditorValue()
    {
        final Double aDoubleValue = UIUtils.getDoubleValue( ( m_TextField.getText() ) );
        return aDoubleValue;
    }
}

// ############################################################################
