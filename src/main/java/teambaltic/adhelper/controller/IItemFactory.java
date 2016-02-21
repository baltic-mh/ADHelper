/**
 * IItemFactory.java
 *
 * Created on 02.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.util.Map;

import teambaltic.adhelper.model.IIdentifiedItem;

// ############################################################################
public interface IItemFactory<ItemType extends IIdentifiedItem>
{
    ItemType createItem( int fID, final Map<String, String> fAttributes);
}

// ############################################################################
