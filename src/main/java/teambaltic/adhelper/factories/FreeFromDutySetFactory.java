/**
 * FreeFromDutySetFactory.java
 *
 * Created on 02.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.factories;

import java.time.LocalDate;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDuty.REASON;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.utils.DateUtils;
import teambaltic.adhelper.utils.ParseUtils;

// ############################################################################
public class FreeFromDutySetFactory implements IItemFactory<FreeFromDutySet>
{
    private static final Logger sm_Log = Logger.getLogger(FreeFromDutySetFactory.class);

    @Override
    public void populateItem( final FreeFromDutySet fFFDSet, final Map<String, String> fAttributes )
    {
        final FreeFromDuty aItemFromBaseData = createItemFromBaseData( fFFDSet.getID(), fAttributes );
        fFFDSet.addItem( aItemFromBaseData );
    }

    private static FreeFromDuty createItemFromBaseData( final int fID, final Map<String, String> fAttributes )
    {
        final String aBeitragsArt = fAttributes.get( IKnownColumns.BEITRAGSART );
        REASON aReason = convertBeitragsArtToReason( aBeitragsArt );
        if( aReason == null ){
            final String aReasonString = fAttributes.get( IKnownColumns.AD_FREE_REASON);
            aReason = convertStringToReason( aReasonString );
            if( aReason == null ){
                return null;
            }
        }
        final FreeFromDuty aItem = createItem( fID, aReason, fAttributes );

        return aItem;
    }

    private static REASON convertStringToReason( final String fReasonString )
    {
        if( fReasonString == null ){
            return null;
        }
        final String aReasonString = fReasonString.trim();
        if( aReasonString == null || "".equals( aReasonString ) ){
            return null;
        }
        try{
            final REASON aReason = FreeFromDuty.REASON.valueOf( aReasonString );
            return aReason;
        }catch( final Exception fEx ){
            sm_Log.warn("Unzulässige Angabe für Grund zur Arbeitsdienstbefreiung: "+aReasonString );
            return REASON.INDIVIDUALREASON;
        }
    }

    private static FreeFromDuty createItem( final int fID, final REASON fReason, final Map<String, String> fAttributes )
    {
        final FreeFromDuty aItem = new FreeFromDuty( fID, fReason );
        fillDates( aItem, fAttributes );
        if( REASON.NO_LONGER_MEMBER.equals( fReason ) ){
            if( DateUtils.MAX_DATE.equals( aItem.getFrom() ) ){
                return null;
            }
        }
        return aItem;
    }

    private static void fillDates( final FreeFromDuty fItem, final Map<String, String> fAttributes )
    {
        final String aFromValue = fAttributes.get( IKnownColumns.AD_FREE_FROM );
        LocalDate aFrom = ParseUtils.getDate( aFromValue );
        if( aFrom == null ){
            aFrom = DateUtils.MIN_DATE;
        } else {
            aFrom = DateUtils.limitToMaxValue( aFrom );
        }
        fItem.setFrom( aFrom );

        final String aUntilValue = fAttributes.get( IKnownColumns.AD_FREE_UNTIL );
        LocalDate aUntil = ParseUtils.getDate( aUntilValue );
        if( aUntil == null ){
            aUntil = DateUtils.MAX_DATE;
        } else {
            aUntil = DateUtils.limitToMaxValue( aUntil );
        }
        fItem.setUntil( aUntil );
    }

    private static REASON convertBeitragsArtToReason( final String fReasonString )
    {
        if( fReasonString == null || "".equals( fReasonString ) ){
            return null;
        }
        for( final REASON aReason : REASON.values() ){
            if( fReasonString.equals( aReason.getStringRep() )){
                return aReason;
            }
        }
        return null;
    }

}

// ############################################################################
