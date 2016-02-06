/**
 * IMemberListProvider.java
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

import java.util.Collection;

import teambaltic.adhelper.model.IIdentifiedItem;

// ############################################################################
public interface IListProvider<ItemType extends IIdentifiedItem>
{
    void add( ItemType fItem );
    ItemType get( int fID );
    Collection<ItemType> getAll();
    void addAll( Collection<ItemType> fItems );
    void clear();
}

// ############################################################################
