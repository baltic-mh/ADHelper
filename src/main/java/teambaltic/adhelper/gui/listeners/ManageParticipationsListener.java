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
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.controller.IPeriodDataController.EPeriodDataSelector;
import teambaltic.adhelper.gui.ParticipationsDialog;
import teambaltic.adhelper.gui.ParticipationsPanel;
import teambaltic.adhelper.gui.model.CBModel_Dates;
import teambaltic.adhelper.gui.model.CBModel_PeriodData;
import teambaltic.adhelper.gui.model.TBLModel_Participation;
import teambaltic.adhelper.model.ActiveMemberFilter;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IParticipationItemContainer;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.Participation;
import teambaltic.adhelper.model.PeriodData;

// ############################################################################
public abstract class ManageParticipationsListener<ParticipationType extends Participation>
    implements ActionListener, TableModelListener
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
    protected IParticipationItemContainer<ParticipationType> getParticipationItemContainer( final InfoForSingleMember fInfoForSingleMember );
    abstract
    protected IParticipationItemContainer<ParticipationType> createParticipationItemContainer( final int fMemberID );
    abstract
    protected void setParticipationItemContainer( InfoForSingleMember fInfoForSingleMember,
                       IParticipationItemContainer<ParticipationType> fParticipationItemContainer );

    abstract
    protected TBLModel_Participation createTableModel( final Object[][] fData, final boolean fReadOnly );

    abstract
    protected List<LocalDate> getParticipationDates( final ADH_DataProvider fDataProvider );

    abstract
    protected ParticipationType createParticipation( final LocalDate fSelectedDate, final Vector fRowValues );

    abstract
    protected void writeToFile(ADH_DataProvider fDataProvider);
    // ########################################################################

    private final ParticipationsDialog m_Dialog;

    private final NewDateListener<ParticipationType> m_NewDateListener;

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
        m_NewDateListener = new NewDateListener<>(this, fPDC );
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

    private JTable getTable()
    {
        return getPanel().getTable();
    }

    protected TBLModel_Participation getTableModel()
    {
        TableModel aTableModel = getTable().getModel();
        if( !(aTableModel instanceof TBLModel_Participation) ){
            aTableModel = createTableModel( null, true );
        }
        return (TBLModel_Participation) aTableModel;
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
                stopTableEditing();
                if( isDirty() ){
                    final Object[] options = {"Ich weiß, was ich tue!", "Nein, das war ein Versehen!"};
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
                stopTableEditing();
                final boolean aDataChanged = writeToMembers( getDataProvider() );
                if( aDataChanged ){
                    writeToFile(m_DataProvider);
//                    m_DataProvider.calculateDutyCharges(aSelectedPeriod.getPeriod());
//                    m_DataProvider.balanceRelatives( aSelectedPeriod.getPeriod());
                    m_DataProvider.balance( aSelectedPeriod.getPeriod());
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
                final Object[][] aData = getData( aSelectedDate, getDataProvider(), aSelectedPeriod );
                final boolean aReadOnly = !isBauausschuss() || isReadOnly( aSelectedPeriod, aSelectedDate );
                final TBLModel_Participation aModel = createTableModel( aData, aReadOnly );
                aModel.addTableModelListener( this );
                getPanel().populate( aModel, aSelectedPeriod );
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

    private void stopTableEditing()
    {
        final JTable aTable = getTable();
        if( aTable.isEditing() ){
            final TableCellEditor tce = aTable.getCellEditor();
            tce.stopCellEditing();
        }
        aTable.editingStopped( null );
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

    protected void populateCmbDates( final JComboBox<LocalDate> fCmb_Date, final IPeriod fSelectedPeriod )
    {
        final List<LocalDate> aParticipationDates = getParticipationDates( getDataProvider() );
        final List<LocalDate> aParticipationDates_Filtered = new ArrayList<>(aParticipationDates.size());
        if( fSelectedPeriod == null ){
            aParticipationDates_Filtered.addAll( aParticipationDates );
        } else {
            for( final LocalDate aWorkEventDate : aParticipationDates ){
                if( !fSelectedPeriod.isWithinMyPeriod( aWorkEventDate ) ){
                    continue;
                }
                aParticipationDates_Filtered.add( aWorkEventDate );
            }
        }
        final LocalDate[] aLDArray = new LocalDate[aParticipationDates_Filtered.size()];
        fCmb_Date.setModel( new CBModel_Dates( aParticipationDates_Filtered.toArray( aLDArray )) );
        fCmb_Date.setSelectedIndex( fCmb_Date.getItemCount()-1 );
    }

    @Override
    public void tableChanged( final TableModelEvent fEvent )
    {
//        final int aCol = fEvent.getColumn();
////        if( aCol == 3 ){
////            setDirty( true );
////        }
//        final int row = fEvent.getFirstRow();
//        final TBLModel_Participation model = (TBLModel_Participation)fEvent.getSource();
//        final String columnName = model.getColumnName(aCol);
//        final Object data = model.getValueAt(row, aCol);
//
//        // Do something with the data...
//        System.out.println(String.format("Data changed: col %s row %d - val %s",
//                columnName, row, data));
    }

    // ------------------------------------------------------------------------
    private boolean isDirty(){
        final TBLModel_Participation aModel = getTableModel();
        return aModel.isDirty();
    }
    // ------------------------------------------------------------------------

    protected Object[][] getData( final LocalDate fDate, final ADH_DataProvider fDataProvider, final PeriodData fSelectedPeriod )
    {
        if( fDate == null ){
            return null;
        }
        final int aColIdx_ParticipationFlag = getColIdx_ParticipationFlag();
        final int aColIdx_ID                = getColIdx_ID();
        final int aColIdx_Name              = getColIdx_Name();
        final int aColumnIdxHours           = getColIdx_Hours();
        final ActiveMemberFilter aFilter    = new ActiveMemberFilter( fSelectedPeriod );
        final List<InfoForSingleMember> aAll = fDataProvider.getAll( aFilter );
        final int aNumColumns = getNumColumns();
        final Object[][] aData = new Object[aAll.size()][aNumColumns];
        for( int aIdx = 0; aIdx < aAll.size(); aIdx++ ){
            final InfoForSingleMember aInfoForSingleMember = aAll.get( aIdx );
            final IClubMember aMember = aInfoForSingleMember.getMember();
            final Object[] aDataRow = new Object[aNumColumns];
            aDataRow[aColIdx_ParticipationFlag] = Boolean.FALSE;
            aDataRow[aColIdx_ID] = aMember.getID();
            aDataRow[aColIdx_Name] = aMember.getName();
            final IParticipationItemContainer<ParticipationType> aParticipationItemContainer = getParticipationItemContainer( aInfoForSingleMember );
            if( aParticipationItemContainer != null ){
                final List<ParticipationType> aParticipationList = aParticipationItemContainer.getParticipationList();
                for( final ParticipationType aParticipation : aParticipationList ){
                    if( fDate.equals( aParticipation.getDate() ) ){
                        aDataRow[aColIdx_ParticipationFlag] = Boolean.TRUE;
                        aDataRow[aColumnIdxHours] = aParticipation.getHours() /100.0;
                        fillSpecificColumnData( aDataRow, aParticipation );
                        break;
                    }
                }
            }
            aData[aIdx] = aDataRow;
        }
        return aData;
    }

    // Wenn eine abgeleitete Klasse eine andere Reihenfolge bei den Spalten hat,
    // muss sie die getColIdx_*-Methoden überschreiben:
    protected int getColIdx_ParticipationFlag()
    {
        return getTableModel().getColIdx_Participationflag();
    }

    protected int getColIdx_ID()
    {
        return getTableModel().getColIdx_ID();
    }

    protected int getColIdx_Name()
    {
        return getTableModel().getColIdx_Name();
    }

    protected int getColIdx_Hours()
    {
        return getTableModel().getColIdx_Hours();
    }

    protected int getNumColumns()
    {
        return getTableModel().getCOLUMNCLASSES().length;
    }

    protected void fillSpecificColumnData( final Object[] fDataRow, final ParticipationType fParticipation )
    {
        // Default-Implementierung - die tut nix!
    }

    /**
     * @return true, wenn irgendetwas geändert wurde, sonst false
     */
    protected boolean writeToMembers( final ADH_DataProvider fDataProvider )
    {
        final JComboBox<LocalDate> aCmb_Date = getCmb_Date();
        final LocalDate aSelectedDate = (LocalDate) aCmb_Date.getSelectedItem();

        final JTable aTable = getTable();
        final TBLModel_Participation aModel = (TBLModel_Participation) aTable.getModel();
        final Vector<Vector> aDataVector = aModel.getDataVector();
        final int aRowCount = aModel.getRowCount();
        boolean aDataChanged = false;
        for( int aIdx = 0; aIdx < aRowCount; aIdx++ ){
            final Double aHoursValue = aModel.getHours( aIdx );
            if( aHoursValue == null ){
                continue;
            } else {
                final Vector aRowVector = aDataVector.get( aIdx );
                final ParticipationType aParticipationEvent = createParticipation( aSelectedDate, aRowVector );
                aDataChanged |= writeToMember( fDataProvider, aParticipationEvent );
            }
        }
        return aDataChanged;
    }

    /**
     * @param fDataProvider
     * @return true, wenn irgendetwas geändert wurde, sonst false
     */
    protected boolean writeToMember( final ADH_DataProvider fDataProvider, final ParticipationType fParticipationItem )
    {
        final int aMemberID = fParticipationItem.getMemberID();
        final InfoForSingleMember aInfoForSingleMember = fDataProvider.get( aMemberID );
        IParticipationItemContainer<ParticipationType> aParticipationItemContainer = getParticipationItemContainer( aInfoForSingleMember );
        if( aParticipationItemContainer == null ){
            if( fParticipationItem.getHours() == 0 ){
                return false;
            }
            aParticipationItemContainer = createParticipationItemContainer( aMemberID );
            setParticipationItemContainer( aInfoForSingleMember, aParticipationItemContainer );
        }
        final LocalDate aDate   = fParticipationItem.getDate();
        final List<ParticipationType> aItemList = aParticipationItemContainer.getParticipationList();
        boolean aSkip = false;
        for( final ParticipationType aKnownParticipationItem : aItemList ){
            final LocalDate aDateOfKnownParticipationItem = aKnownParticipationItem.getDate();
            if( aDateOfKnownParticipationItem.equals( aDate )){
                if( aKnownParticipationItem.equals( fParticipationItem ) ){
                    aSkip = true;
                } else {
                    aParticipationItemContainer.remove( aKnownParticipationItem );
                    if( aParticipationItemContainer.getParticipationList().isEmpty() ) {
                        setParticipationItemContainer( aInfoForSingleMember, null );
                    }
                }
                break;
            }
        }
        if( !aSkip && fParticipationItem.getHours() > 0 ){
            aParticipationItemContainer.add( fParticipationItem );
        }
        return !aSkip;
    }


}

// ############################################################################

