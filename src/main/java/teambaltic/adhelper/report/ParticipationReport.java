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

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;

// ############################################################################
public class ParticipationReport extends AReportBuilderWithDataSource
{
    private static final long serialVersionUID = 1195311505418150048L;

    private static final String REPORTTITLE     = "Beteiligung an Arbeitsdiensten";
    private static final String[] COL_TITLES    = new String[]{ "Name", "Datum", "Stunden" };
    private static final String[] COL_NAMES     = new String[]{ "name", "date",  "hours"   };

    private final Map<Integer, String> m_Relatives;

    public ParticipationReport(final IPeriod fPeriod,
            final InfoForSingleMember fInfoForSingleMember)
    {
        super( fPeriod, fInfoForSingleMember );
        m_Relatives = initRelatives(fInfoForSingleMember);
        init();
    }

    private void init()
    {
        final TextColumnBuilder<Double> aCol_Stunden = Utils.col_Hours(COL_TITLES[2], COL_NAMES[2]);
        this
            .setTemplate(Templates.reportTemplate)
            .title(cmp.text(REPORTTITLE).setStyle(Templates.bold12CenteredStyle))
            .addColumn(col.column(COL_TITLES[0], COL_NAMES[0], type.stringType()).setWidth( 50 ))
            .addColumn(col.column(COL_TITLES[1], COL_NAMES[1], type.dateType()).setValueFormatter( Utils.FORMATTER_DATE ))
            .addColumn(aCol_Stunden.setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT))
            .subtotalsAtSummary(sbt.sum(aCol_Stunden).setValueFormatter( Utils.FORMATTER_HOUR ));
    }

    @Override
    public DRDataSource createDataSource( final ReportParameters fReportParameters )
    {
        final DRDataSource dataSource = new DRDataSource(COL_NAMES);
        final InfoForSingleMember aInfoForSingleMember = getInfoForSingleMember();
        final WorkEventsAttended aWorkEventsAttended = aInfoForSingleMember.getWorkEventsAttended();
        final List<WorkEvent> aAllWorkEvents = aWorkEventsAttended.getAllWorkEvents( getPeriod() );
        for( final WorkEvent aWorkEvent : aAllWorkEvents ){
            final LocalDate aLD = aWorkEvent.getDate();
            final int aHours = aWorkEvent.getHours();
            final int aMemberID = aWorkEvent.getMemberID();
            final Object[] aValues = new Object[]{ getMemberName( aMemberID ), Utils.dateFromLocalDate( aLD ), aHours/100.0};
            dataSource.add(aValues);
        }
        return dataSource;
    }

    private static Map<Integer, String> initRelatives(final InfoForSingleMember fInfoForSingleMember)
    {
        final Map<Integer, String> aRelatives = new HashMap<>();
        final List<InfoForSingleMember> aAllRelatives = fInfoForSingleMember.getAllRelatives();
        aAllRelatives.forEach( aIFSM -> { aRelatives.put( aIFSM.getID(), aIFSM.toString() ); } );
        return aRelatives;
    }

    private String getMemberName( final int fMemberID )
    {
        return m_Relatives.getOrDefault( fMemberID, String.valueOf(fMemberID) );
    }

}

// ############################################################################
