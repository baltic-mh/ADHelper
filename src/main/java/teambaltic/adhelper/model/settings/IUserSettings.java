/**
 * IUserSettings.java
 *
 * Created on 03.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model.settings;

import teambaltic.adhelper.model.EPropType;
import teambaltic.adhelper.model.ERole;
import teambaltic.adhelper.model.IKey;

// ############################################################################
public interface IUserSettings extends ISettings<IUserSettings.EKey>
{
    public enum EKey implements IKey {
        NAME
       ,EMAIL
       ,ROLE
       ;

       // --------------------------------------------------------------------
       private final EPropType m_PropType;
       @Override
       public EPropType getPropType(){ return m_PropType; }
       // --------------------------------------------------------------------

       private EKey()
       {
           this(EPropType.STRINGVALUE);
       }

       private EKey(final EPropType fPropType)
       {
           m_PropType = fPropType;
       }
    }

    String getName();
    String getEMail();
    String getDecoratedEMail();
    ERole getRole();
    boolean isBauausschuss();

}

// ############################################################################
