/**
 * IntegrityChecker.java
 *
 * Created on 20.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public final class IntegrityChecker
{
    private IntegrityChecker(){/**/}

    public static void check(
            final AllSettings fAllSettings
            ) throws Exception
    {
        final IAppSettings aAppSettings = fAllSettings.getAppSettings();
        final Path aDataFolder = aAppSettings.getFolder_Data();
        if( !Files.exists( aDataFolder )){
            throw new Exception( "Benötigtes Verzeichnis nicht gefunden: "+aDataFolder.toString() );
        }
//        final Path aFile_BaseData = aAppSettings.getFile_BaseData();
//        if( !Files.exists( aFile_BaseData )){
//            throw new Exception( "Benötigte Datei nicht gefunden: "+aFile_BaseData.toString() );
//        }
        final File[] aInvoicingPeriodFolders = FileUtils.getInvoicingPeriodFolders( aDataFolder.toFile() );
        if( aInvoicingPeriodFolders == null || aInvoicingPeriodFolders.length == 0 ){
            throw new Exception( "Keine Unterverzeichnisse mit Abrechnungsdaten gefunden in: "+aDataFolder.toString() );
        }
    }


}

// ############################################################################
