/**
 * DetailsReporter.java
 *
 * Created on 18.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.utils.DateUtils;

// ############################################################################
public class DetailsReporter
{
    private static final Logger sm_Log = Logger.getLogger(DetailsReporter.class);

    public static void report(final ADH_DataProvider fDataProvider, final Path fOutputFolder)
    {
        report( fDataProvider, fOutputFolder, false );
    }

    public static void report(
            final ADH_DataProvider fDataProvider,
            final Path fOutputFolder,
            final boolean fOnlyPayers )
    {
        final IPeriod aInvoicingPeriod = fDataProvider.getInvoicingPeriod();
        try{
            final PrintWriter aFileWriter = new PrintWriter(fOutputFolder.toString()+"/Details.txt", "ISO-8859-1");
            for( final InfoForSingleMember aSingleInfo : fDataProvider.getAll() ){
                report( fDataProvider, aFileWriter, aSingleInfo, aInvoicingPeriod, fOnlyPayers );
            }
            aFileWriter.close();
        }catch( FileNotFoundException | UnsupportedEncodingException fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }


    }

    private static void report(
            final ADH_DataProvider fDataProvider,
            final PrintWriter fWriter,
            final InfoForSingleMember fSingleInfo,
            final IPeriod fInvoicingPeriod,
            final boolean fOnlyPayers )
    {
        final IClubMember aMember = fSingleInfo.getMember();
        if( fOnlyPayers && aMember.getLinkID() != 0 ){
            return;
        }
        fWriter.write( "##########################################################################\r\n" );
        fWriter.write( String.format( "(%d) %-21s Abrechnungszeitraum: %s\r\n",
                aMember.getID(), aMember.getName(), fInvoicingPeriod ));
        final DutyCharge aCharge = fSingleInfo.getDutyCharge();
        final FreeFromDuty aFreeFromDuty = fSingleInfo.getFreeFromDuty();
        final int aFreeFromDutyInMonths = DateUtils.getCoverageInMonths(aFreeFromDuty, fInvoicingPeriod );
        if( aFreeFromDutyInMonths > 0 ){
            fWriter.write( "--------------------------------------------------------------------------\r\n" );
            fWriter.write( String.format( "AD-Befreit (%d Monate) wegen: %s\r\n",
                    aFreeFromDutyInMonths, aFreeFromDuty ));
        }

        int aTotalDue = 0;
        final StringBuffer aSB = new StringBuffer("--------------------------------------------------------------------------\r\n");
        aSB.append( "Besuchte Arbeitsdienste:\r\n" );
        final WorkEventsAttended aWorkEventsAttended = fSingleInfo.getWorkEventsAttended();
        if( aWorkEventsAttended == null ){
            aSB.append( "\t- Keine -" );
        } else {
            final List<WorkEvent> aWorkEvents = aWorkEventsAttended.getWorkEvents( fInvoicingPeriod );
            if( aWorkEvents == null || aWorkEvents.size() == 0 ){
                aSB.append( "\t- Keine -" );
            } else {
                aWorkEvents.forEach( aWorkEvent -> {
                    aSB.append( String.format("\t%s:%5.2fh", aWorkEvent.getDate(), aWorkEvent.getHours()/100.0 ) );
                } );
            }
        }
        aSB.append( "\r\n" );

        aSB.append( "==========================================================================\r\n" );
        aSB.append( String.format("%-27s  %6s %6s %6s %6s %6s %6s\r\n",
                "Name", "Guth.", "Gearb.", "Pflicht", "Guth.II", "Zu zahl", "Gut.III" ));
        aSB.append( "--------------------------------------------------------------------------\r\n" );
        final List<DutyCharge> aAllDutyCharges = aCharge.getAllDutyCharges();
        for( final DutyCharge aC : aAllDutyCharges ){
            final IClubMember aRelatedMember = fDataProvider.getMember( aC.getMemberID() );
            final int aHoursDue = aC.getHoursDue();
            aTotalDue += aHoursDue;
            aSB.append( String.format("%-27s %6.2f %6.2f   %6.2f  %6.2f  %6.2f  %6.2f\r\n",
                    aRelatedMember.getName(),
                    aC.getBalance_Original()/100.0,
                    aC.getHoursWorked()/100.0,
                    aHoursDue/100.0,
                    aC.getBalance_Charged()/100.0,
                    aC.getHoursToPay()/100.0,
                    aC.getBalance_ChargedAndAdjusted()/100.0
                    ) );
        }
        aSB.append( "--------------------------------------------------------------------------\r\n" );
        aSB.append(String.format( "Verbleibende Stunden zu zahlen:   %7.2f\r\n",
                aCharge.getHoursToPayTotal()/100.0));
        if( aTotalDue > 0 ){
            fWriter.write( aSB.toString() );
        }
        fWriter.write( "==========================================================================\r\n\r\n" );
    }

}

// ############################################################################
