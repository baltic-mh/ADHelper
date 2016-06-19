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
import java.awt.event.ItemEvent;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.controller.IPeriodDataController.EPeriodDataSelector;
import teambaltic.adhelper.controller.IShutdownListener;
import teambaltic.adhelper.controller.ITransferController;
import teambaltic.adhelper.controller.InitHelper;
import teambaltic.adhelper.controller.IntegrityChecker;
import teambaltic.adhelper.gui.listeners.FinishListener;
import teambaltic.adhelper.gui.listeners.GUIUpdater;
import teambaltic.adhelper.gui.listeners.ManageWorkEventsListener;
import teambaltic.adhelper.gui.listeners.MemberSelectedListener;
import teambaltic.adhelper.gui.listeners.PeriodDataChangedListener;
import teambaltic.adhelper.gui.listeners.UploadListener;
import teambaltic.adhelper.gui.listeners.UserSettingsListener;
import teambaltic.adhelper.gui.model.CBModel_PeriodData;
import teambaltic.adhelper.model.AppReleaseInfo;
import teambaltic.adhelper.model.ERole;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings;
import teambaltic.adhelper.model.settings.IUserSettings;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class ADH_Application
{
    private static final Logger sm_Log = Logger.getLogger(ADH_Application.class);
    private static final String PROPKEY_Log4jFileName = "log4jfilename";

    private static final EPeriodDataSelector ALL = EPeriodDataSelector.ALL;

    // ------------------------------------------------------------------------
    private JFrame m_frame;
    public  JFrame getFrame(){ return m_frame; }
    // ------------------------------------------------------------------------

    private MainPanel m_MainPanel;

    private final List<IShutdownListener> m_ShutdownListeners;

    // ------------------------------------------------------------------------
    private UserSettingsListener m_UserSettingsListener;
    private UserSettingsListener getUserSettingsListener(){ return m_UserSettingsListener; }
    void setUserSettingsListener( final UserSettingsListener fNewVal ){ m_UserSettingsListener = fNewVal; }
    // ------------------------------------------------------------------------

    private GUIUpdater m_GUIUpdater;

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        final String aAppName = "ADHelper";
        final String aLog4jFileName = aAppName+".log";
        System.setProperty(PROPKEY_Log4jFileName, aLog4jFileName);
        Log4J.initLog4J();
        sm_Log.info("==========================================================");
        readAndSetSystemProperties(aAppName);

        final ADH_Application aAppWindow = new ADH_Application();

        EventQueue.invokeLater( new Runnable() {

            @Override
            public void run()
            {
                try{
                    initSettings( aAppWindow );
                    sm_Log.info(composeTitle());
                    final boolean aIsBauausschuss = AllSettings.INSTANCE.getUserSettings().isBauausschuss();
                    IntegrityChecker.check( AllSettings.INSTANCE );
                    final InitHelper aInitHelper = new InitHelper( AllSettings.INSTANCE );

                    aAppWindow.initialize();
                    aAppWindow.setVisible( true );

                    ITransferController aTC = null;
                    boolean aOffline = true;
                    if( !stayLocal() ){
                        aTC = aInitHelper.initTransferController();
                        aTC.start();
                        if( aTC.isConnected() ){
                            aOffline = false;
                            updateDataFromServer( aTC );
                            IntegrityChecker.checkAfterUpdateFromServer( AllSettings.INSTANCE );
                        } else {
                            throw new Exception( "Keine Verbindung zum Server (für Details siehe log-Datei)!" );
                        }

                    }
                    final IPeriodDataController aPDC = aInitHelper.initPeriodDataController();
                    if( !aOffline ){
                        aTC.setPeriodDataController( aPDC );
                        if( aIsBauausschuss ){
                            // Das darf erst und nur geschehen, nachdem alle Daten vom Server heruntergladen worden sind!
                            aPDC.createNewPeriod();
                        }
                    }
                    aAppWindow.setTitle( aOffline );
                    aAppWindow.addShutdownListener( aTC );
                    final ADH_DataProvider aDataProvider = aInitHelper.initDataProvider( aPDC );

                    aAppWindow.initObjects( aDataProvider, aPDC, aTC, aIsBauausschuss );

                }catch( final Exception fEx ){
                    sm_Log.error( "Unerwartete Exception: ", fEx );
                    final String aMsg = ExceptionUtils.getStackTrace(fEx);
                    JOptionPane.showMessageDialog( aAppWindow.m_frame, aMsg, "Fataler Fehler!",
                                JOptionPane.ERROR_MESSAGE );
                    aAppWindow.shutdown("Beenden wegen fataler Exception", 1);
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

    private void initObjects(final ADH_DataProvider fDataProvider,
            final IPeriodDataController fPDC,
            final ITransferController fTransferController,
            final boolean fIsBauausschuss)
    {
        m_GUIUpdater    = new GUIUpdater( m_MainPanel, fDataProvider, fPDC );
        final ActionListener aMemberSelectedListener = new MemberSelectedListener( m_GUIUpdater );

        final JComboBox<PeriodData> aCB_Period = m_MainPanel.getCB_Period();
        final PeriodDataChangedListener aPDCL = new PeriodDataChangedListener( m_GUIUpdater, fDataProvider );
        aCB_Period.addItemListener( aPDCL );

        final List<PeriodData> aPDList = fPDC.getPeriodDataList( ALL );
        final PeriodData[] aPeriods = new PeriodData[aPDList.size()];
        aCB_Period.setModel( new CBModel_PeriodData( aPDList.toArray( aPeriods ) ) );
        aCB_Period.setSelectedItem( fPDC.getActivePeriod() );
        if( aCB_Period.getItemCount() == 1 ){
            // Wenn da nur ein Element in der Box ist, hat das vorherige
            /// setSelectedItem keinen Event ausgelöst!
            final ItemEvent aItemEvent = new ItemEvent(aCB_Period, 0, fPDC.getActivePeriod(), ItemEvent.SELECTED);
            aPDCL.itemStateChanged( aItemEvent );
        }

        final JComboBox<IClubMember> aCB_Members = m_MainPanel.getCB_Members();
        aCB_Members.addActionListener( aMemberSelectedListener );

        final ManageWorkEventsListener aManageWorkEventsListener = new ManageWorkEventsListener(fDataProvider, fPDC, m_GUIUpdater, fIsBauausschuss);
        m_MainPanel.getBtn_ManageWorkEvents().addActionListener( aManageWorkEventsListener );
        final JComboBox<PeriodData> aCB_Period2 = aManageWorkEventsListener.getCmb_Period();
        aCB_Period2.addItemListener( aPDCL );

        final JButton aBtnFinish = m_MainPanel.getBtnFinish();
        aBtnFinish.addActionListener( new FinishListener( m_MainPanel, fPDC, fDataProvider ) );

        final JButton aBtnUpload = m_MainPanel.getBtnUpload();
        aBtnUpload.addActionListener(
                new UploadListener( m_MainPanel, fDataProvider, fTransferController,
                        getUserSettingsListener() ) );
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        m_frame = new JFrame();
        m_frame.setTitle(composeTitle());
        m_frame.setBounds( 100, 100, 924, 580 );
        m_frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(final java.awt.event.WindowEvent e) {
                shutdown("Beenden durch Benutzer", 0);
            }
        });

        m_frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("906px:grow"),},
            new RowSpec[] {
                RowSpec.decode("435px:grow"),}));

        m_MainPanel = new MainPanel();
        m_frame.getContentPane().add(m_MainPanel, "1, 1, fill, fill");

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

    public void shutdown(final String fInfo, final int fExitCode)
    {
        synchronized( m_ShutdownListeners ){
            for( final IShutdownListener aShutdownListener : m_ShutdownListeners ){
                try{
                    aShutdownListener.shutdown();
                }catch( final Throwable fEx ){
                    sm_Log.warn( "Exception: ", fEx );
                }
            }
        }
        sm_Log.info(fInfo);
        sm_Log.info("==========================================================");
        System.exit( fExitCode );
    }

    private static boolean stayLocal()
    {
        final boolean aSysPropStayLocal = Boolean.getBoolean( "staylocal" );
        return aSysPropStayLocal;
    }

    protected void setTitle( final boolean fOffline )
    {
        m_frame.setTitle( composeTitle() );
    }

    private static void updateDataFromServer( final ITransferController fTC ) throws Exception
    {
        final IAppSettings aAppSettings = AllSettings.INSTANCE.getAppSettings();
        final Path aFile_BaseData = aAppSettings.getFile_RootBaseData();
        final IUserSettings aUserSettings = AllSettings.INSTANCE.getUserSettings();
        final ERole aRole = aUserSettings.getRole();
        fTC.updateBaseDataFromServer( aFile_BaseData, aRole );
        fTC.updatePeriodDataFromServer();
    }

    private static void readAndSetSystemProperties(final String fAppName)
    {
        final Path aSysPropFile = Paths.get( fAppName+".prop" );
        if( !Files.exists( aSysPropFile ) ){
            return;
        }
        FileInputStream aInputStream;
        try{
            aInputStream = new FileInputStream( aSysPropFile.toFile() );
            final Properties aSysProps = new Properties();
            aSysProps.load( aInputStream );
            final Enumeration<Object> aKeys = aSysProps.keys();
            while( aKeys.hasMoreElements() ){
                final String aKey = (String) aKeys.nextElement();
                final String aValue = aSysProps.getProperty( aKey );
                sm_Log.info(String.format( "SystemProperty: %s => %s", aKey, aValue));
                System.setProperty( aKey, aValue );
            }
        }catch( final Exception fEx ){
            sm_Log.warn("Exception while loading system properties from: "+aSysPropFile, fEx );
        }

    }

    private static String composeTitle()
    {
        final String aUserName = AllSettings.INSTANCE.getRemoteAccessSettings().getUserName();
        final String aFolderName_Root = AllSettings.INSTANCE.getAppSettings().getFolderName_Root();
        final StringBuffer aSB = new StringBuffer();
        aSB.append( AppReleaseInfo.getProject() );
        aSB.append( " - " );
        aSB.append( AppReleaseInfo.getCopyright() );
        aSB.append( " - " );
        aSB.append( AppReleaseInfo.getVersion() );
        aSB.append( String.format(" (%s@%s)", aUserName, aFolderName_Root ) );
        return aSB.toString();
    }

}

// ############################################################################
