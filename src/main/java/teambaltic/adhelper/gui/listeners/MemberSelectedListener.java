/**
 * MemberSelectedListener.java
 *
 * Created on 11.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.model.IClubMember;

// ############################################################################
public class MemberSelectedListener implements ActionListener
{
    private final MainPanel m_Panel;
    private final ADH_DataProvider m_DataProvider;

    public MemberSelectedListener(
            final MainPanel fPanel,
            final ADH_DataProvider fDataProvider)
    {
        m_Panel = fPanel;
        m_DataProvider = fDataProvider;
    }

    @Override
    public void actionPerformed( final ActionEvent fE )
    {
//        @SuppressWarnings("unchecked")
//        final JComboBox<IClubMember> aCB = (JComboBox<IClubMember>) fE.getSource();
        final JComboBox<IClubMember> aCB = m_Panel.getCB_Members();
        final IClubMember aSelectedItem = (IClubMember) aCB.getSelectedItem();
        final int aMemberID = aSelectedItem.getID();
        GUIUpdater.updateGUI( aMemberID, m_Panel, m_DataProvider );

    }


}
// ############################################################################
