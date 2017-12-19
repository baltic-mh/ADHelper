/**
 * TBLModel_WorkEvents.java
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

// ############################################################################
public class TBLModel_Adjustments extends TBLModel_Participation
{
    private static final long serialVersionUID = 3040033628089631180L;

    private static final double HOURVALUE_MIN = -100.0;
    private static final double HOURVALUE_MAX =  100.0;

    public  static final String[]   COLUMNHEADERS   = new String[] { "Auswahl", "ID", "Name", "Stunden", "Kommentar" };
    private static final Class<?>[] COLUMNCLASSES   = new Class<?>[] { Boolean.class, Integer.class, String.class, Float.class, String.class };
    private static final Integer [] EDITABLECOLUMNS = new Integer [] { Integer.valueOf(0), Integer.valueOf(3), Integer.valueOf(4) };

    public int getColIdx_Comment(){ return 4; }

    public TBLModel_Adjustments(final Object[][] fData, final boolean fReadOnly)
    {
        super( COLUMNHEADERS, COLUMNCLASSES, EDITABLECOLUMNS, fData, fReadOnly );
    }

    @Override
    public double getMaxHoursValue()
    {
        return HOURVALUE_MAX;
    }

    @Override
    public double getMinHoursValue()
    {
        return HOURVALUE_MIN;
    }
}

// ############################################################################
