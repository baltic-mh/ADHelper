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

import teambaltic.adhelper.model.IPeriod;

// ############################################################################
public final class DateUtils
{
    public static final LocalDate MIN_DATE = LocalDate.of( 1966, 10, 3 );
    public static final LocalDate MAX_DATE = LocalDate.of( 2966, 10, 3 );

    private DateUtils(){/**/}

    public static int getCoverageInMonths(
            final IPeriod fCoveringPeriod,
            final IPeriod fPeriodToCover)
    {
        if( fCoveringPeriod == null ){
            return 0;
        }
        final LocalDate aCovering_Start = fCoveringPeriod.getStart() != null ? fCoveringPeriod.getStart() : MIN_DATE;
        final LocalDate aCovering_End   = fCoveringPeriod.getEnd()   != null ? fCoveringPeriod.getEnd()   : MAX_DATE;

        if( !fPeriodToCover.isBeforeMyEnd( aCovering_Start ) ){
            return 0;
        }

        if( !fPeriodToCover.isAfterMyStart( aCovering_End ) ){
            return 0;
        }

        final LocalDate aToCover_Start = fPeriodToCover.getStart() != null ? fPeriodToCover.getStart() : MIN_DATE;
        final LocalDate aToCover_End   = fPeriodToCover.getEnd()   != null ? fPeriodToCover.getEnd()   : MAX_DATE;

        final LocalDate aMax_Start = aCovering_Start.compareTo( aToCover_Start ) > 0 ? aCovering_Start : aToCover_Start;
        final LocalDate aMin_End   = aCovering_End  .compareTo( aToCover_End )   < 0 ? aCovering_End   : aToCover_End;

        final int aYears  = aMin_End.getYear() - aMax_Start.getYear();
        final int aMonths = aMin_End.getMonthValue() - aMax_Start.getMonthValue() +1;

        final int aCoverageInMonths = aYears*12 + aMonths;
        return aCoverageInMonths;
    }
}

// ############################################################################
