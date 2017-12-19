/**
 * AdjustmentFactory.java
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

import java.util.Map;

import teambaltic.adhelper.model.Adjustment;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.Participation;

// ############################################################################
public class AdjustmentFactory extends ParticipationFactory<Adjustment>
{

    public AdjustmentFactory()
    {
        super( IKnownColumns.ADJUSTMENTS );
    }

    @Override
    protected Adjustment cast( final Participation fParticipation )
    {
        return (Adjustment) fParticipation;
    }

    @Override
    public Adjustment createItem( final int fID )
    {
        return new Adjustment(fID);
    }

    @Override
    public void populateSpecificAttributes(final Adjustment fAdjustment, final Map<String, String> fAttributes)
    {
        final String aComment = fAttributes.get( IKnownColumns.COMMENT );
        fAdjustment.setComment( aComment );
    }
}

// ############################################################################
