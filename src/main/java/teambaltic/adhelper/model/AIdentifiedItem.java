/**
 * AIdentifiedItem.java
 *
 * Created on 10.09.2020
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2020 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

// ############################################################################
public abstract class AIdentifiedItem<ItemType> implements IIdentifiedItem<ItemType>
{
    // ------------------------------------------------------------------------
    private final int m_ID;
    @Override
    public int getID() { return m_ID; }
    public int getMemberID() { return getID(); }
    // ------------------------------------------------------------------------

    public AIdentifiedItem(final int fID)
    {
        m_ID = fID;
    }
}

// ############################################################################
