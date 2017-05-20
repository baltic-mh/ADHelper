/**
 * Utils.java
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

import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.definition.ReportParameters;

// ############################################################################
public final class Utils
{

    private Utils(){/**/}

    public static HourFormatter FORMATTER_HOUR = new HourFormatter();
    public static DateFormatter FORMATTER_DATE = new DateFormatter();

    public static class HourFormatter extends AbstractValueFormatter<String, Number> {
        private static final long serialVersionUID = 1L;
        @Override
        public String format(final Number value, final ReportParameters reportParameters) {
            return String.format( "%.2f h", value.doubleValue() );
        }
    }

    public static class DateFormatter extends AbstractValueFormatter<String, Date> {
        private static final long serialVersionUID = 1L;
        @Override
        public String format( final Date fValue, final ReportParameters fReportParameters )
        {
            final LocalDate date = fValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return date.toString();
        }
    }

    public static TextColumnBuilder<Double> col_Hours(final String fTitle, final String fFieldName)
    {
        final TextColumnBuilder<Double> aCol = col.column(fTitle, fFieldName, type.doubleType() ).setValueFormatter(FORMATTER_HOUR);
        aCol.setWidth( 50 );
        return aCol;
    }

    public static Date dateFromLocalDate( final LocalDate aLD1 )
    {
        return Date.from(aLD1.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


}

// ############################################################################
