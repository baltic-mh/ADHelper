/**
 * DateChooserFrame.java
 *
 * Created on 03.05.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdatepicker.DateModel;
import org.jdatepicker.impl.JDatePanelImpl;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.gui.model.UtilLocalDateModel;

// ############################################################################
public class DateChooserFrame extends JFrame
{
    private static final long serialVersionUID = -6151888543651742981L;
    private static final Properties DATECHOOSERPROPS = new Properties();
    static{
        DATECHOOSERPROPS.put( "text.today", "Heute" );
        DATECHOOSERPROPS.put( "text.month", "Monat" );
        DATECHOOSERPROPS.put( "text.year" , "Jahr" );
    }

    private final JDatePanelImpl m_DatePanel;
    private final JButton m_btn_Cancel;
    private final JButton m_btn_OK;

    /**
     * Launch the application.
     */
    public static void main( final String[] args )
    {
        EventQueue.invokeLater( new Runnable() {
            @Override
            public void run()
            {
                try{
                    final DateChooserFrame frame = new DateChooserFrame();
                    frame.setVisible( true );
                }catch( final Exception e ){
                    e.printStackTrace();
                }
            }
        } );
    }

    /**
     * Create the frame.
     */
    public DateChooserFrame()
    {
        final UtilLocalDateModel model = new UtilLocalDateModel();
//        model.setDate(2016,3,9);
//        model.setSelected( true );

        final JPanel aContentPane;

        setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
        setBounds( 100, 100, 450, 300 );
        aContentPane = new JPanel();
        aContentPane.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        setContentPane( aContentPane );
        aContentPane.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,}));

        m_DatePanel = new JDatePanelImpl(model, DATECHOOSERPROPS);
        aContentPane.add(m_DatePanel, "2, 2, 5, 1, fill, fill");

        m_btn_Cancel = new JButton("Abbrechen");
        m_btn_Cancel.setActionCommand( "CANCEL" );
        aContentPane.add(m_btn_Cancel, "4, 4");

        m_btn_OK = new JButton("OK");
        aContentPane.add(m_btn_OK, "6, 4");
    }

    public LocalDate getSelectedDate()
    {
        @SuppressWarnings("unchecked")
        final DateModel<LocalDate> aModel = (DateModel<LocalDate>) m_DatePanel.getModel();
        final LocalDate selectedDate = aModel.getValue();
        return selectedDate;
    }

    public void addActionListener( final ActionListener fListener )
    {
        m_btn_Cancel.addActionListener(fListener);
        m_btn_OK.addActionListener(fListener);
    }
}

// ############################################################################
