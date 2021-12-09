/**
 * FileDiffDialog.java
 *
 * Created on 05.12.2021
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2021 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDialog;

import teambaltic.adhelper.utils.FileComparisonResult;
import teambaltic.adhelper.utils.IntegrityChecker;

// ############################################################################
public class FileDiffDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 3822126796525411791L;

    // ------------------------------------------------------------------------
    private final FileDiffPanel m_contentPanel;
    public FileDiffPanel getContentPanel(){ return m_contentPanel; }
    // ------------------------------------------------------------------------

    private boolean m_DataAccepted;

    public FileDiffDialog( final String fTitle )
    {
        setTitle(fTitle);
        setModalityType(ModalityType.APPLICATION_MODAL);
        m_contentPanel = new FileDiffPanel();
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        setBounds( 100, 100, 1250, 310 );
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add( m_contentPanel );
        m_contentPanel.getBtnOK().addActionListener(this);
        m_contentPanel.getBtnCancel().addActionListener(this);

    }

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        try{
            final FileDiffDialog dialog = new FileDiffDialog( "FileDiff" );
            final FileComparisonResult aDiff = IntegrityChecker.compare(new File("Arbeitsdienstabrechnungen\\Daten\\2020-07-01 - 2020-12-31\\BasisDaten.csv"),
                    new File("Arbeitsdienstabrechnungen\\Daten\\BasisDaten_2019-12-02_18-50-16.csv") );
            final boolean aAccepted = dialog.accept( aDiff );
            System.out.println("Accepted: "+aAccepted);
        }catch( final Exception e ){
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed( final ActionEvent fE ) {
        m_DataAccepted = false;
        switch( fE.getActionCommand() ) {
            case "OK":
                m_DataAccepted = true;
                break;
            case "Cancel":
                break;
            default:
        }
        this.dispose();
    }

    public boolean accept(final FileComparisonResult fDiff) {
        final FileDiffPanel aFileDiffPanel = getContentPanel();
        aFileDiffPanel.populate( fDiff  );
        setVisible(true);
        return m_DataAccepted;
    }

    public static boolean showDiffPanel(final FileComparisonResult fDiff) {

        final FileDiffDialog aFileDiffDialog = new FileDiffDialog("Neue Daten für die aktive Periode!");
        return aFileDiffDialog.accept( fDiff );
    }
}

// ############################################################################
