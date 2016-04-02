/**
 * FinishListener.java
 *
 * Created on 14.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.MainPanel;

// ############################################################################
public class FinishListener implements ActionListener
{
    private static final Logger sm_Log = Logger.getLogger(FinishListener.class);

    private final MainPanel         m_Panel;
    private final ADH_DataProvider  m_DataProvider;

    public FinishListener(
            final MainPanel fPanel,
            final ADH_DataProvider fDataProvider)
    {
        m_Panel = fPanel;
        m_DataProvider = fDataProvider;
    }

    @Override
    public void actionPerformed( final ActionEvent fEvent )
    {
        if( m_DataProvider.isOutputFinished() ){
            final Object[] options = {"Ich weiß, was ich tue!", "Nein, das war ein Versehen!"};
            final int n = JOptionPane.showOptionDialog(null,
                "Dieser Abrechnungszeitraum ist bereits abgeschlossen! Sollen die Daten überschrieben werden??",
                "Sind Sie ganz sicher?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]);
            switch( n ){
                case 1:
                    return;

                default:
            }
        }

        try{
            m_DataProvider.export( true );
            m_Panel.setFinished();
            final File[] aNotUploadedFolders = m_DataProvider.getNotUploadedFolders();
            m_Panel.setUploaded( aNotUploadedFolders.length == 0 );
        }catch( final IOException fEx ){
            final String aMsg = "Probleme beim Export der Daten: "+fEx.getMessage();
            JOptionPane.showMessageDialog(m_Panel, aMsg, "Schwerwiegender Fehler!",
                    JOptionPane.ERROR_MESSAGE);
            sm_Log.warn("Exception: "+aMsg, fEx );
        }
    }


}

// ############################################################################
