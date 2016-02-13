/**
 * InvoicingPeriodBoxModel.java
 *
 * Created on 12.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.model;

import java.time.LocalDate;
import java.time.Year;

import javax.swing.DefaultComboBoxModel;

import teambaltic.adhelper.model.Halfyear;
import teambaltic.adhelper.model.Halfyear.EPart;
import teambaltic.adhelper.model.IInvoicingPeriod;

// ############################################################################
public class InvoicingPeriodBoxModel extends DefaultComboBoxModel<String>
{
    private static final long serialVersionUID = 7082585553407100592L;

    public InvoicingPeriodBoxModel()
    {
        super(createInvoicingPeriods());
    }

    private static String[] createInvoicingPeriods()
    {
        final int aThisYear = LocalDate.now().getYear();
        final String[] aPeriods = new String[6];
        for( int aIdx = 0; aIdx < 3; aIdx++ ){
            aPeriods[2*aIdx  ] = String.format( "%d 1.HJ", aThisYear-2+aIdx );
            aPeriods[2*aIdx+1] = String.format( "%d 2.HJ", aThisYear-2+aIdx );

        }
        return aPeriods;
    }

    @Override
    public IInvoicingPeriod getSelectedItem()
    {
        final String aSelected = (String) super.getSelectedItem();

        final String[] aParts = aSelected.split( " " );
        final int aYearInt = Integer.parseInt( aParts[0] );
        final Year aYear = Year.of( aYearInt );
        final EPart aPart = aParts[1].startsWith( "1" ) ? EPart.FIRST : EPart.SECOND;
        final Halfyear aHalfyear = new Halfyear( aYear, aPart );
        return aHalfyear;
    }

}

// ############################################################################
