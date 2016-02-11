/**
 * MemberSelectedListener.java
 *
 * Created on 11.02.2016
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
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.DutyChargeTableModel;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.gui.model.WorkEventTableModel;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class MemberSelectedListener implements ActionListener
{
    private final MainPanel m_Panel;
    private final ADH_DataProvider m_DataProvider;

    public MemberSelectedListener(
            final MainPanel fPanel,
            final ADH_DataProvider fDataProvider)
    {
        m_Panel = fPanel;
        m_DataProvider = fDataProvider;
    }

    @Override
    public void actionPerformed( final ActionEvent fE )
    {
        @SuppressWarnings("unchecked")
        final JComboBox<IClubMember> aCB = (JComboBox<IClubMember>) fE.getSource();
        final IClubMember aSelectedItem = (IClubMember) aCB.getSelectedItem();
        final int aMemberID = aSelectedItem.getID();
        final MainPanel aPanel = m_Panel;
        fillDutyChargePanel( aPanel, aMemberID, m_DataProvider );
        fillWorkEventPanel( aPanel, aMemberID, m_DataProvider );

    }

    private static void fillWorkEventPanel(
            final MainPanel fPanel, final int fMemberID, final ADH_DataProvider fDataProvider )
    {
        final WorkEventTableModel aDataModel = fPanel.getWorkEventDataModel();
        aDataModel.setRowCount( 0 );

        final InfoForSingleMember aInfoForSingleMember = fDataProvider.get( fMemberID );
        final WorkEventsAttended aWorkEventsAttended = aInfoForSingleMember.getWorkEventsAttended();
        if( aWorkEventsAttended == null ){
            return;
        }

        final List<WorkEventsAttended> aAllWorkEventsAttended = aWorkEventsAttended.getAllWorkEventsAttended();
        for( final WorkEventsAttended aWorkEventsAttended2 : aAllWorkEventsAttended ){
            final String aMemberName = fDataProvider.getMemberName( aWorkEventsAttended2.getMemberID() );
            addWorkEventsForSingleMember( aDataModel, aMemberName, aWorkEventsAttended2 );
        }
    }

    private static void addWorkEventsForSingleMember(
            final WorkEventTableModel fDataModel,
            final String fMemberName,
            final WorkEventsAttended fWorkEventsAttended )
    {
        for( final WorkEvent aWorkEvent : fWorkEventsAttended.getWorkEvents() ){
            addWorkEventRow( fDataModel, fMemberName, aWorkEvent );
        }
    }

    private static void addWorkEventRow(
            final WorkEventTableModel fDataModel,
            final String fMemberName,
            final WorkEvent fWorkEvent )
    {
        final Vector<Object> rowData = new Vector<>();
        rowData.addElement( fMemberName );
        rowData.addElement( fWorkEvent.getDate() );
        rowData.addElement( fWorkEvent.getHours()/100.0f );
        rowData.addElement( fWorkEvent.getCleared() );
        fDataModel.addRow( rowData );
    }

    private static void fillDutyChargePanel(
            final MainPanel fPanel, final int fMemberID, final ADH_DataProvider fDataProvider )
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
