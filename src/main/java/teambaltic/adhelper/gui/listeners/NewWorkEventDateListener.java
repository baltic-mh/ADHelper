/**
 * NewWorkEventDateListener.java
 *
 * Created on 03.05.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.gui.DateChooserFrame;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.PeriodData;

// ############################################################################
public class NewWorkEventDateListener implements ActionListener
{
    private final DateChooserFrame m_DateChooserFrame;

    // ------------------------------------------------------------------------
    private final IPeriodDataController m_PDC;
    private IPeriodDataController getPDC(){ return m_PDC; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final ManageWorkEventsListener m_ManageWorkEventsListener;
    private ManageWorkEventsListener getManageWorkEventsListener()
    {
        return m_ManageWorkEventsListener;
    }
    // ------------------------------------------------------------------------

    public NewWorkEventDateListener(
            final ManageWorkEventsListener fManageWorkEventsListener,
            final DateChooserFrame fDateChooserFrame,
            final IPeriodDataController fPDC)
    {
        m_ManageWorkEventsListener = fManageWorkEventsListener;
        m_DateChooserFrame = fDateChooserFrame;
        m_PDC = fPDC;
    }

    @Override
    public void actionPerformed(final ActionEvent fEvent)
    {
        final String aActionCommand = fEvent.getActionCommand();
        switch( aActionCommand ){
            case "NEU":
                m_DateChooserFrame.setVisible( true );
                break;

            case "OK":
                final LocalDate aSelectedDate = m_DateChooserFrame.getSelectedDate();
                final LocalDate aLimit = getDateLimit();
                if( aSelectedDate == null || aSelectedDate.compareTo( aLimit ) <= 0 ){
                    showWarnDialog( aSelectedDate, aLimit );
                } else {
                    System.err.println( "Neuen Arbeitdiensttermin anlegen: "+aSelectedDate );
                    final JComboBox<LocalDate> aCmb_Date = getManageWorkEventsListener().getCmb_Date();
                    aCmb_Date.addItem( aSelectedDate );
                    aCmb_Date.setSelectedIndex( aCmb_Date.getItemCount()-1 );
                }
                m_DateChooserFrame.setVisible( false );
                break;

            case "CANCEL":
                m_DateChooserFrame.setVisible( false );

            default:
                break;
        }
    }

    private LocalDate getDateLimit()
    {
        final PeriodData aActivePeriodData = getPDC().getActivePeriod();
        final IPeriod    aActivePeriod     = aActivePeriodData.getPeriod();
        if( getPDC().isActivePeriodFinished() ){
            return aActivePeriod.getEnd();
        }
        return getPDC().getPredecessor( aActivePeriod ).getPeriod().getEnd();
    }

    private static void showWarnDialog( final LocalDate fChoosenDate, final LocalDate fLimit )
    {
        String aText = "Kein Datum angegeben!";
        if( fChoosenDate != null){
            aText = String.format( "Ungültiges Datum angegeben: %s! Es muss nach dem %s liegen!", fChoosenDate, fLimit);
        }
        final Object[] options = {"Kommt nicht wieder vor!"};
        JOptionPane.showOptionDialog(null,
            aText,
            "Bitte noch mal neu nachdenken!",
            JOptionPane.OK_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[0]);
    }
}
// ############################################################################
