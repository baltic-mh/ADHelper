/**
 * BalanceHistoryReport.java
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

import static net.sf.dynamicreports.report.builder.DynamicReports.cht;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.awt.Color;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.renderer.category.BarRenderer;

import net.sf.dynamicreports.report.builder.chart.BarChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.definition.chart.DRIChartCustomizer;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.BalanceHistory;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;

// ############################################################################
public class BalanceHistoryReport extends AReportBuilderWithDataSource
{
    private static final long serialVersionUID  = 384924463701935583L;

    private static final String REPORTTITLE     = "Guthabenverlauf";
    private static final String[] COL_TITLES    = new String[]{ "Datum", "Stunden" };
    private static final String[] COL_NAMES     = new String[]{ "date",  "hours"   };

    public BalanceHistoryReport(final IPeriod fPeriod, final InfoForSingleMember fInfoForSingleMember)
    {
        super( fPeriod, fInfoForSingleMember );
        init();
    }

    private void init()
    {
        final TextColumnBuilder<String> dateColumn = col.column(COL_TITLES[0], COL_NAMES[0], type.stringType());
        final TextColumnBuilder<Double> hourColumn = Utils.col_Hours( COL_TITLES[1], COL_NAMES[1] ) ;
        final BarChartBuilder itemChart = cht.barChart()
                .customizers(new ChartCustomizer())
                .setCategory(dateColumn)
                .setShowValues(true)
                .setShowLegend( false )
                .addSerie( cht.serie(hourColumn) );

        this
        .setTemplate(Templates.reportTemplate)
        .title(cmp.text(REPORTTITLE).setStyle(Templates.bold12CenteredStyle))
        .summary(itemChart);

    }

    @Override
    public DRDataSource createDataSource( final ReportParameters fReportParameters )
    {
        final InfoForSingleMember aInfoForSingleMember = getInfoForSingleMember();
        final BalanceHistory aBalanceHistory = aInfoForSingleMember.getBalanceHistory();

        final DRDataSource dataSource = new DRDataSource(COL_NAMES);
        final List<LocalDate> aValidFromList = aBalanceHistory.getValidFromList();
        for( final LocalDate aDate : aValidFromList ){
            final Balance aBalance = aBalanceHistory.getValue( aDate );
            final LocalDate aValidFrom = aBalance.getValidFrom();
            final int aValue_Original = aBalance.getValue_Original();
            dataSource.add( aValidFrom.toString(), aValue_Original/100.0 );
        }

        return dataSource;
    }

    private class ChartCustomizer implements DRIChartCustomizer, Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public void customize(final JFreeChart chart, final ReportParameters reportParameters) {
            final BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
            renderer.setShadowPaint(Color.LIGHT_GRAY);
            renderer.setShadowVisible(true);
            final CategoryAxis domainAxis = chart.getCategoryPlot().getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 2.0));
        }
    }

}

// ############################################################################
