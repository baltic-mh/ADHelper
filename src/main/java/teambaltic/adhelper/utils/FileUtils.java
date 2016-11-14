/**
 * FileUtils.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

// ############################################################################
public final class FileUtils
{
    private static final DateTimeFormatter TIMEFORMAT = DateTimeFormatter.ofPattern("HH-mm-ss");

    private FileUtils(){/**/}

    public static String readFirstLine( final Path fFile )
    {
        return readFirstLine( fFile.toFile() );
    }
    public static String readFirstLine( final File fFile )
    {
        String aLine = null;
        BufferedReader in = null;
        try {
            in = new BufferedReader( new FileReader( fFile ) );
            aLine = in.readLine();
        } catch ( final IOException e ) {
            e.printStackTrace();
        } finally {
            if( in != null ) {
                try {
                    in.close();
                } catch ( final IOException e ){/**/}
            }
        }
        return aLine;
    }

    public static List<String> readAllLines( final Path fFile )
    {
        return readAllLines( fFile.toFile(), 0 );
    }
    public static List<String> readAllLines( final File fFile )
    {
        return readAllLines( fFile, 0 );
    }
    public static List<String> readAllLines( final Path fFile, final int fSkipLines )
    {
        return readAllLines(fFile.toFile(), fSkipLines);
    }
    public static List<String> readAllLines( final File fFile, final int fSkipLines )
    {
        int aLinesRead = 0;
        final List<String> aLines = new ArrayList<>();
        BufferedReader in = null;
        try {
            in = new BufferedReader( new FileReader( fFile ) );
            String aLine;
            while( ( aLine = in.readLine() ) != null ){
                aLinesRead++;
                if( aLinesRead > fSkipLines ){
                    aLines.add( aLine );
                }
            }
        } catch ( final IOException e ) {
            e.printStackTrace();
        } finally {
            if( in != null ) {
                try {
                    in.close();
                } catch ( final IOException e ){/**/}
            }
        }
        return aLines;
    }

    public static List<String> readColumnNames( final File fFile )
    {
        final String aFirstLine = FileUtils.readFirstLine( fFile );
        final String[] aColumnNames = aFirstLine.split( ";" );
        final List<String> aAsList = Arrays.asList( aColumnNames );
        return aAsList;
    }

    public static Map<String, String> makeMap(
            final List<String> fColumnNames, final String fSingleLine )
    {
        final Map<String, String> aMap = new HashMap<>();
        final String[] aSplit = fSingleLine.split( ";" );
        for( int aIdx = 0; aIdx < aSplit.length; aIdx++ ){
            final String aString = aSplit[aIdx];
            aMap.put( fColumnNames.get( aIdx ), aString );
        }
        return aMap;
    }

    public static File[] getChildFolders( final Path fParentFolder, final InvoicingPeriodFolderFilter fFilter )
    {
        final File[] aChildFolders = fParentFolder.toFile().listFiles( fFilter );
        return aChildFolders;
    }

    public static void checkFile( final File fFile ) throws Exception
    {
        if( !fFile.exists() ){
            throw new Exception("File does not exist: "+fFile.getPath());
        }
        if( !fFile.isFile() ){
            throw new Exception("File is no regular file: "+fFile.getPath());
        }
        if( !fFile.canRead() ){
            throw new Exception("Cannot read file: "+fFile.getPath());
        }
    }

    public static String getFileNameWithPostfixAppended(
            final File fFile, final String fPostfix )
    {
        final String aBaseName = FilenameUtils.getBaseName( fFile.getName() );
        final String aExt = FilenameUtils.getExtension( fFile.getName() );
        final String aNewName = String.format( "%s%s.%s", aBaseName, fPostfix, aExt );
        return aNewName;
    }

    public static Path makeBackupCopy( final Path fFile ) throws IOException
    {
        final String aFileName  = fFile.toFile().getName();
        final String aPath      = FilenameUtils.getPath( fFile.toString() );
        final String aBaseName  = FilenameUtils.getBaseName( aFileName );
        final String aExtension = FilenameUtils.getExtension( aFileName );
        final String aBackupFileName = String.format( "%s_%s_%s.%s",
                aBaseName, LocalDate.now(), TIMEFORMAT.format( LocalTime.now() ), aExtension );
        final Path aBackupFile = Paths.get( aPath, aBackupFileName );

        Files.copy( fFile, aBackupFile, StandardCopyOption.REPLACE_EXISTING );
        return aBackupFile;
    }

    public static void writeFinishedFile(
            final Path fFinishedFile, final String fInfo )
            throws IOException
    {
        Writer fw = null;
        try{
            fw = new FileWriter( fFinishedFile.toFile() );
            writeHeader( fw );
            final long aTS = System.currentTimeMillis();
            fw.append( String.format( "%d;%s;Abgeschlossen;%s", aTS, new Date(aTS), fInfo ) );
            fw.append( System.getProperty( "line.separator" ) );

        }finally{
            if( fw != null )
                try{
                    fw.close();
                }catch( final IOException e ){
                    e.printStackTrace();
                }
        }
    }

    public static void writeUploadInfo(
            final Path fUploadInfoFile, final String fInfo )
            throws IOException
    {
        Writer fw = null;
        try{
            final boolean aFileExists = Files.exists( fUploadInfoFile );
            fw = new FileWriter( fUploadInfoFile.toFile(), aFileExists );
            if( !aFileExists ){
                writeHeader( fw );
            }
            final long aTS = System.currentTimeMillis();
            fw.append( String.format( "%d;%s;Hochgeladen;%s", aTS, new Date(aTS), fInfo ) );
            fw.append( System.getProperty( "line.separator" ) );

        }finally{
            if( fw != null )
                try{
                    fw.close();
                }catch( final IOException e ){
                    e.printStackTrace();
                }
        }
    }

    private static void writeHeader( final Writer fw ) throws IOException
    {
        fw.write( "#TimeStamp;TimeStamp(HR);Aktion;Info");
        fw.append( System.getProperty( "line.separator" ) );
    }

    public static void copyFileToFolder( final Path fFile, final Path fOutputFolder ) throws IOException
    {
        copyFileToFolder( fFile.toFile(), fOutputFolder );
    }

    public static Path copyFileToFolder( final File fFile, final Path fOutputFolder ) throws IOException
    {
        return copyFileToFolder( fFile, fOutputFolder, fFile.getName());
    }

    public static Path copyFileToFolder(
            final File fFile, final Path fTargetFolder, final String fTargetName ) throws IOException
    {
        final Path aTargetPath = Paths.get( fTargetFolder.toString(), fTargetName );
        return Files.copy( fFile.toPath(), aTargetPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES );
    }

    public static void rename( final Path fFrom, final Path fTo) throws IOException
    {
        Files.move( fFrom, fTo );
    }
}

// ############################################################################
