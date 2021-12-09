/**
 * TBLModel_FilesDiff.java
 *
 * Created on 05.12.2021
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2021 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.model;

import javax.swing.table.DefaultTableModel;

// ############################################################################
public class TBLModel_FilesDiff extends DefaultTableModel {
    private static final long serialVersionUID = 316283188972541475L;

    public TBLModel_FilesDiff(final String[] fColumnHeaders)
    {
        super( null, fColumnHeaders);
    }

    @Override
    public boolean isCellEditable( final int rowIndex, final int columnIndex )
    {
        return false;
    }
}

// ############################################################################
