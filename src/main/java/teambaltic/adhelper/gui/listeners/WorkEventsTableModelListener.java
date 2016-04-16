/**
 * WorkEventsTableModelListener.java
 *
 * Created on 12.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.listeners;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import teambaltic.adhelper.gui.model.TBLModel_WorkEvents;

// ############################################################################
public class WorkEventsTableModelListener implements TableModelListener
{

    @Override
    public void tableChanged( final TableModelEvent fEvent )
    {
        final int row = fEvent.getFirstRow();
        final int col = fEvent.getColumn();
        final TBLModel_WorkEvents model = (TBLModel_WorkEvents)fEvent.getSource();
        final String columnName = model.getColumnName(col);
        final Object data = model.getValueAt(row, col);

        // Do something with the data...
        System.out.println(String.format("Data changed: col %s row %d - val %s",
                columnName, row, data));
    }

}

// ############################################################################
