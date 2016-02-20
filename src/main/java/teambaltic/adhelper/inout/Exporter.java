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
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IInvoicingPeriod;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class Exporter
{
    private static final Logger sm_Log = Logger.getLogger(Exporter.class);

    // ------------------------------------------------------------------------
    private final ADH_DataProvider m_DataProvider;
    private ADH_DataProvider getDataProvider(){ return m_DataProvider; }
    // ------------------------------------------------------------------------

    public Exporter(final ADH_DataProvider fDataProvider)
    {
        m_DataProvider = fDataProvider;
    }

    public void export( final Path fOutputFolder )
    {
        final File aBIF = getDataProvider().getBaseInfoFile();
        copyFileToFolder( aBIF, fOutputFolder );
        final File aWEF = getDataProvider().getWorkEventFile();
        copyFileToFolder( aWEF, fOutputFolder );

        exportWorkEvents( getDataProvider(), fOutputFolder );
        exportObligations( getDataProvider(), fOutputFolder );
        exportBalances( getDataProvider(), fOutputFolder );
        DetailsReporter.report( getDataProvider(), fOutputFolder );
    }

    private static void exportWorkEvents(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder )
    {
        final IInvoicingPeriod aIP = fDataProvider.getInvoicingPeriod();
        final LocalDate aToday = LocalDate.now();
        try{
            // Die Arbeitsdiensteinträge erhalten "Abgerechnet am"
            // wenn ihr Datum vor dem Enddatum des Abrechnungszeitraumes liegt.
            final PrintWriter aFileWriter = new PrintWriter(fOutputFolder.toString()+"/Arbeitsdienste-neu.csv", "ISO-8859-1");
            aFileWriter.write( String.format("%s;%s;%s;%s;%s\r\n",
                    IKnownColumns.MEMBERID, IKnownColumns.NAME,
                    IKnownColumns.DATE, IKnownColumns.HOURSWORKED,
                    IKnownColumns.CLEARED) );
            for( final InfoForSingleMember aSingleInfo : fDataProvider.getAll() ){
                final WorkEventsAttended aWorkEventsAttended = aSingleInfo.getWorkEventsAttended();
                if( aWorkEventsAttended == null ){
                    continue;
                }
                final int aMemberID = aWorkEventsAttended.getMemberID();
                final String aMemberName = fDataProvider.getMemberName( aMemberID );
                final List<WorkEvent> aWorkEvents = aWorkEventsAttended.getWorkEvents();
                if( aWorkEvents == null ){
                    continue;
                }
                for( final WorkEvent aWorkEvent : aWorkEvents ){
                    final LocalDate aWorkEventDate = aWorkEvent.getDate();
                    if( aIP.isBeforeEnd( aWorkEventDate ) ){
                       aWorkEvent.setCleared( aToday );
                    }
                    final String aLine = String.format( "%s;%s;%s;%.2f;%s\r\n",
                            aMemberID, aMemberName,
                            toStringWithDots(aWorkEventDate),
                            aWorkEvent.getHours()/100.0f,
                            toStringWithDots(aWorkEvent.getCleared()) );
                    aFileWriter.write( aLine );
                }
            }
            aFileWriter.close();
        }catch( final FileNotFoundException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }catch( final UnsupportedEncodingException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }

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
        final IInvoicingPeriod aIP = fDataProvider.getInvoicingPeriod();
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

    private static void copyFileToFolder( final File aFile, final Path fOutputFolder )
    {
        final Path aOUT_BIF = Paths.get( fOutputFolder.toString(), aFile.getName() );
        try{
            Files.copy( aFile.toPath(), aOUT_BIF, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES );
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
