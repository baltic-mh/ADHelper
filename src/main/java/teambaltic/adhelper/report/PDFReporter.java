/**
 * PDFReporter.java
 *
 * Created on 20.05.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.concatenatedReport;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.log4j.Logger;

import net.sf.dynamicreports.jasper.builder.JasperConcatenatedReportBuilder;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.Exporters;
import net.sf.dynamicreports.report.exception.DRException;
import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;

// ############################################################################
public final class PDFReporter
{
    private PDFReporter(){/**/}

    private static final Logger sm_Log = Logger.getLogger(PDFReporter.class);

    private static final String FILENAME = "Details";

    public static void report(final ADH_DataProvider fDataProvider, final int fMemberID)
    {
        final IPeriod aInvoicingPeriod = fDataProvider.getPeriod();
        final InfoForSingleMember aInfoForSingleMember = fDataProvider.get( fMemberID );
        final JasperReportBuilder aReport = buildReport( aInvoicingPeriod, aInfoForSingleMember );
        try{
            aReport.show( false );
        }catch( final DRException fEx ){
            // TODO Auto-generated catch block
            sm_Log.warn("Exception: ", fEx );
        }
    }

    public static void report(final ADH_DataProvider fDataProvider, final Path fOutputFolder)
    {
        final JasperConcatenatedReportBuilder aReportBuilder = concatenatedReport().continuousPageNumbering();

        final IPeriod aInvoicingPeriod = fDataProvider.getPeriod();

        final List<InfoForSingleMember> aAll = fDataProvider.getAll();
        for( final InfoForSingleMember aInfoForSingleMember : aAll ){
            if( !isMemberDuringPeriod( aInfoForSingleMember.getMember(), aInvoicingPeriod ) ) {
                continue;
            }
            aReportBuilder.concatenate( buildReport( aInvoicingPeriod, aInfoForSingleMember ) )
            ;
        }

        final String aFileNameForThisPeriod = String.format( "%s-%s.pdf", FILENAME, aInvoicingPeriod.toString() );
        final File aPDFFile = fOutputFolder.resolve( aFileNameForThisPeriod ).toFile();
        if( aPDFFile.exists() ){
            final boolean aDeleted = aPDFFile.delete();
            if( !aDeleted ){
                sm_Log.error("Konnte Datei nicht löschen: "+aPDFFile);
                return;
            }
        }
        sm_Log.info( "Erzeuge PDF-Report für Zeitraum: "+ aInvoicingPeriod.toString());
        try {
            final long aStartTime = System.currentTimeMillis();
            aReportBuilder.toPdf(Exporters.pdfExporter(aPDFFile));
            final long aTimeLapsed = System.currentTimeMillis() - aStartTime;
            final String aFormatDurationWords = DurationFormatUtils.formatDurationWords( aTimeLapsed, true, true );
            sm_Log.info( String.format( "PDF-Erzeugung hat %s gedauert", aFormatDurationWords ) );
            sm_Log.info( "PDF-Datei: "+aPDFFile );
        } catch (final Throwable e) {
            sm_Log.error("Problem bei der PDF-Erzeugung: ", e);
        }

    }

    private static boolean isMemberDuringPeriod( final IClubMember fMember, final IPeriod fPeriod )
    {
        final LocalDate aMemberUntil = fMember.getMemberUntil();
        if( aMemberUntil != null && fPeriod.isBeforeMyStart( aMemberUntil ) ) {
            return false;
        }
        final LocalDate aMemberFrom = fMember.getMemberFrom();
        if( !fPeriod.isBeforeMyEnd( aMemberFrom ) ) {
            return false;
        }
        return true;
    }

    private static JasperReportBuilder buildReport( final IPeriod fInvoicingPeriod,
            final InfoForSingleMember fInfoForSingleMember )
    {
        return (new PersonalReport(fInvoicingPeriod, fInfoForSingleMember)).build();
    }
}

// ############################################################################
