/**
 * WorkEventTableListener.java
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
import java.time.LocalDate;
import java.util.List;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.gui.WorkEventEditor;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class WorkEventTableListener implements ActionListener
{
    private final WorkEventEditor m_WorkEventEditor;
    private final MainPanel m_Panel;
    private final ADH_DataProvider m_DataProvider;

    public WorkEventTableListener(final WorkEventEditor fWorkEventEditor,
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
            case "Delete":
                // TODO Hier muss ich zuerst die Informationen aus der selektieren Zeile holen,
                //  damit ich das korrekte Objekt aus dem DataProvider löschen kann!
                final WorkEvent aSelectedWorkEvent = m_Panel.getSelectedWorkEvent();
                if( aSelectedWorkEvent == null ){
                    return;
                }
                final boolean aRemoved = m_Panel.removeSelectedWorkEventRow();
                if( !aRemoved ){
                    return;
                }
                // TODO Nach dem Löschen im UI muss das Element aus dem DataProvider entfernt werden!
                final int aMemberID = m_Panel.getSelectedMemberID();
                final InfoForSingleMember aInfoForSingleMember = m_DataProvider.get( aMemberID );
                final WorkEventsAttended aWorkEventsAttended = aInfoForSingleMember.getWorkEventsAttended();
                if( aWorkEventsAttended != null ){
                    final int aMemberIDofSelectedWorkEvent = aSelectedWorkEvent.getMemberID();
                    final List<WorkEventsAttended> aAllWorkEvents = aWorkEventsAttended.getAllWorkEventsAttended();
                    for( final WorkEventsAttended aThisWorkEventsAttended : aAllWorkEvents ){
                        if( aMemberIDofSelectedWorkEvent == aThisWorkEventsAttended.getMemberID() ){
                            for( final WorkEvent aWE : aThisWorkEventsAttended.getWorkEvents() ){
                                final LocalDate aDateOfSelectedWorkEvent = aSelectedWorkEvent.getDate();
                                if( aDateOfSelectedWorkEvent.equals( aWE.getDate() ) ){
                                    aThisWorkEventsAttended.remove( aWE );
                                }
                            }
                        }

                    }
                }
                return;

            default:
                m_WorkEventEditor.display( aActionCommand );
                break;
        }
    }

}

// ############################################################################
