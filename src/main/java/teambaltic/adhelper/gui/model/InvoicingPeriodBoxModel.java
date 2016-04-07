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

import javax.swing.DefaultComboBoxModel;

import teambaltic.adhelper.model.PeriodData;

// ############################################################################
public class InvoicingPeriodBoxModel extends DefaultComboBoxModel<PeriodData>
{
    private static final long serialVersionUID = 7082585553407100592L;

    public InvoicingPeriodBoxModel(final PeriodData[] fPeriods)
    {
        super(fPeriods);
    }

//    @Override
//    public IPeriod getSelectedItem()
//    {
//        final String aSelected = (String) super.getSelectedItem();
//
//        final String[] aParts = aSelected.split( " " );
//        final int aYearInt = Integer.parseInt( aParts[0] );
//        final EPart aPart = aParts[1].startsWith( "1" ) ? EPart.FIRST : EPart.SECOND;
//        final Halfyear aHalfyear = new Halfyear( aYearInt, aPart );
//        return aHalfyear;
//    }

}

// ############################################################################
