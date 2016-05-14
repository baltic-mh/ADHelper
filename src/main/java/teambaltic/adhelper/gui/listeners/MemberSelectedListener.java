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

// ############################################################################
public class MemberSelectedListener implements ActionListener
{
    private final GUIUpdater m_GUIUpdater;

    public MemberSelectedListener(final GUIUpdater fGUIUpdater)
    {
        m_GUIUpdater = fGUIUpdater;
    }

    @Override
    public void actionPerformed( final ActionEvent fE )
    {
        m_GUIUpdater.updateGUI();
    }


}
// ############################################################################
