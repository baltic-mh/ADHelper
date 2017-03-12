/**
 * WorkEventReader.java
 *
 * Created on 04.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.io.File;

import teambaltic.adhelper.factories.WorkEventFactory;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class WorkEventReader extends ParticipationReader<WorkEvent>
{
//    private static final Logger sm_Log = Logger.getLogger(WorkEventReader.class);

    public WorkEventReader( final File fFile )
    {
        super( fFile, new WorkEventFactory() );
    }

    @Override
    protected WorkEventsAttended getCreateParticipationItemContainer( final InfoForSingleMember fInfo )
    {
        WorkEventsAttended aWorkEventsAttended = fInfo.getWorkEventsAttended();
        if( aWorkEventsAttended == null ){
            aWorkEventsAttended = new WorkEventsAttended( fInfo.getID() );
            fInfo.setWorkEventsAttended( aWorkEventsAttended );
        }
        return aWorkEventsAttended;
    }
}

// ############################################################################
