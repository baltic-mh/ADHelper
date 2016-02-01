/**
 * IInvoicingPeriod.java
 *
 * Created on 01.02.2016
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
public interface IInvoicingPeriod
{
    LocalDate getStart();
    LocalDate getEnd();
}

// ############################################################################
