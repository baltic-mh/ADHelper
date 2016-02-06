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
import java.util.List;

// ############################################################################
public class WorkEventsAttended implements IIdentifiedItem
{
    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final List<WorkEvent> m_WorkEvents;
    // ------------------------------------------------------------------------

    public WorkEventsAttended( final int fMemberID )
    {
        m_MemberID   = fMemberID;
        m_WorkEvents = new ArrayList<>();
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

    @Override
    public String toString()
    {
        final StringBuffer aSB = new StringBuffer();
        for( final WorkEvent aWorkEvent : m_WorkEvents ){
            aSB.append( String.format( "\n\t%s: %5.2f", aWorkEvent.getDate(), aWorkEvent.getHours()/100.0f ) );
        }
        return aSB.toString();
    }
}

// ############################################################################
