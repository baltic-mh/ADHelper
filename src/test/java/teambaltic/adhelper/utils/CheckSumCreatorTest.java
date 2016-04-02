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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import teambaltic.adhelper.model.CheckSumInfo;
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
            final CheckSumInfo aCSI = aMD5Creator.calculate( FILE1 );
            assertNotNull("aCSI", aCSI);
            assertEquals("CSI-Hash", "4ddac5efbbbc3bae3961a798011cf07d", aCSI.getHash());
            assertEquals("CSI-FileName", "Anmerkungen.txt", aCSI.getFileName());
        }catch( final Exception fEx ){
            fail( fEx.getMessage() );
        }

    }

    @Test
    public void test_write()
    {
        final CheckSumCreator aMD5Creator = new CheckSumCreator(Type.MD5);
        try{
            final CheckSumInfo aCSI = aMD5Creator.calculate( FILE1 );
            final Path aFileMD5 = aMD5Creator.write( FILE1, aCSI, "UnitTest" );
            assertNotNull("FileMD5", aFileMD5);
            final List<String> aLines = FileUtils.readAllLines( aFileMD5.toFile() );
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

}

// ############################################################################
