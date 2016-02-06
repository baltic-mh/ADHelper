/**
 * DateUtils.java
 *
 * Created on 06.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.time.LocalDate;

import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IInvoicingPeriod;

// ############################################################################
public final class DateUtils
{
    public static final LocalDate MIN_DATE = LocalDate.of( 1966, 10, 3 );
    public static final LocalDate MAX_DATE = LocalDate.of( 2966, 10, 3 );

    private DateUtils(){/**/}

    public static boolean coversFreeFromDuty_InvoicingPeriod(
            final FreeFromDuty fFreeFromDuty,
            final IInvoicingPeriod fInvoicingPeriod)
    {
        final LocalDate aFrom = fFreeFromDuty.getFrom() != null ? fFreeFromDuty.getFrom() : DateUtils.MIN_DATE;
        final LocalDate aUntil = fFreeFromDuty.getUntil() != null ? fFreeFromDuty.getUntil(): DateUtils.MAX_DATE;

        final LocalDate aIPStart = fInvoicingPeriod.getStart();
        final LocalDate aIPEnd = fInvoicingPeriod.getEnd();
        if( aIPStart.compareTo( aFrom ) < 0 ){
            return false;
        }
        if( aIPEnd.compareTo( aUntil ) > 0 ){
            return false;
        }
        return true;
    }
}

// ############################################################################
