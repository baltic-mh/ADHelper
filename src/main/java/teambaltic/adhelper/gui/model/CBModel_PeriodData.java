/**
 * CBModel_PeriodData.java
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
public class CBModel_PeriodData extends DefaultComboBoxModel<PeriodData>
{
    private static final long serialVersionUID = 7082585553407100592L;

    public CBModel_PeriodData(final PeriodData[] fPeriods)
    {
        super(fPeriods);
    }

//    @Override
//    public PeriodData getSelectedItem()
//    {
//        final PeriodData aSelected = (PeriodData) super.getSelectedItem();
//
//        return aSelected;
//    }

}

// ############################################################################
