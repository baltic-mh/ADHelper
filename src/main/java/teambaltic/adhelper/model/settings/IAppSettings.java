/**
 * IAppSettings.java
 *
 * Created on 04.03.2016
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
public interface IAppSettings extends ISettings<IAppSettings.EKey>
{
    public enum EKey implements IKey {
        FOLDERNAME_DATA
       ,FOLDERNAME_SETTINGS
       ,FILENAME_BASEINFO
       ,FILENAME_WORKEVENTS
       ,FILENAME_BALANCES
       ,FILENAME_USERDATA
       ,FILENAME_CLUBDATA
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

    String getFolderName_Data();
    String getFolderName_Settings();

    String getFileName_BaseInfo();
    String getFileName_WorkEvents();
    String getFileName_Balances();
    String getFileName_UserData();
    String getFileName_ClubData();

}

// ############################################################################

