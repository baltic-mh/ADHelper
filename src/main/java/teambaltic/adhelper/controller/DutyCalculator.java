/**
 * DutyCalculator.java
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
public class DutyCalculator
{
    private final long m_DueDate;

    public DutyCalculator( final long fDueDate )
    {
        m_DueDate = fDueDate;
    }

    public int calculate( final IClubMember fMember )
    {
        final int aHoursToWork = 0;
        final long aBirtday = fMember.getBirtday();
        return aHoursToWork;
    }
}

// ############################################################################
