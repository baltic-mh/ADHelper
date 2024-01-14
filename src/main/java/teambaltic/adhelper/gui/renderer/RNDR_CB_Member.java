/**
 * RNDR_CB_Member.java
 *
 * Created on 19.06.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.model.IClubMember;

// ############################################################################
public class RNDR_CB_Member extends JLabel implements ListCellRenderer<IClubMember>
{
    private static final long serialVersionUID = -4751927806273918193L;

    private final ADH_DataProvider m_DataProvider;

    public RNDR_CB_Member(final ADH_DataProvider fDataProvider)
    {
        super();
        m_DataProvider = fDataProvider;
    }
    @Override
    public Component getListCellRendererComponent(
            final JList<? extends IClubMember> fList,
            final IClubMember fClubMember,
            final int fIndex,
            final boolean fIsSelected,
            final boolean fCellHasFocus )
    {
        final String aMemberString = createMemberString(fClubMember);
        setText( aMemberString );
        return this;
    }

	public String createMemberString(final IClubMember fClubMember) {
		if ( fClubMember == null ) {
			return "";
		}
		String aMemberString;
        final int aLinkID = fClubMember.getLinkID();
        if( aLinkID == 0){
            aMemberString = fClubMember.toString();
        } else {
            final String aLinkedMemberString = m_DataProvider.getMember( aLinkID ).toString();
            aMemberString = String.format( "%s (%d => %s)", fClubMember.getName(), fClubMember.getID(), aLinkedMemberString );
        }
		return aMemberString;
	}

}

// ############################################################################
