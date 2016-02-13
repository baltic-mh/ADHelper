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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.listeners.InvoicingPeriodSelectedListener;
import teambaltic.adhelper.gui.listeners.MemberSelectedListener;
import teambaltic.adhelper.gui.model.InvoicingPeriodBoxModel;
import teambaltic.adhelper.gui.model.MemberComboBoxModel;
import teambaltic.adhelper.model.IClubMember;

//import com.jgoodies.forms.layout.ColumnSpec;
//import com.jgoodies.forms.layout.FormLayout;
//import com.jgoodies.forms.layout.RowSpec;

// ############################################################################
public class ADH_Application
{

    private JFrame m_frame;
    MainPanel m_panel;

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        final ADH_DataProvider aChef = initInfo();
        aChef.joinRelatives();

        EventQueue.invokeLater( new Runnable() {
            @Override
            public void run()
            {
                try{
                    final ADH_Application window = new ADH_Application( aChef );
                    window.m_frame.setVisible( true );
                }catch( final Exception e ){
                    e.printStackTrace();
                }
            }
        } );
    }

    /**
     * Create the application.
     */
    public ADH_Application( final ADH_DataProvider fDataProvider)
    {
        initialize();

        final Collection<IClubMember> aAllMembers = fDataProvider.getMembers();
        final IClubMember[] aMemberArr = new IClubMember[aAllMembers.size()] ;
        final JComboBox<IClubMember> aCB_Members = m_panel.getCB_Members();
        aCB_Members.setModel( new MemberComboBoxModel( aAllMembers.toArray( aMemberArr ) ) );
        final ActionListener aListener = new MemberSelectedListener( m_panel, fDataProvider );
        aCB_Members.addActionListener( aListener );

        final JComboBox<String> aCB_InvoicingPeriod = m_panel.getCB_InvoicingPeriod();
        aCB_InvoicingPeriod.setModel( new InvoicingPeriodBoxModel() );
        aCB_InvoicingPeriod.addActionListener( new InvoicingPeriodSelectedListener(m_panel, fDataProvider) );
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

    private static ADH_DataProvider initInfo()
    {
        final File aBaseInfoFile  = new File("misc/TestResources/Tabellen/Mitglieder.csv");
        final File aWorkEventFile = new File("misc/TestResources/Tabellen/Arbeitsdienste1.csv");
        final ADH_DataProvider aChef = new ADH_DataProvider();
        aChef.readBaseInfo( aBaseInfoFile );
        aChef.readWorkEvents( aWorkEventFile );
        return aChef;
    }
}

// ############################################################################
