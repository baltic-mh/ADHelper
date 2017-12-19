/**
 * DutyChargeTabelModel.java
 *
 * Created on 10.02.2016
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
public class TBLModel_DutyCharge extends DefaultTableModel
{

    private static final long serialVersionUID = 7877592326769627796L;

    public TBLModel_DutyCharge()
    {
        super( null,
                new String[] {
                        "Name", "Guthaben", "Korrektur", "Gearbeitet", "Pflicht", "Guthaben II", "Zu zahlen", "Guthaben III"
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
