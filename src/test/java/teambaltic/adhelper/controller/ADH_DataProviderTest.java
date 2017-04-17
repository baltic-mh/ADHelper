/**
 * ADH_DataProviderTest.java
 *
 * Created on 16.04.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

import teambaltic.adhelper.model.ClubMember;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;

// ############################################################################
public class ADH_DataProviderTest
{

    @Test
    public void test_isMembershipEffective()
    {
        final IClubMember aMember = new ClubMember(0);
        final IPeriod aPeriod     = new Halfyear( 2000, EPart.SECOND );
        boolean aIsEffective = ADH_DataProvider.isMembershipEffective( aMember, aPeriod );
        assertTrue("MemberUntil = null", aIsEffective);

        LocalDate aAustritt = LocalDate.of( 2001, 1, 1 );
        aMember.setMemberUntil( aAustritt );
        aIsEffective = ADH_DataProvider.isMembershipEffective( aMember, aPeriod );
        assertTrue("MemberUntil = "+aAustritt, aIsEffective);

        aAustritt = LocalDate.of( 2000, 7, 1 );
        aMember.setMemberUntil( aAustritt );
        aIsEffective = ADH_DataProvider.isMembershipEffective( aMember, aPeriod );
        assertTrue("MemberUntil = "+aAustritt, aIsEffective);

        aAustritt = LocalDate.of( 2000, 6, 30 );
        aMember.setMemberUntil( aAustritt );
        aIsEffective = ADH_DataProvider.isMembershipEffective( aMember, aPeriod );
        assertFalse("MemberUntil = "+aAustritt, aIsEffective);
    }

}

// ############################################################################
