/**
 * IKnownColumns.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw�rmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

// ############################################################################
public interface IKnownColumns
{
    // Verwendung in mehreren Tabellen:
    String MEMBERID = "Mitglieds_Nr";   // Darf nicht 0 sein!
    String DATE = "Datum";              // Format: DD.MM.YYYY

    // Tabelle der Mitgliederdaten:
    String BIRTHDAY = "Geburtsdatum";   // Format wie Datum
    String PLZ = "Plz";                 // Wird nicht verwendet
    String ORT = "Ort";                 // Wird nicht verwendet
    String ANREDE = "Anrede";           // Wird nicht verwendet
    String STREET = "Stra�e";           // Wird nicht verwendet
    String BEITRAGSART = "Beitragsart_1"; // Wird f�r AD-Befreiung verwendet
    String NAME = "Nachname";
    String FIRSTNAME = "Vorname";
    String EINTRITT = "Eintritt";       // Format wie Datum
    String AUSTRITT = "Austritt";       // Format wie Datum
    String LINKID = "Verkn�pfung";      // Es muss eine Mitgliedsnummer mit diesem Wert geben
    String AD_FREE_REASON = "AD-Frei.Grund";
    String AD_FREE_FROM   = "AD-Frei.von";
    String AD_FREE_UNTIL  = "AD-Frei.bis";

    String GUTHABEN_PREFIX = "Guthaben ";   // Momentan taucht in dieser �berschrift das Datum mit auf.
                                            // Besser w�ren zwei Spalten: Guthaben.Wert und Guthaben.am
                                            // Es werden nur zwei Stellen hinter dem Komma ausgewertet!
    String GUTHABEN_WERT     = "Guthaben.Wert";
    String GUTHABEN_AM       = "Guthaben.Am";
    String GUTHABEN_WERT_ALT = "Guthaben.Wert.Alt";

    // Tabelle der Arbeitsdienste:
    String HOURSWORKED = "Gel.Stunden";
    String CLEARED = "Abgerechnet.Am";
    // Tabelle der ZuZahlendenStunden:
    String HOURSTOPAY = "Zu zahl. Stunden";

}

// ############################################################################