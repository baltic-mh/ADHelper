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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;

import teambaltic.adhelper.model.CheckSumInfo;

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

    public CheckSumInfo calculate( final Path fFile ) throws Exception
    {
        if( !Files.exists( fFile ) ){
            return null;
        }
        FileInputStream fis = null;
        String aHash = "0";
        final File aFile = fFile.toFile();
        try{
            fis = new FileInputStream( aFile );
            switch( getType() ){
                case MD5:
                    aHash = DigestUtils.md5Hex(fis);
                    break;

                case SHA512:
                    aHash = DigestUtils.sha512Hex(fis);
                    break;
                default:
                    throw new IllegalStateException( "Unbekannter Checksummentyp: "+getType());
            }
            final CheckSumInfo aCSI = new CheckSumInfo(System.currentTimeMillis(), aHash, aFile.getName());
            return aCSI;
        }finally{
            if( fis != null ){
                fis.close();
            }
        }
    }

    public Path getCheckSumFile( final Path fFile )
    {
        final Path aOutPath = Paths.get( fFile.toString() + getExtension() );
        return aOutPath;
    }

    public Path write( final Path fFile, final CheckSumInfo fCSI, final String fInfo ) throws Exception
    {
        final Path aOutPath = getCheckSumFile( fFile );
        final File aFile = aOutPath.toFile();
        Writer fw = null;
        try{
            fw = new FileWriter( aFile );
            final long aTS = fCSI.getTimeStamp();
            fw.write( "#TimeStamp;TimeStamp(HR);FileName;Info");
            fw.append( System.getProperty( "line.separator" ) ); // e.g. "\n"
            fw.append( String.format( "#%d;%s;%s;%s", aTS, new Date(aTS), fCSI.getFileName(), fInfo ) );
            fw.append( System.getProperty( "line.separator" ) ); // e.g. "\n"
            fw.append( String.format( "%s *%s",fCSI.getHash(), fCSI.getFileName() ) );
            fw.append( System.getProperty( "line.separator" ) ); // e.g. "\n"

            return aOutPath;
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
