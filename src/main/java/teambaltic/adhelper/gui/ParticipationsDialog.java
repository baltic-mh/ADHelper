/**
 * ParticipationsDialog.java
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

import java.awt.BorderLayout;

import javax.swing.JDialog;

// ############################################################################
public abstract class ParticipationsDialog extends JDialog
{
    private static final long serialVersionUID = -7487662229096717267L;

    // ------------------------------------------------------------------------
    private final ParticipationsPanel m_contentPanel;
    public ParticipationsPanel getContentPanel(){ return m_contentPanel; }
    // ------------------------------------------------------------------------

    abstract
    protected ParticipationsPanel createContentPanel();
    /**
     * Create the dialog.
     */
    public ParticipationsDialog( final String fTitle )
    {
        setTitle(fTitle);
        m_contentPanel = createContentPanel();
        setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
        setBounds( 100, 100, 550, 910 );
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add( m_contentPanel );

    }

}

// ############################################################################
