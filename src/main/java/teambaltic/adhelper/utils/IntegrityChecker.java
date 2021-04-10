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
package teambaltic.adhelper.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import teambaltic.adhelper.controller.IPeriodDataController.EPeriodDataSelector;
import teambaltic.adhelper.controller.PeriodDataController;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings;

// ############################################################################
public final class IntegrityChecker
{
    private static final EPeriodDataSelector ALL = EPeriodDataSelector.ALL;

    private static final List<String> REQUIREDCOLUMNNAMES = new ArrayList<>();
    static{
        REQUIREDCOLUMNNAMES.add( IKnownColumns.AD_FREE_FROM );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.AD_FREE_REASON );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.AD_FREE_UNTIL );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.AUSTRITT );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.BEITRAGSART );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.BIRTHDAY );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.EINTRITT );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.FIRSTNAME );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.LINKID );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.MEMBERID );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.NAME );
    }

    private IntegrityChecker(){/**/}

    public static void check(
            final AllSettings fAllSettings
            ) throws Exception
    {
        final IAppSettings aAppSettings = fAllSettings.getAppSettings();
        final Path aDataFolder = aAppSettings.getFolder_Data();
        if( !Files.exists( aDataFolder )){
            Files.createDirectories( aDataFolder );
        }
        final Path aSettingsFolder = aAppSettings.getFolder_Settings();
        if( !Files.exists( aSettingsFolder )){
            Files.createDirectories( aSettingsFolder );
        }
    }

    public static PeriodDataController checkAfterUpdateFromServer(
            final AllSettings fAllSettings
            ) throws Exception
    {
        final IAppSettings aAppSettings = fAllSettings.getAppSettings();
        final Path aDataFolder = aAppSettings.getFolder_Data();
        final Path aFile_BaseData = aAppSettings.getFile_RootBaseData();
        if( !Files.exists( aFile_BaseData ) ){
            throw new Exception( "Benötigte Datei nicht gefunden: " + aFile_BaseData.toString() );
        }
        final PeriodDataController aIPCtrlr = new PeriodDataController( aAppSettings, fAllSettings.getUserSettings() );
        aIPCtrlr.init();
        final List<PeriodData> aPeriods = aIPCtrlr.getPeriodDataList( ALL );
        if(  aPeriods.size() == 0 ){
            throw new Exception(
                    "Keine Unterverzeichnisse mit Abrechnungsdaten gefunden in: " + aDataFolder.toString() );
        }
        return aIPCtrlr;
    }

    public static void checkBaseDataFile(final File fBaseDataFile) throws Exception
    {
        final List<String>aColumnNames = FileUtils.readColumnNames( fBaseDataFile );
        assertRequiredColumnNames( fBaseDataFile, aColumnNames );

        final Map<Integer, String> aProblems    = new LinkedHashMap<>();
        final List<Integer>aSeen_IDs            = new ArrayList<>();
        final Map<Integer, Integer>aSeen_RefIDs = new LinkedHashMap<>();
        final List<String> aAllLines = FileUtils.readAllLines( fBaseDataFile, 1 );
        for( final String aSingleLine : aAllLines ){
            final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aSingleLine );
            final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
            final Integer aID = Integer.parseInt( aIDString );
            if( aSeen_IDs.contains( aID ) ){
                aProblems.put( aID, String.format( "Die ID %d tritt mehrfach auf!", aID.intValue() ));
                continue;
            }
            aSeen_IDs.add( aID );

            final String aRefIDString = aAttributes.get( IKnownColumns.LINKID );
            if( aRefIDString != null && !"".equals( aRefIDString ) ){
                try {
                    final Integer aRefID = Integer.parseInt( aRefIDString );
                    // "Neuerdings" gibt es einige Einträge, bei denen lauter Nullen
                    // in der Spalte "LINKID" stehen! Das wird als "nicht vorhanden" behandelt!
                    if( aRefID != 0 ){
                        if( aID.equals( aRefID ) ) {
                            aProblems.put( aID, "ID und RefID sind identisch!");
                        }
                        aSeen_RefIDs.put( aID, aRefID );
                    }
                } catch ( final NumberFormatException fEx ) {
                    // Leider lässt das Vereinsprogramm es zu, dass man für die RefID
                    // beliebigen Text eintragen kann :-|
                    aProblems.put( aID, String.format("RefID (%s) ist keine Zahl!", aRefIDString ) );

                }
            }

            final String aBirthdayString = aAttributes.get( IKnownColumns.BIRTHDAY );
            if( aBirthdayString == null ){
                aProblems.put( aID, "Kein Geburtsdatum");
            }

            final String aEintrittString = aAttributes.get( IKnownColumns.EINTRITT );
            if( aEintrittString == null ){
                aProblems.put( aID, "Kein Eintrittsdatum!" );
            }

        }
        for( final Entry<Integer, Integer> aEntry : aSeen_RefIDs.entrySet() ){
            final Integer aRefID = aEntry.getValue();
            if( !aSeen_IDs.contains( aRefID ) ){
                aProblems.put( aEntry.getKey(), String.format( "Für die RefID %d gibt es kein Mitglied mit dieser ID!", aRefID.intValue() ));
            }
        }
        if( aProblems.size() > 0 ){
            final StringBuffer aSB = new StringBuffer("Folgende Inkonsistenzen bestehen in der Datei der Basis-Daten: ");
            aSB.append( fBaseDataFile.toString()).append( "\n" );
            for( final Entry<Integer, String> aEntry : aProblems.entrySet() ){
                final Integer aID = aEntry.getKey();
                final String aProblem = aEntry.getValue();
                aSB.append( "\tID " ).append(aID).append(": ").append( aProblem ).append( "\n" );
            }
            throw new Exception( aSB.toString() );
        }

    }

    private static void assertRequiredColumnNames( final File fBaseDataFile, final List<String> fColumnNames ) throws Exception
    {
        final List<String> aMissingColnames = new ArrayList<>();
        for( final String aReqColName : REQUIREDCOLUMNNAMES ){
            if( !fColumnNames.contains( aReqColName ) ){
                aMissingColnames.add( aReqColName );
            }
        }
        if( aMissingColnames.size() > 0 ){
            final StringBuffer aSB = new StringBuffer("Die folgenden Spalten fehlen in der Datei der Basis-Daten: ");
            aSB.append( fBaseDataFile.getPath()).append( "\n" );
            for( final String aMissingColName : aMissingColnames ){
                aSB.append( "\t" ).append( aMissingColName ).append( "\n" );
            }
            throw new Exception( aSB.toString() );
        }
    }
}

// ############################################################################
