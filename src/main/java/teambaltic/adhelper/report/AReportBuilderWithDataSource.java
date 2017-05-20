/**
 * ReportBuilderWithDataSource.java
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

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.definition.ReportParameters;
import teambaltic.adhelper.model.IPeriod;
import teambaltic.adhelper.model.InfoForSingleMember;

// ############################################################################
public abstract class AReportBuilderWithDataSource extends JasperReportBuilder
{
    private static final long serialVersionUID = 8180912082237750778L;

    public abstract DRDataSource createDataSource( ReportParameters fReportParameters );

    // ------------------------------------------------------------------------
    private final IPeriod m_Period;
    public IPeriod getPeriod(){ return m_Period; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final InfoForSingleMember m_InfoForSingleMember;
    public InfoForSingleMember getInfoForSingleMember(){ return m_InfoForSingleMember; }
    // ------------------------------------------------------------------------

    public AReportBuilderWithDataSource( final IPeriod fPeriod, final InfoForSingleMember fInfoForSingleMember )
    {
        m_Period                = fPeriod;
        m_InfoForSingleMember   = fInfoForSingleMember;
    }
}

// ############################################################################
