/**
 * InfoForSingleMember.java
 *
 * Created on 06.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

// ############################################################################
public class InfoForSingleMember extends AIdentifiedItem<InfoForSingleMember>
{
    private static final Logger sm_Log = Logger.getLogger(InfoForSingleMember.class);

    // ------------------------------------------------------------------------
    private IClubMember m_Member;
    public IClubMember getMember(){ return m_Member; }
    public void setMember( final IClubMember fMember ){ m_Member = fMember; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private FreeFromDutySet m_FreeFromDutySet;
    public FreeFromDutySet getFreeFromDutySet(){ return m_FreeFromDutySet; }
    public Collection<FreeFromDuty> getFreeFromDutyItems(final IPeriod fPeriod){ return m_FreeFromDutySet.getFreeFromDutyItems(fPeriod); }
    public void setFreeFromDutySet( final FreeFromDutySet fNewVal ){ m_FreeFromDutySet = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private BalanceHistory m_BalanceHistory;
    public BalanceHistory getBalanceHistory(){ return m_BalanceHistory; }
    public void setBalanceHistory( final BalanceHistory fBalanceHistory ){ m_BalanceHistory = fBalanceHistory; }
    public Balance getBalance(final IPeriod fPeriod){ return getBalanceHistory().getValue(fPeriod); }
    public void addBalance( final Balance fItem ){ addBalance( fItem, false ); }
    public void addBalance( final Balance fItem, final boolean fOverride ){ getBalanceHistory().addBalance( fItem, fOverride ); }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private WorkEventsAttended m_WorkEventsAttended;
    public WorkEventsAttended getWorkEventsAttended(){ return m_WorkEventsAttended; }
    public void setWorkEventsAttended( final WorkEventsAttended fNewVal ){ m_WorkEventsAttended = fNewVal; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private AdjustmentsTaken m_AdjustmentsTaken;
    public AdjustmentsTaken getAdjustmentsTaken(){ return m_AdjustmentsTaken; }
    public void setAdjustmentsTaken( final AdjustmentsTaken fNewVal ){ m_AdjustmentsTaken = fNewVal; }
    public Adjustment getAdjustment(final IPeriod fPeriod){
        final AdjustmentsTaken aAdjustmentsTaken = getAdjustmentsTaken();
        if( aAdjustmentsTaken == null ){
            return null;
        }
        final List<Adjustment> aAdjustmentList = aAdjustmentsTaken.getAdjustmentList( fPeriod );
        if( aAdjustmentList == null || aAdjustmentList.size() == 0 ){
            return null;
        }
        return aAdjustmentList.get( 0 );
    }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private DutyCharge m_DutyCharge;
    public DutyCharge getDutyCharge(){ return m_DutyCharge; }
    public void setDutyCharge( final DutyCharge fDutyCharge ){ m_DutyCharge = fDutyCharge; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Map<Integer, InfoForSingleMember> m_Relatives;
    public List<InfoForSingleMember> getAllRelatives()
    {
        final List <InfoForSingleMember> aAllItems = new ArrayList<>();
        aAllItems.add( this );
        aAllItems.addAll( m_Relatives.values() );
        return aAllItems;
    }
    // ------------------------------------------------------------------------


    public InfoForSingleMember(final int fID)
    {
        super( fID );
        m_BalanceHistory = new BalanceHistory( fID );
        m_Relatives = new HashMap<>();
    }

    @Override
    public int compareTo( final InfoForSingleMember fOther )
    {
        final IClubMember aThisMember   = getMember();
        final IClubMember aOtherMember  = fOther.getMember();
        return aThisMember.compareTo( aOtherMember );
    }

    @Override
    public String toString()
    {
        return getMember().toString();
    }

    public void addRelative( final InfoForSingleMember fItem )
    {
        final int aRelativeID = fItem.getID();
        synchronized( m_Relatives ){
            final Integer aIntegerKey = Integer.valueOf( aRelativeID );
            if( m_Relatives.containsKey( aIntegerKey ) ){
                sm_Log.warn( String.format("%d: Relative %d already linked! Will be ignored!",
                        getID(), aRelativeID ) );
                return;
            }
            m_Relatives.put( aIntegerKey, fItem );
        }
    }

}

// ############################################################################
