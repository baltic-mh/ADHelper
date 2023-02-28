/**
 * FileDiffPanel.java
 *
 * Created on 05.12.2021
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2021 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import teambaltic.adhelper.gui.model.TBLModel_FilesDiff;
import teambaltic.adhelper.utils.DifferingLine;
import teambaltic.adhelper.utils.DifferingLine.EDiffType;
import teambaltic.adhelper.utils.FileComparisonResult;
import teambaltic.adhelper.utils.FileComparisonResult.EReason;
import teambaltic.adhelper.utils.LineInfo;

// ############################################################################
public class FileDiffPanel extends JPanel {
    private static final long serialVersionUID = -798611465501483872L;

    private final JTextField m_File_Ref;
    private final JTextField m_File_New;

    private final JTable m_DifferingLines;

    private final JButton m_btnOK;
    public JButton getBtnOK() { return m_btnOK; }

    private final JButton m_btnCancel;
    private final JLabel m_lbl_ColumnsMissing;
    private final JLabel m_lbl_ColumnsObsolete;
    private final JTextField m_ColumnsMissing;
    private final JTextField m_ColumnsObsolete;
    public JButton getBtnCancel() { return m_btnCancel; }

    /**
     * Create the panel.
     */
    public FileDiffPanel() {
        setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,
                FormSpecs.DEFAULT_COLSPEC,
                FormSpecs.RELATED_GAP_COLSPEC,},
            new RowSpec[] {
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                RowSpec.decode("default:grow"),
                FormSpecs.RELATED_GAP_ROWSPEC,
                FormSpecs.DEFAULT_ROWSPEC,
                FormSpecs.RELATED_GAP_ROWSPEC,}));

        m_DifferingLines = getJTable();

        final JLabel lblNewLabel = new JLabel("Alte Datei");
        add(lblNewLabel, "2, 2, right, default");

        m_File_Ref = new JTextField();
        m_File_Ref.setEditable(false);
        add(m_File_Ref, "4, 2, 5, 1, fill, default");
        m_File_Ref.setColumns(10);

        final JLabel lblNewLabel_1 = new JLabel("Neue Datei");
        add(lblNewLabel_1, "2, 4, right, default");

        m_File_New = new JTextField();
        m_File_New.setEditable(false);
        add(m_File_New, "4, 4, 5, 1, fill, default");
        m_File_New.setColumns(10);

        m_lbl_ColumnsMissing = new JLabel("Fehlende Spalten");
        add(m_lbl_ColumnsMissing, "2, 6, right, fill");

        m_ColumnsMissing = new JTextField();
        m_ColumnsMissing.setEditable(false);
        add(m_ColumnsMissing, "4, 6, 5, 1, fill, default");
        m_ColumnsMissing.setColumns(10);

        m_lbl_ColumnsObsolete = new JLabel("Unnötige Spalten");
        add(m_lbl_ColumnsObsolete, "2, 8, right, default");

        m_ColumnsObsolete = new JTextField();
        m_ColumnsObsolete.setEditable(false);
        add(m_ColumnsObsolete, "4, 8, 5, 1, fill, default");
        m_ColumnsObsolete.setColumns(10);

        final JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, "2, 10, 7, 3");

        scrollPane.setViewportView(m_DifferingLines);
        m_DifferingLines.setFillsViewportHeight(true);


        m_btnCancel = new JButton("Daten verwerfen");
        add(m_btnCancel, "6, 14");
        m_btnCancel.setActionCommand( "Cancel" );

        m_btnOK = new JButton("Daten übernehmen");
        add(m_btnOK, "8, 14");
        m_btnOK.setActionCommand( "OK" );

    }

    public void populate(final FileComparisonResult fDiff) {
        m_File_New.setText(fDiff.getNew().getAbsolutePath());
        m_File_Ref.setText(fDiff.getRef().getAbsolutePath());
        final List<String> aMissingColumns = fDiff.getSuspiciousColumns(EReason.MISSING);
        if( aMissingColumns.size() > 0 ) {
            m_lbl_ColumnsMissing.setForeground( new Color( 255,0,0) );
            m_ColumnsMissing.setText( String.join(", ", aMissingColumns) );
        } else {
            m_ColumnsMissing.setText( "- keine -" );
        }
        final List<String> aObsoleteColumns = fDiff.getSuspiciousColumns(EReason.OBSOLETE);
        if( aObsoleteColumns.size() > 0 ) {
            m_lbl_ColumnsObsolete.setForeground( new Color( 255,128,0) );
            m_ColumnsObsolete.setText( String.join(", ", aObsoleteColumns) );
        } else {
            m_ColumnsObsolete.setText( "- keine -" );
        }
        final List<DifferingLine> aDifferingLines = fDiff.getDifferingLines();
        final List<String> aColumnNames = new ArrayList<>();
        aColumnNames.add("Zeile (alt)");
        aColumnNames.add("Zeile (neu)");
        aColumnNames.add("Typ");
        aColumnNames.addAll( fDiff.getColumnNames() );

        final String[] aColumnNamesAsArray = new String[0];
        aColumnNames.toArray(aColumnNamesAsArray);

        final TBLModel_FilesDiff aTblModel = new TBLModel_FilesDiff( aColumnNames.toArray(aColumnNamesAsArray) );
        m_DifferingLines.setModel( aTblModel );

        for ( final DifferingLine aDifferingLine : aDifferingLines ) {
            final EDiffType aType = aDifferingLine.getType();
            final LineInfo aLineInfo_Ref = aDifferingLine.getLineInfo_Ref();
            final LineInfo aLineInfo_New = aDifferingLine.getLineInfo_New();
            switch( aType ) {
            case ADDED:
                addRow(aTblModel, "Hinzugefügt", aLineInfo_New, true);
                break;
            case DELETED:
                addRow(aTblModel, "Gelöscht", aLineInfo_Ref, false);
                break;
            case MODIFIED:
                addRow(aTblModel, "Alt", aLineInfo_Ref, false);
                addRow(aTblModel, "Neu", aLineInfo_New, true);
                break;
            default:
                break;
            }
        }
    }

    private static void addRow( final TBLModel_FilesDiff fTblModel, final String fType, final LineInfo fLineInfo, final boolean fNew ) {
        final String[] aSplit = fLineInfo.getLine().split(";");
        final String[] aRow = new String[3+aSplit.length];
        aRow[0] = fNew ? "-" : String.valueOf( fLineInfo.getLineNo() );
        aRow[1] = fNew ? String.valueOf( fLineInfo.getLineNo() ) : "-";
        aRow[2] = fType;
        for ( int aIdx = 0; aIdx < aSplit.length; aIdx++ ) {
            final String aString = aSplit[aIdx];
            aRow[3+aIdx] = aString;
        }
        fTblModel.addRow(aRow);
    }

    private static JTable getJTable() {
        return new JTable() {
            private static final long serialVersionUID = -1250116920947009802L;
            @Override
            public Dimension getPreferredScrollableViewportSize() {
                return new Dimension(350, 150);
            }
            @Override
            public Component prepareRenderer(final TableCellRenderer renderer, final int row, final int col) {
                final Component c = super.prepareRenderer(renderer, row, col);
                final String aType = (String)getValueAt(row, 2 );
                if( isModifiedCell( aType, row, col ) ) {
                    c.setBackground( new Color(64, 230, 150) );
                    c.setForeground(Color.BLACK);
                } else {
                    setColorsAccordingToType(c, aType, super.getBackground(), super.getForeground() );
                }
                return c;
            }
            private boolean isModifiedCell( final String fType, final int row, final int col) {
                if( col < 3 ) {
                    return false;
                }
                if(!"Neu".equals( fType ) ){
                    return false;
                }
                final String aValue_New = (String)getValueAt(row, col );
                final String aValue_Old = (String)getValueAt(row-1, col );
                if( aValue_New == null && aValue_Old == null ) {
                    return false;
                }
                if( aValue_New != null && !aValue_New.equals( aValue_Old ) ) {
                    return true;
                }
                if( aValue_Old != null && !aValue_Old.equals( aValue_New ) ) {
                    return true;
                }
                return false;
            }
        };
    }

    private static void setColorsAccordingToType(final Component c, final String fType, final Color fSBack, final Color fSFore) {
        switch(  fType ) {
        case "Alt":
            c.setBackground( new Color(184, 169, 7) );
            c.setForeground(Color.BLACK);
            break;
        case "Neu":
            c.setBackground( new Color( 255, 243, 107 ) );
            c.setForeground(Color.BLACK);
            break;
        case "Hinzugefügt":
            c.setBackground(Color.GREEN);
            c.setForeground(Color.BLACK);
            break;
        case "Gelöscht":
            c.setBackground(Color.RED);
            c.setForeground(Color.WHITE);
            break;
        default:
            c.setBackground(fSBack);
            c.setForeground(fSFore);
        }
    }
}

// ############################################################################
