/**
 * AdjustmentReader.java
 *
 * Created on 06.03.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.io.File;

import teambaltic.adhelper.factories.AdjustmentFactory;
import teambaltic.adhelper.model.Adjustment;
import teambaltic.adhelper.model.AdjustmentsTaken;
import teambaltic.adhelper.model.InfoForSingleMember;

// ############################################################################
public class AdjustmentReader extends ParticipationReader<Adjustment>
{
//    private static final Logger sm_Log = Logger.getLogger(AdjustmentReader.class);

    public AdjustmentReader( final File fFile )
    {
        super( fFile, new AdjustmentFactory() );
    }

    @Override
    protected AdjustmentsTaken getCreateParticipationItemContainer( final InfoForSingleMember fInfo )
    {
        AdjustmentsTaken aAdjustmentsTaken = fInfo.getAdjustmentsTaken();
        if( aAdjustmentsTaken == null ){
            aAdjustmentsTaken = new AdjustmentsTaken( fInfo.getID() );
            fInfo.setAdjustmentsTaken( aAdjustmentsTaken );
        }
        return aAdjustmentsTaken;
    }
}

// ############################################################################
