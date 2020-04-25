/**
 * CompareUtils.java
 *
 * Created on 25.04.2020
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2020 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import teambaltic.adhelper.model.Halfyear;

// ############################################################################
public final class CompareUtils
{
    private static Comparator<String> Comparator_PeriodFolderName = createComparator_PeriodFolderName();

    private CompareUtils() {/**/}

    public static Comparator<String> createComparator_PeriodFolderName(){
        return new Comparator<String>() {
            @Override
            public int compare( final String fFileName1, final String fFileName2 ) {
                final Halfyear aHY1 = createHalfyear(fFileName1);
                final Halfyear aHY2 = createHalfyear(fFileName2);
                return aHY2.isBeforeMyStart(aHY1.getEnd()) ? 1 : -1;
            }

            private Halfyear createHalfyear( final String fFileName1 ) {
                final Matcher matcher1 = Pattern.compile( InvoicingPeriodFolderFilter.REGEX_MATCH ).matcher( fFileName1 );
                if(  matcher1.find() ) {
                    final String aGroup = matcher1.group();
                    return Halfyear.create(aGroup);
                }
                return null;
            }
        };

    }

    /**
     * Sortiert die übergebene Liste der Abrechnungszeiträume in absteigender Reihenfolge
     * @param fPeriodFolderList
     */
    public static void sortPeriodFolderList( final List<String> fPeriodFolderList) {
        Collections.sort( fPeriodFolderList, Comparator_PeriodFolderName );
    }

}

// ############################################################################
