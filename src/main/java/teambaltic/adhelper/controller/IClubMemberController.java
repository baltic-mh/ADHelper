/**
 * IClubMemberController.java
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

import teambaltic.adhelper.model.IClubMember;

// ############################################################################
public interface IClubMemberController
{
    void add( IClubMember fClubMember );
    IClubMember get( int fMemberID );

}

// ############################################################################
