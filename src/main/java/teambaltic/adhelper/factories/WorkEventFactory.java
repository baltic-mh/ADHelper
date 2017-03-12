/**
 * WorkEventFactory.java
 *
 * Created on 11.03.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.factories;

import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.Participation;
import teambaltic.adhelper.model.WorkEvent;

// ############################################################################
public class WorkEventFactory extends ParticipationFactory<WorkEvent>
{

    public WorkEventFactory()
    {
        super( IKnownColumns.HOURSWORKED );
    }

    @Override
    protected WorkEvent cast( final Participation fParticipation )
    {
        return (WorkEvent) fParticipation;
    }

    @Override
    public WorkEvent createItem( final int fID )
    {
        return new WorkEvent(fID);
    }

}

// ############################################################################
