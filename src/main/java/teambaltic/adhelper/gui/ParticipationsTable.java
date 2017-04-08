/**
 * ParticipationsTable.java
 *
 * Created on 07.04.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

// ############################################################################
public class ParticipationsTable extends JTable
{
    private static final long serialVersionUID = -902549756144605630L;

    public ParticipationsTable()
    {
        // Ich glaube, diese Property bewirkt nix...
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

    }

    @Override
    public void changeSelection(final int row, final int column, final boolean toggle, final boolean extend)
    {
        stopEditing();
        super.changeSelection(row, 3, false, false);
        editCellAt(row, 3);
        transferFocus();
    }

    private void stopEditing()
    {
        if( isEditing() ){
            final TableCellEditor tce = getCellEditor();
            tce.stopCellEditing();
        }
        editingStopped( null );
    }

    /*
     *  Override to provide Select All editing functionality
     */
    @Override
    public boolean editCellAt(final int row, final int column, final EventObject e)
    {
        final boolean result = super.editCellAt(row, column, e);
        selectAll(e);
        return result;
    }
    /*
     * Select the text when editing on a text related cell is started
     */
    private void selectAll(final EventObject e)
    {
        final Component editor = getEditorComponent();

        if (editor == null ){
            return;
        }

        if ( ! (editor instanceof JTextComponent) ){
            return;
        }

        if (e == null)
        {
            ((JTextComponent)editor).selectAll();
            return;
        }

        //  Typing in the cell was used to activate the editor

        if ( e instanceof KeyEvent )
        {
            ((JTextComponent)editor).selectAll();
            return;
        }

        //  F2 was used to activate the editor

        if ( e instanceof ActionEvent )
        {
            ((JTextComponent)editor).selectAll();
            return;
        }

        //  A mouse click was used to activate the editor.
        //  Generally this is a double click and the second mouse click is
        //  passed to the editor which would remove the text selection unless
        //  we use the invokeLater()

        if ( e instanceof MouseEvent )
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    ((JTextComponent)editor).selectAll();
                }
            });
        }
    }
}

// ############################################################################
