/**
 * ParticipationsPanel.java
 *
 * Created on 06.03.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.gui.model.TBLModel_Participation;
import teambaltic.adhelper.model.PeriodData;

// ############################################################################
public class ParticipationsPanel extends JPanel
{
    private static final long serialVersionUID = 157096461352369629L;

    private static final DefaultTableCellRenderer CENTERRENDERER = createCenterRenderer();

    // ------------------------------------------------------------------------
    private final JTable m_table;
    public JTable getTable(){ return m_table; }
    // ------------------------------------------------------------------------

    private final TableRowSorter<TBLModel_Participation> m_sorter;
    private final JTextField                             m_tf_Filter;

    private final TeilnehmerFilterController m_FilterController;

    // ------------------------------------------------------------------------
    private final JComboBox<LocalDate> m_cmb_Date;
    public JComboBox<LocalDate> getCmb_Date(){ return m_cmb_Date; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JComboBox<PeriodData> m_cmb_Period;
    public JComboBox<PeriodData> getCmb_Period(){ return m_cmb_Period; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JButton m_btn_ToggleTeilnehmerFilter;
    private JButton getBtn_ToggleTeilnehmerFilter(){ return m_btn_ToggleTeilnehmerFilter; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JButton m_btn_Ok;
    public JButton getBtn_Ok(){ return m_btn_Ok; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JButton m_btn_Neu;
    public JButton getBtn_Neu(){ return m_btn_Neu; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JButton m_btn_Abbrechen;
    public JButton getBtn_Abbrechen(){ return m_btn_Abbrechen; }
    // ------------------------------------------------------------------------

    /**
     * Create the panel.
     */
    public ParticipationsPanel()
    {
        super();

        m_table = new ParticipationsTable();
        m_sorter = new TableRowSorter<>();
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
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,}));

        final JLabel lblZeitraum = new JLabel("Zeitraum");
        add(lblZeitraum, "2, 2, right, default");

        m_cmb_Period = new JComboBox<>();
        m_cmb_Period.setActionCommand( "PERIODSELECTED" );
        add(m_cmb_Period, "4, 2, 7, 1, fill, default");

        final JLabel lblDate = new JLabel("Datum");
        add(lblDate, "2, 4, right, default");

        m_cmb_Date = new JComboBox<>();
        m_cmb_Date.setActionCommand( "DATESELECTED" );
        add(m_cmb_Date, "4, 4, 7, 1, fill, default");

        m_btn_Neu = new JButton("Neu");
        m_btn_Neu.setActionCommand( "NEU" );
        add(m_btn_Neu, "12, 4");

        final JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "2, 6, 11, 1, fill, fill");

        scrollPane.setViewportView(m_table);
        m_table.setFillsViewportHeight(true);

        final JButton btnResetTextFilter = new JButton("Reset Textfilter");
        btnResetTextFilter.setActionCommand( "ResetTextfilter" );
        add(btnResetTextFilter, "10, 8");

        m_btn_ToggleTeilnehmerFilter = new JButton("Nur Teilnehmer");
        m_btn_ToggleTeilnehmerFilter.setActionCommand( "ToggleNurTeilnehmer" );
        add(m_btn_ToggleTeilnehmerFilter, "12, 8");

        final JLabel lblFilter = new JLabel("Filter");
        add(lblFilter, "2, 8, right, default");

        m_tf_Filter = new JTextField();
        add(m_tf_Filter, "4, 8, 5, 1, fill, default");
        m_tf_Filter.setColumns(10);

        m_FilterController = new TeilnehmerFilterController( m_sorter, m_btn_ToggleTeilnehmerFilter, m_tf_Filter );

        m_btn_Ok = new JButton("Ok");
        m_btn_Ok.setActionCommand( "OK" );
        add(m_btn_Ok, "10, 10");

        m_btn_Abbrechen = new JButton("Abbrechen");
        m_btn_Abbrechen.setActionCommand( "CANCEL" );
        add(m_btn_Abbrechen, "12, 10");

        m_tf_Filter.getDocument().addDocumentListener( m_FilterController );
        m_btn_ToggleTeilnehmerFilter.addActionListener( m_FilterController );
        btnResetTextFilter.addActionListener( m_FilterController );

    }

    public void populate(final TBLModel_Participation fModel)
    {
        // Das kurzzeitige Entkoppeln der Table vom Sorter war notwendig,
        // um eine Exception zu verhindern, deren Ursache ich nicht komplett
        // verstanden habe:
        m_table.setRowSorter( null );
        m_sorter.setModel( fModel );
        m_table.setRowSorter( m_sorter  );

        m_table.setModel( fModel );
        m_table.getColumnModel().getColumn(fModel.getColIdx_Participationflag()).setMaxWidth(75);
        m_table.getColumnModel().getColumn(fModel.getColIdx_ID()).setMaxWidth(75);
        m_table.getColumnModel().getColumn(fModel.getColIdx_Name()).setCellRenderer( CENTERRENDERER );
        m_table.getColumnModel().getColumn(fModel.getColIdx_Hours()).setMaxWidth(150);
        final String aColName_Hours = fModel.getColumnName( fModel.getColIdx_Hours() );
        final HoursCellEditor   aCellEditor = new HoursCellEditor();
        m_table.getColumn(aColName_Hours).setCellEditor(aCellEditor);
        final HoursCellRenderer aCellRender = new HoursCellRenderer();
        m_table.getColumn(aColName_Hours).setCellRenderer(aCellRender);

        configureButtons( fModel.isReadOnly() );
    }

    protected void configureButtons( final boolean fReadOnly )
    {
        final JButton aBtn_ToggleTeilnehmerFilter = getBtn_ToggleTeilnehmerFilter();
        m_FilterController.setFilter_NurTeilnehmer( fReadOnly );
        aBtn_ToggleTeilnehmerFilter.setEnabled( !fReadOnly );

        final JButton aBtn_Neu = getBtn_Neu();
        aBtn_Neu.setEnabled( !fReadOnly );
        final JButton aBtn_Ok = getBtn_Ok();
        aBtn_Ok.setEnabled( !fReadOnly );
    }

    private static DefaultTableCellRenderer createCenterRenderer()
    {
        final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        return centerRenderer;
    }

}

// ############################################################################
