/**
 * CheckSumCreatorTest.java
 *
 * Created on 20.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import teambaltic.adhelper.utils.CheckSumCreator.Type;

// ############################################################################
public class CheckSumCreatorTest
{
    private final static Path FILE1 = Paths.get("misc", "TestResources", "CheckSumCreator", "Anmerkungen.txt");

    @Test
    public void test_MD5()
    {
        final CheckSumCreator aMD5Creator = new CheckSumCreator(Type.MD5);
        try{
            final Path aFileMD5 = aMD5Creator.process( FILE1 );
            assertNotNull("FileMD5", aFileMD5);
            final List<String> aLines = readLines( aFileMD5.toFile() );
            assertEquals("FileMD5-Lines", 3, aLines.size());
            final String aLine2 = aLines.get( 2 );
            final String[] aParts = aLine2.split( " " );
            assertEquals("MD5", "4ddac5efbbbc3bae3961a798011cf07d", aParts[0]);
            assertEquals("MD5-FileName", "*Anmerkungen.txt", aParts[1]);
            Files.delete( aFileMD5 );
        }catch( final Exception fEx ){
            fail( fEx.getMessage() );
        }

    }

    private static List<String> readLines( final File fFile)
    {
        final List<String> aLines = new ArrayList<>();
        BufferedReader in = null;
        try{
            in = new BufferedReader(new FileReader(fFile));

            String line = null;
            while ((line = in.readLine()) != null) {
                aLines.add( line );
            }
        }catch( final IOException fEx ){
            fail("Exception: "+fEx.getMessage() );
        } finally {
            if( in != null){
                try { in.close(); } catch ( final Exception e ) {/**/}
            }
        }
        return aLines;
    }
}

// ############################################################################
