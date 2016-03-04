/**
 * ExportListener.java
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.model.IPeriod;

// ############################################################################
public class ExportListener implements ActionListener
{
    private static final Logger sm_Log = Logger.getLogger(ExportListener.class);

    private final MainPanel m_Panel;
    private final ADH_DataProvider m_DataProvider;
    private final String m_DataFolderName;

    public ExportListener(
            final MainPanel fPanel,
            final ADH_DataProvider fDataProvider,
            final String fDataFolderName)
    {
        m_Panel = fPanel;
        m_DataProvider = fDataProvider;
        m_DataFolderName = fDataFolderName;
    }


    @Override
    public void actionPerformed( final ActionEvent fE )
    {
        final IPeriod aInvoicingPeriod = m_DataProvider.getInvoicingPeriod();
        if( aInvoicingPeriod == null ){
            JOptionPane.showMessageDialog(m_Panel, "Es wurde noch kein Abrechnungszeitraum ausgewählt!",
                    "Ups!",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        final Path aOutputFolder = Paths.get( m_DataFolderName, aInvoicingPeriod.toString() );
        if( Files.exists( aOutputFolder ) ){
            final Object[] options = {"Ich weiß, was ich tue!", "Nein, das war ein Versehen!"};
            final int n = JOptionPane.showOptionDialog(null,
                "Es hat bereits eine Ausgabe für diesen Abrechnungszeitraum stattgefunden! Sollen die Daten überschrieben werden??",
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
        } else {
            try{
                Files.createDirectories( aOutputFolder );
            }catch( final IOException fEx ){
                final String aMsg = "Konnte Verzeichnis nicht anlegen: "+aOutputFolder;
                JOptionPane.showMessageDialog(m_Panel, aMsg, "Schwerwiegender Fehler!",
                        JOptionPane.ERROR_MESSAGE);
                sm_Log.warn("Exception: "+aMsg, fEx );
            }
        }

        m_DataProvider.export( aOutputFolder );
    }

}

// ############################################################################
