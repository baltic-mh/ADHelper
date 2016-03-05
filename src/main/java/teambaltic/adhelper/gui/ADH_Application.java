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
import java.util.Collection;

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
import teambaltic.adhelper.gui.listeners.ExportListener;
import teambaltic.adhelper.gui.listeners.GUIUpdater;
import teambaltic.adhelper.gui.listeners.MemberSelectedListener;
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

    private JFrame m_frame;
    private MainPanel m_panel;

    // ------------------------------------------------------------------------
    private UserSettingsListener m_UserSettingsListener;
    private UserSettingsListener getUserSettingsListener(){ return m_UserSettingsListener; }
    private void setUserSettingsListener( final UserSettingsListener fNewVal ){ m_UserSettingsListener = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private String m_DataFolderName;
    private String getDataFolderName(){ return m_DataFolderName; }
    private void setDataFolderName( final String fNewVal ){ m_DataFolderName = fNewVal; }
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
                    try{
                        AllSettings.INSTANCE.init();
                        final String aDFN = AllSettings.INSTANCE.getAppSettings().getStringValue( IAppSettings.EKey.FOLDERNAME_DATA );
                        aAppWindow.setDataFolderName( aDFN );
                        final IUserSettings aUserSettings = AllSettings.INSTANCE.getUserSettings();
                        aAppWindow.setUserSettingsListener( initUserSettings( aUserSettings ) );

                        aAppWindow.initialize();
                        aAppWindow.m_frame.setVisible( true );

                        final ADH_DataProvider aDataProvider = new ADH_DataProvider(AllSettings.INSTANCE);
                        aDataProvider.init();
                        aAppWindow.populate( aDataProvider );
                    }catch( final Exception fEx ){
                        sm_Log.error( "Unerwartete Exception: ", fEx );
                        final String aMsg = ExceptionUtils.getStackTrace(fEx);
                        JOptionPane.showMessageDialog( aAppWindow.m_frame, aMsg, "Fataler Fehler!",
                                    JOptionPane.ERROR_MESSAGE );
                        System.exit( 1 );
                    }
                }catch( final Exception fEx ){
                    sm_Log.error( "Unerwartete Exception: ", fEx );
                }
            }

        } );
    }

    private static UserSettingsListener initUserSettings(final IUserSettings fUserSettings)
    {
        final UserSettingsDialog aDialog = new UserSettingsDialog();
        final JButton aBtn_OK = aDialog.getBtn_OK();
        final UserSettingsListener l = new UserSettingsListener( aDialog, fUserSettings );
        aBtn_OK.addActionListener( l );

        final String aRoleStr = fUserSettings.getStringValue( IUserSettings.EKey.ROLE );
        if( aRoleStr == null || ERole.ESKIMO.toString().equals( aRoleStr ) ){
            aDialog.setVisible( true );
        }

        return l;
    }

    /**
     * Create the application.
     */
    public ADH_Application()
    {
//        initialize();
    }

    public void populate( final ADH_DataProvider fDataProvider )
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

        final JButton aBtnExport = m_panel.getBtnExport();
        aBtnExport.addActionListener( new ExportListener( m_panel, fDataProvider, getDataFolderName() ) );

        GUIUpdater.updateGUI( m_panel.getSelectedMemberID(), m_panel, fDataProvider );
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        m_frame = new JFrame();
        m_frame.setTitle("KVK Arbeitsdienst-Helferlein (C) 2016 TeamBaltic");
        m_frame.setBounds( 100, 100, 924, 580 );
        m_frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
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
        m_mnit_UserSettings.addActionListener( getUserSettingsListener() );
        mnAktionen.add(m_mnit_UserSettings);

        final Component horizontalGlue = Box.createHorizontalGlue();
        menuBar.add(horizontalGlue);

        final JMenu mnHilfe = new JMenu("Hilfe");
        menuBar.add(mnHilfe);
    }

}

// ############################################################################
