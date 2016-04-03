/**
 * WorkEventFactory.java
 *
 * Created on 04.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.factories;

import java.time.LocalDate;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.utils.ParseUtils;

// ############################################################################
public class WorkEventFactory implements IItemFactory<WorkEvent>
{
    private static final Logger sm_Log = Logger.getLogger(WorkEventFactory.class);

    @Override
    public WorkEvent createItem( final int fID, final Map<String, String> fAttributes )
    {
        final WorkEvent aWorkEvent = new WorkEvent( fID );
        final String aDateValue = fAttributes.get( IKnownColumns.DATE );
        final LocalDate aDate = ParseUtils.getDate( aDateValue );
        if( aDate != null ){
            aWorkEvent.setDate( aDate );
        }

        final String aHoursWorkedValue = fAttributes.get( IKnownColumns.HOURSWORKED );
        try{
            final float aFloatValue  = Float.parseFloat( aHoursWorkedValue.replaceAll( ",", "." ) );
            final int   aHoursWorked = Math.round( aFloatValue * 100 );
            aWorkEvent.setHours( aHoursWorked );
        }catch( final NumberFormatException fEx ){
            sm_Log.warn("Stunden-Angabe ist keine Zahl: "+ aHoursWorkedValue );
        }

        return aWorkEvent;
    }

}

// ############################################################################
