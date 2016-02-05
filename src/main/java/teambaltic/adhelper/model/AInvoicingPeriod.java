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
public abstract class AInvoicingPeriod implements IInvoicingPeriod
{
    @Override
    public boolean isAfterStart(  final LocalDate fDate )
    {
        final int aComparedTo = getStart().compareTo( fDate );
        return aComparedTo <= 0;
    }

    @Override
    public boolean isBeforeEnd(  final LocalDate fDate )
    {
        final int aComparedTo = getEnd().compareTo( fDate );
        return aComparedTo >= 0;
    }

    @Override
    public boolean isWithinPeriod( final LocalDate fDate )
    {
        return isAfterStart( fDate ) && isBeforeEnd( fDate );
    }


}

// ############################################################################
