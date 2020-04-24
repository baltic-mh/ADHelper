/**
 * ISettings.java
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

import java.io.IOException;

import teambaltic.adhelper.model.IKey;
import teambaltic.adhelper.model.IPeriod;

// ############################################################################
public interface ISettings<KeyType extends IKey>
{
    int getIntValue(KeyType fKey);
    void setIntValue( KeyType fKey, int fNewVal );

    int getHourValue( KeyType fKey );
    int getHourValue(KeyType fKey, IPeriod fPeriod);
    void setHourValue( KeyType fKey, int fNewVal );

    String getStringValue( KeyType fKey );
    void setStringValue( KeyType fKey, String fNewVal );

    void writeToFile() throws IOException;
}

// ############################################################################
