/**
 * DutyCalculator.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.settings.IClubSettings;
import teambaltic.adhelper.utils.DateUtils;

// ############################################################################
/**
 * @author Mathias
 *
 *  Berechnet die Anzahl der zu erbringenden Arbeitsstunden pro Abbuchungszeitraum
 *
 */
public class DutyCalculator
{
//    private static final Logger sm_Log = Logger.getLogger(DutyCalculator.class);

    // ------------------------------------------------------------------------
    private final IClubSettings m_ClubSettings;
    private IClubSettings getClubSettings(){ return m_ClubSettings; }
    // ------------------------------------------------------------------------

    public DutyCalculator( final IClubSettings fClubSettings )
    {
        m_ClubSettings  = fClubSettings;
    }

    public int calculateHoursToWork( final Collection<FreeFromDuty> fFFDItems, final IPeriod fPeriod )
    {
        int aDutyHoursForThisPeriod = getClubSettings().getDutyHoursPerPeriod(fPeriod);
		if( aDutyHoursForThisPeriod == 0 || fFFDItems == null ){
            return aDutyHoursForThisPeriod;
        }
        final List<Month> aMonthsDue = getMonthsDue( fPeriod, fFFDItems );
        final int aNumMonthsDue = aMonthsDue.size();
        if( aNumMonthsDue == 0 ){
            return 0;
        }

        final Period aPeriod = Period.between( fPeriod.getStart(), fPeriod.getEnd() );
        final int aMonthsInInvoicingPeriod = aPeriod.getMonths()+1;
        final int aHoursToWork = aDutyHoursForThisPeriod * aNumMonthsDue / aMonthsInInvoicingPeriod;
        return aHoursToWork;
    }

    public static List<Month> getMonthsDue(
            final IPeriod fInvoicingPeriod,
            final Collection<FreeFromDuty> fEffectiveFFDs )
    {
        LocalDate aMonthsCursor = LocalDate.from( fInvoicingPeriod.getStart() );
        final List<Month> aMonthsDue = new ArrayList<>();
        do {
            final Month aMonthInQuestion = aMonthsCursor.getMonth();
            final boolean aMonthCovered = isMonthCovered( aMonthInQuestion, fInvoicingPeriod, fEffectiveFFDs );
            if( !aMonthCovered ){
                aMonthsDue.add( aMonthInQuestion );
            }
            aMonthsCursor = aMonthsCursor.plusMonths(1);
        } while( aMonthsCursor.compareTo( fInvoicingPeriod.getEnd() ) < 0 );
        return aMonthsDue;
    }

    private static boolean isMonthCovered(
            final Month fMonthInQuestion,
            final IPeriod fInvoicingPeriod,
            final Collection<FreeFromDuty> fEffectiveFFDs )
    {
        for( final FreeFromDuty aEffectiveFFD : fEffectiveFFDs ){
            final List<Month> aMonthsCovered = DateUtils.getMonthsCovered( aEffectiveFFD, fInvoicingPeriod );
            if( aMonthsCovered.contains( fMonthInQuestion ) ){
                return true;
            }
        }
        return false;
    }

}

// ############################################################################
