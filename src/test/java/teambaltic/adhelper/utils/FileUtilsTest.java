/**
 * FileUtilsTest.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

// ############################################################################
public class FileUtilsTest
{

    @Test
    public void test_determineNewestInvoicingPeriodFolder()
    {
        try{
            final Path aTempDirectory = Files.createTempDirectory("DataDir");
            Files.createDirectory( Paths.get(aTempDirectory.toString(), "2011-01-01 - 2011-06-30") );
            Files.createDirectory( Paths.get(aTempDirectory.toString(), "2011-07-01 - 2011-12-31") );
            Files.createDirectory( Paths.get(aTempDirectory.toString(), "2031-01-01 - 2031-06-30") );

            final File aResult = FileUtils.determineNewestInvoicingPeriodFolder( aTempDirectory.toFile() );
            assertEquals("Neuestes Verzeichnis", "2031-01-01 - 2031-06-30", aResult.getName());
        }catch( final IOException fEx ){
            fail( "Exception: "+fEx.getMessage() );
        }
    }

}

// ############################################################################
