/**
 * AdjustmentDialogs.java
 *
 * Created on 12.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import javax.swing.JDialog;

import teambaltic.adhelper.gui.model.TBLModel_Adjustments;

// ############################################################################
public class AdjustmentsDialog extends ParticipationsDialog
{
    private static final long serialVersionUID = -7487662229096717267L;

    private static int IDX_SEED = 100100;

    private static final Object[][] DATA = {
            { Boolean.FALSE, Integer.valueOf( IDX_SEED++ ), "Smith, Kathy", Float.valueOf(123.0f), "Kommentar 1" },
            { Boolean.FALSE, Integer.valueOf( IDX_SEED++ ), "Black, Joe", null, null },
            { Boolean.FALSE, Integer.valueOf( IDX_SEED++ ), "Flash, Jumpin Jack", null, null },
            { Boolean.TRUE, Integer.valueOf( IDX_SEED++ ), "White, Jane", null, null }
        };

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        try{
            final AdjustmentsDialog dialog = new AdjustmentsDialog();
            dialog.populate();
            dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
            dialog.setVisible( true );
        }catch( final Exception e ){
            e.printStackTrace();
        }
    }

    private void populate()
    {
        final TBLModel_Adjustments aModel = new TBLModel_Adjustments( DATA, false );
        getContentPanel().populate( aModel, null );
    }

    /**
     * Create the dialog.
     */
    public AdjustmentsDialog()
    {
        super("Gutschriften/Abzüge");
    }

    @Override
    protected ParticipationsPanel createContentPanel()
    {
        return new AdjustmentsPanel();
    }

}

// ############################################################################
