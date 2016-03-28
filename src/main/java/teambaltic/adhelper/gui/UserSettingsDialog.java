/**
 * UserDataDialog.java
 *
 * Created on 02.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.model.ERole;

// ############################################################################
public class UserSettingsDialog extends JDialog
{
    private static final long serialVersionUID = 531136141766493612L;

    private final JPanel m_contentPanel = new JPanel();

    // ------------------------------------------------------------------------
    private JTextField m_tf_Name;
    public JTextField getTf_Name(){ return m_tf_Name; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private JTextField m_tf_EMail;
    public JTextField getTf_EMail(){ return m_tf_EMail; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private JComboBox<ERole> m_cb_Role;
    public JComboBox<ERole> getCb_Role(){ return m_cb_Role; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private JButton m_btn_OK;
    public JButton getBtn_OK(){ return m_btn_OK; }
    // ------------------------------------------------------------------------

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        try{
            final UserSettingsDialog dialog = new UserSettingsDialog();
            dialog.setVisible( true );
        }catch( final Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public UserSettingsDialog()
    {
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setTitle("Bitte gib deine Benutzerdaten an:");
        setBounds( 100, 100, 450, 300 );
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
            final JLabel lblName = new JLabel("Name");
            m_contentPanel.add(lblName, "2, 2, right, default");
        }
        {
            m_tf_Name = new JTextField();
            m_contentPanel.add(m_tf_Name, "4, 2, fill, default");
            m_tf_Name.setColumns(10);
        }
        {
            final JLabel lblEmail = new JLabel("EMail");
            m_contentPanel.add(lblEmail, "2, 4, right, default");
        }
        {
            m_tf_EMail = new JTextField();
            m_contentPanel.add(m_tf_EMail, "4, 4, fill, default");
            m_tf_EMail.setColumns(10);
        }
        {
            final JLabel lblRolle = new JLabel("Rolle");
            m_contentPanel.add(lblRolle, "2, 6, right, default");
        }
        {
            m_cb_Role = new JComboBox<>();
            for( final ERole aThisRole : ERole.values() ){
                m_cb_Role.addItem( aThisRole );
            }
            m_contentPanel.add(m_cb_Role, "4, 6, fill, default");
        }
        {
            final JPanel buttonPane = new JPanel();
            buttonPane.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
            getContentPane().add( buttonPane, BorderLayout.SOUTH );
            {
                m_btn_OK = new JButton( "OK" );
                m_btn_OK.setActionCommand( "OK" );
                buttonPane.add( m_btn_OK );
                getRootPane().setDefaultButton( m_btn_OK );
            }
//            {
//                final JButton cancelButton = new JButton( "Cancel" );
//                cancelButton.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(final ActionEvent e) {
//                        setVisible(false);
//                    }
//                });
//                cancelButton.setActionCommand( "Cancel" );
//                buttonPane.add( cancelButton );
//            }
        }
    }

}

// ############################################################################
