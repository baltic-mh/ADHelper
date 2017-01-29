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

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.gui.model.CBModel_Member;
import teambaltic.adhelper.gui.model.TBLModel_AttendedWorkEvent;
import teambaltic.adhelper.gui.model.TBLModel_DutyCharge;
import teambaltic.adhelper.gui.model.TBLModel_DutyFree;
import teambaltic.adhelper.gui.renderer.RNDR_CB_Member;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IUserSettings;

// ############################################################################
public class GUIUpdater
{
//    private static final Logger sm_Log = Logger.getLogger(GUIUpdater.class);

    private final MainPanel m_Panel;
    private final ADH_DataProvider m_DataProvider;

    // ------------------------------------------------------------------------
    private final IPeriodDataController m_PDC;
    private IPeriodDataController getPDC(){ return m_PDC; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private PeriodData m_PeriodData;
    public  PeriodData getPeriodData(){ return m_PeriodData; }
    // ------------------------------------------------------------------------

    private final RNDR_CB_Member m_Renderer_Member;

    public GUIUpdater(
            final MainPanel             fPanel,
            final ADH_DataProvider      fDataProvider,
            final IPeriodDataController fPDC )
    {
        m_Panel = fPanel;
        m_DataProvider = fDataProvider;
        m_PDC = fPDC;
        m_Renderer_Member = new RNDR_CB_Member( m_DataProvider );
    }

    public void updateGUI()
    {
        updateGUI( m_DataProvider.getPeriodData() );
    }

    public void updateGUI( final PeriodData fPeriodData)
    {
        final boolean aPeriodChanged = isPeriodChanged( fPeriodData );
        if( aPeriodChanged ){
            if( fPeriodData != null ){
                m_PeriodData = fPeriodData;
                assertExistsInComboBoxAndIsSelected( fPeriodData );
            }
            // Erhalten des ehemals selektierten Mitglieds:
            final IClubMember aSelectedMember = m_Panel.getSelectedMember();
            final JComboBox<IClubMember> aCB_Members = m_Panel.getCB_Members();
            final List<IClubMember> aAllMembers = m_DataProvider.getMembers();
            final IClubMember[] aMemberArray = new IClubMember[ aAllMembers.size() ];
            final CBModel_Member aMemberCBModel = new CBModel_Member( aAllMembers.toArray( aMemberArray ) );
            aCB_Members.setRenderer( m_Renderer_Member );
            aCB_Members.setModel( aMemberCBModel );
            if( aSelectedMember != null ){
                // Ehemals selektiertes Mitglied wird wieder selektiert:
                aMemberCBModel.setSelectedItem( m_DataProvider.getMember( aSelectedMember.getID() ) );
            }
        }
        final int aMemberID = m_Panel.getSelectedMemberID();
        final InfoForSingleMember aInfoForSingleMember = m_DataProvider.get( aMemberID );
        fill_Birthday_Eintritt_Austritt(aInfoForSingleMember, m_Panel);
        final TBLModel_DutyFree aDM_DutyFree = m_Panel.getDataModel_DutyFree();
        fillPanel_DutyFree  ( aDM_DutyFree, aInfoForSingleMember, m_Panel, m_DataProvider );
        final TBLModel_AttendedWorkEvent aDM_WorkEventsAttended = m_Panel.getWorkEventsAttended();
        fillPanel_WorkEventsAttended ( aDM_WorkEventsAttended, fPeriodData, aInfoForSingleMember, m_Panel, m_DataProvider );
        final TBLModel_DutyCharge aDM_DutyChargs = m_Panel.getDataModel_DutyCharge();
        fillPanel_DutyCharge( aDM_DutyChargs, aInfoForSingleMember, m_Panel, m_DataProvider, fPeriodData.getPeriod() );

        if( fPeriodData != null ){
            configureButtons( m_Panel, getPDC(), fPeriodData );
        }
    }

    private void assertExistsInComboBoxAndIsSelected( final PeriodData fPeriodData )
    {
        final JComboBox<PeriodData> aCB_Period = m_Panel.getCB_Period();
        final DefaultComboBoxModel<PeriodData> aModel = (DefaultComboBoxModel<PeriodData>) aCB_Period.getModel();
        boolean aFound = false;
        for( int aIdx = 0; aIdx < aModel.getSize(); aIdx++ ){
            final PeriodData aThisElement = aModel.getElementAt( aIdx );
            if( fPeriodData.equals( aThisElement ) ){
                aFound = true;
                break;
            }
        }
        if( !aFound ){
            aModel.addElement( fPeriodData );
        }
        aCB_Period.setSelectedItem( fPeriodData );
    }

    private boolean isPeriodChanged( final PeriodData fPeriodData )
    {
        if( fPeriodData == null ){
            return m_PeriodData != null;
        }
        final boolean aPeriodChanged = m_PeriodData == null ? true : !m_PeriodData.equals( fPeriodData );
        return aPeriodChanged;
    }

    private void fill_Birthday_Eintritt_Austritt( final InfoForSingleMember fInfoForSingleMember, final MainPanel fPanel )
    {
        final IClubMember aMember = fInfoForSingleMember.getMember();
        final JTextField aTF_Birthday = m_Panel.getTf_BirthDay();
        aTF_Birthday.setText( aMember.getBirthday().toString() );
        final JTextField aTF_Eintritt = m_Panel.getTf_Eintritt();
        aTF_Eintritt.setText( aMember.getMemberFrom().toString() );
        final JTextField aTF_Austritt = m_Panel.getTf_Austritt();
        final LocalDate aMemberUntil = aMember.getMemberUntil();
        if( aMemberUntil != null ){
            aTF_Austritt.setText( aMemberUntil.toString() );
        }
    }

    private static void fillPanel_WorkEventsAttended(
            final TBLModel_AttendedWorkEvent fDataModel,
            final PeriodData                 fPeriodData,
            final InfoForSingleMember        fInfoForSingleMember,
            final MainPanel                  fPanel,
            final ADH_DataProvider           fDataProvider )
    {
        fDataModel.setRowCount( 0 );
        final WorkEventsAttended aWorkEventsAttended = fInfoForSingleMember.getWorkEventsAttended();
        if( aWorkEventsAttended == null ){
            return;
        }

        final List<WorkEvent> aAllWorkEvents = aWorkEventsAttended.getAllWorkEvents(fPeriodData.getPeriod());

        for( final WorkEvent aWorkEvent : aAllWorkEvents ){
            final int aMemberID = aWorkEvent.getID();
            final String aMemberName = fDataProvider.getMemberName( aMemberID );
            addWorkEventRow( fDataModel, aMemberName, aMemberID, aWorkEvent);
        }
    }

    private static void addWorkEventRow(
            final TBLModel_AttendedWorkEvent fDataModel,
            final String                     fMemberName,
            final int                        fMemberID,
            final WorkEvent                  fWorkEvent )
    {
        final Vector<Object> rowData = new Vector<>();
        rowData.addElement( Integer.valueOf( fMemberID ) );
        rowData.addElement( fMemberName );
        rowData.addElement( fWorkEvent.getDate() );
        rowData.addElement( fWorkEvent.getHours()/100.0f );
        rowData.addElement( fWorkEvent.getCleared() );
        fDataModel.addRow( rowData );
    }

    private static void fillPanel_DutyFree(
            final TBLModel_DutyFree     fDataModel,
            final InfoForSingleMember   fInfoForSingleMember,
            final MainPanel             fPanel,
            final ADH_DataProvider      fDataProvider )
    {
        fDataModel.setRowCount( 0 );
        final FreeFromDutySet aFreeFromDutySet = fInfoForSingleMember.getFreeFromDutySet();
        final Collection<FreeFromDuty> aFreeFromDutyItems = aFreeFromDutySet.getFreeFromDutyItems();
        final IPeriod aPeriod = fDataProvider.getPeriod();
        for( final FreeFromDuty aFreeFromDuty : aFreeFromDutyItems ){
            addRow_DutyFree( fDataModel, aFreeFromDuty, aPeriod );
        }
    }

    private static void addRow_DutyFree(
            final TBLModel_DutyFree fDataModel,
            final FreeFromDuty      fFreeFromDuty,
            final IPeriod           fPeriod )
    {
        if( !fPeriod.isWithinMyPeriod( fFreeFromDuty ) ){
            return;
        }
        final Vector<Object> rowData = new Vector<>();
        final LocalDate aFrom = fFreeFromDuty.getFrom();
        rowData.addElement( fFreeFromDuty.getReason() );
        rowData.addElement( aFrom );
        rowData.addElement( fFreeFromDuty.getUntil() );
        fDataModel.addRow( rowData );
    }

    private static void fillPanel_DutyCharge(
            final TBLModel_DutyCharge   fDataModel,
            final InfoForSingleMember   fInfoForSingleMember,
            final MainPanel             fPanel,
            final ADH_DataProvider      fDataProvider,
            final IPeriod               fPeriod )
    {
        fDataModel.setRowCount( 0 );
        final List<InfoForSingleMember> aAllRelatives = fInfoForSingleMember.getAllRelatives();
        for( final InfoForSingleMember aInfoForThisRelative : aAllRelatives ){
            final String aMemberName = fDataProvider.getMemberName( aInfoForThisRelative.getID() );
            final DutyCharge aDutyCharge = aInfoForThisRelative.getDutyCharge();
            final Balance aBalance = aInfoForThisRelative.getBalance( fPeriod );
            addRow_DutyChargeAndBalance( fDataModel, aMemberName, aDutyCharge, aBalance );
        }
        final DutyCharge aDutyChargeOfThisMember = fInfoForSingleMember.getDutyCharge();
        fPanel.setTotalHoursToPay( aDutyChargeOfThisMember.getHoursToPayTotal() / 100.0f );
    }

    private static void addRow_DutyChargeAndBalance(
            final TBLModel_DutyCharge   fDataModel,
            final String                fMemberName,
            final DutyCharge            fDutyCharge,
            final Balance               fBalance )
    {
        final Vector<Object> rowData = new Vector<>();
        rowData.addElement( fMemberName );
        rowData.addElement( fBalance.getValue_Original()/100.0f );
        rowData.addElement( fDutyCharge.getHoursWorked()/100.0f );
        rowData.addElement( fDutyCharge.getHoursDue()/100.0f );
        rowData.addElement( fBalance.getValue_Charged()/100.0f );
        rowData.addElement( fDutyCharge.getHoursToPay()/100.0f );
        rowData.addElement( fBalance.getValue_ChargedAndAdjusted()/100.0f );
        fDataModel.addRow( rowData );
    }

    private static void configureButtons(
            final MainPanel fPanel,
            final IPeriodDataController fPDC,
            final PeriodData fPeriodData)
    {
        final boolean aFinished = fPDC.isFinished( fPeriodData );
        fPanel.enableBtn_Finish( !aFinished );
//        fPanel.enableBtn_Upload( aFinished && !fPDC.isUploaded( fPeriodData ) );
        fPanel.enableBtn_Upload( true );

        final IUserSettings aUserSettings = AllSettings.INSTANCE.getUserSettings();
        fPanel.configure( aUserSettings.getRole() );
    }

}

// ############################################################################
