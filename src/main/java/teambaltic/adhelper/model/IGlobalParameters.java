/**
 * IGlobalParameters.java
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

// ############################################################################
public interface IGlobalParameters
{
    public enum EKey {
         MIN_AGE_FOR_DUTY
        ,MAX_AGE_FOR_DUTY
        // Anzahl der Monate, die man nach Vereinseintritt vom AD befreit ist:
        ,PROTECTION_TIME
        // Anzahl der Monate eines Abrechnungszeitraumes
        ,MONTHS_PER_INVOICEPERIOD
        ,DUTYHOURS_PER_INVOICEPERIOD
        ;
    }
}

// ############################################################################
