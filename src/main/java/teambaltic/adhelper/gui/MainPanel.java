/**
 * ADMainPanel.java
 *
 * Created on 09.02.2016
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.gui.model.TBLModel_AttendedWorkEvent;
import teambaltic.adhelper.gui.model.TBLModel_DutyCharge;
import teambaltic.adhelper.gui.model.TBLModel_DutyFree;
import teambaltic.adhelper.model.ERole;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.WorkEvent;

// ############################################################################
public class MainPanel extends JPanel
{
    private static final long serialVersionUID = 7281848889524771889L;

    private final JTextField    m_tf_HoursToPay;
    private final JTable        m_tbl_WorkEventsAttended;
    private final JTable        m_tbl_DutyCharges;
    private final JTable        m_tbl_DutyFree;

    // ------------------------------------------------------------------------
    private final JComboBox<IClubMember> m_cmb_Members;
    public JComboBox<IClubMember> getCB_Members(){ return m_cmb_Members; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JTextField m_tf_MemberFilter;
    public JTextField getTF_MemberFilter(){ return m_tf_MemberFilter; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JButton m_btn_ClearFilter;
    public JButton getBtn_ClearFilter() { return m_btn_ClearFilter; }
    // ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
    private final JComboBox<PeriodData> m_cmb_Period;
    public JComboBox<PeriodData> getCB_Period(){ return m_cmb_Period; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JButton m_btnFinish;
    public JButton getBtnFinish(){ return m_btnFinish; }
    // ------------------------------------------------------------------------
    private final JButton m_btnUpload;
    public JButton getBtnUpload(){ return m_btnUpload; }
    // ------------------------------------------------------------------------
    private final JButton m_btn_ManageWorkEvents;
    public JButton getBtn_ManageWorkEvents(){ return m_btn_ManageWorkEvents; }
    // ------------------------------------------------------------------------
    private final JButton m_btn_ManageAdjustment;
    public JButton getBtn_ManageAdjustment(){ return m_btn_ManageAdjustment; }
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    private final TBLModel_DutyCharge m_DataModel_DutyCharge;
    public TBLModel_DutyCharge getDataModel_DutyCharge(){ return m_DataModel_DutyCharge; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final TBLModel_DutyFree m_DataModel_DutyFree;
    public TBLModel_DutyFree getDataModel_DutyFree(){ return m_DataModel_DutyFree; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final TBLModel_AttendedWorkEvent m_WorkEventsAttended;
    public TBLModel_AttendedWorkEvent getWorkEventsAttended(){ return m_WorkEventsAttended; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JTextField m_tf_BirthDay;
    public JTextField getTf_BirthDay(){ return m_tf_BirthDay; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JTextField m_tf_Eintritt;
    public JTextField getTf_Eintritt(){ return m_tf_Eintritt; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final JTextField m_tf_Austritt;
    public JTextField getTf_Austritt(){ return m_tf_Austritt; }
    // ------------------------------------------------------------------------

    public MainPanel()
    {
        setLayout(new FormLayout(new ColumnSpec[] {
        		FormSpecs.RELATED_GAP_COLSPEC,
        		ColumnSpec.decode("default:grow"),
        		FormSpecs.RELATED_GAP_COLSPEC,
        		FormSpecs.DEFAULT_COLSPEC,
        		FormSpecs.RELATED_GAP_COLSPEC,
        		ColumnSpec.decode("default:grow"),
        		FormSpecs.RELATED_GAP_COLSPEC,
        		FormSpecs.DEFAULT_COLSPEC,
        		FormSpecs.RELATED_GAP_COLSPEC,
        		ColumnSpec.decode("default:grow"),
        		FormSpecs.RELATED_GAP_COLSPEC,
        		FormSpecs.DEFAULT_COLSPEC,
        		FormSpecs.RELATED_GAP_COLSPEC,
        		ColumnSpec.decode("default:grow"),
        		FormSpecs.RELATED_GAP_COLSPEC,
        		ColumnSpec.decode("default:grow"),
        		FormSpecs.RELATED_GAP_COLSPEC,
        		ColumnSpec.decode("default:grow"),
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
        		FormSpecs.DEFAULT_ROWSPEC,
        		FormSpecs.RELATED_GAP_ROWSPEC,
        		RowSpec.decode("default:grow"),
        		FormSpecs.RELATED_GAP_ROWSPEC,
        		RowSpec.decode("default:grow"),
        		FormSpecs.RELATED_GAP_ROWSPEC,
        		RowSpec.decode("default:grow"),
        		FormSpecs.RELATED_GAP_ROWSPEC,
        		FormSpecs.DEFAULT_ROWSPEC,
        		FormSpecs.RELATED_GAP_ROWSPEC,}));

        final JLabel lblPeriod = new JLabel("Zeitraum");
        add(lblPeriod, "2, 2, right, default");

        m_cmb_Period = new JComboBox<>();
        add(m_cmb_Period, "4, 2, 19, 1, fill, default");

        final JLabel lblMitglied = new JLabel("Mitglied");
        add(lblMitglied, "2, 4, right, default");

        m_cmb_Members = new JComboBox<>();
        add(getCB_Members(), "4, 4, 11, 1, fill, default");

        final JLabel lblMemberFilter = new JLabel("Filter");
        add(lblMemberFilter, "16, 4, right, default");

        m_btn_ClearFilter = new JButton("x");
        add(m_btn_ClearFilter, "18, 4");

        m_tf_MemberFilter = new JTextField();
        add(m_tf_MemberFilter, "20, 4, 3, 1, fill, default");
        m_tf_MemberFilter.setColumns(10);

        final JLabel aLBL_Birthday = new JLabel("Geb.Datum");
        add(aLBL_Birthday, "4, 6, right, default");

        m_tf_BirthDay = new JTextField();
        add(m_tf_BirthDay, "6, 6, fill, default");
        m_tf_BirthDay.setColumns(10);

        final JLabel aLBL_Eintritt = new JLabel("Eintritt");
        add(aLBL_Eintritt, "8, 6, right, default");

        m_tf_Eintritt = new JTextField();
        add(m_tf_Eintritt, "10, 6, fill, default");
        m_tf_Eintritt.setColumns(10);

        final JLabel aLBL_Austritt = new JLabel("Austritt");
        add(aLBL_Austritt, "12, 6, right, default");

        m_tf_Austritt = new JTextField();
        add(m_tf_Austritt, "14, 6, fill, default");
        m_tf_Austritt.setColumns(10);

        final JPanel aPnl_DutyFree = new JPanel();
        aPnl_DutyFree.setBorder(new TitledBorder(null, "AD-Befreiungen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(aPnl_DutyFree, "2, 8, 21, 1, fill, fill");
        aPnl_DutyFree.setLayout(new FormLayout(new ColumnSpec[] {
                ColumnSpec.decode("426px:grow"),
                ColumnSpec.decode("2px"),},
                new RowSpec[] {
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,}));

        final JScrollPane aScrP_DutyFree = new JScrollPane();
        aPnl_DutyFree.add(aScrP_DutyFree, "1, 2, fill, fill");

        m_tbl_DutyFree = new JTable();
        aScrP_DutyFree.setViewportView(m_tbl_DutyFree);

        m_DataModel_DutyFree = new TBLModel_DutyFree();
        m_tbl_DutyFree.setModel(m_DataModel_DutyFree);

        final JPanel m_pnl_Accountings = new JPanel();
        m_pnl_Accountings.setBorder(new TitledBorder(null, "Abrechnungen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(m_pnl_Accountings, "2, 12, 21, 1, fill, fill");
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
        m_DataModel_DutyCharge = new TBLModel_DutyCharge();
        m_tbl_DutyCharges.setModel(m_DataModel_DutyCharge);
        m_tbl_DutyCharges.setFillsViewportHeight(true);
        scrollPane_1.setViewportView(m_tbl_DutyCharges);

        final JLabel lblZuZahlendeStunden = new JLabel("Zu zahlende Stunden");
        m_pnl_Accountings.add(lblZuZahlendeStunden, "2, 4");

        m_tf_HoursToPay = new JTextField();
        m_pnl_Accountings.add(m_tf_HoursToPay, "6, 4, fill, default");
        m_tf_HoursToPay.setColumns(10);

        final JPanel m_pnl_WorkEvents = new JPanel();
        m_pnl_WorkEvents.setBorder(new TitledBorder(null, "Arbeitsdienste", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(m_pnl_WorkEvents, "2, 10, 21, 1, fill, fill");
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

        m_tbl_WorkEventsAttended = new JTable();
        ((JLabel)m_tbl_WorkEventsAttended.getDefaultRenderer(String.class)).setHorizontalAlignment (JLabel.RIGHT);
        m_WorkEventsAttended = new TBLModel_AttendedWorkEvent();
        m_tbl_WorkEventsAttended.setModel(m_WorkEventsAttended);

        m_tbl_WorkEventsAttended.setFillsViewportHeight(true);
        scrollPane.setViewportView(m_tbl_WorkEventsAttended);

        m_btn_ManageWorkEvents = new JButton("Arbeitsdienste...");
        m_btn_ManageWorkEvents.setActionCommand( "OPEN" );
        add(m_btn_ManageWorkEvents, "12, 14");

        m_btn_ManageAdjustment = new JButton("Korrekturen...");
        m_btn_ManageAdjustment.setActionCommand( "OPEN" );
        add(m_btn_ManageAdjustment, "14, 14");

        m_btnFinish = new JButton("Zeitraum abschließen");
        m_btnFinish.setActionCommand( "Zeitraum abschließen" );
        add(m_btnFinish, "16, 14");

        m_btnUpload = new JButton("Daten hochladen...");
        add(m_btnUpload, "22, 14");

    }

    public void setTotalHoursToPay( final float fF )
    {
        m_tf_HoursToPay.setText( String.valueOf( fF ) );
    }

//    public void setWorkEventTableListener( final WorkEventTableListener fWorkEventTableListener )
//    {
//    }

    public boolean removeSelectedWorkEventRow()
    {
        final int aSelectedRow = m_tbl_WorkEventsAttended.getSelectedRow();
        if( aSelectedRow == -1 ){
            return false;
        }
        final Object[] options = {"Ich weiß, was ich tue!", "Nein, das war ein Versehen!"};
        final int n = JOptionPane.showOptionDialog(null,
            "Soll der Arbeitsdiensteintrag wirklich gel\u00F6scht werden?",
            "Sind Sie ganz sicher?",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[1]);
        switch( n ){
            case 0:
                ((DefaultTableModel)m_tbl_WorkEventsAttended.getModel()).removeRow( aSelectedRow );
                return true;

            default:
                return false;
        }
    }

    public WorkEvent getSelectedWorkEvent()
    {
        final int aSelectedRow = m_tbl_WorkEventsAttended.getSelectedRow();
        if( aSelectedRow == -1 ){
            return null;
        }
        final Integer aWorkerID = (Integer)   m_tbl_WorkEventsAttended.getValueAt( aSelectedRow, 0 );
        final LocalDate aDate   = (LocalDate) m_tbl_WorkEventsAttended.getValueAt( aSelectedRow, 2 );
        final WorkEvent aWorkEvent = new WorkEvent( aWorkerID );
        aWorkEvent.setDate( aDate );
        return aWorkEvent;
    }

    public IClubMember getSelectedMember()
    {
        return (IClubMember) getCB_Members().getSelectedItem();
    }

    public int getSelectedMemberID()
    {
        final IClubMember aSelectedMember = getSelectedMember();
        if( aSelectedMember == null ) {
        	return -1;
        }
		return aSelectedMember.getID();
    }

    public void enableBtn_Finish(final boolean fEnable)
    {
        m_btnFinish.setEnabled( fEnable );
    }

    public void enableBtn_Upload( final boolean fEnable )
    {
        m_btnUpload.setEnabled( fEnable );
    }

    public void configure( final ERole fRole )
    {
        switch( fRole ){
            case BAUAUSSCHUSS:

                break;

            case MITGLIEDERWART:
                m_btnUpload.setEnabled( true );
                m_btnFinish.setEnabled( false );
                break;

            default:
                break;
        }
    }
}

// ############################################################################
