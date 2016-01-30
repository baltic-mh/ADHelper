/**
 * IClubMember.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw�rmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

// ############################################################################
public interface IClubMember
{

    int getID();
    int getLinkID();
    String getName();
    long getMemberSince();
    long getMemberUntil();
    EMemberKind getMemberKind();
    Long getManagementMemberSince();
    Long getFreedFromDutySince();

}

// ############################################################################
