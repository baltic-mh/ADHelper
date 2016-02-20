/**
 * IntegrityChecker.java
 *
 * Created on 20.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.io.File;

// ############################################################################
public class IntegrityChecker
{
    public IntegrityChecker()
    {

    }

    public void check() throws Exception
    {

    }

    private static String assertExistenceOfDataFiles( final File fBaseInfoFile, final File fWorkEventFile )
    {
        String aMsg = "Folgende Dateien existieren nicht: \n\t";
        final boolean aExists_BIF = fBaseInfoFile.exists();
        if( !aExists_BIF ){
            aMsg += fBaseInfoFile.getAbsolutePath();
        }
        final boolean aExists_WEF = fWorkEventFile.exists();
        if( !aExists_WEF ){
            if( !aExists_BIF ){
                aMsg += ",";
            }
            aMsg += "\n\t"+fWorkEventFile.getAbsolutePath();
        }

        return aExists_BIF && aExists_WEF ? null : aMsg;
    }

}

// ############################################################################
