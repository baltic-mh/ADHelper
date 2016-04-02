/**
 * ADH_Application.java
 *
 * Created on 08.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IShutdownListener;
import teambaltic.adhelper.controller.ITransferController;
import teambaltic.adhelper.controller.InitHelper;
import teambaltic.adhelper.controller.IntegrityChecker;
import teambaltic.adhelper.gui.listeners.FinishListener;
import teambaltic.adhelper.gui.listeners.GUIUpdater;
import teambaltic.adhelper.gui.listeners.MemberSelectedListener;
import teambaltic.adhelper.gui.listeners.UploadListener;
import teambaltic.adhelper.gui.listeners.UserSettingsListener;
import teambaltic.adhelper.gui.listeners.WorkEventEditorActionListener;
import teambaltic.adhelper.gui.listeners.WorkEventTableListener;
import teambaltic.adhelper.gui.model.MemberComboBoxModel;
import teambaltic.adhelper.model.ERole;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings;
import teambaltic.adhelper.model.settings.IUserSettings;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class ADH_Application
{
    private static final Logger sm_Log = Logger.getLogger(ADH_Application.class);
    private static final String TITLE = "KVK Arbeitsdienst-Helferlein (C) 2016 TeamBaltic";

    // ------------------------------------------------------------------------
    private JFrame m_frame;
    public  JFrame getFrame(){ return m_frame; }
    // ------------------------------------------------------------------------

    private MainPanel m_panel;

    private final List<IShutdownListener> m_ShutdownListeners;

    // ------------------------------------------------------------------------
    private UserSettingsListener m_UserSettingsListener;
    private UserSettingsListener getUserSettingsListener(){ return m_UserSettingsListener; }
    void setUserSettingsListener( final UserSettingsListener fNewVal ){ m_UserSettingsListener = fNewVal; }
    // ------------------------------------------------------------------------

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        System.setProperty( "appname", "ADHelper" );
        Log4J.initLog4J();

        final ADH_Application aAppWindow = new ADH_Application();

        EventQueue.invokeLater( new Runnable() {

            @Override
            public void run()
            {
                try{
                    initSettings( aAppWindow );

                    IntegrityChecker.check( AllSettings.INSTANCE );

                    aAppWindow.initialize();
                    aAppWindow.setVisible( true );

                    ITransferController aTC = null;
                    boolean aOffline = true;
                    if( !stayLocal() ){
                        aTC = InitHelper.initTransferController( AllSettings.INSTANCE );
                        if( aTC != null ){
                            aTC.start();
                            if( aTC.isConnected() ){
                                aOffline = false;
                                updateDataFromServer( aTC );
                                IntegrityChecker.checkAfterUpdateFromServer( AllSettings.INSTANCE );
                            } else {
                                sm_Log.warn( "Keine Verbindung zum Server!" );
                            }

                        }
                    }
                    aAppWindow.setTitle( aOffline );
                    aAppWindow.addShutdownListener( aTC );
                    final ADH_DataProvider aDataProvider = InitHelper.initDataProvider();

                    aAppWindow.configure( aDataProvider );
                    aAppWindow.populate( aDataProvider, aTC );

                }catch( final Exception fEx ){
                    sm_Log.error( "Unerwartete Exception: ", fEx );
                    final String aMsg = ExceptionUtils.getStackTrace(fEx);
                    JOptionPane.showMessageDialog( aAppWindow.m_frame, aMsg, "Fataler Fehler!",
                                JOptionPane.ERROR_MESSAGE );
                    aAppWindow.shutdown(1);
                }
            }

        } );
    }

    protected void addShutdownListener( final IShutdownListener fSL )
    {
        if( fSL == null ){
            return;
        }
        synchronized( m_ShutdownListeners ){
            if( !m_ShutdownListeners.contains(fSL) ){
                m_ShutdownListeners.add( fSL );
            }
        }

    }
    /**
     * Create the application.
     */
    public ADH_Application()
    {
        m_ShutdownListeners = new ArrayList<>();
    }

    protected void setVisible( final boolean fB )
    {
        m_frame.setVisible( fB );
    }

    private void populate(
            final ADH_DataProvider fDataProvider,
            final ITransferController fTransferController )
    {
        final Collection<IClubMember> aAllMembers = fDataProvider.getMembers();
        final IClubMember[] aMemberArr = new IClubMember[aAllMembers.size()] ;
        final JComboBox<IClubMember> aCB_Members = m_panel.getCB_Members();
        final MemberComboBoxModel aMemberModel = new MemberComboBoxModel( aAllMembers.toArray( aMemberArr ) );
        aCB_Members.setModel( aMemberModel );
        final ActionListener aMemberSelectedListener = new MemberSelectedListener( m_panel, fDataProvider );
        aCB_Members.addActionListener( aMemberSelectedListener );

        final JTextField aWidget_InvoicingPeriod = m_panel.getWidget_InvoicingPeriod();
        aWidget_InvoicingPeriod.setText( fDataProvider.getInvoicingPeriod().toString() );

        // WorkEventEditor
        final WorkEventEditor aWorkEventEditor = new WorkEventEditor();
        final WorkEventEditorActionListener aWEEListener = new WorkEventEditorActionListener(aWorkEventEditor, m_panel, fDataProvider);

        final JComboBox<IClubMember> aCb_Members_WEE = aWorkEventEditor.getCB_Members();
        aWorkEventEditor.setWorkEventEditorListeners(aWEEListener);
        aCb_Members_WEE.setModel( aMemberModel );
        aCb_Members_WEE.addActionListener( aMemberSelectedListener );

        // Der WorkEventEditor wird noch mal gebraucht:
        final WorkEventTableListener aWorkEventTableListener = new WorkEventTableListener(aWorkEventEditor, m_panel, fDataProvider);
        m_panel.setWorkEventTableListener(aWorkEventTableListener);

        final JButton aBtnFinish = m_panel.getBtnFinish();
        aBtnFinish.addActionListener( new FinishListener( m_panel, fDataProvider ) );

        final JButton aBtnUpload = m_panel.getBtnUpload();
        aBtnUpload.addActionListener(
                new UploadListener( m_panel, fDataProvider, fTransferController,
                        getUserSettingsListener() ) );

        GUIUpdater.updateGUI( m_panel.getSelectedMemberID(), m_panel, fDataProvider );
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        m_frame = new JFrame();
        m_frame.setTitle(TITLE);
        m_frame.setBounds( 100, 100, 924, 580 );
        m_frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(final java.awt.event.WindowEvent e) {
                shutdown(0);
            }
        });

        m_frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("906px:grow"),},
            new RowSpec[] {
                RowSpec.decode("435px:grow"),}));

        m_panel = new MainPanel();
        m_frame.getContentPane().add(m_panel, "1, 1, fill, fill");

        final JMenuBar menuBar = new JMenuBar();
        m_frame.setJMenuBar(menuBar);

        final JMenu mnDatei = new JMenu("Datei");
        menuBar.add(mnDatei);

        final JMenuItem mntmBeenden = new JMenuItem("Beenden");
        mnDatei.add(mntmBeenden);

        final JMenu mnAktionen = new JMenu("Aktionen");
        menuBar.add(mnAktionen);

        final JMenuItem m_mnit_UserSettings = new JMenuItem("Benutzerdaten...");
        m_mnit_UserSettings.setActionCommand( "Editieren" );
        m_mnit_UserSettings.addActionListener( getUserSettingsListener() );
        mnAktionen.add(m_mnit_UserSettings);

        final Component horizontalGlue = Box.createHorizontalGlue();
        menuBar.add(horizontalGlue);

        final JMenu mnHilfe = new JMenu("Hilfe");
        menuBar.add(mnHilfe);
    }

    private static void initSettings( final ADH_Application fAppWindow ) throws Exception
    {
        AllSettings.INSTANCE.init();
        final IUserSettings aUserSettings = AllSettings.INSTANCE.getUserSettings();
        fAppWindow.setUserSettingsListener( populateUserSettings( aUserSettings ) );
    }

    private static UserSettingsListener populateUserSettings(final IUserSettings fUserSettings)
    {
        final UserSettingsDialog aDialog = new UserSettingsDialog();
        aDialog.getTf_Name().setText( fUserSettings.getName() );
        aDialog.getTf_EMail().setText( fUserSettings.getEMail() );
        final JButton aBtn_OK = aDialog.getBtn_OK();
        final UserSettingsListener l = new UserSettingsListener( aDialog, fUserSettings );
        aBtn_OK.addActionListener( l );

        final ERole aRole = fUserSettings.getRole();
        if( aRole == null ){
            aDialog.getCb_Role().setSelectedItem( ERole.MITGLIEDERWART );
            aDialog.setVisible( true );
        }

        return l;
    }

    public void shutdown(final int fExitCode)
    {
        synchronized( m_ShutdownListeners ){
            m_ShutdownListeners.forEach( aListener -> {
                try{
                    aListener.shutdown();
                }catch( final Exception fEx ){
                    sm_Log.warn( "Exception: ", fEx );
                }
            } );
        }
        System.exit( fExitCode );
    }

    private static boolean stayLocal()
    {
        final boolean aSysPropStayLocal = Boolean.getBoolean( "staylocal" );
        return aSysPropStayLocal;
    }

    protected void setTitle( final boolean fOffline )
    {
        m_frame.setTitle(String.format("%s (%s)", TITLE, fOffline ? "Offline" : "Online" ));
    }

    private void configure(final ADH_DataProvider fDataProvider)
    {
        final File[] aFolders_NotUploaded = fDataProvider.getNotUploadedFolders();
        m_panel.setUploaded( aFolders_NotUploaded.length == 0 );

        final IUserSettings aUserSettings = AllSettings.INSTANCE.getUserSettings();
        m_panel.configure( aUserSettings.getRole() );
    }

    private static void updateDataFromServer( final ITransferController fTC ) throws Exception
    {
        final IAppSettings aAppSettings = AllSettings.INSTANCE.getAppSettings();
        final Path aFile_BaseData = aAppSettings.getFile_BaseData();
        final IUserSettings aUserSettings = AllSettings.INSTANCE.getUserSettings();
        final ERole aRole = aUserSettings.getRole();
        fTC.updateBaseDataFromServer( aFile_BaseData, aRole );
        fTC.updateBillingDataFromServer();
    }

}

// ############################################################################
