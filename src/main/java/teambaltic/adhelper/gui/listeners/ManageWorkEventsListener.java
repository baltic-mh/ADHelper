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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.gui.ParticipationsDialog;
import teambaltic.adhelper.gui.WorkEventsDialog;
import teambaltic.adhelper.gui.model.TBLModel_Participation;
import teambaltic.adhelper.gui.model.TBLModel_WorkEvents;
import teambaltic.adhelper.model.IParticipationItemContainer;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class ManageWorkEventsListener extends ManageParticipationsListener<WorkEvent>
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
    protected WorkEvent createParticipation( final LocalDate fSelectedDate, final Vector<Object> fRowValues )
    {
        final Integer aMemberID = (Integer) fRowValues.get(  getColIdx_ID() );
        final WorkEvent aWorkEvent = new WorkEvent( aMemberID );
        aWorkEvent.setDate( fSelectedDate );
        final Double aHoursValue = (Double) fRowValues.get( getColIdx_Hours() );
        final int aHoursWorked = Double.valueOf(100.0*aHoursValue).intValue();
        aWorkEvent.setHours( aHoursWorked );
        return aWorkEvent;
    }

    @Override
    protected IParticipationItemContainer<WorkEvent> getParticipationItemContainer( final InfoForSingleMember aInfoForSingleMember )
    {
        return aInfoForSingleMember.getWorkEventsAttended();
    }
    @Override
    protected IParticipationItemContainer<WorkEvent> createParticipationItemContainer( final int fMemberID )
    {
        return new WorkEventsAttended( fMemberID );
    }
    @Override
    protected void setParticipationItemContainer( final InfoForSingleMember fInfoForSingleMember,
            final IParticipationItemContainer<WorkEvent> fParticipationItemContainer )
    {
        fInfoForSingleMember.setWorkEventsAttended( (WorkEventsAttended) fParticipationItemContainer );
    }

    @Override
    protected void writeToFile(final ADH_DataProvider fDataProvider)
    {
        fDataProvider.writeToFile_WorkEvents();
    }

    @Override
    protected List<LocalDate> getParticipationDates( final ADH_DataProvider fDataProvider )
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

}

// ############################################################################
