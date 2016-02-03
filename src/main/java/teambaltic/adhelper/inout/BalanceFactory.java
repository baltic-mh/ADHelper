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

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.IKnownColumns;

// ############################################################################
public class BalanceFactory implements IItemFactory<Balance>
{
    private static final Logger sm_Log = Logger.getLogger(BalanceFactory.class);

    @Override
    public Balance createItem( final int fID, final Map<String, String> fAttributes )
    {
        String aBalanceString = null;
        for( final String aColumnName : fAttributes.keySet() ){
            if( aColumnName.startsWith( IKnownColumns.GUTHABEN_PREFIX )){
                if( aBalanceString == null ){
                    aBalanceString = fAttributes.get( aColumnName );
                } else {
                    sm_Log.warn("Mehr als eine Spalte beginnt mit "+IKnownColumns.GUTHABEN_PREFIX);
                    sm_Log.warn("Es wird der Wert der ersten Spalte genommen: "+aBalanceString);
                }
            }
        }
        if( aBalanceString == null || "".equals(aBalanceString) ){
            return null;
        }
        try{
            final float aFloatValue = Float.parseFloat( aBalanceString.replaceAll( ",", "." ) );
            final int   aIntValue = Math.round( aFloatValue * 100 );
            final Balance aItem = new Balance( fID, aIntValue );
            return aItem;
        }catch( final NumberFormatException fEx ){
            sm_Log.warn("Guthaben-Angabe ist keine Zahl: "+ aBalanceString );
            return null;
        }
    }

//    private REASON convertToReason( final String fReasonString )
//    {
//        if( fReasonString == null || "".equals( fReasonString ) ){
//            return null;
//        }
//        for( final REASON aReason : REASON.values() ){
//            if( fReasonString.equals( aReason.getStringRep() )){
//                return aReason;
//            }
//        }
//        return null;
//    }

}

// ############################################################################
