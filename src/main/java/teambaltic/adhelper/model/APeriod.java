/**
 * AInvoicingPeriod.java
 *
 * Created on 04.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.time.LocalDate;

// ############################################################################
public abstract class APeriod implements IPeriod
{

    @Override
    public boolean isAfterMyStart(  final LocalDate fDate )
    {
        if( fDate == null ) {
            return true;
        }
        final LocalDate aMyStart = getStart();
        if( aMyStart == null ){
            return true;
        }
        final int aComparedTo = aMyStart.compareTo( fDate );
        return aComparedTo <= 0;
    }

    @Override
    public boolean isBeforeMyEnd(  final LocalDate fDate )
    {
        if( fDate == null ) {
            return true;
        }
        final LocalDate aMyEnd = getEnd();
        if( aMyEnd == null ){
            return true;
        }
        final int aComparedTo = aMyEnd.compareTo( fDate );
        return aComparedTo >= 0;
    }

    @Override
    public boolean isBeforeMyStart(  final LocalDate fDate )
    {
        if( fDate == null ) {
            return true;
        }
        final LocalDate aMyStart = getStart();
        if( aMyStart == null ){
            return true;
        }
        final int aComparedTo = aMyStart.compareTo( fDate );
        return aComparedTo > 0;
    }

    @Override
    public boolean isWithinMyPeriod( final LocalDate fDate )
    {
        return isAfterMyStart( fDate ) && isBeforeMyEnd( fDate );
    }

    @Override
    public boolean isWithinMyPeriod( final IPeriod fOther )
    {
        if( fOther == null ) {
            return true;
        }
        final LocalDate aOtherStart = fOther.getStart();
        final LocalDate aOtherEnd   = fOther.getEnd();
        if( aOtherStart == null ){
            if( aOtherEnd == null ){
                return true;
            }
            return isWithinMyPeriod( aOtherEnd );
        }
        if( aOtherEnd == null ){
            return isWithinMyPeriod( aOtherStart );
        }

        if( isBeforeMyEnd( aOtherStart ) && isAfterMyStart( aOtherEnd ) ){
            return true;
        }
        return false;
    }

}

// ############################################################################
