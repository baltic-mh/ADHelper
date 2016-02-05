/**
 * IADInfoListProvider.java
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

import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public interface IADInfoListProvider
{
    IListProvider<IClubMember>          getMemberListProvider();
    IListProvider<FreeFromDuty>         getFreeFromDutyListProvider();
    IListProvider<Balance>              getBalanceListProvider();
    IListProvider<WorkEventsAttended>   getWorkEventsAttendedListProvider();
}

// ############################################################################
