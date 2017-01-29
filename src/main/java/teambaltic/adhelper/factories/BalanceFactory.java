/**
 * BalanceFactory.java
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

import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.IPeriod;

// ############################################################################
public class BalanceFactory implements IItemFactory<Balance>
{
    private static final Logger sm_Log = Logger.getLogger(BalanceFactory.class);

    private final boolean m_Old;
    private boolean isOld(){ return m_Old; }

    public BalanceFactory()
    {
        this(false);
    }

    public BalanceFactory( final boolean fOld )
    {
        m_Old = fOld;
    }

    @Override
    public Balance createItem( final int fID, final Map<String, String> fAttributes )
    {
        final Balance aItem = isOld() ? readBalanceOld( fID, fAttributes ) : readBalanceNew( fID, fAttributes );
        return aItem;
    }

    private static Balance readBalanceNew( final int fID, final Map<String, String> fAttributes )
    {
        String aBalanceValueString   = null;
        String aBalanceValidFromString = null;
        for( final String aColumnName : fAttributes.keySet() ){
            if( IKnownColumns.GUTHABEN_WERT.equals( aColumnName )){
                aBalanceValueString = fAttributes.get( aColumnName );
            } else if( IKnownColumns.GUTHABEN_AM.equals( aColumnName ) ){
                aBalanceValidFromString = fAttributes.get( aColumnName );
            }
        }
        if( aBalanceValueString == null || "".equals(aBalanceValueString) ){
            return null;
        }
        final LocalDate aValidFrom = readValidFrom( aBalanceValidFromString );
        return createBalance( fID, aBalanceValueString, aValidFrom );
    }

    private static Balance readBalanceOld( final int fID, final Map<String, String> fAttributes )
    {
        String aBalanceValueString   = null;
        String aBalanceValidFromString = null;
        for( final String aColumnName : fAttributes.keySet() ){
            if( IKnownColumns.GUTHABEN_WERT_ALT.equals( aColumnName )){
                aBalanceValueString = fAttributes.get( aColumnName );
            } else if( IKnownColumns.GUTHABEN_AM.equals( aColumnName ) ){
                aBalanceValidFromString = fAttributes.get( aColumnName );
            }
        }
        if( aBalanceValueString == null || "".equals(aBalanceValueString) ){
            return null;
        }
        final LocalDate aValidFrom = readValidFrom( aBalanceValidFromString );
        final IPeriod aPeriod = new Halfyear( aValidFrom );
        final LocalDate aValidFrom_Old = aPeriod.createPredeccessor().getStart();
        return createBalance( fID, aBalanceValueString, aValidFrom_Old );
    }

    private static Balance createBalance( final int fID, final String fBalanceValueString, final LocalDate fValidFrom )
    {
        try{
            final float aFloatValue = Float.parseFloat( fBalanceValueString.replaceAll( ",", "." ) );
            final int   aIntValue = Math.round( aFloatValue * 100 );
            final Balance aBalance = new Balance( fID, fValidFrom, aIntValue );
            return aBalance;
        }catch( final NumberFormatException fEx ){
            sm_Log.warn("Guthaben-Angabe ist keine Zahl: "+ fBalanceValueString );
            return null;
        }
    }

    private static LocalDate readValidFrom( final String aBalanceValidOnString )
    {
        final String[] aParts = aBalanceValidOnString.split( "\\." );
        final int aYear = Integer.parseInt( aParts[2] );
        final int aMonth = Integer.parseInt( aParts[1] );
        final int aDayOfMonth = Integer.parseInt( aParts[0] );
        final LocalDate aValidFrom = LocalDate.of( aYear, aMonth, aDayOfMonth );
        return aValidFrom;
    }

}

// ############################################################################
