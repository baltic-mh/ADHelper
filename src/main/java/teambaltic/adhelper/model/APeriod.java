/**
 * AInvoicingPeriod.java
 *
 * Created on 04.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.time.LocalDate;

// ############################################################################
public abstract class APeriod implements IPeriod
{

    @Override
    public boolean isAfterMyStart(  final LocalDate fDate )
    {
        final int aComparedTo = getStart().compareTo( fDate );
        return aComparedTo <= 0;
    }

    @Override
    public boolean isBeforeMyEnd(  final LocalDate fDate )
    {
        final int aComparedTo = getEnd().compareTo( fDate );
        return aComparedTo >= 0;
    }

    @Override
    public boolean isWithinMyPeriod( final LocalDate fDate )
    {
        return isAfterMyStart( fDate ) && isBeforeMyEnd( fDate );
    }


}

// ############################################################################
