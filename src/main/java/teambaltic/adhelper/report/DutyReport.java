/**
 * DutyReport.java
 *
 * Created on 01.05.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.sbt;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.utils.DateUtils;

// ############################################################################
public class DutyReport extends AReportBuilderWithDataSource
{
    private static final long serialVersionUID  = 384924463701935583L;

    private static final String REPORTTITLE     = "Belastungen";
    private static final String[] COL_TITLES    = new String[]{"Monat", "Befreiung", "Pflichtstunden"};
    private static final String[] COL_NAMES     = new String[]{"month", "freeby",    "duty"};

    public DutyReport(final IPeriod fPeriod, final InfoForSingleMember fInfoForSingleMember)
    {
        super( fPeriod, fInfoForSingleMember );
        init();
    }

    private void init()
    {
        final TextColumnBuilder<Double> aCol_Belastung = Utils.col_Hours(COL_TITLES[2], COL_NAMES[2]);
        this.setTemplate(Templates.reportTemplate)
            .title(cmp.text(REPORTTITLE).setStyle(Templates.bold12CenteredStyle))
            .addColumn(col.column(COL_TITLES[0], COL_NAMES[0], type.stringType()).setWidth( 50 ))
            .addColumn(col.column(COL_TITLES[1], COL_NAMES[1], type.stringType()).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
            .addColumn(aCol_Belastung.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
            .setSubtotalStyle(Templates.boldStyle.setTopBorder(stl.pen1Point()))
            .subtotalsAtSummary(sbt.sum(aCol_Belastung).setValueFormatter(Utils.FORMATTER_HOUR));
    }

    @Override
    public DRDataSource createDataSource(final ReportParameters reportParameters)
    {
        final Collection<FreeFromDuty> aEffectiveFFDs = getInfoForSingleMember().getFreeFromDutyItems( getPeriod() );
        final DRDataSource dataSource = new DRDataSource(COL_NAMES);
        final int aMonthValue_Start = getPeriod().getStart().getMonthValue();
        final int aMonthValue_End = getPeriod().getEnd().getMonthValue();
        final Map<Month, String>aFFDMap = new HashMap<>();
        for( int aMonthValue = aMonthValue_Start; aMonthValue <= aMonthValue_End; aMonthValue++ ){
            final Month aMonth = Month.of( aMonthValue );
            for( final FreeFromDuty aFFD : aEffectiveFFDs ){
                final List<Month> aMonthsCovered = DateUtils.getMonthsCovered( aFFD, getPeriod() );
                if( aMonthsCovered.contains( aMonth ) ){
                    addFFDReason(aFFDMap, aMonth, aFFD.getReason().toString());
                }
            }
        }
        for( int aMonthValue = aMonthValue_Start; aMonthValue <= aMonthValue_End; aMonthValue++ ){
            final Month aMonth      = Month.of( aMonthValue );
            final String aFFD       = aFFDMap.get( aMonth );
            final String aMonthName = DateUtils.getName( aMonth );
            if( aFFD == null ){
                // TODO Zahlenwert durch Berechnung ersetzen
                dataSource.add(aMonthName, "- KEINE -", 0.5);
            } else {
                dataSource.add(aMonthName, aFFD, 0.0);
            }
        }
        return dataSource;

    }

    private static void addFFDReason( final Map<Month, String> fFFDMap, final Month fMonth, final String fReason )
    {
        final String aOldValue = fFFDMap.get( fMonth );
        if( aOldValue == null ){
            fFFDMap.put( fMonth, fReason );
        } else {
            fFFDMap.put( fMonth, aOldValue+","+fReason );
        }
    }

}

// ############################################################################
