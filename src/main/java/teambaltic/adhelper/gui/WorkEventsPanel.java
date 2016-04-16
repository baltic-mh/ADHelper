/**
 * WorkEventsPanel.java
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

import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableRowSorter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.gui.model.TBLModel_WorkEvents;

// ############################################################################
public class WorkEventsPanel extends JPanel
{
    private static final long serialVersionUID = 157096461352369629L;

    private final JTable                            m_table;
    private final TableRowSorter<TBLModel_WorkEvents>      m_sorter;
    private final JTextField                        m_tf_Filter;

    private final WorkEventsFilterController m_FilterController;

    // ------------------------------------------------------------------------
    private final JComboBox<LocalDate> m_cmb_Date;
    public JComboBox<LocalDate> getCmb_Date(){ return m_cmb_Date; }
    // ------------------------------------------------------------------------

    /**
     * Create the panel.
     */
    public WorkEventsPanel()
    {
        super();

        m_table = new JTable();
        m_sorter = new TableRowSorter<TBLModel_WorkEvents>();
        m_table.setRowSorter( m_sorter  );

        setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("70dlu"),
                FormSpecs.RELATED_GAP_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,}));

        final JLabel lblDate = new JLabel("Datum");
        add(lblDate, "2, 2, right, default");

        m_cmb_Date = new JComboBox<>();
        m_cmb_Date.setActionCommand( "DATESELECTED" );
        add(m_cmb_Date, "4, 2, 7, 1, fill, default");

        final JButton btnNeu = new JButton("Neu");
        add(btnNeu, "12, 2");

        final JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "2, 4, 11, 1, fill, fill");

        scrollPane.setViewportView(m_table);
        m_table.setFillsViewportHeight(true);

        final JButton btnResetTextFilter = new JButton("Reset Textfilter");
        btnResetTextFilter.setActionCommand( "ResetTextfilter" );
        add(btnResetTextFilter, "10, 6");

        final JButton btnToggleTeilnehmerFilter = new JButton("Nur Teilnehmer");
        btnToggleTeilnehmerFilter.setActionCommand( "ToggleNurTeilnehmer" );
        add(btnToggleTeilnehmerFilter, "12, 6");

        final JLabel lblFilter = new JLabel("Filter");
        add(lblFilter, "2, 6, right, default");

        m_tf_Filter = new JTextField();
        add(m_tf_Filter, "4, 6, 5, 1, fill, default");
        m_tf_Filter.setColumns(10);

        m_FilterController = new WorkEventsFilterController( m_sorter, btnToggleTeilnehmerFilter, m_tf_Filter );

        final JButton btnOk = new JButton("Ok");
        add(btnOk, "10, 8");

        final JButton btnAbbrechen = new JButton("Abbrechen");
        add(btnAbbrechen, "12, 8");
        m_tf_Filter.getDocument().addDocumentListener( m_FilterController );
        btnToggleTeilnehmerFilter.addActionListener( m_FilterController );
        btnResetTextFilter.addActionListener( m_FilterController );

    }

    public void populate(final Object[][] fData, final boolean fReadOnly)
    {
        final TBLModel_WorkEvents aModel = new TBLModel_WorkEvents( fData, fReadOnly );
        m_sorter.setModel( aModel );
        m_table.setModel( aModel );
    }
}

// ############################################################################
