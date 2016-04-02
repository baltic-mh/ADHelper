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
import teambaltic.adhelper.controller.ITransferController;
import teambaltic.adhelper.controller.IntegrityChecker;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.model.ERole;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class UploadListener implements ActionListener
{
    private static final Logger sm_Log = Logger.getLogger(FinishListener.class);

    private final MainPanel         m_Panel;
    private final ADH_DataProvider  m_DataProvider;

    private final ITransferController m_TransferController;

    private final UserSettingsListener  m_UserSettingsListener;

    public UploadListener(
            final MainPanel fPanel,
            final ADH_DataProvider fDataProvider,
            final ITransferController fTransferController,
            final UserSettingsListener fUserSettingsListener)
    {
        m_Panel                 = fPanel;
        m_DataProvider          = fDataProvider;
        m_TransferController    = fTransferController;
        m_UserSettingsListener  = fUserSettingsListener;
    }

    @Override
    public void actionPerformed( final ActionEvent fEvent )
    {
        try{
            boolean aUploaded = false;
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
                    aUploaded = m_TransferController.uploadBillingData();
                    break;
                case MITGLIEDERWART:
                    aUploaded = uploadBaseData();

                default:
            }
            m_Panel.setUploaded(aUploaded);

        }catch( final Exception fEx ){
            final String aMsg = "Probleme beim Hochladen der Daten: "+fEx.getMessage();
            JOptionPane.showMessageDialog(m_Panel, aMsg, "Schwerwiegender Fehler!",
                    JOptionPane.ERROR_MESSAGE);
            sm_Log.warn("Exception: "+aMsg );
        }
    }

    private boolean uploadBaseData() throws Exception
    {
        final JFileChooser aFileChooser = new JFileChooser();
        aFileChooser.setCurrentDirectory( m_DataProvider.getBaseDataFile() );
        final int aResult = aFileChooser.showOpenDialog(m_Panel);
        if( aResult == JFileChooser.APPROVE_OPTION ) {
            final File aSelectedFile = aFileChooser.getSelectedFile();
            IntegrityChecker.checkBaseDataFile( aSelectedFile );
            final Path aBaseDataFile = copyToDataFolder( aSelectedFile );
            if( m_TransferController == null || !m_TransferController.isConnected() ){
                throw new IOException("Keine Verbindung zu Server!");
            }
            m_TransferController.upload( aBaseDataFile );
            return true;
        }
        return false;
    }

    private Path copyToDataFolder( final File fSelectedFile ) throws IOException
    {
        final File aBaseDataFile = m_DataProvider.getBaseDataFile();
        final Path aBaseDataFileAsPath = aBaseDataFile.toPath();
        FileUtils.makeBackupCopy( aBaseDataFileAsPath );
        Files.copy( fSelectedFile.toPath(), aBaseDataFileAsPath, StandardCopyOption.REPLACE_EXISTING );

        return aBaseDataFileAsPath;
    }

}

// ############################################################################
