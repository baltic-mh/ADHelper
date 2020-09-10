/**
 * ZipUtils.java
 *
 * Created on 29.03.2016
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

// ############################################################################
public class ZipUtils
{
    private static final Logger sm_Log = Logger.getLogger(ZipUtils.class);

    private static final int BUFFERSIZE = 1024;

    public static Path zip( final Path fItemToZip ) throws Exception
    {
        FileOutputStream fos = null;
        ZipOutputStream  zos = null;
        try {
            final String aZipFileName = fItemToZip.toString() + ".zip";
            fos = new FileOutputStream(aZipFileName);
            zos = new ZipOutputStream(fos);

            final FileTreeWalker aTreeWalker = new FileTreeWalker(fItemToZip, zos);
            Files.walkFileTree( fItemToZip, aTreeWalker );

            return Paths.get( aZipFileName );
        } finally {
            if( zos != null ){
                zos.close();
            }
            if( fos != null ){
                fos.close();
            }
        }
    }

    public static void unzip(final Path fFileToUnzip) throws Exception
    {
        final Path aRoot = fFileToUnzip.getRoot();
        String aPath = FilenameUtils.getPath( fFileToUnzip.toString() );
        if( aRoot != null ) {
            aPath = aRoot+aPath;
        }
        final String aBaseName = FilenameUtils.getBaseName( fFileToUnzip.getFileName().toString() );
        final String aRootFolder = Paths.get(aPath, aBaseName).toString();
        ZipFile zipFile = null;
        try{
            zipFile = new ZipFile( fFileToUnzip.toString() );
            final Enumeration<?> enu = zipFile.entries();
            while( enu.hasMoreElements() ){
                final ZipEntry zipEntry = (ZipEntry) enu.nextElement();

                final String name = zipEntry.getName();
                final long size = zipEntry.getSize();
                final long compressedSize = zipEntry.getCompressedSize();
                if( sm_Log.isDebugEnabled() ){
                    sm_Log.debug( String.format("name: %-20s | size: %6d | compressed size: %6d\n", name, size, compressedSize ));
                }

                final File file = new File( aRootFolder, name );
                if( name.endsWith( "/" ) ){
                    file.mkdirs();
                    continue;
                }

                final File parent = file.getParentFile();
                if( parent != null ){
                    parent.mkdirs();
                }

                final InputStream is = zipFile.getInputStream( zipEntry );
                final FileOutputStream fos = new FileOutputStream( file );
                final byte[] bytes = new byte[BUFFERSIZE];
                int length;
                while( ( length = is.read( bytes ) ) >= 0 ){
                    fos.write( bytes, 0, length );
                }
                is.close();
                fos.close();
                final FileTime aLastModifiedTime = zipEntry.getLastModifiedTime();
                if( aLastModifiedTime != null ){
                    file.setLastModified( aLastModifiedTime.toMillis() );
                }
            }
        }finally{
            zipFile.close();
        }
    }

    private static class FileTreeWalker extends SimpleFileVisitor<Path>
    {
        private final ZipOutputStream  zos;
        private final Path directoryToZip;

        public FileTreeWalker(final Path fDirectoryToZip, final ZipOutputStream fZos)
        {
            super();
            directoryToZip = fDirectoryToZip;
            zos = fZos;
        }

        @Override
        public FileVisitResult visitFile( final Path fFile, final BasicFileAttributes fAttrs ) throws IOException
        {
            FileInputStream fis = null;
            final File file = fFile.toFile();
            try{
                // we want the zipEntry's path to be a relative path that is relative
                // to the directory being zipped, so chop off the rest of the path
                final String zipFilePath = directoryToZip.relativize(fFile).toString();
                final ZipEntry zipEntry = new ZipEntry(zipFilePath);
                zos.putNextEntry(zipEntry);

                final byte[] bytes = new byte[BUFFERSIZE];
                int length;
                fis = new FileInputStream(file);
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
            }finally{
                zos.closeEntry();
                if( fis != null ){
                    fis.close();
                }
            }
            return FileVisitResult.CONTINUE;
        }

    }

}

// ############################################################################
