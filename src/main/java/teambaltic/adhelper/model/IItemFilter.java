/**
 * IItemFilter.java
 *
 * Created on 08.12.2018
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2018 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.util.Collection;

// ############################################################################
public interface IItemFilter<ItemType extends IIdentifiedItem<ItemType>>
{
    boolean accept( ItemType fItem );
    Collection<ItemType> filter( Collection<ItemType> fValues );
}

// ############################################################################
