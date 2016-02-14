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
import java.util.Collection;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.listeners.ExportListener;
import teambaltic.adhelper.gui.listeners.GUIUpdater;
import teambaltic.adhelper.gui.listeners.InvoicingPeriodSelectedListener;
import teambaltic.adhelper.gui.listeners.MemberSelectedListener;
import teambaltic.adhelper.gui.listeners.WorkEventEditorActionListener;
import teambaltic.adhelper.gui.listeners.WorkEventTableListener;
import teambaltic.adhelper.gui.model.InvoicingPeriodBoxModel;
import teambaltic.adhelper.gui.model.MemberComboBoxModel;
import teambaltic.adhelper.model.ApplicationProperties;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class ADH_Application
{
    private static final Logger sm_Log = Logger.getLogger(ADH_Application.class);


    private JFrame m_frame;
    MainPanel m_panel;

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        System.setProperty( "appname", "ADHelper" );
        Log4J.initLog4J();
        final String aDataFolder    = ApplicationProperties.INSTANCE.getDataFolderName();
        final String aFN_BaseInfo   = ApplicationProperties.INSTANCE.getFileName_BaseInfo();
        final String aFN_WorkEvents = ApplicationProperties.INSTANCE.getFileName_WorkEvents();
        final File aBaseInfoFile  = new File(aDataFolder, aFN_BaseInfo);
        final File aWorkEventFile = new File(aDataFolder, aFN_WorkEvents);

        final ADH_Application aAppWindow = new ADH_Application();

        EventQueue.invokeLater( new Runnable() {
            @Override
            public void run()
            {
                try{
                    aAppWindow.m_frame.setVisible( true );
                    final String aMsg = assertExistenceOfDataFiles( aBaseInfoFile, aWorkEventFile );
                    if( aMsg != null ){
                        sm_Log.error(aMsg);
                        JOptionPane.showMessageDialog(aAppWindow.m_frame,aMsg,
                                "Fataler Fehler!",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit( 1 );
                    }
                    final ADH_DataProvider aDataProvider = initInfo(aBaseInfoFile, aWorkEventFile);
                    aAppWindow.populate( aDataProvider );
                }catch( final Exception e ){
                    e.printStackTrace();
                }
            }

        } );
    }

    /**
     * Create the application.
     */
    public ADH_Application()
    {
        initialize();
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

        final JComboBox<String> aCB_InvoicingPeriod = m_panel.getCB_InvoicingPeriod();
        aCB_InvoicingPeriod.setModel( new InvoicingPeriodBoxModel() );
        aCB_InvoicingPeriod.addActionListener( new InvoicingPeriodSelectedListener(m_panel, fDataProvider) );


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
        aBtnExport.addActionListener( new ExportListener( m_panel, fDataProvider ) );

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

        final JMenu mnAktionen = new JMenu("Aktionen");
        menuBar.add(mnAktionen);

        final Component horizontalGlue = Box.createHorizontalGlue();
        menuBar.add(horizontalGlue);

        final JMenu mnHilfe = new JMenu("Hilfe");
        menuBar.add(mnHilfe);
    }

    private static ADH_DataProvider initInfo(final File fBaseInfoFile, final File fWorkEventFile)
    {
        final ADH_DataProvider aDataProvider = new ADH_DataProvider();
        aDataProvider.readBaseInfo( fBaseInfoFile );
        aDataProvider.readWorkEvents( fWorkEventFile );
        aDataProvider.joinRelatives();
        return aDataProvider;
    }

    private static String assertExistenceOfDataFiles( final File fBaseInfoFile, final File fWorkEventFile )
    {
        String aMsg = "Folgende Dateien existieren nicht: \n\t";
        final boolean aExists_BIF = fBaseInfoFile.exists();
        if( !aExists_BIF ){
            aMsg += fBaseInfoFile.getAbsolutePath();
        }
        final boolean aExists_WEF = fWorkEventFile.exists();
        if( !aExists_WEF ){
            if( !aExists_BIF ){
                aMsg += ",";
            }
            aMsg += "\n\t"+fWorkEventFile.getAbsolutePath();
        }

        return aExists_BIF && aExists_WEF ? null : aMsg;
    }
}

// ############################################################################
