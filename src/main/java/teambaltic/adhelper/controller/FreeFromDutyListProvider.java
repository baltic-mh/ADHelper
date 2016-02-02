/**
 * MemberListProvider.java
 *
 * Created on 02.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.FreeFromDuty;

// ############################################################################
public class FreeFromDutyListProvider implements IFreeFromDutyListProvider
{
    private static final Logger sm_Log = Logger.getLogger(FreeFromDutyListProvider.class);

    private final Map<Integer, FreeFromDuty> m_Items;

    public FreeFromDutyListProvider()
    {
        m_Items = new HashMap<>();
    }

    @Override
    public void add(final FreeFromDuty fItem)
    {
        final Integer aID = fItem.getMemberID();
        synchronized( m_Items ){
            if( m_Items.containsKey( aID )) {
                sm_Log.warn( "Member already contained! Will be ignored: "+fItem);
                return;
            }
            m_Items.put( aID, fItem );
        }
    }

    @Override
    public FreeFromDuty get( final int fMemberID )
    {
        synchronized( m_Items ){
            return m_Items.get( fMemberID );
        }
    }

    @Override
    public void addAll( final Collection<FreeFromDuty> fItem )
    {
        synchronized( m_Items ){
            fItem.forEach( aItem -> {
                add( aItem );
            } );
        }

    }

}

// ############################################################################
