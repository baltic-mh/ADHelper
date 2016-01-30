/**
 * ClubMemberController.java
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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.IClubMember;

// ############################################################################
public class ClubMemberController implements IClubMemberController
{
    private static final Logger sm_Log = Logger.getLogger(ClubMemberController.class);

    private final Map<Integer, IClubMember> m_ClubMembers;

    public ClubMemberController()
    {
        m_ClubMembers = new HashMap<>();
    }

    @Override
    public void add(final IClubMember fClubMember)
    {
        final Integer aID = fClubMember.getID();
        synchronized( m_ClubMembers ){
            if( m_ClubMembers.containsKey( aID )) {
                sm_Log.warn( "Member already contained! Will be ignored: "+fClubMember);
                return;
            }
            m_ClubMembers.put( aID, fClubMember );
        }
    }

    @Override
    public IClubMember get( final int fMemberID )
    {
        synchronized( m_ClubMembers ){
            return m_ClubMembers.get( fMemberID );
        }
    }
}

// ############################################################################
