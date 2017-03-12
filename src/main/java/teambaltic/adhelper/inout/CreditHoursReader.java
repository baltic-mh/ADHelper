/**
 * CreditHoursReader.java
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

import teambaltic.adhelper.factories.CreditHoursFactory;
import teambaltic.adhelper.model.CreditHours;
import teambaltic.adhelper.model.CreditHoursGranted;
import teambaltic.adhelper.model.InfoForSingleMember;

// ############################################################################
public class CreditHoursReader extends ParticipationReader<CreditHours>
{
//    private static final Logger sm_Log = Logger.getLogger(CreditHoursReader.class);

    public CreditHoursReader( final File fFile )
    {
        super( fFile, new CreditHoursFactory() );
    }

    @Override
    protected CreditHoursGranted getCreateParticipationItemContainer( final InfoForSingleMember fInfo )
    {
        CreditHoursGranted aCreditHoursGranted = fInfo.getCreditHoursGranted();
        if( aCreditHoursGranted == null ){
            aCreditHoursGranted = new CreditHoursGranted( fInfo.getID() );
            fInfo.setCreditHoursGranted( aCreditHoursGranted );
        }
        return aCreditHoursGranted;
    }
}

// ############################################################################
