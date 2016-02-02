/**
 * FreeFromDutyFactory.java
 *
 * Created on 02.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.util.Map;

import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.FreeFromDuty.REASON;
import teambaltic.adhelper.model.IKnownColumns;

// ############################################################################
public class FreeFromDutyFactory implements IItemFactory<FreeFromDuty>
{

    @Override
    public FreeFromDuty createItem( final int fID, final Map<String, String> fAttributes )
    {
        final String aReasonString = fAttributes.get( IKnownColumns.BEITRAGSART );
        final REASON aReason = convertToReason( aReasonString );
        if( aReason == null ){
            // TODO Hier muss zusätzlich nach einer Spalte gesucht werden, die
            // einen Grund UND den Beginn der Arbeitsdienstbefreiung angibt!
            return null;
        }
        final FreeFromDuty aItem = new FreeFromDuty( fID, aReason );
        return aItem;
    }

    private REASON convertToReason( final String fReasonString )
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
