/**
 * GUIUpdater.java
 *
 * Created on 12.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.listeners;

import java.util.List;
import java.util.Vector;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.DutyChargeTableModel;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.gui.model.WorkEventTableModel;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class GUIUpdater
{
    public static void updateGUI( final int fMemberID,
            final MainPanel fPanel, final ADH_DataProvider fDataProvider )
    {
        fillDutyChargePanel( fMemberID, fPanel, fDataProvider );
        fillWorkEventPanel( fMemberID, fPanel, fDataProvider );
    }

    private static void fillWorkEventPanel(final int fMemberID,
            final MainPanel fPanel, final ADH_DataProvider fDataProvider )
    {
        final WorkEventTableModel aDataModel = fPanel.getWorkEventDataModel();
        aDataModel.setRowCount( 0 );

        final InfoForSingleMember aInfoForSingleMember = fDataProvider.get( fMemberID );
        final WorkEventsAttended aWorkEventsAttended = aInfoForSingleMember.getWorkEventsAttended();
        if( aWorkEventsAttended == null ){
            return;
        }

        final List<WorkEventsAttended> aAllWorkEventsAttended = aWorkEventsAttended.getAllWorkEventsAttended();
        for( final WorkEventsAttended aThisWorkEventsAttended : aAllWorkEventsAttended ){
            final int aMemberID = aThisWorkEventsAttended.getMemberID();
            final String aMemberName = fDataProvider.getMemberName( aMemberID );
            addWorkEventsForSingleMember( aDataModel, aMemberName, aMemberID, aThisWorkEventsAttended );
        }
    }

    private static void addWorkEventsForSingleMember(
            final WorkEventTableModel fDataModel,
            final String fMemberName,
            final int    fMemberID,
            final WorkEventsAttended fWorkEventsAttended )
    {
        for( final WorkEvent aWorkEvent : fWorkEventsAttended.getWorkEvents() ){
            addWorkEventRow( fDataModel, fMemberName, fMemberID, aWorkEvent );
        }
    }

    private static void addWorkEventRow(
            final WorkEventTableModel fDataModel,
            final String fMemberName,
            final int    fMemberID,
            final WorkEvent fWorkEvent )
    {
        final Vector<Object> rowData = new Vector<>();
        rowData.addElement( Integer.valueOf( fMemberID ) );
        rowData.addElement( fMemberName );
        rowData.addElement( fWorkEvent.getDate() );
        rowData.addElement( fWorkEvent.getHours()/100.0f );
        rowData.addElement( fWorkEvent.getCleared() );
        fDataModel.addRow( rowData );
    }

    private static void fillDutyChargePanel(final int fMemberID,
            final MainPanel fPanel, final ADH_DataProvider fDataProvider )
    {
        final DutyChargeTableModel aDataModel = fPanel.getDutyChargeDataModel();
        aDataModel.setRowCount( 0 );
        final InfoForSingleMember aInfoForSingleMember = fDataProvider.get( fMemberID );
        final DutyCharge aDutyChargeOfPayer = aInfoForSingleMember.getDutyCharge();
        final List<DutyCharge> aAllDutyCharges = aDutyChargeOfPayer.getAllDutyCharges();
        for( final DutyCharge aDutyCharge : aAllDutyCharges ){
            final String aMemberName = fDataProvider.getMemberName( aDutyCharge.getMemberID() );
            addDutyChargeRow( aDataModel, aMemberName, aDutyCharge );
        }
        fPanel.setTotalHoursToPay( aDutyChargeOfPayer.getHoursToPayTotal() / 100.0f );
    }

    private static void addDutyChargeRow(
            final DutyChargeTableModel fDataModel,
            final String fMemberName,
            final DutyCharge fDutyCharge )
    {
        final Vector<Object> rowData = new Vector<>();
        rowData.addElement( fMemberName );
        rowData.addElement( fDutyCharge.getBalance_Original()/100.0f );
        rowData.addElement( fDutyCharge.getHoursWorked()/100.0f );
        rowData.addElement( fDutyCharge.getHoursDue()/100.0f );
        rowData.addElement( fDutyCharge.getBalance_Charged()/100.0f );
        rowData.addElement( fDutyCharge.getHoursToPay()/100.0f );
        rowData.addElement( fDutyCharge.getBalance_ChargedAndAdjusted()/100.0f );
        fDataModel.addRow( rowData );
    }


}

// ############################################################################
