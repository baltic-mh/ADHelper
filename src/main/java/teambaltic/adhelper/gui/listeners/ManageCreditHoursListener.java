/**
 * ManageCreditHoursListener.java
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
import teambaltic.adhelper.gui.CreditHoursDialog;
import teambaltic.adhelper.gui.ParticipationsDialog;
import teambaltic.adhelper.gui.model.CBModel_Dates;
import teambaltic.adhelper.gui.model.TBLModel_CreditHours;
import teambaltic.adhelper.gui.model.TBLModel_Participation;
import teambaltic.adhelper.model.CreditHours;
import teambaltic.adhelper.model.CreditHoursGranted;
import teambaltic.adhelper.model.IParticipationItemContainer;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;

// ############################################################################
public class ManageCreditHoursListener extends ManageParticipationsListener<CreditHours>
{
//    private static final Logger sm_Log = Logger.getLogger(ManageAdjustmentsListener.class);

    public ManageCreditHoursListener(
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
        return new CreditHoursDialog();
    }

    @Override
    protected TBLModel_Participation createTableModel( final Object[][] fData, final boolean fReadOnly )
    {
        final TBLModel_CreditHours aModel = new TBLModel_CreditHours( fData, fReadOnly );
        return aModel;
    }

    @Override
    protected void fillSpecificColumnData( final Object[] fDataRow, final CreditHours fParticipation )
    {
        final int aColIdx_Comment = ((TBLModel_CreditHours)getTableModel()).getColIdx_Comment();
        final String aComment = fParticipation.getComment();
        fDataRow[aColIdx_Comment] = aComment;
    }

    @Override
    protected IParticipationItemContainer<CreditHours> getParticipationItemContainer( final InfoForSingleMember aInfoForSingleMember )
    {
        return aInfoForSingleMember.getCreditHoursGranted();
    }
    @Override
    protected IParticipationItemContainer<CreditHours> createParticipationItemContainer( final int fMemberID )
    {
        return new CreditHoursGranted( fMemberID );
    }
    @Override
    protected void setParticipationItemContainer( final InfoForSingleMember fInfoForSingleMember,
            final IParticipationItemContainer<CreditHours> fParticipationItemContainer )
    {
        fInfoForSingleMember.setCreditHoursGranted( (CreditHoursGranted) fParticipationItemContainer );
    }

    @Override
    protected CreditHours createParticipation( final LocalDate fSelectedDate, final Vector<Object> fRowValues )
    {
        final Integer fMemberID = (Integer) fRowValues.get( getColIdx_ID() );
        final CreditHours aCreditHours = new CreditHours( fMemberID );
        aCreditHours.setDate( fSelectedDate );
        final Double aHoursValue = (Double) fRowValues.get( getColIdx_Hours() );
        final int aHoursWorked = Double.valueOf(100.0*aHoursValue).intValue();
        aCreditHours.setHours( aHoursWorked );
        final String aComment = (String) fRowValues.get( ((TBLModel_CreditHours)getTableModel()).getColIdx_Comment() );
        aCreditHours.setComment( aComment );
        return aCreditHours;
    }

    @Override
    protected void writeToFile(final ADH_DataProvider fDataProvider)
    {
        fDataProvider.writeToFile_CreditHours();
    }

    @Override
    protected List<LocalDate> getParticipationDates( final ADH_DataProvider fDataProvider )
    {
        final List<InfoForSingleMember> aAll = fDataProvider.getAll();

        final List<LocalDate> aCreditHoursDates = new ArrayList<>();
        for( final InfoForSingleMember aInfoForSingleMember : aAll ){
            final CreditHoursGranted aCreditHoursGranted = aInfoForSingleMember.getCreditHoursGranted();
            if( aCreditHoursGranted == null ){
                continue;
            }
            final List<CreditHours> aCreditHoursList = aCreditHoursGranted.getCreditHoursList();
            for( final CreditHours aCreditHours : aCreditHoursList ){
                final LocalDate aDate = aCreditHours.getDate();
                if( aCreditHoursDates.contains( aDate ) ){
                    continue;
                }
                aCreditHoursDates.add( aDate );
            }
        }
        Collections.sort( aCreditHoursDates );
        return aCreditHoursDates;
    }

    @Override
    protected void populateCmbDates( final JComboBox<LocalDate> fCmb_Date, final IPeriod fSelectedPeriod )
    {
        final LocalDate[] aLDArray = new LocalDate[1];
        aLDArray[0] = fSelectedPeriod.getStart();
        fCmb_Date.setModel( new CBModel_Dates( aLDArray ) );
        fCmb_Date.setSelectedIndex( 0 );
    }
}

// ############################################################################
