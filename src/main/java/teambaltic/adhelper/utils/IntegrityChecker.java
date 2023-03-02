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
import java.util.HashMap;
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
import teambaltic.adhelper.utils.DifferingLine.EDiffType;

// ############################################################################
public final class IntegrityChecker
{
    private static final EPeriodDataSelector ALL = EPeriodDataSelector.ALL;

    public static final List<String> REQUIREDCOLUMNNAMES = new ArrayList<>();
    static{
        // Die Reihenfolge muss so sein, weil sie im FileDiffPanel so benötigt wird!
        REQUIREDCOLUMNNAMES.add( IKnownColumns.MEMBERID );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.LINKID );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.FIRSTNAME );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.NAME );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.BIRTHDAY );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.EINTRITT );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.AUSTRITT );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.AD_FREE_FROM );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.BEITRAGSART );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.AD_FREE_UNTIL );
        REQUIREDCOLUMNNAMES.add( IKnownColumns.AD_FREE_REASON );
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

    public static FileComparisonResult compare( final File fRef, final File fNew ) throws Exception
    {

        final FileComparisonResult aResult = new FileComparisonResult( fRef, fNew, REQUIREDCOLUMNNAMES );

        final List<String> aColumnNames_Ref = FileUtils.readColumnNames( fRef );
        final List<String> aColumnNames_New = FileUtils.readColumnNames( fNew );

        try {
            final List<String> aAllLines_Ref = FileUtils.readAllLines( fRef, 1 );
            final Map<Integer, Integer> aLineIdxByMemberID_Ref = mapLineIdxIDToMemberAndCleanAllLines(
                    aColumnNames_Ref, REQUIREDCOLUMNNAMES, aAllLines_Ref);

            final List<String> aAllLines_New = FileUtils.readAllLines( fNew, 1 );
            final Map<Integer, Integer> aLineIdxByMemberID_New = mapLineIdxIDToMemberAndCleanAllLines(
                    aColumnNames_New, REQUIREDCOLUMNNAMES, aAllLines_New);

            final List<String> aLinesNotProcessed = new ArrayList<>(aAllLines_New);
            for ( final Integer aMemberID : aLineIdxByMemberID_Ref.keySet() ) {
                final Integer aLineIdx_Ref = aLineIdxByMemberID_Ref.get(aMemberID);
                final String aLine_Ref = aAllLines_Ref.get( aLineIdx_Ref );
                final Integer aLineIdx_New = aLineIdxByMemberID_New.get(aMemberID);
                if( aLineIdx_New == null ) {
                    // Nicht enthalten in New:
                    final DifferingLine aDiff = new DifferingLine( new LineInfo(aLineIdx_Ref+1, aLine_Ref), null, EDiffType.DELETED);
                    aResult.add( aDiff );
                    continue;
                }
                final String aLine_New = aAllLines_New.get( aLineIdx_New );
                if( aLine_Ref.equals(aLine_New) ) {
                    // Beide Zeilen identisch
                    aLinesNotProcessed.remove( aLine_New );
                    continue;
                }
                // Zeilen unterschiedlich:
                final DifferingLine aDiff = new DifferingLine( new LineInfo(aLineIdx_Ref+1, aLine_Ref), new LineInfo(aLineIdx_New+1, aLine_New), EDiffType.MODIFIED);
                aResult.add( aDiff );
                aLinesNotProcessed.remove( aLine_New );
            }
            // Jetzt sind in aLinesNotProcessed nur noch Zeilen, die in AllLines_Ref nicht enthalten waren:
            final Map<Integer, Integer> aLineIdxByMemberID_NotProcessed = mapLineIdxIDToMemberAndCleanAllLines(
                    aColumnNames_New, REQUIREDCOLUMNNAMES, aLinesNotProcessed);
            for ( final Integer aMemberID : aLineIdxByMemberID_NotProcessed.keySet() ) {
                final Integer aLineIdx_New = aLineIdxByMemberID_New.get(aMemberID);
                final String aLine_New = aAllLines_New.get( aLineIdx_New );
                final DifferingLine aDiff = new DifferingLine( null, new LineInfo(aLineIdx_New+1, aLine_New), EDiffType.ADDED);
                aResult.add( aDiff );
            }
        } catch ( final Exception fEx ) {
            throw fEx;
        }

        return aResult;
    }

    static Map<Integer, Integer> mapLineIdxIDToMemberAndCleanAllLines(
            final List<String> fColumnNames,
            final List<String> fRequiredColumnNames,
            final List<String> fAllLines )
    {
        final List<String> aCleanLines = new ArrayList<>();
        final Map<Integer, Integer> aLineIdxByMemberID = new HashMap<>();
        int aLineIdx = 0;
        for( final String aSingleLine : fAllLines ){
            final Map<String, String> aAttributes = FileUtils.makeMap( fColumnNames, aSingleLine );
            final Map<String, String> aCleanAttributes = new HashMap<>();
            final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
            final Integer aID = Integer.parseInt( aIDString );
            aLineIdxByMemberID.put( aID, Integer.valueOf(aLineIdx) );
            for( final String aColumnName: fRequiredColumnNames ) {
                String v = aAttributes.get( aColumnName );
                if( v.matches("00.00.0000")) {
                    v = null;
                } else  if( v.matches("^[0]+$")) {
                    v = null;
                } else  if( v.matches("31.12.2099")) {
                    v = null;
                }
                if( IKnownColumns.LINKID.equals( aColumnName ) ) {
                    try {
                        final Integer aLinkID = Integer.parseInt( v );
                        if( aLinkID != 0 ) {
                            v = String.valueOf(aLinkID);
                        }
                    } catch ( final NumberFormatException fEx ) {
                        // Keine valide Zahl - wird ignoriert
                    }
                }
                aCleanAttributes.put(aColumnName, v);
            }
            aCleanAttributes.put( IKnownColumns.MEMBERID, String.valueOf(aID));
            final String aCleanLine = makeCSVLine(REQUIREDCOLUMNNAMES, aCleanAttributes);
            aCleanLines.add( aCleanLine );
            aLineIdx++;
        }
        fAllLines.clear();
        fAllLines.addAll(aCleanLines);
        return aLineIdxByMemberID;
    }

    static String makeCSVLine(
            final List<String> fColumnNames,
            final Map<String, String> fAttributes )
    {
        final StringBuilder aLine = new StringBuilder( fAttributes.get(fColumnNames.get(0)));
        for ( int aIdx = 1; aIdx < fColumnNames.size(); aIdx++ ) {
            final String aColumnName = fColumnNames.get(aIdx);
            final String aAttr = fAttributes.get(aColumnName);
            aLine.append(";").append(aAttr == null ? "" : aAttr);
        }
        return aLine.toString();
    }

}

// ############################################################################
