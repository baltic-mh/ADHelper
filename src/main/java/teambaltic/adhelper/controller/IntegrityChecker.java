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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teambaltic.adhelper.controller.IPeriodDataController.EPeriodDataSelector;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.PeriodData;
import teambaltic.adhelper.model.settings.AllSettings;
import teambaltic.adhelper.model.settings.IAppSettings;
import teambaltic.adhelper.utils.FileUtils;

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

    public static void checkAfterUpdateFromServer(
            final AllSettings fAllSettings
            ) throws Exception
    {
        final IAppSettings aAppSettings = fAllSettings.getAppSettings();
        final Path aDataFolder = aAppSettings.getFolder_Data();
        final Path aFile_BaseData = aAppSettings.getFile_RootBaseData();
        if( !Files.exists( aFile_BaseData ) ){
            throw new Exception( "Benötigte Datei nicht gefunden: " + aFile_BaseData.toString() );
        }
        final PeriodDataController aIPCtrlr = new PeriodDataController( aAppSettings, null );
        aIPCtrlr.init();
        final List<PeriodData> aPeriods = aIPCtrlr.getPeriodDataList( ALL );
        if(  aPeriods.size() == 0 ){
            throw new Exception(
                    "Keine Unterverzeichnisse mit Abrechnungsdaten gefunden in: " + aDataFolder.toString() );
        }
    }

    public static void checkBaseDataFile(final File fBaseDataFile) throws Exception
    {
        final List<String>aColumnNames = FileUtils.readColumnNames( fBaseDataFile );
        assertRequiredColumnNames( fBaseDataFile, aColumnNames );

        final List<String> aProblems    = new ArrayList<>();
        final List<Integer>aSeen_IDs    = new ArrayList<>();
        final List<Integer>aSeen_RefIDs = new ArrayList<>();
        final List<String> aAllLines = FileUtils.readAllLines( fBaseDataFile, 1 );
        for( final String aSingleLine : aAllLines ){
            final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aSingleLine );
            final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
            final Integer aID = Integer.parseInt( aIDString );
            if( aSeen_IDs.contains( aID ) ){
                aProblems.add( String.format( "Die ID %d tritt mehrfach auf!", aID.intValue() ));
                continue;
            }
            aSeen_IDs.add( aID );

            final String aRefIDString = aAttributes.get( IKnownColumns.LINKID );
            if( aRefIDString != null && !"".equals( aRefIDString ) ){
                final Integer aRefID = Integer.parseInt( aRefIDString );
                aSeen_RefIDs.add( aRefID );
            }

            final String aBirthdayString = aAttributes.get( IKnownColumns.BIRTHDAY );
            if( aBirthdayString == null ){
                aProblems.add( String.format( "Kein Geburtsdatum bei ID %d!", aID.intValue() ));
            }

            final String aEintrittString = aAttributes.get( IKnownColumns.EINTRITT );
            if( aEintrittString == null ){
                aProblems.add( String.format( "Kein Eintrittsdatum bei ID %d!", aID.intValue() ));
            }

        }
        for( final Integer aRefID : aSeen_RefIDs ){
            if( !aSeen_IDs.contains( aRefID ) ){
                aProblems.add( String.format( "Für die RefID %d gibt es kein Mitglied mit dieser ID!", aRefID.intValue() ));
            }
        }
        if( aProblems.size() > 0 ){
            final StringBuffer aSB = new StringBuffer("Folgende Inkonsistenzen bestehen in der Datei der Basis-Daten: ");
            aSB.append( fBaseDataFile.toString()).append( "\n" );
            for( final String aProblem : aProblems ){
                aSB.append( "\t" ).append( aProblem ).append( "\n" );
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
