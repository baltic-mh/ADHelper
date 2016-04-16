/**
 * WorkEventsFilterController.java
 *
 * Created on 12.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import teambaltic.adhelper.gui.model.TBLModel_WorkEvents;

// ############################################################################
public class WorkEventsFilterController
    implements ActionListener, DocumentListener
{
    // ------------------------------------------------------------------------
    private final TableRowSorter<TBLModel_WorkEvents> m_Sorter;
    private TableRowSorter<TBLModel_WorkEvents> getSorter(){ return m_Sorter; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final RowFilter_NurTeilnehmer m_RowFilter_NurTeilnehmer;
    private RowFilter_NurTeilnehmer getRowFilter_NurTeilnehmer(){ return m_RowFilter_NurTeilnehmer; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final RowFilter_Text m_RowFilter_Text;
    private RowFilter_Text getRowFilter_Text(){ return m_RowFilter_Text; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JButton m_btn_ToggleDabeiFilter;

    private JButton getBtn_ToggleDabeiFilter()
    {
        return m_btn_ToggleDabeiFilter;
    }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JTextField m_tf_Filter;

    private JTextField getTf_Filter()
    {
        return m_tf_Filter;
    }
    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public WorkEventsFilterController(
            final TableRowSorter<TBLModel_WorkEvents> fSorter,
            final JButton fBtn_ToggleDabeiFilter,
            final JTextField fTF_Filter)
    {
        m_Sorter = fSorter;
        m_btn_ToggleDabeiFilter = fBtn_ToggleDabeiFilter;
        m_tf_Filter = fTF_Filter;
        m_RowFilter_NurTeilnehmer = new RowFilter_NurTeilnehmer();
        m_RowFilter_Text = new RowFilter_Text();

        // final List<RowFilter<MyTableModel, ? extends Object> > filters = new
        // ArrayList<>();
        @SuppressWarnings("rawtypes")
        final List filters = new ArrayList<>();
        filters.add( m_RowFilter_NurTeilnehmer );
        filters.add( m_RowFilter_Text );
        @SuppressWarnings("rawtypes")
        final RowFilter aCombinedFilter = RowFilter.andFilter( filters );
        getSorter().setRowFilter( aCombinedFilter );
    }

    @Override
    public void actionPerformed( final ActionEvent fEvent )
    {
        switch( fEvent.getActionCommand() ){
            case "ToggleNurTeilnehmer":
                toggleNurTeilnehmerFilter();
                break;
            case "ResetTextfilter":
                getTf_Filter().setText( "" );
                getRowFilter_Text().clear();
                ;
                break;
        }
    }

    @Override
    public void insertUpdate( final DocumentEvent fEvent )
    {
        setTextFilterRegExp( getTf_Filter().getText() );
    }

    @Override
    public void removeUpdate( final DocumentEvent fEvent )
    {
        setTextFilterRegExp( getTf_Filter().getText() );
    }

    @Override
    public void changedUpdate( final DocumentEvent fEvent )
    {
        setTextFilterRegExp( getTf_Filter().getText() );
    }

    public void setTextFilterRegExp( final String fRegexp )
    {
        getRowFilter_Text().setRegExp( fRegexp );
        getSorter().sort();
    }

    private void toggleNurTeilnehmerFilter()
    {
        final RowFilter_NurTeilnehmer aNurTeilnehmer = getRowFilter_NurTeilnehmer();
        if( aNurTeilnehmer.isEnabled() ){
            // Bisher ist "Nur Dabei" ausgewählt, also soll nun "Alle"
            // ausgewählt sein:
            aNurTeilnehmer.setEnabled( false );
            getBtn_ToggleDabeiFilter().setText( "Nur Teilnehmer" );
            getSorter().sort();
            return;
        }
        // Bisher sind "Alle" ausgewählt, also soll nun "Nur Dabei" ausgewählt
        // sein:
        getBtn_ToggleDabeiFilter().setText( "Alle" );
        aNurTeilnehmer.setEnabled( true );
        getSorter().sort();
    }

}

// ############################################################################
