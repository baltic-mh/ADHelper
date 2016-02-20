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
import teambaltic.adhelper.model.IInvoicingPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
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
        try{
            final PrintWriter aFileWriter = new PrintWriter(fOutputFolder.toString()+"/Details.txt", "ISO-8859-1");
            aFileWriter.write( "########################################################################\r\n" );
            final IInvoicingPeriod aInvoicingPeriod = fDataProvider.getInvoicingPeriod();
            aFileWriter.write( "Abrechnungszeitraum: "+aInvoicingPeriod+"\r\n" );
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
            final IInvoicingPeriod fInvoicingPeriod,
            final boolean fOnlyPayers )
    {
        final DutyCharge aCharge = fSingleInfo.getDutyCharge();
        final IClubMember aMember = fSingleInfo.getMember();
        if( fOnlyPayers && aMember.getLinkID() != 0 ){
            return;
        }
        fWriter.write( "==========================================================================\r\n" );
        final FreeFromDuty aFreeFromDuty = fSingleInfo.getFreeFromDuty();
        if( isFreeFromDutyEffective( fInvoicingPeriod, aCharge, aFreeFromDuty ) ){
            fWriter.write( String.format( "%-30s: %s\r\n", aMember.getName(), aFreeFromDuty ));
        } else {
            fWriter.write( String.format("%-27s  %6s %6s %6s %6s %6s %6s\r\n",
                    "Name", "Guth.", "Gearb.", "Pflicht", "Guth.II", "Zu zahl", "Gut.III" ));
            fWriter.write( "--------------------------------------------------------------------------\r\n" );
            final List<DutyCharge> aAllDutyCharges = aCharge.getAllDutyCharges();
            for( final DutyCharge aC : aAllDutyCharges ){
                final IClubMember aRelatedMember = fDataProvider.getMember( aC.getMemberID() );
                fWriter.write( String.format("%-27s %6.2f %6.2f   %6.2f  %6.2f  %6.2f  %6.2f\r\n",
                        aRelatedMember.getName(),
                        aC.getBalance_Original()/100.0,
                        aC.getHoursWorked()/100.0,
                        aC.getHoursDue()/100.0,
                        aC.getBalance_Charged()/100.0,
                        aC.getHoursToPay()/100.0,
                        aC.getBalance_ChargedAndAdjusted()/100.0
                        ) );
            }
            fWriter.write( "--------------------------------------------------------------------------\r\n" );
            fWriter.write(String.format( "Verbleibende Stunden zu zahlen:   %7.2f\r\n",
                    aCharge.getHoursToPayTotal()/100.0));
        }
    }


    private static boolean isFreeFromDutyEffective(
            final IInvoicingPeriod fIP,
            final DutyCharge fCharge,
            final FreeFromDuty fFreeFromDuty )
    {
        if( fFreeFromDuty == null ){
            return false;
        }
        if( fCharge.getAllDutyCharges().size() > 1 ){
            return false;
        }
        if( fCharge.getHoursToPayTotal() > 0 ){
            return false;
        }
        return DateUtils.coversFreeFromDuty_InvoicingPeriod( fFreeFromDuty, fIP );
    }


}

// ############################################################################
