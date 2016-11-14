/**
 * TBLModel_AttendedWorkEvent.java
 *
 * Created on 11.02.2016
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
public class TBLModel_DutyFree extends DefaultTableModel
{
    private static final long serialVersionUID = -3057350695228513850L;

    public TBLModel_DutyFree()
    {
        super( null,
                new String[] {
                        "Grund", "von", "bis"
                    }
                );
    }

    @Override
    public boolean isCellEditable( final int rowIndex, final int columnIndex )
    {
        return false;
    }
}

// ############################################################################
