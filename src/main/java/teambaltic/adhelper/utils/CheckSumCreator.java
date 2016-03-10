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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

// ############################################################################
public class CheckSumCreator
{
    public enum Type{
        MD5,
        SHA512;
    }

    // ------------------------------------------------------------------------
    private final Type m_Type;
    private Type getType(){ return m_Type; }
    // ------------------------------------------------------------------------

    public CheckSumCreator(final Type fType)
    {
        m_Type = fType;
    }

    public Path process( final Path fFile ) throws Exception
    {
        FileInputStream fis = null;
        String aMD5Hash = "0";
        final File aFile = fFile.toFile();
        try{
            fis = new FileInputStream( aFile );
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
            final Path aOutPath = Paths.get( aFile.getPath() + getExtension() );
            write( aOutPath, aMD5Hash );
            return aOutPath;
        }finally{
            if( fis != null ){
                fis.close();
            }
        }
    }

    private static void write( final Path fOutPath, final String fMD5Hash ) throws Exception
    {
        Writer fw = null;
        try{
            final File aFile = fOutPath.toFile();
            final String aBaseName = FilenameUtils.getBaseName(aFile.getName());
            fw = new FileWriter( aFile );
            final Date aTimeStamp = new Date();
            fw.write( "#TimeStamp;TimeStamp(HR);FileName");
            fw.append( System.getProperty( "line.separator" ) ); // e.g. "\n"
            fw.append( String.format( "#%d;%s;%s",aTimeStamp.getTime(), aTimeStamp, aBaseName ) );
            fw.append( System.getProperty( "line.separator" ) ); // e.g. "\n"
            fw.append( String.format( "%s *%s",fMD5Hash, aBaseName ) );
            fw.append( System.getProperty( "line.separator" ) ); // e.g. "\n"
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
