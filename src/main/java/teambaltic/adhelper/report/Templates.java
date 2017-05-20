/**
 * Templates.java
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
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.tableOfContentsCustomizer;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;

import java.awt.Color;
import java.util.Locale;

import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.datatype.BigDecimalType;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.tableofcontents.TableOfContentsCustomizerBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;

// ############################################################################
public class Templates
{

    public static final StyleBuilder rootStyle;
    public static final StyleBuilder boldStyle;
    public static final StyleBuilder italicStyle;
    public static final StyleBuilder boldCenteredStyle;
    public static final StyleBuilder bold12CenteredStyle;
    public static final StyleBuilder bold18CenteredStyle;
    public static final StyleBuilder bold22CenteredStyle;
    public static final StyleBuilder columnStyle;
    public static final StyleBuilder columnTitleStyle;
    public static final StyleBuilder groupStyle;
    public static final StyleBuilder subtotalStyle;

    public static final ReportTemplateBuilder reportTemplate;
    public static final CurrencyType currencyType;
    public static final ComponentBuilder<?, ?> dynamicReportsComponent;
    public static final ComponentBuilder<?, ?> footerComponent;

    static {
        rootStyle           = stl.style().setPadding(2);
        boldStyle           = stl.style(rootStyle).bold();
        italicStyle         = stl.style(rootStyle).italic();
        boldCenteredStyle   = stl.style(boldStyle)
                                 .setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE);
        bold12CenteredStyle = stl.style(boldCenteredStyle)
                                 .setFontSize(12);
        bold18CenteredStyle = stl.style(boldCenteredStyle)
                                 .setFontSize(18);
        bold22CenteredStyle = stl.style(boldCenteredStyle)
                             .setFontSize(22);
        columnStyle         = stl.style(rootStyle).setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
        columnTitleStyle    = stl.style(columnStyle)
                                 .setBorder(stl.pen1Point())
                                 .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)
                                 .setBackgroundColor(Color.LIGHT_GRAY)
                                 .bold();
        groupStyle          = stl.style(boldStyle)
                                 .setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
        subtotalStyle       = stl.style(boldStyle)
                                 .setTopBorder(stl.pen1Point())
                                 .setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);;

        final StyleBuilder crosstabGroupStyle      = stl.style(columnTitleStyle);
        final StyleBuilder crosstabGroupTotalStyle = stl.style(columnTitleStyle)
                                                  .setBackgroundColor(new Color(170, 170, 170));
        final StyleBuilder crosstabGrandTotalStyle = stl.style(columnTitleStyle)
                                                  .setBackgroundColor(new Color(140, 140, 140));
        final StyleBuilder crosstabCellStyle       = stl.style(columnStyle)
                                                  .setBorder(stl.pen1Point());

        final TableOfContentsCustomizerBuilder tableOfContentsCustomizer = tableOfContentsCustomizer()
            .setHeadingStyle(0, stl.style(rootStyle).bold());

        reportTemplate = template()
                           .setLocale(Locale.GERMAN)
                           .setColumnStyle(columnStyle)
                           .setColumnTitleStyle(columnTitleStyle)
                           .setGroupStyle(groupStyle)
                           .setGroupTitleStyle(groupStyle)
                           .setSubtotalStyle(subtotalStyle)
                           .highlightDetailEvenRows()
                           .crosstabHighlightEvenRows()
                           .setCrosstabGroupStyle(crosstabGroupStyle)
                           .setCrosstabGroupTotalStyle(crosstabGroupTotalStyle)
                           .setCrosstabGrandTotalStyle(crosstabGrandTotalStyle)
                           .setCrosstabCellStyle(crosstabCellStyle)
                           .setTableOfContentsCustomizer(tableOfContentsCustomizer);

        currencyType = new CurrencyType();

        dynamicReportsComponent =
          cmp.horizontalList(
            cmp.image(Templates.class.getResource("/KVK.jpg")).setFixedDimension(80, 80),
            cmp.verticalList(
                cmp.text("Arbeitsdienste II/2016").setStyle(bold22CenteredStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER),
                cmp.text("http://www.kv-kiel.de").setStyle(italicStyle)
                )
            ).setFixedWidth(300);

        footerComponent = cmp.pageXslashY()
                             .setStyle(
                                stl.style(boldCenteredStyle)
                                   .setTopBorder(stl.pen1Point()));
    }

    /**
     * Creates custom component which is possible to add to any report band component
     */
    public static ComponentBuilder<?, ?> createTitleComponent(final String fLabel1, final String fLabel2) {
        return cmp.horizontalList(
                cmp.image(Templates.class.getResource("/KVK.jpg")).setFixedDimension(100, 80),
                cmp.verticalList(
                        cmp.text("Arbeitsdienstabrechnung").setStyle(bold22CenteredStyle),
                        cmp.text(fLabel1).setStyle(bold12CenteredStyle),
                        cmp.text(fLabel2).setStyle(bold18CenteredStyle)
                        ),
                cmp.image(Templates.class.getResource("/ADHelper.png")).setFixedDimension(60, 60)
                )
                .newRow()
                .add(cmp.line())
                .newRow()
                .add(cmp.verticalGap(10));
//        return cmp.horizontalList()
//                .add(
//                    dynamicReportsComponent,
//                    cmp.text(label).setStyle(bold18CenteredStyle).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT),
//                    cmp.image(Templates.class.getResource("/ADHelper.png")).setFixedDimension(60, 60)
//                 )
//                .newRow()
//                .add(cmp.line())
//                .newRow()
//                .add(cmp.verticalGap(10));
    }

    public static CurrencyValueFormatter createCurrencyValueFormatter(final String label) {
        return new CurrencyValueFormatter(label);
    }

    public static class CurrencyType extends BigDecimalType {
        private static final long serialVersionUID = 1L;

        @Override
        public String getPattern() {
            return "$ #,###.00";
        }
    }

    private static class CurrencyValueFormatter extends AbstractValueFormatter<String, Number> {
        private static final long serialVersionUID = 1L;

        private final String label;

        public CurrencyValueFormatter(final String label) {
            this.label = label;
        }

        @Override
        public String format(final Number value, final ReportParameters reportParameters) {
            return label + currencyType.valueToString(value, reportParameters.getLocale());
        }
    }

}

// ############################################################################
