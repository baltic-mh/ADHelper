/**
 * HoursCellRenderer.java
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

import javax.swing.table.DefaultTableCellRenderer;

// ############################################################################
public class HoursCellRenderer extends DefaultTableCellRenderer
{
    private static final long serialVersionUID = -3270972513509650969L;

    public HoursCellRenderer()
    {
        super();
        setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
    }

    @Override
    public void setValue( final Object fRawValue )
    {
        String aStringValue = "";
        if( fRawValue != null ){
            final Double aDoubleValue = UIUtils.getDoubleValue( fRawValue );
            if( aDoubleValue != 0.0 ){
                aStringValue = String.format("%.2f",aDoubleValue);
            }
        }
        setText( aStringValue );
    }

}

// ############################################################################
