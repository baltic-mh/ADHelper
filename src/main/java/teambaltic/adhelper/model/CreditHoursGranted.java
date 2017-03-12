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

import org.apache.log4j.Logger;

// ############################################################################
public class CreditHoursGranted implements IIdentifiedItem<CreditHoursGranted>, IParticipationItemContainer<CreditHours>
{
    private static final Logger sm_Log = Logger.getLogger(CreditHoursGranted.class);

    // ------------------------------------------------------------------------
    private final int m_MemberID;
    @Override
    public int getID() { return getMemberID(); }
    public int getMemberID() { return m_MemberID; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final List<CreditHours> m_CreditHoursList;
    public        List<CreditHours> getCreditHoursList(){ return m_CreditHoursList; }
    @Override
    public        List<CreditHours> getParticipationList(){ return getCreditHoursList(); }
    // ------------------------------------------------------------------------

    public CreditHoursGranted( final int fMemberID )
    {
        m_MemberID          = fMemberID;
        m_CreditHoursList   = new ArrayList<>();
    }

    @Override
    public void add( final CreditHours fCreditHours ){
        synchronized( m_CreditHoursList ){
            m_CreditHoursList.add( fCreditHours );
        }
    }
    @Override
    public void remove( final CreditHours fItemToRemove )
    {
        CreditHours aMoriturus = null;
        synchronized( m_CreditHoursList ){
            for( final CreditHours aCredit : m_CreditHoursList ){
                if( fItemToRemove.getDate().equals( aCredit.getDate() )){
                    aMoriturus = aCredit;
                    break;
                }
            }
            if( aMoriturus != null ){
                m_CreditHoursList.remove( aMoriturus );
            }
        }
    }


    public List<CreditHours> getCreditHoursList( final IPeriod fPeriod )
    {
        final List<CreditHours> aCreditHoursList = getCreditHoursList();
        if( fPeriod == null ){
            return aCreditHoursList;
        }
        final List<CreditHours> aMatchingCreditHoursList = new ArrayList<>();
        for( final CreditHours aCreditHours : aCreditHoursList ){
            if( fPeriod.isWithinMyPeriod( aCreditHours.getDate() )){
                aMatchingCreditHoursList.add( aCreditHours );
            }
        }
        if( aMatchingCreditHoursList.size() > 1 ){
            sm_Log.warn(String.format( "%d: Mehr als eine Gutschrift für Periode: %s",
                    getMemberID(), fPeriod));
        }
        return aMatchingCreditHoursList;
    }

    public int getTotalCreditHoursGranted(  final IPeriod fInvoicingPeriod )
    {
        int aTotalCreditHoursGranted = 0;
        final List<CreditHours> aCredits = getCreditHoursList( fInvoicingPeriod );
        for( final CreditHours aCredit : aCredits ){
            final int aHours = aCredit.getHours();
            aTotalCreditHoursGranted += aHours;
        }
        return aTotalCreditHoursGranted;
    }

    @Override
    public String toString()
    {
        final StringBuffer aSB = new StringBuffer(String.format("%d:", getMemberID()));
        for( final CreditHours aCreditHours : getCreditHoursList( null ) ){
            aSB.append( String.format( "\n\t%s: %5.2f", aCreditHours.getDate(), aCreditHours.getHours()/100.0f ) );
        }
        return aSB.toString();
    }

    @Override
    public int compareTo( final CreditHoursGranted fOther )
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
