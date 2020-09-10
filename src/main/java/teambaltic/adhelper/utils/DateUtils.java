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
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import teambaltic.adhelper.model.IPeriod;

// ############################################################################
public final class DateUtils
{
    public static final LocalDate MIN_DATE  = LocalDate.of( 1966, 10, 3 );
    public static final LocalDate MAX_DATE  = LocalDate.of( 2966, 10, 3 );
    // Dies ist der Default-Wert, den das Mitgliederprogramm als "unendlich" annimmt:
    public static final LocalDate MAX_DATE2 = LocalDate.of( 2099, 12, 1 );;

    private DateUtils(){/**/}

    public static String getName(final Month fMonth)
    {
        final String aDisplayName = fMonth.getDisplayName( TextStyle.SHORT, Locale.GERMAN );
        return aDisplayName;
    }

    public static String getNames(final Collection<Month> fMonths)
    {
        final StringBuffer aSB = new StringBuffer();
        final boolean aAppendSep = fMonths.size() > 1;
        for( final Month aMonth : fMonths ){
            aSB.append( getName( aMonth ) );
            if( aAppendSep ){
                aSB.append( ", " );
            }
        }
        return aSB.toString();
    }

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

    public static List<Month> getMonthsNotCovered(
            final IPeriod fCoveringPeriod,
            final IPeriod fPeriodToCover)
    {
        final LocalDate aToCover_Start = fPeriodToCover.getStart();
        final LocalDate aToCover_End   = fPeriodToCover.getEnd();
        final List<Month> aMonthsNotCovered = new ArrayList<>();
        LocalDate aCursor = LocalDate.from( aToCover_Start );
        do {
            if( fCoveringPeriod == null || !fCoveringPeriod.isWithinMyPeriod( aCursor ) ){
                final Month aThisMonth = aCursor.getMonth();
                aMonthsNotCovered.add( aThisMonth );
            }
            aCursor = aCursor.plusMonths( 1 );
        } while( aCursor.compareTo( aToCover_End ) < 0 );

        return aMonthsNotCovered;
    }

    public static List<Month> getMonthsCovered(
            final IPeriod fCoveringPeriod,
            final IPeriod fPeriodToCover)
    {
        final LocalDate aToCover_Start = fPeriodToCover.getStart();
        final LocalDate aToCover_End   = fPeriodToCover.getEnd();
        final List<Month> aMonthsNotCovered = new ArrayList<>();
        LocalDate aCursor = LocalDate.from( aToCover_Start );
        do {
            if( fCoveringPeriod != null && fCoveringPeriod.isWithinMyPeriod( aCursor ) ){
                final Month aThisMonth = aCursor.getMonth();
                aMonthsNotCovered.add( aThisMonth );
            }
            aCursor = aCursor.plusMonths( 1 );
        } while( aCursor.compareTo( aToCover_End ) < 0 );

        return aMonthsNotCovered;
    }

    public static LocalDate limitToMaxValue( LocalDate fDate )
    {
        if( DateUtils.MAX_DATE2.equals( fDate ) ){
            fDate = DateUtils.MAX_DATE;
        }
        return fDate;
    }

    public static LocalDate readFrom( final String fDateString )
    {
        final String[] aParts = fDateString.split( "\\." );
        final int aYear = Integer.parseInt( aParts[2] );
        final int aMonth = Integer.parseInt( aParts[1] );
        final int aDayOfMonth = Integer.parseInt( aParts[0] );
        return LocalDate.of( aYear, aMonth, aDayOfMonth );
    }

}

// ############################################################################
