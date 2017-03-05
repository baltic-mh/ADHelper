/**
 * ManageParticipationsListener.java
 *
 * Created on 06.03.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.controller.IPeriodDataController.EPeriodDataSelector;
import teambaltic.adhelper.gui.DateChooserFrame;
import teambaltic.adhelper.gui.ParticipationsDialog;
import teambaltic.adhelper.gui.ParticipationsPanel;
import teambaltic.adhelper.gui.model.CBModel_Dates;
import teambaltic.adhelper.gui.model.CBModel_PeriodData;
import teambaltic.adhelper.gui.model.TBLModel_Participation;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public abstract class ManageParticipationsListener implements ActionListener, TableModelListener
{
//    private static final Logger sm_Log = Logger.getLogger(ManageParticipationsListener.class);

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

    // ########################################################################
    // Abstrakte Methoden:
    abstract
    protected ParticipationsDialog createDialog();

    abstract
    protected Object[][] getData( final LocalDate fADDate, final ADH_DataProvider fDataProvider );

    abstract
    protected TBLModel_Participation createTableModel( final Object[][] fData, final boolean fReadOnly );

    /**
     * @return true, wenn irgendetwas geändert wurde, sonst false
     */
    abstract
    protected boolean writeToMembers(final ADH_DataProvider fDataProvider);

    abstract
    protected void writeToFile(ADH_DataProvider fDataProvider);
    // ########################################################################

    private final ParticipationsDialog m_Dialog;

    private final NewDateListener m_NewDateListener;

    public ManageParticipationsListener(
            final ADH_DataProvider fDataProvider,
            final IPeriodDataController fPDC,
            final GUIUpdater fGUIUpdater,
            final boolean fIsBauausschuss )
    {
        m_DataProvider   = fDataProvider;
        m_PDC            = fPDC;
        m_GUIUpdater     = fGUIUpdater;
        m_IsBauausschuss = fIsBauausschuss;

        m_Dialog = createDialog();
        final DateChooserFrame aDateChooserFrame = new DateChooserFrame();
        m_NewDateListener = new NewDateListener(this, aDateChooserFrame, fPDC );
        aDateChooserFrame.addActionListener( m_NewDateListener );
        getBtn_Ok().addActionListener( this );
        getBtn_Neu().addActionListener( this );
        getBtn_Abbrechen().addActionListener( this );
        getCmb_Date().addActionListener( this );
        final JComboBox<PeriodData> aCmb_Period = getCmb_Period();
        aCmb_Period.addActionListener( this );
        populateCmbPeriods( aCmb_Period, getPDC() );

        m_Dialog.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
    }

    protected ParticipationsPanel getPanel()
    {
        final ParticipationsPanel aPanel = m_Dialog.getContentPanel();
        return aPanel;
    }

    private JButton getBtn_Abbrechen()
    {
        return getPanel().getBtn_Abbrechen();
    }

    private JButton getBtn_Neu()
    {
        return getPanel().getBtn_Neu();
    }

    private JButton getBtn_Ok()
    {
        return getPanel().getBtn_Ok();
    }

    public JComboBox<LocalDate> getCmb_Date()
    {
        final ParticipationsPanel aPanel = getPanel();
        return aPanel.getCmb_Date();
    }

    public JComboBox<PeriodData> getCmb_Period()
    {
        final ParticipationsPanel aPanel = getPanel();
        return aPanel.getCmb_Period();
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
                getPanel().getTable().editingStopped( null );
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
                m_Dialog.setVisible(false);
                break;

            case "OK":
                getPanel().getTable().editingStopped( null );
                final boolean aDataChanged = writeToMembers( getDataProvider() );
                if( aDataChanged ){
                    writeToFile(m_DataProvider);
                    m_DataProvider.calculateDutyCharges(aSelectedPeriod.getPeriod());
                    m_DataProvider.balanceRelatives( aSelectedPeriod.getPeriod());
                    getGUIUpdater().updateGUI();
                }
                m_Dialog.setVisible(false);
                break;

            case "OPEN":
                m_Dialog.setVisible( true );
                aCmb_Period.setSelectedItem( getDataProvider().getPeriodData() );
                populateCmbDates( aCmb_Date, getDataProvider().getPeriod() );
                break;

            case "DATESELECTED":
                final LocalDate aSelectedDate = (LocalDate) aCmb_Date.getSelectedItem();
                final Object[][] aData = getData( aSelectedDate, getDataProvider() );
                final boolean aReadOnly = !isBauausschuss() || isReadOnly( aSelectedPeriod, aSelectedDate );
                final TBLModel_Participation aModel = createTableModel( aData, aReadOnly );
                aModel.addTableModelListener( this );
                getPanel().populate( aModel );
                break;

            case "PERIODSELECTED":
                populateCmbDates( aCmb_Date, aSelectedPeriod.getPeriod() );
                break;

            case "NEU":
                // Event einfach weiterleiten:
                m_NewDateListener.actionPerformed( fEvent );
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
        fCmb_Date.setModel( new CBModel_Dates( aWorkEventDates_Filtered.toArray( aLDArray )) );
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
        final ParticipationsPanel aPanel = getPanel();
        final TBLModel_Participation aModel = aPanel.getTableModel();
        return aModel.isDirty();
    }
    // ------------------------------------------------------------------------

}

// ############################################################################
