/**
 * WorkEventsDialog.java
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

import teambaltic.adhelper.gui.model.TBLModel_WorkEvents;

// ############################################################################
public class WorkEventsDialog extends ParticipationsDialog
{
    private static final long serialVersionUID = -7487662229096717267L;

    private static int IDX_SEED = 100100;
    private static final Object[][] DATA = {
            { Boolean.FALSE, Integer.valueOf( IDX_SEED++ ), "Smith, Kathy", Float.valueOf(123.0f) },
            { Boolean.FALSE, Integer.valueOf( IDX_SEED++ ), "Black, Joe", null },
            { Boolean.FALSE, Integer.valueOf( IDX_SEED++ ), "Flash, Jumpin Jack", null },
            { Boolean.TRUE, Integer.valueOf( IDX_SEED++ ), "White, Jane", null }
        };

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        try{
            final WorkEventsDialog dialog = new WorkEventsDialog();
            dialog.populate();
            dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
            dialog.setVisible( true );
        }catch( final Exception e ){
            e.printStackTrace();
        }
    }

    private void populate()
    {
        final TBLModel_WorkEvents aModel = new TBLModel_WorkEvents( DATA, false );
        getContentPanel().populate( aModel, null );
    }

    /**
     * Create the dialog.
     */
    public WorkEventsDialog()
    {
        super("Arbeitsdienste");

    }

    @Override
    protected ParticipationsPanel createContentPanel()
    {
        return new WorkEventsPanel();
    }

}

// ############################################################################
