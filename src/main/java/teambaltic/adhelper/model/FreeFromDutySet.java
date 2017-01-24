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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import teambaltic.adhelper.model.FreeFromDuty.REASON;

// ############################################################################
public class FreeFromDutySet implements IIdentifiedItem<FreeFromDutySet>
{
    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Map<REASON, FreeFromDuty> m_FreeFromDutyMap;
    public Collection<FreeFromDuty> getFreeFromDutyItems(){ return m_FreeFromDutyMap.values(); }
    // ------------------------------------------------------------------------

    public FreeFromDutySet( final int fMemberID )
    {
        m_FreeFromDutyMap = new HashMap<>();
        m_MemberID = fMemberID;
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
}

// ############################################################################
