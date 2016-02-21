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

import java.time.LocalDate;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.IItemFactory;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.IKnownColumns;

// ############################################################################
public class BalanceFactoryNew implements IItemFactory<Balance>
{
    private static final Logger sm_Log = Logger.getLogger(BalanceFactoryNew.class);

    @Override
    public Balance createItem( final int fID, final Map<String, String> fAttributes )
    {
        String aBalanceValueString   = null;
        String aBalanceValidOnString = null;
        for( final String aColumnName : fAttributes.keySet() ){
            if( IKnownColumns.GUTHABEN_WERT.equals( aColumnName )){
                aBalanceValueString = fAttributes.get( aColumnName );
            } else if( IKnownColumns.GUTHABEN_AM.equals( aColumnName ) ){
                aBalanceValidOnString = fAttributes.get( aColumnName );
            }
        }
        if( aBalanceValueString == null || "".equals(aBalanceValueString) ){
            return null;
        }
        try{
            final float aFloatValue = Float.parseFloat( aBalanceValueString.replaceAll( ",", "." ) );
            final int   aIntValue = Math.round( aFloatValue * 100 );
            final Balance aItem = new Balance( fID, aIntValue );
            if( aBalanceValidOnString != null && !"".equals( aBalanceValidOnString ) ){
                final String[] aParts = aBalanceValidOnString.split( "\\." );
                final int aYear = Integer.parseInt( aParts[2] );
                final int aMonth = Integer.parseInt( aParts[1] );
                final int aDayOfMonth = Integer.parseInt( aParts[0] );
                final LocalDate aValidOn = LocalDate.of( aYear, aMonth, aDayOfMonth );
                aItem.setValidOn( aValidOn );
            }
            return aItem;
        }catch( final NumberFormatException fEx ){
            sm_Log.warn("Guthaben-Angabe ist keine Zahl: "+ aBalanceValueString );
            return null;
        }
    }

}

// ############################################################################
