/**
 * AdjustmentsPanel.java
 *
 * Created on 06.03.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import javax.swing.JButton;

// ############################################################################
public class AdjustmentsPanel extends ParticipationsPanel
{
    private static final long serialVersionUID = 157096461352369629L;

    /**
     * Create the panel.
     */
    public AdjustmentsPanel()
    {
        super();
    }

    @Override
    protected void configureButtons( final boolean fReadOnly )
    {
        super.configureButtons( fReadOnly );
        final JButton aBtn_Neu = getBtn_Neu();
        aBtn_Neu.setEnabled( false );
    }

}

// ############################################################################
