/**
 * Writer.java
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
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.BalanceHistory;
import teambaltic.adhelper.model.CreditHours;
import teambaltic.adhelper.model.CreditHoursGranted;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.report.DetailsReporter;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class Writer
{
    private static final Logger sm_Log = Logger.getLogger(Writer.class);
    private static final String LF = "\r\n";
    private static final String CHARSET_ISO_8859_1 = "ISO-8859-1";
    private static final String BALANCEFORMAT_OLD = "%s;%s;%.2f;%.2f;%s"+LF;
    private static final String BALANCEFORMAT = "%s;%s;%.2f;%s"+LF;
    private static final String CREDITFORMAT = "%s;%s;%.2f;%s;%s"+LF;

    // ------------------------------------------------------------------------
    private final ADH_DataProvider m_DataProvider;
    private ADH_DataProvider getDataProvider(){ return m_DataProvider; }
    // ------------------------------------------------------------------------

    public Writer( final ADH_DataProvider fDataProvider )
    {
        m_DataProvider = fDataProvider;
    }

    public void writeFiles( final Path fOutputFolder )
            throws IOException
    {
        writeToFile_WorkEvents  ( getDataProvider(), fOutputFolder );
        writeToFile_CreditHours ( getDataProvider(), fOutputFolder );
        writeToFile_Obligations ( getDataProvider(), fOutputFolder );
        writeToFile_Balances    ( getDataProvider(), fOutputFolder );
        writeToFile_BalanceHistories( getDataProvider(), fOutputFolder );
        DetailsReporter.report  ( getDataProvider(), fOutputFolder );
    }

    public static void writeToFile_WorkEvents(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder )
    {
        try{
            final List<WorkEvent> aAllWorkEvents = getAllWorkEvents( fDataProvider );
            final PrintWriter aFileWriter = new PrintWriter(fOutputFolder.toString()+"/Arbeitsdienste.csv", CHARSET_ISO_8859_1);
            aFileWriter.write( String.format("%s;%s;%s;%s"+LF,
                    IKnownColumns.MEMBERID, IKnownColumns.NAME,
                    IKnownColumns.DATE, IKnownColumns.HOURSWORKED ) );
            for( final WorkEvent aWorkEvent : aAllWorkEvents ){
                final LocalDate aWorkEventDate = aWorkEvent.getDate();
                final int aMemberID = aWorkEvent.getMemberID();
                if( !fDataProvider.isMemberInCurrentPeriod( aMemberID ) ){
                    continue;
                }
                final String aMemberName = fDataProvider.getMemberName( aMemberID );
                final String aLine = String.format( "%s;%s;%s;%.2f"+LF,
                        aMemberID, aMemberName,
                        toStringWithDots(aWorkEventDate),
                        aWorkEvent.getHours()/100.0f );
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

    public static void writeToFile_Obligations(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder )
    {
        try{
            final PrintWriter aFileWriter = new PrintWriter(fOutputFolder.toString()+"/ZuZahlendeStunden.csv", CHARSET_ISO_8859_1);
            aFileWriter.write( String.format("%s;%s;%s"+LF, IKnownColumns.MEMBERID, IKnownColumns.NAME, IKnownColumns.HOURSTOPAY ) );
            for( final InfoForSingleMember aSingleInfo : fDataProvider.getAll() ){
                if( !fDataProvider.isMemberInCurrentPeriod( aSingleInfo.getID() ) ){
                    continue;
                }
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
                final String aLine = String.format( "%s;%s;%.2f"+LF, aMemberID,
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

    public static void writeToFile_Balances(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder)
    {
        final IPeriod aPeriod = fDataProvider.getPeriod();
        final String aBalanceAt = getNewBalanceDateString( aPeriod.getEnd() );
        try{
            final PrintWriter aFileWriter = new PrintWriter(fOutputFolder.toString()+"/Guthaben.csv", CHARSET_ISO_8859_1);
            aFileWriter.write( String.format("%s;%s,%s;%s;%s"+LF,
                    IKnownColumns.MEMBERID, IKnownColumns.NAME,
                    IKnownColumns.GUTHABEN_WERT_ALT, IKnownColumns.GUTHABEN_WERT, IKnownColumns.GUTHABEN_AM ) );
            for( final InfoForSingleMember aSingleInfo : fDataProvider.getAll() ){
                final int aMemberID = aSingleInfo.getID();
                if( !fDataProvider.isMemberInCurrentPeriod( aMemberID ) ){
                    continue;
                }
                final String aMemberName = fDataProvider.getMemberName( aMemberID );
                final Balance aBalance = aSingleInfo.getBalance( aPeriod );
                writeSingleBalanceLine( aFileWriter, aMemberName, aBalance, aBalanceAt, false );
            }
            aFileWriter.close();
        }catch( final FileNotFoundException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }catch( final UnsupportedEncodingException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
    }

    public static Path writeToFile_BalanceHistories(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder)
    {
        try{
            final Path aPath = fOutputFolder.resolve( "GuthabenVerlauf.csv" );
            final PrintWriter aFileWriter = new PrintWriter(aPath.toFile(), CHARSET_ISO_8859_1);
            aFileWriter.write( String.format("%s;%s;%s;%s"+LF,
                    IKnownColumns.MEMBERID, IKnownColumns.NAME,
                    IKnownColumns.GUTHABEN_WERT, IKnownColumns.GUTHABEN_AM ) );
            for( final InfoForSingleMember aSingleInfo : fDataProvider.getAll() ){
                final int aMemberID = aSingleInfo.getID();
                if( !fDataProvider.isMemberInCurrentPeriod( aMemberID ) ){
                    continue;
                }
                final String aMemberName = fDataProvider.getMemberName( aMemberID );
                final BalanceHistory aBalanceHistory = aSingleInfo.getBalanceHistory();
                for(final LocalDate aValidFrom : aBalanceHistory.getValidFromList() ){
                    final Balance aBalance = aBalanceHistory.getValue( aValidFrom );
                    if( aBalance.getValue_Original() == 0 ){
                        continue;
                    }
                    writeSingleBalanceLine( aFileWriter, aMemberName, aBalance, toStringWithDots(aValidFrom), true );
                }
            }
            aFileWriter.close();
            return aPath;
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
            return null;
        }
    }

    private static void writeSingleBalanceLine(
            final PrintWriter   fFileWriter,
            final String        fMemberName,
            final Balance       fBalance,
            final String        fValidFrom,
            final boolean       fNewFormat )
    {
        final int aBalance_Org = fBalance.getValue_Original();
        final int aBalance_CAA = fBalance.getValue_ChargedAndAdjusted();
        if( aBalance_Org == 0 && aBalance_CAA == 0 ){
            return;
        }

        final String aLine;
        if( fNewFormat ){
            aLine = String.format( BALANCEFORMAT,
                    fBalance.getMemberID(), fMemberName,
                    aBalance_Org/100.0f, fValidFrom );
        } else {
            aLine = String.format( BALANCEFORMAT_OLD,
                    fBalance.getMemberID(), fMemberName,
                    aBalance_Org/100.0f, aBalance_CAA/100.0f, fValidFrom );
        }
//                sm_Log.info( aLine );
        fFileWriter.write( aLine );
    }

    public static Path writeToFile_CreditHours(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder)
    {
        try{
            final Path aPath = fOutputFolder.resolve( "Gutschriften.csv" );
            final PrintWriter aFileWriter = new PrintWriter(aPath.toFile(), CHARSET_ISO_8859_1);
            aFileWriter.write( String.format("%s;%s;%s;%s;%s"+LF,
                    IKnownColumns.MEMBERID, IKnownColumns.NAME,
                    IKnownColumns.CREDITHOURS, IKnownColumns.DATE, IKnownColumns.COMMENT ) );
            for( final InfoForSingleMember aSingleInfo : fDataProvider.getAll() ){
                final int aMemberID = aSingleInfo.getID();
                if( !fDataProvider.isMemberInCurrentPeriod( aMemberID ) ){
                    continue;
                }
                final String aMemberName = fDataProvider.getMemberName( aMemberID );
                final CreditHoursGranted aCreditHoursGranted = aSingleInfo.getCreditHoursGranted();
                if( aCreditHoursGranted == null ){
                    continue;
                }
                for(final CreditHours aCreditHours : aCreditHoursGranted.getCreditHoursList(null) ){
                    final int aHours = aCreditHours.getHours();
                    if( aHours == 0 ){
                        continue;
                    }
                    writeSingleCreditHoursLine( aFileWriter, aMemberName, aCreditHours, toStringWithDots(aCreditHours.getDate()) );
                }
            }
            aFileWriter.close();
            return aPath;
        }catch( final Exception fEx ){
            sm_Log.warn("Exception: ", fEx );
            return null;
        }
    }

    private static void writeSingleCreditHoursLine(
            final PrintWriter   fFileWriter,
            final String        fMemberName,
            final CreditHours   fCreditHours,
            final String        fGrantedAt )
    {
        final int aHours = fCreditHours.getHours();
        final String aLine = String.format( CREDITFORMAT,
                fCreditHours.getMemberID(), fMemberName,
                aHours/100.0f, fGrantedAt, fCreditHours.getComment() );

//                sm_Log.info( aLine );
        fFileWriter.write( aLine );
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

    public static void shiftBalanceValues(final File fFile) throws IOException
    {
        final List<String>aColumnNames = FileUtils.readColumnNames( fFile );

        final List<String> aShiftedLines = new ArrayList<>();
        aShiftedLines.add( String.join( ";", aColumnNames ) );
        final List<String> aAllLines = FileUtils.readAllLines( fFile, 1 );
        for( final String aSingleLine : aAllLines ){
            final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aSingleLine );
            final String aGuthaben_Wert     = aAttributes.get( IKnownColumns.GUTHABEN_WERT );
//            final String aGuthaben_Wert_Alt = aAttributes.get( IKnownColumns.GUTHABEN_WERT_ALT );
            aAttributes.put( IKnownColumns.GUTHABEN_WERT_ALT, aGuthaben_Wert );
            aShiftedLines.add( String.join( ";", aAttributes.values() ) );
        }

        final PrintWriter aFileWriter = new PrintWriter(fFile, CHARSET_ISO_8859_1);
        for( final String aShiftedLine : aShiftedLines ){
            aFileWriter.write( aShiftedLine+LF);
        }
        aFileWriter.close();
    }

}

// ############################################################################
