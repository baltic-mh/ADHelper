/**
 * ListProvider.java
 *
 * Created on 02.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw�rmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.IIdentifiedItem;

// ############################################################################
public class ListProvider<ItemType extends IIdentifiedItem> implements IListProvider<ItemType>
{
    private static final Logger sm_Log = Logger.getLogger(ListProvider.class);

    private final Map<Integer, ItemType> m_Items;

    public ListProvider()
    {
        m_Items = new HashMap<>();
    }

    @Override
    public void clear(){
        m_Items.clear();
    }

    @Override
    public void add(final ItemType fItem)
    {
        final Integer aID = fItem.getID();
        synchronized( m_Items ){
            if( m_Items.containsKey( aID )) {
                sm_Log.warn( "Item already contained! Will be ignored: "+fItem);
                return;
            }
            m_Items.put( aID, fItem );
        }
    }

    @Override
    public ItemType get( final int fID )
    {
        synchronized( m_Items ){
            return m_Items.get( fID );
        }
    }

    @Override
    public void addAll( final Collection<ItemType> fItem )
    {
        synchronized( m_Items ){
            fItem.forEach( aItem -> {
                add( aItem );
            } );
        }

    }

    @Override
    public Collection<ItemType> getAll()
    {
        synchronized( m_Items ){
            return new ArrayList<ItemType>( m_Items.values() );
        }
    }

}

// ############################################################################