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

import java.time.LocalDate;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JTable;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.gui.ParticipationsDialog;
import teambaltic.adhelper.gui.WorkEventsDialog;
import teambaltic.adhelper.gui.model.TBLModel_Participation;
import teambaltic.adhelper.gui.model.TBLModel_WorkEvents;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class ManageWorkEventsListener extends ManageParticipationsListener
{
//    private static final Logger sm_Log = Logger.getLogger(ManageWorkEventsListener.class);

    public ManageWorkEventsListener(
            final ADH_DataProvider fDataProvider,
            final IPeriodDataController fPDC,
            final GUIUpdater fGUIUpdater,
            final boolean fIsBauausschuss )
    {
        super( fDataProvider, fPDC, fGUIUpdater, fIsBauausschuss );
    }

    @Override
    protected ParticipationsDialog createDialog()
    {
        return new WorkEventsDialog();
    }

    @Override
    protected TBLModel_Participation createTableModel( final Object[][] fData, final boolean fReadOnly )
    {
        final TBLModel_WorkEvents aModel = new TBLModel_WorkEvents( fData, fReadOnly );
        return aModel;
    }

    @Override
    protected Object[][] getData( final LocalDate fADDate, final ADH_DataProvider fDataProvider )
    {
        if( fADDate == null ){
            return null;
        }
        final int aColumnIdxHours = TBLModel_Participation.COLUMN_IDX_HOURS;
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
                        aWorkEventDataForThisMember[aColumnIdxHours] = aWorkEvent.getHours() /100.0;
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
    @Override
    protected boolean writeToMembers( final ADH_DataProvider fDataProvider )
    {
        final JComboBox<LocalDate> aCmb_Date = getCmb_Date();
        final LocalDate aSelectedDate = (LocalDate) aCmb_Date.getSelectedItem();

        final JTable aTable = getPanel().getTable();
        final TBLModel_Participation aModel = (TBLModel_Participation) aTable.getModel();
        final int aRowCount = aModel.getRowCount();
        boolean aDataChanged = false;
        for( int aIdx = 0; aIdx < aRowCount; aIdx++ ){
            final Double aHoursValue = aModel.getHours( aIdx );
            if( aHoursValue == null ){
                continue;
            } else {
                final Integer aMemberID = (Integer)aModel.getValueAt( aIdx, 1 );
                fDataProvider.get( aMemberID );
                final WorkEvent aWorkEvent = new WorkEvent( aMemberID );
                aWorkEvent.setDate( aSelectedDate );
                final int aHoursWorked = Double.valueOf(100.0*aHoursValue).intValue();
                aWorkEvent.setHours( aHoursWorked );
                aDataChanged |= writeToMember( fDataProvider, aWorkEvent );
            }
        }
        return aDataChanged;
    }

    /**
     * @param fDataProvider
     * @return true, wenn irgendetwas geändert wurde, sonst false
     */
    private static boolean writeToMember( final ADH_DataProvider fDataProvider, final WorkEvent fWorkEvent )
    {
        if( fWorkEvent.getHours() == 0 ){
            return false;
        }
        final int aMemberID = fWorkEvent.getMemberID();
        final InfoForSingleMember aInfoForSingleMember = fDataProvider.get( aMemberID );
        WorkEventsAttended aWorkEventsAttended = aInfoForSingleMember.getWorkEventsAttended();
        if( aWorkEventsAttended == null ){
            aWorkEventsAttended = new WorkEventsAttended( aMemberID );
            aInfoForSingleMember.setWorkEventsAttended( aWorkEventsAttended );
            final int aLinkID = aInfoForSingleMember.getMember().getLinkID();
            if( aLinkID != 0 ){
                final InfoForSingleMember aLinkedInfo = fDataProvider.get( aLinkID );
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
    protected void writeToFile(final ADH_DataProvider fDataProvider)
    {
        fDataProvider.writeToFile_WorkEvents();
    }

}

// ############################################################################
