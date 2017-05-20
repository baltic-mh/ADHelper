/**
 * GeneratePDFReportListener.java
 *
 * Created on 20.05.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.report.PDFReporter;

// ############################################################################
public class GeneratePDFReportListener implements ActionListener
{
    private static final Logger sm_Log = Logger.getLogger(GeneratePDFReportListener.class);

    // ------------------------------------------------------------------------
    private ADH_DataProvider  m_DataProvider;
    private ADH_DataProvider getDataProvider(){ return m_DataProvider; }
    public void setDataProvider( final ADH_DataProvider fDataProvider ){ m_DataProvider = fDataProvider; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Path m_OutputFolder;
    private Path getOutputFolder(){ return m_OutputFolder;}
    // ------------------------------------------------------------------------

    public GeneratePDFReportListener(final Path fOutputFolder)
    {
        m_OutputFolder = fOutputFolder;
    }

    @Override
    public void actionPerformed( final ActionEvent fE )
    {
        if( getDataProvider() == null || getOutputFolder() == null ){
            // Noch nicht initialisiert!
            return;
        }
        if( !Files.exists( m_OutputFolder )){
            try{
                Files.createDirectories( m_OutputFolder );
            }catch( final Throwable fEx ){
                sm_Log.warn("Probleme beim Erzeugen des PDF-Reports: ", fEx );
                return;
            }
        }
        PDFReporter.report( m_DataProvider, m_OutputFolder );
    }

}

// ############################################################################
