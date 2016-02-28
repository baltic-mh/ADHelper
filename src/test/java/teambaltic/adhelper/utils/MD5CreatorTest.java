/**
 * MD5CreatorTest.java
 *
 * Created on 20.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw�rmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

// ############################################################################
public class MD5CreatorTest
{

    @Test
    public void test()
    {
        final MD5Creator aMD5Creator = new MD5Creator();
        try{
            aMD5Creator.process( new File("Ausgabe.txt") );
        }catch( final Exception fEx ){
            fail( fEx.getMessage() );
        }

    }

}

// ############################################################################