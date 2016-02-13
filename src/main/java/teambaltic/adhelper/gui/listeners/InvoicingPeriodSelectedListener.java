/**
 * InvoicingPeriodSelectedListener.java
 *
 * Created on 12.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.gui.MainPanel;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IInvoicingPeriod;

// ############################################################################
public class InvoicingPeriodSelectedListener implements ActionListener
{
    private final MainPanel m_Panel;
    private final ADH_DataProvider m_DataProvider;

    public InvoicingPeriodSelectedListener(
            final MainPanel fPanel,
            final ADH_DataProvider fDataProvider)
    {
        m_Panel = fPanel;
        m_DataProvider = fDataProvider;
    }

    @Override
    public void actionPerformed( final ActionEvent fE )
    {
        @SuppressWarnings("unchecked")
        final JComboBox<String> aCB = (JComboBox<String>) fE.getSource();
        final IInvoicingPeriod aInvoicingPeriod = (IInvoicingPeriod) aCB.getSelectedItem();

        m_DataProvider.calculateDutyCharges( aInvoicingPeriod );
        m_DataProvider.balanceRelatives();

        final JComboBox<IClubMember> aCb_Members = m_Panel.getCB_Members();
        final IClubMember aSelectedItem = (IClubMember) aCb_Members.getSelectedItem();
        final int aMemberID = aSelectedItem.getID();

        GUIUpdater.updateGUI( aMemberID, m_Panel, m_DataProvider );
    }

}

// ############################################################################
