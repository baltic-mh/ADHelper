/**
 * CBModel_WorkEventDates.java
 *
 * Created on 12.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.model;

import java.time.LocalDate;

import javax.swing.DefaultComboBoxModel;

// ############################################################################
public class CBModel_WorkEventDates extends DefaultComboBoxModel<LocalDate>
{
    private static final long serialVersionUID = -449859646339642143L;

    public CBModel_WorkEventDates( final LocalDate[] fDates )
    {
        super( fDates );
    }
}

// ############################################################################
