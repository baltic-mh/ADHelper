/**
 * WorkEventsAttended.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

// ############################################################################
public class WorkEventsAttended implements IIdentifiedItem
{
    private static final Logger sm_Log = Logger.getLogger(WorkEventsAttended.class);

    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final List<WorkEvent> m_WorkEvents;
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Map<Integer, WorkEventsAttended> m_WorkEventsOfRelatives;
    public List<WorkEventsAttended> getAllWorkEventsAttended()
    {
        final List <WorkEventsAttended> aAllItems = new ArrayList<>();
        aAllItems.add( this );
        aAllItems.addAll( m_WorkEventsOfRelatives.values() );
        return aAllItems;
    }
    // ------------------------------------------------------------------------

    public WorkEventsAttended( final int fMemberID )
    {
        m_MemberID   = fMemberID;
        m_WorkEvents = new ArrayList<>();
        m_WorkEventsOfRelatives = new HashMap<>();
    }

    public void addWorkEvent( final WorkEvent fEvent ){
        synchronized( m_WorkEvents ){
            m_WorkEvents.add( fEvent );
        }
    }

    public List<WorkEvent> getWorkEvents()
    {
        synchronized( m_WorkEvents ){
            return new ArrayList<>( m_WorkEvents );
        }
    }

    public List<WorkEvent> getWorkEvents( final IInvoicingPeriod fInvoicingPeriod )
    {
        final List<WorkEvent> aWorkEvents = getWorkEvents();
        if( fInvoicingPeriod == null ){
            return aWorkEvents;
        }
        final List<WorkEvent> aMatchingEvents = new ArrayList<>();
        for( final WorkEvent aEvent : aWorkEvents ){
            if( fInvoicingPeriod.isWithinPeriod( aEvent.getDate() )){
                aMatchingEvents.add( aEvent );
            }
        }
        return aMatchingEvents;
    }

    public int getTotalHoursWorked(  final IInvoicingPeriod fInvoicingPeriod )
    {
        int aTotalHoursWorked = 0;
        final List<WorkEvent> aWorkEvents = getWorkEvents( fInvoicingPeriod );
        for( final WorkEvent aEvent : aWorkEvents ){
            final int aHours = aEvent.getHours();
            aTotalHoursWorked += aHours;
        }
        return aTotalHoursWorked;
    }

    public void addRelative( final WorkEventsAttended fItem )
    {
        final int aRelativeID = fItem.getMemberID();
        synchronized( m_WorkEventsOfRelatives ){
            final Integer aIntegerKey = Integer.valueOf( aRelativeID );
            if( m_WorkEventsOfRelatives.containsKey( aIntegerKey ) ){
                sm_Log.warn( String.format("%d: WorkEvents from id %d already included! Will be ignored!",
                        getMemberID(), aRelativeID ) );
                return;
            }
            m_WorkEventsOfRelatives.put( aIntegerKey, fItem );
        }
    }

    @Override
    public String toString()
    {
        final StringBuffer aSB = new StringBuffer();
        for( final WorkEvent aWorkEvent : m_WorkEvents ){
            aSB.append( String.format( "\n\t%s: %5.2f", aWorkEvent.getDate(), aWorkEvent.getHours()/100.0f ) );
        }
        return aSB.toString();
    }

    public void remove( final WorkEvent fWE )
    {
        WorkEvent aMoriturus = null;
        synchronized( m_WorkEvents ){
            for( final WorkEvent aWorkEvent : m_WorkEvents ){
                if( fWE.getDate().equals( aWorkEvent.getDate() )){
                    aMoriturus = aWorkEvent;
                    break;
                }
            }
            if( aMoriturus != null ){
                m_WorkEvents.remove( aMoriturus );
            }
        }
    }
}

// ############################################################################
