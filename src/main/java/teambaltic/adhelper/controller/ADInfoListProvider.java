/**
 * ADInfoListProvider.java
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

import java.io.File;

import org.apache.log4j.Logger;

import teambaltic.adhelper.inout.BaseInfoReader;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class ADInfoListProvider implements IADInfoListProvider
{
    private static final Logger sm_Log = Logger.getLogger(ADInfoListProvider.class);

    // ------------------------------------------------------------------------
    private final IListProvider<IClubMember> m_MemberListProvider;
    @Override
    public IListProvider<IClubMember> getMemberListProvider(){ return m_MemberListProvider; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IListProvider<FreeFromDuty> m_FreeFromDutyListProvider;
    @Override
    public IListProvider<FreeFromDuty> getFreeFromDutyListProvider(){ return m_FreeFromDutyListProvider; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IListProvider<Balance> m_BalanceListProvider;
    @Override
    public IListProvider<Balance> getBalanceListProvider(){ return m_BalanceListProvider; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IListProvider<WorkEventsAttended> m_WorkEventsAttendedListProvider;
    @Override
    public IListProvider<WorkEventsAttended> getWorkEventsAttendedListProvider(){ return m_WorkEventsAttendedListProvider; }
    // ------------------------------------------------------------------------

    public ADInfoListProvider()
    {
        m_MemberListProvider            = new ListProvider<>();
        m_FreeFromDutyListProvider      = new ListProvider<>();
        m_BalanceListProvider           = new ListProvider<>();
        m_WorkEventsAttendedListProvider= new ListProvider<>();
    }

    public void readBaseInfo( final File fBaseInfoFile )
    {
        final BaseInfoReader aBaseInfoReader = new BaseInfoReader( fBaseInfoFile );
        try{
            aBaseInfoReader.read();
            m_MemberListProvider.addAll( aBaseInfoReader.getMemberList() );
            m_FreeFromDutyListProvider.addAll( aBaseInfoReader.getFreeFromDutyList() );
            m_BalanceListProvider.addAll( aBaseInfoReader.getBalanceList() );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
    }

}

// ############################################################################
