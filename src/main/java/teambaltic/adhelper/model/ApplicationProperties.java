/**
 * ApplicationProperties.java
 *
 * Created on 14.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

// ############################################################################
public enum ApplicationProperties
{
    INSTANCE;

    // ------------------------------------------------------------------------
    private final String m_DataFolder = "Daten";
    public String getDataFolderName(){ return m_DataFolder; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_FileName_BaseInfo = "Mitglieder.csv";
    public String getFileName_BaseInfo(){ return m_FileName_BaseInfo; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_FileName_WorkEvents = "Arbeitsdienste.csv";
    public String getFileName_WorkEvents(){return m_FileName_WorkEvents; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_FileName_Balances = "Guthaben.csv";
    public String getFileName_Balances(){return m_FileName_Balances; }
    // ------------------------------------------------------------------------

}

// ############################################################################
