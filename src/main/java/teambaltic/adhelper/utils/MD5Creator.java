/**
 * MD5Creator.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.codec.digest.DigestUtils;

// ############################################################################
public class MD5Creator
{
    public MD5Creator()
    {

    }

    public String process( final File fFile ) throws Exception
    {
        final FileInputStream fis = new FileInputStream( fFile );
        final String aMD5Hash = DigestUtils.md5Hex(fis);
        fis.close();
        write( fFile, aMD5Hash );
        return aMD5Hash;
    }

    private static void write( final File fFile, final String fMD5Hash )
    {
        Writer fw = null;
        try{
            fw = new FileWriter( fFile.getPath() + ".md5" );
            fw.write( fMD5Hash );
            fw.append( System.getProperty( "line.separator" ) ); // e.g. "\n"
        }catch( final IOException e ){
            System.err.println( "Konnte Datei nicht erstellen" );
        }finally{
            if( fw != null )
                try{
                    fw.close();
                }catch( final IOException e ){
                    e.printStackTrace();
                }
        }
    }
}


// ############################################################################
