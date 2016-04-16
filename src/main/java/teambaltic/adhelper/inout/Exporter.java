/**
 * Exporter.java
 *
 * Created on 15.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class Exporter
{
    private static final Logger sm_Log = Logger.getLogger(Exporter.class);

    // ------------------------------------------------------------------------
    private final ADH_DataProvider m_DataProvider;
    private ADH_DataProvider getDataProvider(){ return m_DataProvider; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_FinishedFileName;
    private String getFinishedFileName(){ return m_FinishedFileName; }
    // ------------------------------------------------------------------------

    public Exporter(
            final ADH_DataProvider fDataProvider,
            final String fFinishedFileName)
    {
        m_DataProvider = fDataProvider;
        m_FinishedFileName = fFinishedFileName;
    }

    public void export( final Path fOutputFolder, final String fInfo, final boolean fSetFinished )
            throws IOException
    {
        if( !Files.exists( fOutputFolder )){
            Files.createDirectories( fOutputFolder );
        }

        final File aBIF = getDataProvider().getBaseDataFile();
        copyFileToFolder( aBIF, fOutputFolder );
        final File aWEF = getDataProvider().getWorkEventFile();
        final String aNewName = FileUtils.getFileNameWithPostfixAppended( aWEF, "_old" );
        copyFileUnderNewNameToFolder( aWEF, fOutputFolder, aNewName );

        exportWorkEvents( getDataProvider(), fOutputFolder );
        exportObligations( getDataProvider(), fOutputFolder );
        exportBalances( getDataProvider(), fOutputFolder );
        DetailsReporter.report( getDataProvider(), fOutputFolder );

        if( fSetFinished ){
            FileUtils.writeFinishedFile( fOutputFolder.resolve( getFinishedFileName() ), fInfo );
        }
    }

    public static void exportWorkEvents(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder )
    {
        final IPeriod aIP = fDataProvider.getPeriod();
        final LocalDate aToday = LocalDate.now();
        try{
            final List<WorkEvent> aAllWorkEvents = getAllWorkEvents( fDataProvider );
            // Die Arbeitsdiensteinträge erhalten "Abgerechnet am"
            // wenn ihr Datum vor dem Enddatum des Abrechnungszeitraumes liegt.
            final PrintWriter aFileWriter = new PrintWriter(fOutputFolder.toString()+"/Arbeitsdienste.csv", "ISO-8859-1");
            aFileWriter.write( String.format("%s;%s;%s;%s;%s\r\n",
                    IKnownColumns.MEMBERID, IKnownColumns.NAME,
                    IKnownColumns.DATE, IKnownColumns.HOURSWORKED,
                    IKnownColumns.CLEARED) );
            for( final WorkEvent aWorkEvent : aAllWorkEvents ){
                final LocalDate aWorkEventDate = aWorkEvent.getDate();
                if( aIP.isBeforeMyEnd( aWorkEventDate ) ){
                   aWorkEvent.setCleared( aToday );
                }
                final int aMemberID = aWorkEvent.getMemberID();
                final String aMemberName = fDataProvider.getMemberName( aMemberID );
                final String aLine = String.format( "%s;%s;%s;%.2f;%s\r\n",
                        aMemberID, aMemberName,
                        toStringWithDots(aWorkEventDate),
                        aWorkEvent.getHours()/100.0f,
                        toStringWithDots(aWorkEvent.getCleared()) );
                aFileWriter.write( aLine );
            }
            aFileWriter.close();
        }catch( final FileNotFoundException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }catch( final UnsupportedEncodingException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }

    }

    private static List<WorkEvent> getAllWorkEvents( final ADH_DataProvider fDataProvider )
    {
        final List<WorkEvent>aAllWorkEvents = new ArrayList<>();
        for( final InfoForSingleMember aSingleInfo : fDataProvider.getAll() ){
            final WorkEventsAttended aWorkEventsAttended = aSingleInfo.getWorkEventsAttended();
            if( aWorkEventsAttended == null ){
                continue;
            }
            final List<WorkEvent> aWorkEvents = aWorkEventsAttended.getWorkEvents();
            if( aWorkEvents == null ){
                continue;
            }
            aAllWorkEvents.addAll( aWorkEvents );
        }
        Collections.sort( aAllWorkEvents );
        return aAllWorkEvents;
    }

    private static void exportObligations(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder )
    {
        try{
            final PrintWriter aFileWriter = new PrintWriter(fOutputFolder.toString()+"/ZuZahlendeStunden.csv", "ISO-8859-1");
            aFileWriter.write( String.format("%s;%s;%s\r\n", IKnownColumns.MEMBERID, IKnownColumns.NAME, IKnownColumns.HOURSTOPAY ) );
            for( final InfoForSingleMember aSingleInfo : fDataProvider.getAll() ){
                final DutyCharge aCharge = aSingleInfo.getDutyCharge();
                final IClubMember aMember = aSingleInfo.getMember();
                if( aMember.getLinkID() != 0 ){
                    continue;
                }
                final int aMemberID = aCharge.getMemberID();
                final int aHoursToPay = aCharge.getHoursToPayTotal();
                if( aHoursToPay == 0 ){
                    continue;
                }
                final String aLine = String.format( "%s;%s;%.2f\r\n", aMemberID,
                        fDataProvider.getMemberName( aMemberID ), aHoursToPay/100.0f );
//                sm_Log.info( aLine );
                aFileWriter.write( aLine );
            }
            aFileWriter.close();
        }catch( final FileNotFoundException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }catch( final UnsupportedEncodingException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
    }

    private static void exportBalances(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder)
    {
        final IPeriod aIP = fDataProvider.getPeriod();
        final String aBalanceAt = getNewBalanceDateString( aIP.getEnd() );
        try{
            final PrintWriter aFileWriter = new PrintWriter(fOutputFolder.toString()+"/Guthaben.csv", "ISO-8859-1");
            aFileWriter.write( String.format("%s;%s;%s;%s;%s\r\n",
                    IKnownColumns.MEMBERID, IKnownColumns.NAME,
                    IKnownColumns.GUTHABEN_WERT_ALT, IKnownColumns.GUTHABEN_WERT, IKnownColumns.GUTHABEN_AM ) );
            for( final InfoForSingleMember aSingleInfo : fDataProvider.getAll() ){
                final DutyCharge aCharge = aSingleInfo.getDutyCharge();
                final int aMemberID = aCharge.getMemberID();
                final int aBalance_Old = aCharge.getBalance_Original();
                final int aBalance_New = aCharge.getBalance_ChargedAndAdjusted();
                if( aBalance_Old == 0 && aBalance_New == 0 ){
                    continue;
                }
                final String aLine = String.format( "%s;%s;%.2f;%.2f;%s\r\n",
                        aMemberID, fDataProvider.getMemberName( aMemberID ),
                        aBalance_Old/100.0f, aBalance_New/100.0f, aBalanceAt );
//                sm_Log.info( aLine );
                aFileWriter.write( aLine );
            }
            aFileWriter.close();
        }catch( final FileNotFoundException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }catch( final UnsupportedEncodingException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
    }

    private static void copyFileToFolder( final File fFile, final Path fOutputFolder )
    {
        copyFileUnderNewNameToFolder( fFile, fOutputFolder, fFile.getName());
    }

    private static void copyFileUnderNewNameToFolder(
            final File fFile, final Path fTargetFolder, final String fNewFileName )
    {
        final Path aTargetPath = Paths.get( fTargetFolder.toString(), fNewFileName );
        try{
            Files.copy( fFile.toPath(), aTargetPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.COPY_ATTRIBUTES );
        }catch( final IOException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
    }

    private static String getNewBalanceDateString( final LocalDate fDate )
    {
        final LocalDate aDate = fDate.plusDays( 1 );
        return toStringWithDots( aDate );
    }
    private static String toStringWithDots( final LocalDate fDate )
    {
        if( fDate == null ){
            return "";
        }
        return String.format( "%02d.%02d.%04d", fDate.getDayOfMonth(), fDate.getMonthValue(), fDate.getYear() );
    }

}

// ############################################################################
