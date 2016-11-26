/**
 * IUISettings.java
 *
 * Created on 26.11.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model.settings;

import teambaltic.adhelper.model.EPropType;
import teambaltic.adhelper.model.IKey;

// ############################################################################
public interface IUISettings extends ISettings<IUISettings.EKey>
{
    public enum EKey implements IKey {
        MAINFRAME_WIDTH
       ,MAINFRAME_HEIGHT
       ,MAINFRAME_POSX
       ,MAINFRAME_POSY
       ;
        // --------------------------------------------------------------------
        private final EPropType m_PropType;
        @Override
        public EPropType getPropType(){ return m_PropType; }
        // --------------------------------------------------------------------

        private EKey()
        {
            this(EPropType.INTVALUE);
        }

        private EKey(final EPropType fPropType)
        {
            m_PropType = fPropType;
        }

    }

    int  getMainFrame_Width();
    void setMainFrame_Width(int fValue);
    int  getMainFrame_Height();
    void setMainFrame_Height(int fValue);
    int  getMainFrame_PosX();
    void setMainFrame_PosX(int fValue);
    int  getMainFrame_PosY();
    void setMainFrame_PosY(int fValue);

}

// ############################################################################
