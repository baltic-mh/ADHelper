/**
 * CBModel_Member.java
 *
 * Created on 09.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.model;

import javax.swing.DefaultComboBoxModel;

import teambaltic.adhelper.model.IClubMember;

// ############################################################################
public class CBModel_Member extends DefaultComboBoxModel<IClubMember>
{
//    private static final Logger sm_Log = Logger.getLogger(DefaultComboBoxModel.class);

    private static final long serialVersionUID = -1976447286675597394L;

    public CBModel_Member(final IClubMember[] fMembers)
    {
        super(fMembers);
    }

    @Override
    public IClubMember getSelectedItem()
    {
        final IClubMember aSelected = (IClubMember) super.getSelectedItem();
        // do something with this member before returning...
        return aSelected;
    }

}

// ############################################################################
