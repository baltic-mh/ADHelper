/**
 * ColumnNamesMapping.java
 *
 * Created on 06.05.2020
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2020 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public enum ColumnNamesMapping {
    INSTANCE;

    private static final Logger sm_Log = Logger.getLogger(ColumnNamesMapping.class);

    private final Properties m_Mappings;

    private ColumnNamesMapping()
    {
        m_Mappings = readMappings();
    }

    public List<String> map(final List<String> fInStrings){
        final List<String> aOutStrings = new ArrayList<>();
        fInStrings.forEach( it -> aOutStrings.add( m_Mappings.getProperty(it, it) ));
        return aOutStrings;
    }

    private static Properties readMappings() {
        final Properties aMappings = new Properties();

        final IAppSettings aAppSettings = AllSettings.INSTANCE.getAppSettings();
        final String aFileName_ColumnNamesMapping = aAppSettings.getFileName_ColumnNamesMapping();
        final InputStream aResourceStream = FileUtils.getResourceAsStream( aFileName_ColumnNamesMapping );

        try {
            aMappings.load(aResourceStream);
        } catch ( final IOException fEx ) {
            sm_Log.error(String.format( "Unexpected exception: %s - %s ", fEx.getClass().getSimpleName() , fEx.getMessage() ));
        }
        return aMappings;
    }
}

// ############################################################################
