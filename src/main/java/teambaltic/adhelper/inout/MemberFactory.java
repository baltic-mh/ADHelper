/**
 * MemberFactory.java
 *
 * Created on 02.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.time.LocalDate;
import java.util.Map;

import teambaltic.adhelper.model.ClubMember;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.utils.ParseUtils;

// ############################################################################
public class MemberFactory implements IItemFactory<IClubMember>
{
//    private static final Logger sm_Log = Logger.getLogger(MemberFactory.class);

    @Override
    public IClubMember createItem( final int fID, final Map<String, String> fAttributes )
    {
        final ClubMember aCM = new ClubMember( fID );
        populate( aCM, fAttributes );
        return aCM;
    }

    private static void populate(
            final ClubMember fCM, final Map<String, String> fAttributes )
    {
        final String aNachname = fAttributes.get( IKnownColumns.NAME );
        final String aVorname  = fAttributes.get( IKnownColumns.FIRSTNAME );
        fCM.setName( String.format("%s %s", aVorname, aNachname) );

        final String aBirthdayValue = fAttributes.get( IKnownColumns.BIRTHDAY );
        final LocalDate aBirthday = ParseUtils.getDate( aBirthdayValue );
        if( aBirthday != null ){
            fCM.setBirthday( aBirthday );
        }

        final String aEintrittValue = fAttributes.get( IKnownColumns.EINTRITT );
        final LocalDate aEintritt = ParseUtils.getDate( aEintrittValue );
        if( aEintritt != null ){
            fCM.setMemberFrom( aEintritt );
        }

        final String aAustrittValue = fAttributes.get( IKnownColumns.AUSTRITT );
        final LocalDate aAustritt = ParseUtils.getDate( aAustrittValue );
        if( aAustritt != null ){
            fCM.setMemberFrom( aAustritt );
        }

        final String aLinkIDValue = fAttributes.get( IKnownColumns.LINKID );
        final Integer aLink = ParseUtils.getInteger( aLinkIDValue );
        if( aLink != null ){
            fCM.setLinkID( aLink.intValue() );
        }

    }

}

// ############################################################################
