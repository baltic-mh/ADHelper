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

import java.awt.BorderLayout;

import javax.swing.JDialog;

import teambaltic.adhelper.gui.model.TBLModel_WorkEvents;

// ############################################################################
public class WorkEventsDialog extends JDialog
{
    private static final long serialVersionUID = -7487662229096717267L;

    private static int IDX_SEED = 100100;
    private static final Object[][] DATA = {
            { Boolean.FALSE, Integer.valueOf( IDX_SEED++ ), "Smith, Kathy", Float.valueOf(123.0f) },
            { Boolean.FALSE, Integer.valueOf( IDX_SEED++ ), "Black, Joe", null },
            { Boolean.FALSE, Integer.valueOf( IDX_SEED++ ), "Flash, Jumpin Jack", null },
            { Boolean.TRUE, Integer.valueOf( IDX_SEED++ ), "White, Jane", null }
        };

    // ------------------------------------------------------------------------
    private final WorkEventsPanel m_contentPanel;
    public WorkEventsPanel getContentPanel(){ return m_contentPanel; }
    // ------------------------------------------------------------------------

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
        m_contentPanel.populate( aModel );
    }

    /**
     * Create the dialog.
     */
    public WorkEventsDialog()
    {
        setTitle("Arbeitsdienste");
        m_contentPanel = new WorkEventsPanel();
        setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
        setBounds( 100, 100, 550, 910 );
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add( m_contentPanel );

    }

}

// ############################################################################
