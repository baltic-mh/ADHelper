/**
 * ManageWorkEventsListener.java
 *
 * Created on 12.04.2016
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.gui.WorkEventsDialog;
import teambaltic.adhelper.gui.WorkEventsPanel;
import teambaltic.adhelper.gui.model.CBModel_WorkEventDates;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class ManageWorkEventsListener implements ActionListener
{
    // ------------------------------------------------------------------------
    private final ADH_DataProvider m_DataProvider;
    private ADH_DataProvider getDataProvider(){ return m_DataProvider; }
    // ------------------------------------------------------------------------

    private WorkEventsDialog m_WorkEventsDialog;
    private final IPeriodDataController m_PDC;

    public ManageWorkEventsListener(final ADH_DataProvider fDataProvider, final IPeriodDataController fPDC)
    {
        m_DataProvider = fDataProvider;
        m_PDC = fPDC;
        try{
            m_WorkEventsDialog = new WorkEventsDialog();
            final JComboBox<LocalDate> aCmb_Date = getCmb_Date();
            aCmb_Date.addActionListener( this );

            m_WorkEventsDialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        }catch( final Exception e ){
            e.printStackTrace();
        }
    }

    private WorkEventsPanel getWorkEventsPanel()
    {
        final WorkEventsPanel aWorkEventsPanel = m_WorkEventsDialog.getContentPanel();
        return aWorkEventsPanel;
    }

    private JComboBox<LocalDate> getCmb_Date()
    {
        final WorkEventsPanel aWorkEventsPanel = getWorkEventsPanel();
        final JComboBox<LocalDate> aCmb_Date = aWorkEventsPanel.getCmb_Date();
        return aCmb_Date;
    }

    @Override
    public void actionPerformed( final ActionEvent fEvent )
    {
        final String aActionCommand = fEvent.getActionCommand();
        final JComboBox<LocalDate> aCmb_Date = getCmb_Date();
        switch( aActionCommand ){
            case "OPEN":
                m_WorkEventsDialog.setVisible( true );
                final List<LocalDate> aWorkEventDates = getWorkEventDates( getDataProvider() );
                final LocalDate[] aLDArray = new LocalDate[aWorkEventDates.size()];
                aCmb_Date.setModel( new CBModel_WorkEventDates( aWorkEventDates.toArray( aLDArray )) );
                aCmb_Date.setSelectedIndex( aCmb_Date.getItemCount()-1 );
                break;

            case "DATESELECTED":
                final Object aSelectedItem = aCmb_Date.getSelectedItem();
                final Object[][] aWorkEventData = getWorkEventData( (LocalDate) aSelectedItem, getDataProvider() );
                final boolean aFinished = m_PDC.isFinished( getDataProvider().getPeriod() );
                getWorkEventsPanel().populate( aWorkEventData, aFinished );
                break;

            default:
                break;
        }

    }

    private static List<LocalDate> getWorkEventDates( final ADH_DataProvider fDataProvider )
    {
        final List<InfoForSingleMember> aAll = fDataProvider.getAll();

        final List<LocalDate> aWorkEventDates = new ArrayList<>();
        for( final InfoForSingleMember aInfoForSingleMember : aAll ){
            final WorkEventsAttended aWorkEventsAttended = aInfoForSingleMember.getWorkEventsAttended();
            if( aWorkEventsAttended == null ){
                continue;
            }
            final List<WorkEvent> aWorkEvents = aWorkEventsAttended.getWorkEvents();
            for( final WorkEvent aWorkEvent : aWorkEvents ){
                final LocalDate aWorkEventDate = aWorkEvent.getDate();
                if( aWorkEventDates.contains( aWorkEventDate ) ){
                    continue;
                }
                aWorkEventDates.add( aWorkEventDate );
            }
        }
        Collections.sort( aWorkEventDates );
        return aWorkEventDates;
    }

    private static Object[][] getWorkEventData( final LocalDate fADDate, final ADH_DataProvider fDataProvider )
    {
        final List<InfoForSingleMember> aAll = fDataProvider.getAll();
        final Object[][] aWorkEventData = new Object[aAll.size()][4];
        for( int aIdx = 0; aIdx < aAll.size(); aIdx++ ){
            final InfoForSingleMember aInfoForSingleMember = aAll.get( aIdx );
            final IClubMember aMember = aInfoForSingleMember.getMember();
            final Object[] aWorkEventDataForThisMember = new Object[4];
            aWorkEventDataForThisMember[0] = Boolean.FALSE;
            aWorkEventDataForThisMember[1] = aMember.getID();
            aWorkEventDataForThisMember[2] = aMember.getName();
            final WorkEventsAttended aWorkEventsAttended = aInfoForSingleMember.getWorkEventsAttended();
            if( aWorkEventsAttended != null ){
                final List<WorkEvent> aWorkEvents = aWorkEventsAttended.getWorkEvents();
                for( final WorkEvent aWorkEvent : aWorkEvents ){
                    if( fADDate.equals( aWorkEvent.getDate() ) ){
                        aWorkEventDataForThisMember[0] = Boolean.TRUE;
                        aWorkEventDataForThisMember[3] = aWorkEvent.getHours();
                        break;
                    }
                }
            }
            aWorkEventData[aIdx] = aWorkEventDataForThisMember;
        }
        return aWorkEventData;
    }
}

// ############################################################################
