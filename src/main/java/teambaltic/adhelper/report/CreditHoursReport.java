/**
 * ParticipationReport.java
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
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import teambaltic.adhelper.model.CreditHours;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;

// ############################################################################
public class CreditHoursReport extends AReportBuilderWithDataSource
{
    private static final long serialVersionUID = 1195311505418150048L;

    private static final String REPORTTITLE     = "Gutschriften";
    private static final String[] COL_TITLES    = new String[]{ "Kommentar", "Stunden" };
    private static final String[] COL_NAMES     = new String[]{ "comment",  "hours"   };

    public CreditHoursReport(final IPeriod fPeriod, final InfoForSingleMember fInfoForSingleMember)
    {
        super( fPeriod, fInfoForSingleMember );
        init();
    }

    private void init()
    {
        final TextColumnBuilder<Double> aCol_Stunden = Utils.col_Hours(COL_TITLES[1], COL_NAMES[1]);
        this
            .setTemplate(Templates.reportTemplate)
            .title(cmp.text(REPORTTITLE).setStyle(Templates.bold12CenteredStyle))
            .addColumn(col.column(COL_TITLES[0], COL_NAMES[0], type.stringType()) )
            .addColumn(aCol_Stunden.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
            .subtotalsAtSummary(sbt.sum(aCol_Stunden).setValueFormatter( Utils.FORMATTER_HOUR ));
    }

    @Override
    public DRDataSource createDataSource( final ReportParameters fReportParameters )
    {
        final DRDataSource dataSource = new DRDataSource(COL_NAMES);
        final InfoForSingleMember aInfoForSingleMember = getInfoForSingleMember();
        final CreditHours aCreditHours = aInfoForSingleMember.getCreditHours( getPeriod() );
        final int aHours = aCreditHours.getHours();
        final Object[] aValues = new Object[]{ aCreditHours.getComment(), aHours/100.0};
        dataSource.add(aValues);
        return dataSource;
    }

}

// ############################################################################
