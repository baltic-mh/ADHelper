/**
 * CreditHoursFactory.java
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

import teambaltic.adhelper.model.CreditHours;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.Participation;

// ############################################################################
public class CreditHoursFactory extends ParticipationFactory<CreditHours>
{

    public CreditHoursFactory()
    {
        super( IKnownColumns.CREDITHOURS );
    }

    @Override
    protected CreditHours cast( final Participation fParticipation )
    {
        return (CreditHours) fParticipation;
    }

    @Override
    public CreditHours createItem( final int fID )
    {
        return new CreditHours(fID);
    }

    @Override
    public void populateSpecificAttributes(final CreditHours fCreditHours, final Map<String, String> fAttributes)
    {
        final String aComment = fAttributes.get( IKnownColumns.COMMENT );
        fCreditHours.setComment( aComment );
    }
}

// ############################################################################
