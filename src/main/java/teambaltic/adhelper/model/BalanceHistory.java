/**
 * BalanceHistory.java
 *
 * Created on 22.01.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

// ############################################################################
public class BalanceHistory implements IIdentifiedItem<BalanceHistory>
{
    private static final Logger sm_Log = Logger.getLogger(BalanceHistory.class);

    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final List<Balance> m_BalanceList;
    public List<Balance> getBalanceList(){
        if( !isListSorted() ){
            sortBalanceList();
        }
        return new ArrayList<>(m_BalanceList);
    }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private boolean m_ListSorted;
    private boolean isListSorted(){ return m_ListSorted; }
    private void setListSorted( final boolean fListSorted ){ m_ListSorted = fListSorted; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private Balance m_NewestValue;
    public Balance getNewestValue()
    {
        synchronized( m_BalanceList ){
            if( m_BalanceList.size() == 0 ){
                return null;
            }
            if( !isListSorted() ){
                sortBalanceList();
                m_NewestValue = m_BalanceList.get( m_BalanceList.size() - 1 );
            }
            return m_NewestValue;
        }
    }
    // ------------------------------------------------------------------------

    public BalanceHistory( final int fMemberID )
    {
        m_MemberID  = fMemberID;
        m_BalanceList = new ArrayList<>();
    }

    public void addBalance( final Balance fNewBalance )
    {
        synchronized( m_BalanceList ){
            checkNewKid( fNewBalance );
            m_BalanceList.add( fNewBalance );
            setListSorted( false );
        }
    }

    @Override
    public int compareTo( final BalanceHistory fO )
    {
        // TODO Auto-generated method stub
        // Ich habe momentan keine Vorstellung, wie man zwei Guthaben-Verläufe
        // miteinander vergleichen sollte:
        return 0;
    }

    @Override
    public String toString()
    {
        synchronized( m_BalanceList ){
            if( !isListSorted() ){
                sortBalanceList();
            }
            final StringBuffer aSB = new StringBuffer( String.format( "%d => ", getID() ) );
            for( final Balance aBalance : m_BalanceList ){
                aSB.append( String.format( "%s : %d | ", aBalance.getValidFrom(), aBalance.getValue_Original() ) );
            }
            return aSB.toString();
        }
    }
    /**
     * Sortiert die übergebene Liste nach Datum, aufsteigend
     */
    private void sortBalanceList()
    {
        synchronized( m_BalanceList ){
            Collections.sort( m_BalanceList, new Comparator<Balance>() {
                @Override
                public int compare( final Balance fBalance1, final Balance fBalance2 )
                {
                    final LocalDate aValidFrom1 = fBalance1.getValidFrom();
                    final LocalDate aValidFrom2 = fBalance2.getValidFrom();
                    final int aDatesCompared = aValidFrom1.compareTo( aValidFrom2 );
                    return aDatesCompared;
                }
            } );
            setListSorted( true );
        }
    }

    private void checkNewKid(final Balance fBalanceToAdd)
    {
        final int aMemberID = getMemberID();
        final int aMemberIDToAdd = fBalanceToAdd.getMemberID();
        if( aMemberID != aMemberIDToAdd ){
            throw new IllegalStateException( String.format("Guthaben für Mitglied '%d' kann nicht Mitglied %d zugefügt werden",
                    aMemberIDToAdd, aMemberID) );
        }
        final LocalDate aNewValidFrom = fBalanceToAdd.getValidFrom();
        for( final Balance aBalance : m_BalanceList ){
            if( aNewValidFrom.equals( aBalance.getValidFrom() )){
                throw new IllegalStateException( String.format("Für Mitglied '%d' existieren zwei Guthabenwerte mit demselben Stichdatum: %s",
                        aMemberID, aNewValidFrom) );
            }
        }
    }
}

// ############################################################################
