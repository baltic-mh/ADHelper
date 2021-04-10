/**
 * ManageAdjustmentListener.java
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.IPeriodDataController;
import teambaltic.adhelper.gui.AdjustmentsDialog;
import teambaltic.adhelper.gui.ParticipationsDialog;
import teambaltic.adhelper.gui.model.CBModel_Dates;
import teambaltic.adhelper.gui.model.TBLModel_Adjustments;
import teambaltic.adhelper.gui.model.TBLModel_Participation;
import teambaltic.adhelper.model.Adjustment;
import teambaltic.adhelper.model.AdjustmentsTaken;
import teambaltic.adhelper.model.IParticipationItemContainer;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;

// ############################################################################
public class ManageAdjustmentListener extends ManageParticipationsListener<Adjustment>
{
//    private static final Logger sm_Log = Logger.getLogger(ManageAdjustmentsListener.class);

    public ManageAdjustmentListener(
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
        return new AdjustmentsDialog();
    }

    @Override
    protected TBLModel_Participation createTableModel( final Object[][] fData, final boolean fReadOnly )
    {
        final TBLModel_Adjustments aModel = new TBLModel_Adjustments( fData, fReadOnly );
        return aModel;
    }

    @Override
    protected void fillSpecificColumnData( final Object[] fDataRow, final Adjustment fParticipation )
    {
        final int aColIdx_Comment = ((TBLModel_Adjustments)getTableModel()).getColIdx_Comment();
        final String aComment = fParticipation.getComment();
        fDataRow[aColIdx_Comment] = aComment;
    }

    @Override
    protected IParticipationItemContainer<Adjustment> getParticipationItemContainer( final InfoForSingleMember aInfoForSingleMember )
    {
        return aInfoForSingleMember.getAdjustmentsTaken();
    }
    @Override
    protected IParticipationItemContainer<Adjustment> createParticipationItemContainer( final int fMemberID )
    {
        return new AdjustmentsTaken( fMemberID );
    }
    @Override
    protected void setParticipationItemContainer( final InfoForSingleMember fInfoForSingleMember,
            final IParticipationItemContainer<Adjustment> fParticipationItemContainer )
    {
        fInfoForSingleMember.setAdjustmentsTaken( (AdjustmentsTaken) fParticipationItemContainer );
    }

    @Override
    protected Adjustment createParticipation( final LocalDate fSelectedDate, final Vector fRowValues )
    {
        final Integer fMemberID = (Integer) fRowValues.get( getColIdx_ID() );
        final Adjustment aAdjustment = new Adjustment( fMemberID );
        aAdjustment.setDate( fSelectedDate );
        final Double aHoursValue = (Double) fRowValues.get( getColIdx_Hours() );
        final int aHoursWorked = Double.valueOf(100.0*aHoursValue).intValue();
        aAdjustment.setHours( aHoursWorked );
        final String aComment = (String) fRowValues.get( ((TBLModel_Adjustments)getTableModel()).getColIdx_Comment() );
        aAdjustment.setComment( aComment );
        return aAdjustment;
    }

    @Override
    protected void writeToFile(final ADH_DataProvider fDataProvider)
    {
        fDataProvider.writeToFile_Adjustments();
    }

    @Override
    protected List<LocalDate> getParticipationDates( final ADH_DataProvider fDataProvider )
    {
        final List<InfoForSingleMember> aAll = fDataProvider.getAll();

        final List<LocalDate> aAdjustmentDates = new ArrayList<>();
        for( final InfoForSingleMember aInfoForSingleMember : aAll ){
            final AdjustmentsTaken aAdjustmentsTaken = aInfoForSingleMember.getAdjustmentsTaken();
            if( aAdjustmentsTaken == null ){
                continue;
            }
            final List<Adjustment> aAdjustmentList = aAdjustmentsTaken.getAdjustmentList();
            for( final Adjustment aAdjustment : aAdjustmentList ){
                final LocalDate aDate = aAdjustment.getDate();
                if( aAdjustmentDates.contains( aDate ) ){
                    continue;
                }
                aAdjustmentDates.add( aDate );
            }
        }
        Collections.sort( aAdjustmentDates );
        return aAdjustmentDates;
    }

    @Override
    protected void populateCmbDates( final JComboBox<LocalDate> fCmb_Date, final IPeriod fSelectedPeriod )
    {
        if( fSelectedPeriod == null ){
            return;
        }
        final LocalDate[] aLDArray = new LocalDate[1];
        aLDArray[0] = fSelectedPeriod.getStart();
        fCmb_Date.setModel( new CBModel_Dates( aLDArray ) );
        fCmb_Date.setSelectedIndex( 0 );
    }
}

// ############################################################################
