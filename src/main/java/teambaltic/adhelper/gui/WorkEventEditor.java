/**
 * WorkEventEditor.java
 *
 * Created on 13.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw‰rmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jdatepicker.DateModel;
import org.jdatepicker.JDateComponentFactory;
import org.jdatepicker.JDatePicker;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.gui.listeners.WorkEventEditorActionListener;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.WorkEvent;


// ############################################################################
public class WorkEventEditor extends JDialog
{
    private static final long serialVersionUID = 3791963724089535848L;

    private final JPanel m_contentPanel = new JPanel();

    // ------------------------------------------------------------------------
    private final JComboBox<IClubMember> m_cmb_Members;
    public JComboBox<IClubMember> getCB_Members(){ return m_cmb_Members; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private JButton m_btn_Apply;
    private JButton getBtnApply(){ return m_btn_Apply; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private JButton m_btn_OK;
    private JButton getBtnOK(){ return m_btn_OK; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private JButton m_btn_Cancel;
    private JButton getBtnCancel(){ return m_btn_Cancel; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
//    private DateField m_DateField;
    private JDatePicker m_DateField;
    private LocalDate getDate()
    {
        final DateModel<?> aModel = m_DateField.getModel();
        final int aYear = aModel.getYear();
        final int aMonth= aModel.getMonth()+1;
        final int aDay= aModel.getDay();
        return LocalDate.of( aYear, aMonth, aDay );
    }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private JSpinner m_spn_HoursWorked;
    private int getHoursWorked()
    {
        final Double aValue = (Double) m_spn_HoursWorked.getValue();
        return (int) ( 100*aValue );
    }
    // ------------------------------------------------------------------------

    /**
     * Create the dialog.
     */
    public WorkEventEditor()
    {
        setAlwaysOnTop(true);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setTitle("Arbeitsdiensteingabe");
        setBounds( 300, 100, 491, 175 );
        getContentPane().setLayout( new BorderLayout() );
        m_contentPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        getContentPane().add( m_contentPanel, BorderLayout.CENTER );
        m_contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,}));
        {
            final JLabel lblNewLabel = new JLabel("Mitglied");
            m_contentPanel.add(lblNewLabel, "2, 2, right, default");
        }
        {
            m_cmb_Members = new JComboBox<>();
            m_contentPanel.add(m_cmb_Members, "4, 2, fill, default");
        }
        {
            final JLabel lblDatum = new JLabel("Datum");
            lblDatum.setHorizontalAlignment(SwingConstants.RIGHT);
            m_contentPanel.add(lblDatum, "2, 4, right, default");
        }
        {
//            m_DateField = CalendarFactory.createDateField();
            final JDateComponentFactory aDCFactory = new JDateComponentFactory();
            m_DateField = aDCFactory.createJDatePicker();
            m_contentPanel.add( (JComponent)m_DateField, "4, 4, fill, default");
        }
        {
            final JLabel lblStundenGearbeitet = new JLabel("Stunden gearbeitet");
            lblStundenGearbeitet.setHorizontalAlignment(SwingConstants.RIGHT);
            m_contentPanel.add(lblStundenGearbeitet, "2, 6");
        }
        {
            m_spn_HoursWorked = new JSpinner(new SpinnerNumberModel(0., 0., 100., 0.25));
              final JSpinner.NumberEditor editor =
                    new JSpinner.NumberEditor(m_spn_HoursWorked, "0.##");
              final DecimalFormat format = editor.getFormat();
              format.setMinimumFractionDigits(2);
              m_spn_HoursWorked.setEditor(editor);
            m_contentPanel.add(m_spn_HoursWorked, "4, 6");
        }
        {
            final JPanel buttonPane = new JPanel();
            buttonPane.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
            getContentPane().add( buttonPane, BorderLayout.SOUTH );
            {
                m_btn_Cancel = new JButton( "Abbrechen" );
                m_btn_Cancel.setActionCommand( "Cancel" );
                buttonPane.add( m_btn_Cancel );
            }
            {
                m_btn_Apply = new JButton("‹bernehmen");
                m_btn_Apply.setActionCommand( "Apply" );
                buttonPane.add(m_btn_Apply);
            }
            {
                m_btn_OK = new JButton( "Schlieﬂen" );
                m_btn_OK.setActionCommand( "OK" );
                buttonPane.add( m_btn_OK );
                getRootPane().setDefaultButton( m_btn_OK );
            }
        }
    }

    public void display( final String fActionCommand)
    {
        switch( fActionCommand ){
            case "New":
                getCB_Members().setEnabled( true );
                break;

            case "Edit":
                getCB_Members().setEnabled( false );
                break;

            default:
                break;
        }
        try{
            setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
            setVisible( true );
        }catch( final Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        final WorkEventEditor dialog = new WorkEventEditor();
        dialog.display("New" );
    }

    public void setWorkEventEditorListeners(final WorkEventEditorActionListener fListener)
    {
        getBtnApply().addActionListener(fListener);
        getBtnOK().addActionListener(fListener);
        getBtnCancel().addActionListener(fListener);
    }

    private WorkEvent createNewWorkEvent()
    {
        final int aMemberID = getCurrentMemberID();
        final WorkEvent aResult = new WorkEvent( aMemberID );
        return aResult;
    }

    private int getCurrentMemberID()
    {
        final JComboBox<IClubMember> aCB = getCB_Members();
        final IClubMember aSelectedItem = (IClubMember) aCB.getSelectedItem();
        final int aMemberID = aSelectedItem.getID();
        return aMemberID;
    }

    public WorkEvent getWorkEvent()
    {
        final WorkEvent aWorkEvent = createNewWorkEvent();
        aWorkEvent.setDate( getDate() );
        final int aHours = getHoursWorked();
        aWorkEvent.setHours( aHours );
        return aWorkEvent;
    }

    public void reset()
    {
        m_spn_HoursWorked.setValue( Double.valueOf( 0.00 ) );
    }

}

// ############################################################################
