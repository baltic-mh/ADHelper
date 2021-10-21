/**
 * ActiveMemberFilter.java
 *
 * Created on 08.12.2018
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2018 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

// ############################################################################
public class ActiveMemberFilter implements IItemFilter<InfoForSingleMember>
{
    private final PeriodData m_Period;
    public PeriodData getPeriod(){ return m_Period; }

    public ActiveMemberFilter( final PeriodData fPeriod )
    {
        m_Period = fPeriod;
    }

    @Override
    public boolean accept( final InfoForSingleMember fInfoForSingleMember )
    {
        final LocalDate aMemberUntil = fInfoForSingleMember.getMember().getMemberUntil();
        if( aMemberUntil == null ){
            return true;
        }
        final IPeriod aPeriod = getPeriod().getPeriod();
        return aPeriod == null ? true : !aPeriod.isBeforeMyStart( aMemberUntil );
    }

    @Override
    public Collection<InfoForSingleMember> filter( final Collection<InfoForSingleMember> fInfoList )
    {
        final Collection<InfoForSingleMember> aFiltered = new ArrayList<>();
        for( final InfoForSingleMember aInfoForSingleMember : fInfoList ){
            if( accept( aInfoForSingleMember ) ){
                aFiltered.add( aInfoForSingleMember );
            }
        }
        return aFiltered;
    }

}

// ############################################################################
