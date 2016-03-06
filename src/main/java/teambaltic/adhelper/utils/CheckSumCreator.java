/**
 * CheckSumCreator.java
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
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;

// ############################################################################
public class CheckSumCreator
{
    public enum Type{
        MD5,
        SHA512;
    }

    private final Type m_Type;
    private Type getType(){ return m_Type; }

    public CheckSumCreator(final Type fType)
    {
        m_Type = fType;
    }

    public String process( final File fFile ) throws Exception
    {
        FileInputStream fis = null;
        String aMD5Hash = "0";
        try{
            fis = new FileInputStream( fFile );
            switch( getType() ){
                case MD5:
                    aMD5Hash = DigestUtils.md5Hex(fis);
                    break;

                case SHA512:
                    aMD5Hash = DigestUtils.sha512Hex(fis);
                    break;
                default:
                    throw new IllegalStateException( "Unbekannter Checksummentyp: "+getType());
            }
            return aMD5Hash;
        }finally{
            if( fis != null ){
                fis.close();
                write( fFile, aMD5Hash );
            }
        }
    }

    private void write( final File fFile, final String fMD5Hash )
    {
        Writer fw = null;
        try{
            fw = new FileWriter( fFile.getPath() + getExtension() );
            final Date aTimeStamp = new Date();
            fw.write( "#TimeStamp;TimeStamp(HR);FileName");
            fw.append( System.getProperty( "line.separator" ) ); // e.g. "\n"
            fw.append( String.format( "#%d;%s;%s",aTimeStamp.getTime(), aTimeStamp, fFile.getName() ) );
            fw.append( System.getProperty( "line.separator" ) ); // e.g. "\n"
            fw.append( String.format( "%s *%s",fMD5Hash, fFile.getName() ) );
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

    private String getExtension()
    {
        return "."+getType().toString().toLowerCase();
    }
}


// ############################################################################
