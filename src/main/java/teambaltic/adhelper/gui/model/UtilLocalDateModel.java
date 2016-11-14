/**
 * UtilLocalDateModel.java
 *
 * Created on 03.05.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.model;

import java.time.LocalDate;
import java.util.Calendar;

import org.jdatepicker.AbstractDateModel;

//############################################################################
public class UtilLocalDateModel extends AbstractDateModel<LocalDate>
{
    public UtilLocalDateModel()
    {
        this( null );
    }

    public UtilLocalDateModel(final LocalDate value)
    {
        super();
        setValue( value );
    }

    @Override
    protected LocalDate fromCalendar(final Calendar fFrom)
    {
        final int aDayOfMonth = fFrom.get( Calendar.DAY_OF_MONTH );
        final int aMonth      = fFrom.get( Calendar.MONTH ) +1;
        final int aYear       = fFrom.get( Calendar.YEAR );
        return LocalDate.of( aYear, aMonth, aDayOfMonth );
    }

    @Override
    protected Calendar toCalendar(final LocalDate fFrom)
    {
        final Calendar aTo = Calendar.getInstance();
        aTo.set( Calendar.YEAR, fFrom.getYear() );
        aTo.set( Calendar.MONTH, fFrom.getMonthValue()-1 );
        aTo.set( Calendar.DAY_OF_MONTH, fFrom.getDayOfMonth());
        return aTo;
    }

}

// ############################################################################
