/**
 * UploadListener.java
 *
 * Created on 26.03.2016
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.controller.ITransferController;
import teambaltic.adhelper.gui.FileDiffDialog;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.model.ERole;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.utils.FileComparisonResult;
import teambaltic.adhelper.utils.FileUtils;
import teambaltic.adhelper.utils.IntegrityChecker;

// ############################################################################
public class UploadListener implements ActionListener
{
    private static final Logger sm_Log = Logger.getLogger(FinishListener.class);

    private final MainPanel         m_Panel;

    private final ITransferController m_TransferController;
    private final UserSettingsListener  m_UserSettingsListener;

    // ------------------------------------------------------------------------
    private final IPeriodDataController m_PDC;
    private IPeriodDataController getPDC(){ return m_PDC; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final ADH_DataProvider m_DataProvider;
    private ADH_DataProvider getDataProvider(){ return m_DataProvider; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final GUIUpdater m_GUIUpdater;
    private GUIUpdater getGUIUpdater(){ return m_GUIUpdater; }
    // ------------------------------------------------------------------------


    public UploadListener(
            final MainPanel fPanel,
            final ADH_DataProvider      fDataProvider,
            final ITransferController   fTransferController,
            final UserSettingsListener  fUserSettingsListener,
            final IPeriodDataController fPDC,
            final GUIUpdater fGUIUpdater )
    {
        m_Panel                 = fPanel;
        m_TransferController    = fTransferController;
        m_UserSettingsListener  = fUserSettingsListener;
        m_PDC                   = fPDC;
        m_DataProvider          = fDataProvider;
        m_GUIUpdater            = fGUIUpdater;
    }

    @Override
    public void actionPerformed( final ActionEvent fEvent )
    {
        try{
            ERole aRole = m_UserSettingsListener.getUserSettings().getRole();
            while( aRole == null ) {
                final String aMsg = "Esikmos dürfen hier schon mal gar nichts! Bitte Rolle angeben!";
                JOptionPane.showMessageDialog(m_Panel, aMsg, "Schwerwiegender Fehler!",
                        JOptionPane.ERROR_MESSAGE);
                m_UserSettingsListener.getDialog().setVisible( true );
                aRole = m_UserSettingsListener.getUserSettings().getRole();
            }
            switch( aRole ){
                case BAUAUSSCHUSS:
                    m_TransferController.uploadPeriodData();
                    break;
                case MITGLIEDERWART:
                    uploadBaseData();

                default:
            }

        }catch( final Exception fEx ){
            final String aMsg = String.format( "Probleme beim Hochladen der Daten:\n%s\nDatei wird nicht hochgeladen!",
                    fEx.getMessage());
            JOptionPane.showMessageDialog(m_Panel, aMsg, "Schwerwiegender Fehler!",
                    JOptionPane.ERROR_MESSAGE);
            sm_Log.warn("Exception: "+ fEx.getMessage() );
        }
    }

    private boolean uploadBaseData() throws Exception
    {
        final JFileChooser aFileChooser = new JFileChooser();
        final Path aFolder_Data = getDataFolder();
        aFileChooser.setCurrentDirectory( aFolder_Data.toFile() );
        final int aResult = aFileChooser.showOpenDialog(m_Panel);
        if( aResult == JFileChooser.APPROVE_OPTION ) {
            final File aSelectedFile = aFileChooser.getSelectedFile();
            IntegrityChecker.checkBaseDataFile( aSelectedFile );
            final PeriodData aNewestPeriodData = getPDC().getNewestPeriodData();
            final Path aCurrentBaseDataFile = getPDC().getFile_BaseData(aNewestPeriodData);
            final FileComparisonResult aDiff = IntegrityChecker.compare( aCurrentBaseDataFile.toFile(), aSelectedFile );
            if( !aDiff.filesDiffer() ) {
                sm_Log.info("Hochzuladende Daten sind identisch mit Daten der aktuellen Periode! Kein Upload.");
                return false;
            }
            final boolean aAcceptData = FileDiffDialog.showDiffPanel(aDiff);
            if( aAcceptData ) {
                final Path aBaseDataFile = copyToDataFolder( aSelectedFile );
                if( m_TransferController == null || !m_TransferController.isConnected() ){
                    throw new IOException("Keine Verbindung zu Server!");
                }
                m_TransferController.upload( aBaseDataFile );
                final PeriodData aNewestPeriodDataUpdated = getPDC().createNewPeriod();
                getDataProvider().init( aNewestPeriodDataUpdated );
                getGUIUpdater().updateGUI( aNewestPeriodDataUpdated );

                return true;
            }
        }
        return false;
    }

    private static Path copyToDataFolder( final File fSelectedFile ) throws IOException
    {
        final Path aBaseDataFile = getRootBaseDataFile();
        FileUtils.makeBackupCopy( aBaseDataFile );
        Files.copy( fSelectedFile.toPath(), aBaseDataFile, StandardCopyOption.REPLACE_EXISTING );

        return aBaseDataFile;
    }

    private static Path getDataFolder()
    {
        return AllSettings.INSTANCE.getAppSettings().getFolder_Data();
    }

    private static Path getRootBaseDataFile()
    {
        return AllSettings.INSTANCE.getAppSettings().getFile_RootBaseData();
    }

}

// ############################################################################
