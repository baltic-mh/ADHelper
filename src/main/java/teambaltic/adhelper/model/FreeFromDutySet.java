/**
 * FreeFromDutySet.java
 *
 * Created on 03.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teambaltic.adhelper.model.FreeFromDuty.REASON;

// ############################################################################
public class FreeFromDutySet extends AIdentifiedItem<FreeFromDutySet>
{
    // ------------------------------------------------------------------------
    private final Map<REASON, FreeFromDuty> m_FreeFromDutyMap;
    public Collection<FreeFromDuty> getFreeFromDutyItems(final IPeriod fPeriod){
        return getEffectiveFreeFromDutyItems(fPeriod, m_FreeFromDutyMap.values());
    }
    // ------------------------------------------------------------------------

    public FreeFromDutySet( final int fMemberID )
    {
        super( fMemberID );
        m_FreeFromDutyMap = new HashMap<>();
    }

    public void addItem( final FreeFromDuty fItem )
    {
        if( fItem == null ){
            return;
        }
        m_FreeFromDutyMap.put( fItem.getReason(), fItem );
    }

    public FreeFromDuty getItem( final REASON fReason )
    {
        return m_FreeFromDutyMap.get( fReason );
    }

    @Override
    public int compareTo( final FreeFromDutySet fOther )
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String toString()
    {
        synchronized( m_FreeFromDutyMap ){
            final StringBuffer aSB = new StringBuffer( String.format( "%d => ", getID() ) );
            for( final FreeFromDuty aFFD : m_FreeFromDutyMap.values() ){
                aSB.append( String.format( "%s | ", aFFD ) );
            }
            return aSB.toString();
        }
    }

    static List<FreeFromDuty> getEffectiveFreeFromDutyItems(
            final IPeriod fInvoicingPeriod,
            final Collection<FreeFromDuty> fFFDItems )
    {
        final List<FreeFromDuty> aEffectiveFFDs = new ArrayList<>();
        for( final FreeFromDuty aFreeFromDutyItem : fFFDItems ){
            if( fInvoicingPeriod.isWithinMyPeriod( aFreeFromDutyItem ) ){
                aEffectiveFFDs.add( aFreeFromDutyItem );
            }
        }
        return aEffectiveFFDs;
    }


}

// ############################################################################
