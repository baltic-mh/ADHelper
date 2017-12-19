/**
 * AdjustmentsTaken.java
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

import org.apache.log4j.Logger;

// ############################################################################
public class AdjustmentsTaken implements IIdentifiedItem<AdjustmentsTaken>, IParticipationItemContainer<Adjustment>
{
    private static final Logger sm_Log = Logger.getLogger(AdjustmentsTaken.class);

    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final List<Adjustment> m_AdjustmentList;
    public        List<Adjustment> getAdjustmentList(){ return m_AdjustmentList; }
    @Override
    public        List<Adjustment> getParticipationList(){ return getAdjustmentList(); }
    // ------------------------------------------------------------------------

    public AdjustmentsTaken( final int fMemberID )
    {
        m_MemberID         = fMemberID;
        m_AdjustmentList   = new ArrayList<>();
    }

    @Override
    public void add( final Adjustment fAdjustment ){
        synchronized( m_AdjustmentList ){
            m_AdjustmentList.add( fAdjustment );
        }
    }
    @Override
    public void remove( final Adjustment fItemToRemove )
    {
        Adjustment aMoriturus = null;
        synchronized( m_AdjustmentList ){
            for( final Adjustment aAdjustment : m_AdjustmentList ){
                if( fItemToRemove.getDate().equals( aAdjustment.getDate() )){
                    aMoriturus = aAdjustment;
                    break;
                }
            }
            if( aMoriturus != null ){
                m_AdjustmentList.remove( aMoriturus );
            }
        }
    }


    public List<Adjustment> getAdjustmentList( final IPeriod fPeriod )
    {
        final List<Adjustment> aAdjustmentList = getAdjustmentList();
        if( fPeriod == null ){
            return aAdjustmentList;
        }
        final List<Adjustment> aMatchingAdjustmentList = new ArrayList<>();
        for( final Adjustment aAdjustment : aAdjustmentList ){
            if( fPeriod.isWithinMyPeriod( aAdjustment.getDate() )){
                aMatchingAdjustmentList.add( aAdjustment );
            }
        }
        if( aMatchingAdjustmentList.size() > 1 ){
            sm_Log.warn(String.format( "%d: Mehr als eine Korrektur für Periode: %s",
                    getMemberID(), fPeriod));
        }
        return aMatchingAdjustmentList;
    }

    public int getTotalAdjustmentsTaken( final IPeriod fInvoicingPeriod )
    {
        int AdjustmentsTaken = 0;
        final List<Adjustment> aAdjustments = getAdjustmentList( fInvoicingPeriod );
        for( final Adjustment aAdjustment : aAdjustments ){
            final int aHours = aAdjustment.getHours();
            AdjustmentsTaken += aHours;
        }
        return AdjustmentsTaken;
    }

    @Override
    public String toString()
    {
        final StringBuffer aSB = new StringBuffer(String.format("%d:", getMemberID()));
        for( final Adjustment aAdjustment : getAdjustmentList( null ) ){
            aSB.append( String.format( "\n\t%s: %5.2f", aAdjustment.getDate(), aAdjustment.getHours()/100.0f ) );
        }
        return aSB.toString();
    }

    @Override
    public int compareTo( final AdjustmentsTaken fOther )
    {
        final int aThisValue = getMemberID();
        final int aOtherValue = fOther.getMemberID();
        if( aThisValue < aOtherValue ){
            return -1;
        }
        if( aThisValue > aOtherValue ){
            return 1;
        }
        return 0;
    }
}

// ############################################################################
