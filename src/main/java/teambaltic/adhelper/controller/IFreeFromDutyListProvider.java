/**
 * IFreeFromDutyListProvider.java
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

import teambaltic.adhelper.model.FreeFromDuty;

// ############################################################################
public interface IFreeFromDutyListProvider
{
    void add( FreeFromDuty fItem );
    FreeFromDuty get( int fMemberID );
    void addAll( Collection<FreeFromDuty> fItem );
}

// ############################################################################
