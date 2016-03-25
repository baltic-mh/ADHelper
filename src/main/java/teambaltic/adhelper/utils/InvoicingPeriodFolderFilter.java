/**
 * InvoicingPeriodFolderFilter.java
 *
 * Created on 16.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.io.File;
import java.io.FilenameFilter;

// ############################################################################
public class InvoicingPeriodFolderFilter implements FilenameFilter
{

    public static final String sm_SplitRegex = "\\s*-\\s*";
    public static final String sm_MatchRegex = "\\d{4}-\\d{2}-\\d{2} - \\d{4}-\\d{2}-\\d{2}";

    @Override
    public boolean accept( final File fDir, final String fName )
    {
        if( !fDir.isDirectory() ){
            return false;
        }
        return fName.matches( sm_MatchRegex );
//        final String[] aParts = fName.split( sm_SplitRegex );
//        return aParts.length == 6;
    }

}

// ############################################################################
