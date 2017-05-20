/**
 * AccountingReport.java
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
import static net.sf.dynamicreports.report.builder.DynamicReports.variable;

import java.util.List;

import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.VariableBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.constant.Calculation;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.CreditHours;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.report.Utils.HourFormatter;

// ############################################################################
public class AccountingReport extends AReportBuilderWithDataSource
{
    private static final long serialVersionUID  = 384924463701935583L;

    private static final String REPORTTITLE     = "Abrechnung";
    private static final String[] COL_TITLES    = new String[]{"Name", "Guthaben", "Gutschrift", "Gearbeitet", "Pflicht", "Guthaben II", "Zu zahlen", "Guthaben III" };
    private static final String[] COL_NAMES     = new String[]{"name", "balance",  "credit",     "worked",     "duty",    "balance2",    "topay",     "balance3"     };

    public AccountingReport(final IPeriod fPeriod, final InfoForSingleMember fInfoForSingleMember)
    {
        super( fPeriod, fInfoForSingleMember );
        init();
    }


    private void init()
    {
        final TextColumnBuilder<Double> aCol_ToPay  = Utils.col_Hours(COL_TITLES[6], COL_NAMES[6]);
        final TextColumnBuilder<Double> aCol_Bal3   = Utils.col_Hours(COL_TITLES[7], COL_NAMES[7]);
        final VariableBuilder<Double>   sumToPay    = variable(aCol_ToPay, Calculation.SUM);
        final VariableBuilder<Double>   sumBal3     = variable(aCol_Bal3,  Calculation.SUM);

        final TextFieldBuilder<String>  toPayText   = cmp.text(new CustomTextSubtotal(sumToPay, sumBal3))
        .setStyle(Templates.subtotalStyle);

        final HourFormatter aFORM_H = Utils.FORMATTER_HOUR;
        this
        .setTemplate(Templates.reportTemplate)
        .title(cmp.text(REPORTTITLE).setStyle(Templates.bold12CenteredStyle))
        .variables( sumToPay, sumBal3 )
        .addColumn( col.column(COL_TITLES[0], COL_NAMES[0], type.stringType()))
        .addColumn( Utils.col_Hours(COL_TITLES[1], COL_NAMES[1]) )
        .addColumn( Utils.col_Hours(COL_TITLES[2], COL_NAMES[2]) )
        .addColumn( Utils.col_Hours(COL_TITLES[3], COL_NAMES[3]) )
        .addColumn( Utils.col_Hours(COL_TITLES[4], COL_NAMES[4]) )
        .addColumn( Utils.col_Hours(COL_TITLES[5], COL_NAMES[5]) )
        .addColumn(aCol_ToPay)
        .addColumn(aCol_Bal3)
        .subtotalsAtSummary(
                sbt.sum(aCol_ToPay).setValueFormatter(aFORM_H),
                sbt.sum(aCol_Bal3).setValueFormatter(aFORM_H))
        .summary( toPayText );
    }

    @Override
    public DRDataSource createDataSource( final ReportParameters fReportParameters )
    {
        final DRDataSource dataSource = new DRDataSource(COL_NAMES);

        final InfoForSingleMember aInfoForSingleMember = getInfoForSingleMember();
        final List<InfoForSingleMember> aAllRelatives = aInfoForSingleMember.getAllRelatives();
        for( final InfoForSingleMember aInfoForThisMember : aAllRelatives ){
            final String aName = aInfoForThisMember.getMember().getName();
            final Balance aBalance = aInfoForThisMember.getBalance( getPeriod() );
            final double aGuthaben = aBalance.getValue_Original()/100.0;
            final double aGuthabenII = aBalance.getValue_Charged()/100.0;
            final double aGuthabenIII = aBalance.getValue_ChargedAndAdjusted()/100.0;
            final CreditHours aCreditHours = aInfoForThisMember.getCreditHours( getPeriod() );
            final double aGutSchrift = aCreditHours == null ? 0.0 : aCreditHours.getHours()/100.0;
            final DutyCharge aDutyCharge = aInfoForThisMember.getDutyCharge();
            final double aGearbeitet = aDutyCharge.getHoursWorked()/100.0;
            final double aDuty = aDutyCharge.getHoursDue()/100.0;
            final double aToPay = aDutyCharge.getHoursToPay()/100.0;
            dataSource.add(aName, aGuthaben, aGutSchrift, aGearbeitet, aDuty, aGuthabenII, aToPay, aGuthabenIII);
        }

        return dataSource;
    }

    private class CustomTextSubtotal extends AbstractSimpleExpression<String> {
        private static final long serialVersionUID = 1L;

        private final VariableBuilder<Double> sumToPay;
        private final VariableBuilder<Double> sumBalance;

        public CustomTextSubtotal(final VariableBuilder<Double> sumToPay, final VariableBuilder<Double> sumBalance) {
            this.sumToPay = sumToPay;
            this.sumBalance = sumBalance;
        }

        @Override
        public String evaluate(final ReportParameters reportParameters) {
            final Double sumBalanceValue = reportParameters.getValue(sumBalance);
            final Double sumToPayValue = sumBalanceValue > 0 ? 0.0 : reportParameters.getValue(sumToPay);
            return String.format( "Zu zahlende Stunden: %.2f h", sumToPayValue );
        }
    }
}

// ############################################################################
