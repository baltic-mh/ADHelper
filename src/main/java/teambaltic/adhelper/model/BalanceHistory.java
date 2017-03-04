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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private final Map<LocalDate, Balance> m_BalanceMap;
    public List<Balance> getBalanceList(){
        if( !isListSorted() ){
            sortValidFromList();
        }
        return new ArrayList<>(m_BalanceMap.values());
    }
    public Balance getValue( final IPeriod fPeriod ){ return getValue( fPeriod.getStart() ); }
    public Balance getValue( final LocalDate fValidFrom ){ return m_BalanceMap.get( fValidFrom ); }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final List<LocalDate> m_ValidFromList;
    public List<LocalDate> getValidFromList()
    {
        if( !isListSorted() ){
            sortValidFromList();
        }
        return new ArrayList<>( m_ValidFromList );
    }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private boolean m_ListSorted;
    private boolean isListSorted(){ return m_ListSorted; }
    private void setListSorted( final boolean fListSorted ){ m_ListSorted = fListSorted; }
    // ------------------------------------------------------------------------

    public BalanceHistory( final int fMemberID )
    {
        m_MemberID   = fMemberID;
        m_BalanceMap = new LinkedHashMap<>();
        m_ValidFromList = new ArrayList<>();
    }

    public void addBalance( final Balance fNewBalance )
    {
        if( fNewBalance == null ) {
            return;
        }
        synchronized( m_BalanceMap ){
            final boolean aOK = checkNewKid( fNewBalance );
            if( !aOK ){
                return;
            }
            final LocalDate aValidFrom = fNewBalance.getValidFrom();
            m_BalanceMap.put( aValidFrom, fNewBalance );
            m_ValidFromList.add( aValidFrom );
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
        synchronized( m_BalanceMap ){
            if( !isListSorted() ){
                sortValidFromList();
            }
            final StringBuffer aSB = new StringBuffer( String.format( "%d => ", getID() ) );
            for( final Balance aBalance : m_BalanceMap.values() ){
                aSB.append( String.format( "%s : %d | ", aBalance.getValidFrom(), aBalance.getValue_Original() ) );
            }
            return aSB.toString();
        }
    }
    /**
     * Sortiert die übergebene Liste nach Datum, aufsteigend
     */
    private void sortValidFromList()
    {
        synchronized( m_BalanceMap ){
            Collections.sort( m_ValidFromList, new Comparator<LocalDate>() {
                @Override
                public int compare( final LocalDate aValidFrom1, final LocalDate aValidFrom2 )
                {
                    final int aDatesCompared = aValidFrom1.compareTo( aValidFrom2 );
                    return aDatesCompared;
                }
            } );
            setListSorted( true );
        }
    }

    private boolean checkNewKid(final Balance fBalanceToAdd)
    {
        final int aMemberID = getMemberID();
        final int aMemberIDToAdd = fBalanceToAdd.getMemberID();
        if( aMemberID != aMemberIDToAdd ){
            throw new IllegalStateException( String.format("Guthaben für Mitglied '%d' kann nicht Mitglied %d zugefügt werden",
                    aMemberIDToAdd, aMemberID) );
        }
        final LocalDate aNewValidFrom = fBalanceToAdd.getValidFrom();
        if( aNewValidFrom == null ){
            throw new IllegalStateException( String.format("Guthaben für Mitglied '%d' hat kein gültiges Stichdatum!",
                    aMemberID) );
        }
        final Balance aBalance = m_BalanceMap.get( aNewValidFrom );
        if( aBalance == null ){
            return true;
        }

        final int aOldValue_Original = aBalance.getValue_Original();
        final int aNewValue_Original = fBalanceToAdd.getValue_Original();
        if( aNewValue_Original != aOldValue_Original){
            sm_Log.error( String.format("Für Mitglied '%d' existieren zwei Guthabenwerte mit"
                    +" demselben Stichdatum (%s) aber unterschiedlichen Werten: Alt: %d Neu: %d."
                    +" Der neue Wert wird ignoriert!",
                    aMemberID, aNewValidFrom, aOldValue_Original, aNewValue_Original) );
        }
        return false;
    }
}

// ############################################################################
