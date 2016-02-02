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
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.HoursWorked;
import teambaltic.adhelper.model.IClubMember;

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
    private final IListProvider<HoursWorked> m_HoursWorkedListProvider;
    @Override
    public IListProvider<HoursWorked> getHoursWorkedListProvider(){ return m_HoursWorkedListProvider; }
    // ------------------------------------------------------------------------

    public ADInfoListProvider()
    {
        m_MemberListProvider        = new ListProvider<IClubMember>();
        m_FreeFromDutyListProvider  = new ListProvider<FreeFromDuty>();
        m_HoursWorkedListProvider   = new ListProvider<HoursWorked>();
    }

    public void readBaseInfo( final File fBaseInfoFile )
    {
        final BaseInfoReader aBaseInfoReader = new BaseInfoReader( fBaseInfoFile );
        try{
            aBaseInfoReader.read();
            m_MemberListProvider.addAll( aBaseInfoReader.getMemberList() );
            m_FreeFromDutyListProvider.addAll( aBaseInfoReader.getFreeFromDutyList() );
        }catch( final Exception fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
    }

}

// ############################################################################
