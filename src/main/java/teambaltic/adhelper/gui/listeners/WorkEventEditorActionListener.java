/**
 * WorkEventEditorActionListener.java
 *
 * Created on 13.02.2016
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

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.gui.WorkEventEditor;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public final class WorkEventEditorActionListener implements ActionListener
{
    private final WorkEventEditor m_WorkEventEditor;
    private final MainPanel m_Panel;
    private final ADH_DataProvider m_DataProvider;

    public WorkEventEditorActionListener(
            final WorkEventEditor fWorkEventEditor,
            final MainPanel fPanel,
            final ADH_DataProvider fDataProvider)
    {
        m_WorkEventEditor = fWorkEventEditor;
        m_Panel = fPanel;
        m_DataProvider = fDataProvider;
    }

    @Override
    public void actionPerformed( final ActionEvent fE )
    {
        final String aActionCommand = fE.getActionCommand();
        switch( aActionCommand ){
            case "Cancel":
                m_WorkEventEditor.setVisible(false);
                break;

            case "Apply":
                addWorkEventToCurrentMember();
                break;
            case "OK":
                addWorkEventToCurrentMember();
                m_WorkEventEditor.setVisible(false);
                break;

            default:
                break;
        }
    }

    private void addWorkEventToCurrentMember()
    {
        final WorkEvent aWorkEvent = m_WorkEventEditor.getWorkEvent();
        if( aWorkEvent.getHours() == 0 ){
            return;
        }
        final int aMemberID = aWorkEvent.getMemberID();
        final InfoForSingleMember aInfoForSingleMember = m_DataProvider.get( aMemberID );
        WorkEventsAttended aWorkEventsAttended = aInfoForSingleMember.getWorkEventsAttended();
        if( aWorkEventsAttended == null ){
            aWorkEventsAttended = new WorkEventsAttended( aMemberID );
            aInfoForSingleMember.setWorkEventsAttended( aWorkEventsAttended );
            final int aLinkID = aInfoForSingleMember.getMember().getLinkID();
            if( aLinkID != 0 ){
                final InfoForSingleMember aLinkedInfo = m_DataProvider.get( aLinkID );
                WorkEventsAttended aLinkedToWorkEventsAttended = aLinkedInfo.getWorkEventsAttended();
                if( aLinkedToWorkEventsAttended  == null ){
                    aLinkedToWorkEventsAttended = new WorkEventsAttended( aLinkID );
                    aLinkedInfo.setWorkEventsAttended( aLinkedToWorkEventsAttended );
                }
                aLinkedToWorkEventsAttended.addRelative( aWorkEventsAttended );
            }
        }
        aWorkEventsAttended.addWorkEvent( aWorkEvent );
        m_WorkEventEditor.reset();
        GUIUpdater.updateGUI( aMemberID, m_Panel, m_DataProvider );
    }
}
// ############################################################################
