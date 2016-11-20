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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.controller.IPeriodDataController.EPeriodDataSelector;
import teambaltic.adhelper.gui.DateChooserFrame;
import teambaltic.adhelper.gui.WorkEventsDialog;
import teambaltic.adhelper.gui.WorkEventsPanel;
import teambaltic.adhelper.gui.model.CBModel_PeriodData;
import teambaltic.adhelper.gui.model.CBModel_WorkEventDates;
import teambaltic.adhelper.gui.model.TBLModel_WorkEvents;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class ManageWorkEventsListener implements ActionListener, TableModelListener
{
//    private static final Logger sm_Log = Logger.getLogger(ManageWorkEventsListener.class);

    private static final EPeriodDataSelector ALL = EPeriodDataSelector.ALL;

    private static final PeriodData ALLPERIODS = new PeriodData( null ){
        @Override
        public String toString(){ return "ALLE"; }
    };
    // ------------------------------------------------------------------------
    private final ADH_DataProvider m_DataProvider;
    private ADH_DataProvider getDataProvider(){ return m_DataProvider; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IPeriodDataController m_PDC;
    private IPeriodDataController getPDC(){ return m_PDC; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final GUIUpdater m_GUIUpdater;
    private GUIUpdater getGUIUpdater(){ return m_GUIUpdater; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final boolean m_IsBauausschuss;
    public boolean isBauausschuss(){ return m_IsBauausschuss; }
    // ------------------------------------------------------------------------

    private final WorkEventsDialog m_WorkEventsDialog;

    private final NewWorkEventDateListener m_NewWorkEventDateListener;

    public ManageWorkEventsListener(
            final ADH_DataProvider fDataProvider,
            final IPeriodDataController fPDC,
            final GUIUpdater fGUIUpdater,
            final boolean fIsBauausschuss )
    {
        m_DataProvider   = fDataProvider;
        m_PDC            = fPDC;
        m_GUIUpdater     = fGUIUpdater;
        m_IsBauausschuss = fIsBauausschuss;

        m_WorkEventsDialog = new WorkEventsDialog();
        final DateChooserFrame aDateChooserFrame = new DateChooserFrame();
        m_NewWorkEventDateListener = new NewWorkEventDateListener(this, aDateChooserFrame, fPDC );
        aDateChooserFrame.addActionListener( m_NewWorkEventDateListener );
        getBtn_Ok().addActionListener( this );
        getBtn_Neu().addActionListener( this );
        getBtn_Abbrechen().addActionListener( this );
        getCmb_Date().addActionListener( this );
        final JComboBox<PeriodData> aCmb_Period = getCmb_Period();
        aCmb_Period.addActionListener( this );
        populateCmbPeriods( aCmb_Period, getPDC() );

        m_WorkEventsDialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
    }

    private WorkEventsPanel getWorkEventsPanel()
    {
        final WorkEventsPanel aWorkEventsPanel = m_WorkEventsDialog.getContentPanel();
        return aWorkEventsPanel;
    }

    private JButton getBtn_Abbrechen()
    {
        return getWorkEventsPanel().getBtn_Abbrechen();
    }

    private JButton getBtn_Neu()
    {
        return getWorkEventsPanel().getBtn_Neu();
    }

    private JButton getBtn_Ok()
    {
        return getWorkEventsPanel().getBtn_Ok();
    }

    public JComboBox<LocalDate> getCmb_Date()
    {
        final WorkEventsPanel aWorkEventsPanel = getWorkEventsPanel();
        return aWorkEventsPanel.getCmb_Date();
    }

    public JComboBox<PeriodData> getCmb_Period()
    {
        final WorkEventsPanel aWorkEventsPanel = getWorkEventsPanel();
        return aWorkEventsPanel.getCmb_Period();
    }

    @Override
    public void actionPerformed( final ActionEvent fEvent )
    {
        final String aActionCommand = fEvent.getActionCommand();
        final JComboBox<LocalDate> aCmb_Date = getCmb_Date();
        final JComboBox<PeriodData> aCmb_Period = getCmb_Period();
        final PeriodData aSelectedPeriod = (PeriodData) aCmb_Period.getSelectedItem();
        switch( aActionCommand ){
            case "CANCEL":
                m_WorkEventsDialog.getContentPanel().getTable().editingStopped( null );
                if( isDirty() ){
                    final Object[] options = {"Ich wei\u00DF, was ich tue!", "Nein, das war ein Versehen!"};
                    final int n = JOptionPane.showOptionDialog(null,
                        "Es wurden Daten eingegeben! Sollen diese Daten verworfen werden??",
                        "Sind Sie ganz sicher?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[1]);
                    switch( n ){
                        case 1:
                            return;

                        default:
                    }
                }
                m_WorkEventsDialog.setVisible(false);
                break;

            case "OK":
                m_WorkEventsDialog.getContentPanel().getTable().editingStopped( null );
                final boolean aDataChanged = writeWorkEventsToMembers();
                if( aDataChanged ){
                    m_DataProvider.writeToFile_WorkEvents();
                    m_DataProvider.calculateDutyCharges(aSelectedPeriod.getPeriod());
                    m_DataProvider.balanceRelatives();
                    getGUIUpdater().updateGUI();
                }
                m_WorkEventsDialog.setVisible(false);
                break;

            case "OPEN":
                m_WorkEventsDialog.setVisible( true );
                aCmb_Period.setSelectedItem( getDataProvider().getPeriodData() );
                populateCmbDates( aCmb_Date, getDataProvider().getPeriod() );
                break;

            case "DATESELECTED":
                final LocalDate aSelectedDate = (LocalDate) aCmb_Date.getSelectedItem();
                final Object[][] aWorkEventData = getWorkEventData( aSelectedDate, getDataProvider() );
                final boolean aReadOnly = !isBauausschuss() || isReadOnly( aSelectedPeriod, aSelectedDate );
                final TBLModel_WorkEvents aModel = new TBLModel_WorkEvents( aWorkEventData, aReadOnly );
                aModel.addTableModelListener( this );
                getWorkEventsPanel().populate( aModel );
                break;

            case "PERIODSELECTED":
                populateCmbDates( aCmb_Date, aSelectedPeriod.getPeriod() );
                break;

            case "NEU":
                // Event einfach weiterleiten:
                m_NewWorkEventDateListener.actionPerformed( fEvent );
                break;

            default:
                break;
        }

    }

    private boolean isReadOnly( final PeriodData fSelectedPeriod, final LocalDate fSelectedDate )
    {
        if( ALLPERIODS == fSelectedPeriod ){
            return getPDC().isActivePeriodFinished();
        }
        if( fSelectedPeriod.isActive() ){
            return false;
        }
        final boolean aReadOnly = fSelectedDate == null ? true: getPDC().isFinished( fSelectedDate );
        return aReadOnly;
    }

    private static void populateCmbPeriods(
            final JComboBox<PeriodData> fCmb_Period,
            final IPeriodDataController fPDC )
    {
        final List<PeriodData> aPeriodDataList = fPDC.getPeriodDataList( ALL );
        final PeriodData[] aPeriods = new PeriodData[aPeriodDataList.size()+1];
        aPeriodDataList.toArray( aPeriods );
        aPeriods[aPeriods.length-1] = ALLPERIODS;
        fCmb_Period.setModel( new CBModel_PeriodData( aPeriods ) );
    }

    private void populateCmbDates( final JComboBox<LocalDate> fCmb_Date, final IPeriod fSelectedPeriod )
    {
        final List<LocalDate> aWorkEventDates = getWorkEventDates( getDataProvider() );
        final List<LocalDate> aWorkEventDates_Filtered = new ArrayList<>(aWorkEventDates.size());
        if( fSelectedPeriod == null ){
            aWorkEventDates_Filtered.addAll( aWorkEventDates );
        } else {
            for( final LocalDate aWorkEventDate : aWorkEventDates ){
                if( !fSelectedPeriod.isWithinMyPeriod( aWorkEventDate ) ){
                    continue;
                }
                aWorkEventDates_Filtered.add( aWorkEventDate );
            }
        }
        final LocalDate[] aLDArray = new LocalDate[aWorkEventDates_Filtered.size()];
        fCmb_Date.setModel( new CBModel_WorkEventDates( aWorkEventDates_Filtered.toArray( aLDArray )) );
        fCmb_Date.setSelectedIndex( fCmb_Date.getItemCount()-1 );
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
        if( fADDate == null ){
            return null;
        }
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
                    if( fADDate != null && fADDate.equals( aWorkEvent.getDate() ) ){
                        aWorkEventDataForThisMember[0] = Boolean.TRUE;
                        aWorkEventDataForThisMember[3] = aWorkEvent.getHours() /100.0;
                        break;
                    }
                }
            }
            aWorkEventData[aIdx] = aWorkEventDataForThisMember;
        }
        return aWorkEventData;
    }

    /**
     * @return true, wenn irgendetwas geändert wurde, sonst false
     */
    private boolean writeWorkEventsToMembers()
    {
        final JComboBox<LocalDate> aCmb_Date = getCmb_Date();
        final LocalDate aSelectedDate = (LocalDate) aCmb_Date.getSelectedItem();

        final JTable aTable = getWorkEventsPanel().getTable();
        final TBLModel_WorkEvents aModel = (TBLModel_WorkEvents) aTable.getModel();
        final int aRowCount = aModel.getRowCount();
        boolean aDataChanged = false;
        for( int aIdx = 0; aIdx < aRowCount; aIdx++ ){
            final Double aHoursValue = (Double) aModel.getValueAt( aIdx, 3 );
            if( aHoursValue == null ){
                continue;
            } else {
                final ADH_DataProvider aDataProvider = getDataProvider();
                final Integer aMemberID = (Integer)aModel.getValueAt( aIdx, 1 );
                aDataProvider.get( aMemberID );
                final WorkEvent aWorkEvent = new WorkEvent( aMemberID );
                aWorkEvent.setDate( aSelectedDate );
                final int aHoursWorked = Double.valueOf(100.0*aHoursValue).intValue();
                aWorkEvent.setHours( aHoursWorked );
                aDataChanged |= writeWorkEventToMember( aWorkEvent );
            }
        }
        return aDataChanged;
    }

    /**
     * @param fWorkEvent
     * @return true, wenn irgendetwas geändert wurde, sonst false
     */
    private boolean writeWorkEventToMember( final WorkEvent fWorkEvent )
    {
        if( fWorkEvent.getHours() == 0 ){
            return false;
        }
        final int aMemberID = fWorkEvent.getMemberID();
        final InfoForSingleMember aInfoForSingleMember = m_DataProvider.get( aMemberID );
        WorkEventsAttended aWorkEventsAttended = aInfoForSingleMember.getWorkEventsAttended();
        if( aWorkEventsAttended == null ){
            aWorkEventsAttended = new WorkEventsAttended( aMemberID );
            aInfoForSingleMember.setWorkEventsAttended( aWorkEventsAttended );
            final int aLinkID = aInfoForSingleMember.getMember().getLinkID();
            if( aLinkID != 0 ){
                final InfoForSingleMember aLinkedInfo = m_DataProvider.get( aLinkID );
                WorkEventsAttended aLinkedToWorkEventsAttended = aLinkedInfo.getWorkEventsAttended();
                if( aLinkedToWorkEventsAttended == null ){
                    aLinkedToWorkEventsAttended = new WorkEventsAttended( aLinkID );
                    aLinkedInfo.setWorkEventsAttended( aLinkedToWorkEventsAttended );
                }
                aLinkedToWorkEventsAttended.addRelative( aWorkEventsAttended );
            }
        }
        final LocalDate aDateOfWorkEvent  = fWorkEvent.getDate();
        final int aHoursOfWorkEvent       = fWorkEvent.getHours();
        final List<WorkEvent> aWorkEvents = aWorkEventsAttended.getWorkEvents();
        boolean aSkip = false;
        for( final WorkEvent aKnownWorkEvent : aWorkEvents ){
            final LocalDate aDateOfKnownWorkEvent = aKnownWorkEvent.getDate();
            if( aDateOfKnownWorkEvent.equals( aDateOfWorkEvent )){
                if( aHoursOfWorkEvent == aKnownWorkEvent.getHours() ){
                    aSkip = true;
                } else {
                    aWorkEventsAttended.remove( aKnownWorkEvent );
                }
                break;
            }
        }
        if( !aSkip ){
            aWorkEventsAttended.addWorkEvent( fWorkEvent );
        }
        return !aSkip ;
    }

    @Override
    public void tableChanged( final TableModelEvent fEvent )
    {
//        final int aCol = fEvent.getColumn();
//        if( aCol == 3 ){
//            setDirty( true );
//        }
//        final int row = fEvent.getFirstRow();
//        final int col = fEvent.getColumn();
//        final TBLModel_WorkEvents model = (TBLModel_WorkEvents)fEvent.getSource();
//        final String columnName = model.getColumnName(col);
//        final Object data = model.getValueAt(row, col);

        // Do something with the data...
//        System.out.println(String.format("Data changed: col %s row %d - val %s",
//                columnName, row, data));
    }

    // ------------------------------------------------------------------------
    private boolean isDirty(){
        final WorkEventsPanel aWorkEventsPanel = getWorkEventsPanel();
        final TBLModel_WorkEvents aModel = aWorkEventsPanel.getTableModel();
        return aModel.isDirty();
    }
    // ------------------------------------------------------------------------

}

// ############################################################################
