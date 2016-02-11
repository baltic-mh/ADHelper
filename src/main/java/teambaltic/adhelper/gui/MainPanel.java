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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.gui.model.WorkEventTableModel;
import teambaltic.adhelper.model.IClubMember;

// ############################################################################
public class MainPanel extends JPanel
{
    private static final long serialVersionUID = 7281848889524771889L;

    private final JTextField m_tf_HoursToPay;
    private final JTable m_tbl_WorkEvents;
    private final JTable m_tbl_DutyCharges;

    // ------------------------------------------------------------------------
    private final JComboBox<IClubMember> m_CB_Members;
    public JComboBox<IClubMember> getCB_Members(){ return m_CB_Members; }
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
                RowSpec.decode("max(58dlu;default)"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("max(55dlu;default)"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,}));

        final JLabel lblMitglied = new JLabel("Mitglied");
        add(lblMitglied, "2, 2, right, default");

        m_CB_Members = new JComboBox<>();
        add(getCB_Members(), "4, 2, 9, 1, fill, default");

        getCB_Members().getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased( final KeyEvent e )
            {
                if( e.getKeyCode() != 38 && e.getKeyCode() != 40 && e.getKeyCode() != 10 ){
                    final int aItemCount = getCB_Members().getItemCount();
                    final String a = getCB_Members().getEditor().getItem().toString();
                    getCB_Members().removeAllItems();
                    int st = 0;

                    for( int i = 0; i < aItemCount; i++ ){
                        final IClubMember aItem = getCB_Members().getItemAt( i );
                        if( aItem.toString().startsWith( a ) ){
                            getCB_Members().addItem( aItem );
                            st++;
                        }
                    }
                    getCB_Members().getEditor().setItem( new String( a ) );
                    getCB_Members().hidePopup();
                    if( st != 0 ){
                        getCB_Members().showPopup();
                    }
                }
            } } );

        final JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Abrechnungen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel, "2, 4, 11, 1, fill, fill");
        panel.setLayout(new FormLayout(new ColumnSpec[] {
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
        panel.add(scrollPane_1, "2, 2, 7, 1, fill, fill");

        m_tbl_DutyCharges = new JTable();
        ((JLabel)m_tbl_DutyCharges.getDefaultRenderer(String.class)).setHorizontalAlignment (JLabel.RIGHT);
        m_DutyChargeDataModel = new DutyChargeTableModel();
        m_tbl_DutyCharges.setModel(m_DutyChargeDataModel);
        m_tbl_DutyCharges.setFillsViewportHeight(true);
        scrollPane_1.setViewportView(m_tbl_DutyCharges);

        final JLabel lblZuZahlendeStunden = new JLabel("Zu zahlende Stunden");
        panel.add(lblZuZahlendeStunden, "2, 4");

        m_tf_HoursToPay = new JTextField();
        panel.add(m_tf_HoursToPay, "6, 4, fill, default");
        m_tf_HoursToPay.setColumns(10);

        final JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Arbeitsdienste", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel_1, "2, 6, 11, 1, fill, fill");
        panel_1.setLayout(new FormLayout(new ColumnSpec[] {
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
        panel_1.add(scrollPane, "2, 2, 9, 1, fill, fill");

        m_tbl_WorkEvents = new JTable();
        ((JLabel)m_tbl_WorkEvents.getDefaultRenderer(String.class)).setHorizontalAlignment (JLabel.RIGHT);
        m_WorkEventDataModel = new WorkEventTableModel();
        m_tbl_WorkEvents.setModel(m_WorkEventDataModel);
        m_tbl_WorkEvents.setFillsViewportHeight(true);
        scrollPane.setViewportView(m_tbl_WorkEvents);

        final JButton btnBerechnen = new JButton("Berechnen");
        panel_1.add(btnBerechnen, "4, 4");

        final JButton btnEntfernen = new JButton("Entfernen");
        panel_1.add(btnEntfernen, "6, 4");

        final JButton btnBearbeiten = new JButton("Bearbeiten");
        btnBearbeiten.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
            }
        });
        panel_1.add(btnBearbeiten, "8, 4");

        final JButton btnNeu = new JButton("Neu");
        panel_1.add(btnNeu, "10, 4");

        final JButton btnAusgabe = new JButton("Ausgabe");
        add(btnAusgabe, "12, 8");

    }

    public void setTotalHoursToPay( final float fF )
    {
        m_tf_HoursToPay.setText( String.valueOf( fF ) );
    }

}

// ############################################################################
