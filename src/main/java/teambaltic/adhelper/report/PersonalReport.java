/**
 * PersonalReport.java
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
import static net.sf.dynamicreports.report.builder.DynamicReports.report;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.component.SubreportBuilder;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.BalanceHistory;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;

// ############################################################################
public class PersonalReport
{
//    private static final Logger sm_Log = Logger.getLogger(PersonalReport.class);

    private final Map<Integer, AReportBuilderWithDataSource> m_ReportMap;

    // ------------------------------------------------------------------------
    private final IPeriod m_Period;
    private IPeriod getPeriod(){ return m_Period; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final InfoForSingleMember m_InfoForSingleMember;
    public InfoForSingleMember getInfoForSingleMember(){ return m_InfoForSingleMember; }
    // ------------------------------------------------------------------------

    public PersonalReport(
            final IPeriod fPeriod,
            final InfoForSingleMember fInfoForSingleMember)
    {
        m_Period = fPeriod;
        m_InfoForSingleMember   = fInfoForSingleMember;
        m_ReportMap             = new HashMap<>();
    }

    public JasperReportBuilder build()
    {
        final SubreportBuilder subreport = cmp.subreport( new SubreportExpression() )
                .setDataSource( new SubreportDataSourceExpression() );

        final JasperReportBuilder aReport = report();
        aReport
            .title( Templates.createTitleComponent( getPeriod().toString(), m_InfoForSingleMember.toString() ) )
            .setPageFormat(PageType.A4)
            .detail( subreport, cmp.verticalGap( 10 ) )
            .pageFooter( Templates.footerComponent )
            .setDataSource( createDataSource() )
            ;
        return aReport;
    }

    private static JRDataSource createDataSource() {
        return new JREmptyDataSource(5);
    }

    private class SubreportExpression extends AbstractSimpleExpression<JasperReportBuilder> {
        private static final long serialVersionUID = 1L;

        @Override
        public JasperReportBuilder evaluate(final ReportParameters reportParameters) {
            final int masterRowNumber = reportParameters.getReportRowNumber();
            AReportBuilderWithDataSource aReport = m_ReportMap.get( masterRowNumber );
            if( aReport == null ){
                final InfoForSingleMember aInfoForSingleMember = getInfoForSingleMember();
                final IPeriod aPeriod = getPeriod();
                switch(masterRowNumber){
                    case 1:
                        aReport = new DutyReport(aPeriod, aInfoForSingleMember);
                        break;
                    case 2:
                        aReport = new AdjustmentReport(aPeriod, aInfoForSingleMember);
                        break;
                    case 3:
                        aReport = new ParticipationReport(aPeriod, aInfoForSingleMember);
                        break;
                    case 4:
                        aReport = new AccountingReport(aPeriod, aInfoForSingleMember);
                        break;
                    case 5:
                        if( !isBalanceHistoryEmpty( aInfoForSingleMember ) ){
                            aReport = new BalanceHistoryReport(aPeriod, aInfoForSingleMember);
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unbekannte Report-Nummer: "+masterRowNumber);
                }
                m_ReportMap.put( masterRowNumber, aReport );
            }
            return aReport;
        }

    }

    private class SubreportDataSourceExpression extends AbstractSimpleExpression<JRDataSource> {
        private static final long serialVersionUID = 1L;

        @Override
        public JRDataSource evaluate(final ReportParameters reportParameters) {
            final int masterRowNumber = reportParameters.getReportRowNumber();
            final AReportBuilderWithDataSource aReport = m_ReportMap.get( masterRowNumber );
            if(aReport == null){
                return null;
//                throw new IllegalStateException("Kein ReportBuilder gefunden für Nummer: "+masterRowNumber);
            }
            return aReport.createDataSource(reportParameters);
        }
    }

    private static boolean isBalanceHistoryEmpty( final InfoForSingleMember fInfoForSingleMember )
    {
        final BalanceHistory aBalanceHistory = fInfoForSingleMember.getBalanceHistory();
        final List<LocalDate> aValidFromList = aBalanceHistory.getValidFromList();
        for( final LocalDate aDate : aValidFromList ){
            final Balance aBalance = aBalanceHistory.getValue( aDate );
            final int aValue_Original = aBalance.getValue_Original();
            if( aValue_Original > 0 ){
                return false;
            }
        }
        return true;
    }

}

// ############################################################################
