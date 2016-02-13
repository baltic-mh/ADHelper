/**
 * UIUtils.java
 *
 * Created on 13.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;

import teambaltic.adhelper.model.IClubMember;

// ############################################################################
public final class UIUtils
{
    private UIUtils(){/**/}

    public static void setItemStartsWithSelector( final JComboBox<IClubMember> fCB_Members )
    {
        fCB_Members.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased( final KeyEvent e )
            {
                if( e.getKeyCode() != 38 && e.getKeyCode() != 40 && e.getKeyCode() != 10 ){
                    final int aItemCount = fCB_Members.getItemCount();
                    final String a = fCB_Members.getEditor().getItem().toString();
                    fCB_Members.removeAllItems();
                    int st = 0;

                    for( int i = 0; i < aItemCount; i++ ){
                        final IClubMember aItem = fCB_Members.getItemAt( i );
                        if( aItem.toString().startsWith( a ) ){
                            fCB_Members.addItem( aItem );
                            st++;
                        }
                    }
                    fCB_Members.getEditor().setItem( new String( a ) );
                    fCB_Members.hidePopup();
                    if( st != 0 ){
                        fCB_Members.showPopup();
                    }
                }
            } } );
    }


}

// ############################################################################
