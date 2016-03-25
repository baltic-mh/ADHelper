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

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.ADH_Application;

// ############################################################################
public class ExportListener implements ActionListener
{
    private static final Logger sm_Log = Logger.getLogger(ExportListener.class);

    private final ADH_Application  m_App;
    private final ADH_DataProvider m_DataProvider;

    public ExportListener(
            final ADH_Application fFrame,
            final ADH_DataProvider fDataProvider)
    {
        m_App = fFrame;
        m_DataProvider = fDataProvider;
    }


    @Override
    public void actionPerformed( final ActionEvent fE )
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
            System.exit(0);
        }catch( final IOException fEx ){
            final String aMsg = "Probleme beim Export der Daten: "+fEx.getMessage();
            JOptionPane.showMessageDialog(m_App.getFrame(), aMsg, "Schwerwiegender Fehler!",
                    JOptionPane.ERROR_MESSAGE);
            sm_Log.warn("Exception: "+aMsg, fEx );
        }
    }


}

// ############################################################################
