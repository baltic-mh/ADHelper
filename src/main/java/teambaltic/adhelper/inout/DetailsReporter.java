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
import java.time.Month;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.controller.DutyCalculator;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.CreditHours;
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
    private static final String LINE1 = "###################################################################################\r\n";
    private static final String LINE3 = "-----------------------------------------------------------------------------------\r\n";
    private static final String LINE2 = "===================================================================================\r\n";
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
        final IPeriod aInvoicingPeriod = fDataProvider.getPeriod();
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
        fWriter.write( LINE1 );
        fWriter.write( String.format( "(%d) %-25s      Abrechnungszeitraum: %s\r\n",
                aMember.getID(), aMember.getName(), fInvoicingPeriod ));
        final DutyCharge aCharge = fSingleInfo.getDutyCharge();
        final Collection<FreeFromDuty> aEffectiveFFDs = fSingleInfo.getFreeFromDutyItems(fInvoicingPeriod);
        if( aEffectiveFFDs.size() > 0 ){
            fWriter.write( LINE3 );
            for( final FreeFromDuty aFFD : aEffectiveFFDs ){
                final List<Month> aMonthsCovered = DateUtils.getMonthsCovered( aFFD, fInvoicingPeriod );
                fWriter.write( String.format( "AD-Befreit wegen %s: %s\r\n",
                        aFFD.getReason(), DateUtils.getNames( aMonthsCovered ) ));
            }
        }
        final List<Month> aMonthsDue = DutyCalculator.getMonthsDue( fInvoicingPeriod, aEffectiveFFDs );
        if( aMonthsDue.size() > 0 ){
            fWriter.write( LINE3 );
            fWriter.write( String.format( "Fuer AD angerechnete Monate: %s\r\n",
                    DateUtils.getNames( aMonthsDue ) ));
        }

        int aTotalDue = 0;
        final StringBuffer aSB = new StringBuffer(LINE3);
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

        aSB.append( LINE2 );
        aSB.append( String.format("%-27s  %6s %6s %6s %6s %6s %6s %6s\r\n",
                "Name", "Guth.", "Gutschr.", "Gearb.", "Pflicht", "Guth.II", "Zu zahl", "Gut.III" ));
        aSB.append( LINE3 );
        final List<InfoForSingleMember> aAllRelatives = fSingleInfo.getAllRelatives();
        for( final InfoForSingleMember aInfoOfThisMember : aAllRelatives ){
            final IClubMember aRelatedMember = aInfoOfThisMember.getMember();
            final Balance aBalance = aInfoOfThisMember.getBalance( fInvoicingPeriod );
            final DutyCharge aC = aInfoOfThisMember.getDutyCharge();
            final CreditHours aCH = aInfoOfThisMember.getCreditHours( fInvoicingPeriod );
            final int aCreditHours = aCH == null ? 0 : aCH.getHours();
            final int aHoursDue = aC.getHoursDue();
            aTotalDue += aHoursDue;
            aSB.append( String.format("%-27s %6.2f   %6.2f %6.2f   %6.2f  %6.2f  %6.2f  %6.2f\r\n",
                    aRelatedMember.getName(),
                    aBalance.getValue_Original()/100.0,
                    aCreditHours/100.0,
                    aC.getHoursWorked()/100.0,
                    aHoursDue/100.0,
                    aBalance.getValue_Charged()/100.0,
                    aC.getHoursToPay()/100.0,
                    aBalance.getValue_ChargedAndAdjusted()/100.0
                    ) );
        }
        aSB.append( LINE3 );
        aSB.append(String.format( "Verbleibende Stunden zu zahlen:   %7.2f\r\n",
                aCharge.getHoursToPayTotal()/100.0));
        if( aTotalDue > 0 ){
            fWriter.write( aSB.toString() );
        }
        fWriter.write( LINE2+"\r\n" );
    }

}

// ############################################################################
