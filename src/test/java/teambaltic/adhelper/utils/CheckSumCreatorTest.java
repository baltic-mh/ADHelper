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

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import teambaltic.adhelper.utils.CheckSumCreator.Type;

// ############################################################################
public class CheckSumCreatorTest
{

    @Test
    public void test_MD5()
    {
        final CheckSumCreator aMD5Creator = new CheckSumCreator(Type.MD5);
        try{
            aMD5Creator.process( new File("Ausgabe.txt") );
        }catch( final Exception fEx ){
            fail( fEx.getMessage() );
        }

    }

}

// ############################################################################
