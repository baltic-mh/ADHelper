/**
 * ADMainPanel.java
 *
 * Created on 09.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw‰rmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.gui.listeners.WorkEventTableListener;
import teambaltic.adhelper.gui.model.WorkEventTableModel;
import teambaltic.adhelper.model.ERole;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.WorkEvent;

// ############################################################################
public class MainPanel extends JPanel
{
    private static final long serialVersionUID = 7281848889524771889L;

    private final JTextField    m_tf_HoursToPay;
    private final JTable        m_tbl_WorkEvents;
    private final JTable        m_tbl_DutyCharges;

    // ------------------------------------------------------------------------
    private final JComboBox<IClubMember> m_cmb_Members;
    public JComboBox<IClubMember> getCB_Members(){ return m_cmb_Members; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    final JTextField m_txb_InvoicingPeriod;
    public JTextField getWidget_InvoicingPeriod(){ return m_txb_InvoicingPeriod; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JButton m_btnNew;
    private JButton getBtnNew(){ return m_btnNew; }
    // ------------------------------------------------------------------------
    private final JButton m_btnDelete;
    private JButton getBtnDelete(){ return m_btnDelete; }
    // ------------------------------------------------------------------------
    private final JButton m_btnFinish;
    public JButton getBtnFinish(){ return m_btnFinish; }
    // ------------------------------------------------------------------------
    private final JButton m_btnUpload;
    public JButton getBtnUpload(){ return m_btnUpload; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final DutyChargeTableModel m_DutyChargeDataModel;
    public DutyChargeTableModel getDutyChargeDataModel(){ return m_DutyChargeDataModel; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final WorkEventTableModel m_WorkEventDataModel;
    public WorkEventTableModel getWorkEventDataModel(){ return m_WorkEventDataModel; }
    // ------------------------------------------------------------------------

    public MainPanel()
    {
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
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("max(58dlu;default)"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("max(55dlu;default)"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,}));

        final JLabel lblMitglied = new JLabel("Mitglied");
        add(lblMitglied, "2, 2, right, default");

        m_cmb_Members = new JComboBox<>();
        add(getCB_Members(), "4, 2, 9, 1, fill, default");

        final JComboBox<IClubMember> aCb_Members = getCB_Members();
        UIUtils.setItemStartsWithSelector( aCb_Members );

        final JLabel lblAbrechnungszeitraum = new JLabel("Abrechnungszeitraum");
        add(lblAbrechnungszeitraum, "2, 4, right, default");

        m_txb_InvoicingPeriod = new JTextField();
        m_txb_InvoicingPeriod.setEditable(false);
        add(m_txb_InvoicingPeriod, "4, 4, 9, 1, fill, default");

        final JPanel m_pnl_Accountings = new JPanel();
        m_pnl_Accountings.setBorder(new TitledBorder(null, "Abrechnungen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(m_pnl_Accountings, "2, 6, 11, 1, fill, fill");
        m_pnl_Accountings.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.PREF_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,}));

        final JScrollPane scrollPane_1 = new JScrollPane();
        m_pnl_Accountings.add(scrollPane_1, "2, 2, 7, 1, fill, fill");

        m_tbl_DutyCharges = new JTable();
        ((JLabel)m_tbl_DutyCharges.getDefaultRenderer(String.class)).setHorizontalAlignment (JLabel.RIGHT);
        m_DutyChargeDataModel = new DutyChargeTableModel();
        m_tbl_DutyCharges.setModel(m_DutyChargeDataModel);
        m_tbl_DutyCharges.setFillsViewportHeight(true);
        scrollPane_1.setViewportView(m_tbl_DutyCharges);

        final JLabel lblZuZahlendeStunden = new JLabel("Zu zahlende Stunden");
        m_pnl_Accountings.add(lblZuZahlendeStunden, "2, 4");

        m_tf_HoursToPay = new JTextField();
        m_pnl_Accountings.add(m_tf_HoursToPay, "6, 4, fill, default");
        m_tf_HoursToPay.setColumns(10);

        final JPanel m_pnl_WorkEvents = new JPanel();
        m_pnl_WorkEvents.setBorder(new TitledBorder(null, "Arbeitsdienste", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(m_pnl_WorkEvents, "2, 8, 11, 1, fill, fill");
        m_pnl_WorkEvents.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,}));


        final JScrollPane scrollPane = new JScrollPane();
        m_pnl_WorkEvents.add(scrollPane, "2, 2, 9, 1, fill, fill");

        m_tbl_WorkEvents = new JTable();
        ((JLabel)m_tbl_WorkEvents.getDefaultRenderer(String.class)).setHorizontalAlignment (JLabel.RIGHT);
        m_WorkEventDataModel = new WorkEventTableModel();
        m_tbl_WorkEvents.setModel(m_WorkEventDataModel);

        m_tbl_WorkEvents.setFillsViewportHeight(true);
        scrollPane.setViewportView(m_tbl_WorkEvents);
        m_tbl_WorkEvents.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent event) {
                final int aSelectedRow = m_tbl_WorkEvents.getSelectedRow();
                final boolean aEnable = aSelectedRow >= 0 && m_tbl_WorkEvents.getValueAt(aSelectedRow, 4) == null;
                m_btnDelete.setEnabled( aEnable );
            }
        });
        m_btnDelete = new JButton("Entfernen");
        m_btnDelete.setActionCommand( "Delete" );
        m_btnDelete.setEnabled( false );
        m_pnl_WorkEvents.add(m_btnDelete, "8, 4");

        m_btnNew = new JButton("Neu");
        m_btnNew.setActionCommand( "New" );
        m_pnl_WorkEvents.add(m_btnNew, "10, 4");

        m_btnUpload = new JButton("Hochladen...");
        add(m_btnUpload, "10, 10");

        m_btnFinish = new JButton("Abrechnung abschlieﬂen");
        m_btnFinish.setActionCommand( "Abrechnung abschlieﬂen" );
        add(m_btnFinish, "12, 10");

    }

    public void setTotalHoursToPay( final float fF )
    {
        m_tf_HoursToPay.setText( String.valueOf( fF ) );
    }

    public void setWorkEventTableListener( final WorkEventTableListener fWorkEventTableListener )
    {
        getBtnNew().addActionListener(fWorkEventTableListener);
        getBtnDelete().addActionListener(fWorkEventTableListener);
    }

    public boolean removeSelectedWorkEventRow()
    {
        final int aSelectedRow = m_tbl_WorkEvents.getSelectedRow();
        if( aSelectedRow == -1 ){
            return false;
        }
        final Object[] options = {"Ich weiﬂ, was ich tue!", "Nein, das war ein Versehen!"};
        final int n = JOptionPane.showOptionDialog(null,
            "Soll der Arbeitsdiensteintrag wirklich gelˆscht werden?",
            "Sind Sie ganz sicher?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[1]);
        switch( n ){
            case 0:
                ((DefaultTableModel)m_tbl_WorkEvents.getModel()).removeRow( aSelectedRow );
                return true;

            default:
                return false;
        }
    }

    public WorkEvent getSelectedWorkEvent()
    {
        final int aSelectedRow = m_tbl_WorkEvents.getSelectedRow();
        if( aSelectedRow == -1 ){
            return null;
        }
        final Integer aWorkerID = (Integer)   m_tbl_WorkEvents.getValueAt( aSelectedRow, 0 );
        final LocalDate aDate   = (LocalDate) m_tbl_WorkEvents.getValueAt( aSelectedRow, 2 );
        final WorkEvent aWorkEvent = new WorkEvent( aWorkerID );
        aWorkEvent.setDate( aDate );
        return aWorkEvent;
    }

    public IClubMember getSelectedMember()
    {
        final IClubMember aSelectedItem = (IClubMember) getCB_Members().getSelectedItem();
        return aSelectedItem;
    }

    public int getSelectedMemberID()
    {
        return getSelectedMember().getID();
    }

    public void setFinished()
    {
        m_btnFinish.setEnabled( false );
        m_btnNew.setEnabled( false );
    }

    public void setUploaded()
    {
        m_btnUpload.setEnabled( false );
    }

    public void configure( final ERole fRole )
    {
        switch( fRole ){
            case BAUAUSSCHUSS:

                break;

            case MITGLIEDERWART:
                m_btnNew.setEnabled( false );
                m_btnFinish.setEnabled( false );
                break;

            default:
                break;
        }
    }
}

// ############################################################################
