/**
 * MemberFilterChangedListener.java
 *
 * Created on 14.01.2024
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2024 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.listeners;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import teambaltic.adhelper.model.IClubMember;

// ############################################################################
public class MemberFilterChangedListener extends KeyAdapter {

	private final JComboBox<IClubMember>  m_CB_Members;
	private final JTextField m_TF_FilterText;
	public JTextField getTF_FilterText() { return m_TF_FilterText; }

	private List<IClubMember> m_AllMembers;
	public List<IClubMember> getAllMembers() { return m_AllMembers; }
	public void setAllMembers(final List<IClubMember> fAllMembers) { m_AllMembers = fAllMembers; }

//	private int currentCaretPosition = 0;
	private String m_PreviousFilterText;

	public MemberFilterChangedListener(final JComboBox<IClubMember> fCB_Members, final JTextField fTF_FilterText) {
		m_CB_Members = fCB_Members;
		m_TF_FilterText = fTF_FilterText;
		m_PreviousFilterText = "";
	}

	@Override
	public void keyPressed(final KeyEvent fEvent) {
		SwingUtilities.invokeLater(() -> {
			final JTextField aTextField = getTF_FilterText();
			final String aCurrentFilterText = aTextField.getText();
			if( aCurrentFilterText.equals(m_PreviousFilterText)) {
				return;
			}
			m_PreviousFilterText = aCurrentFilterText;
			filter(aCurrentFilterText);
		});
	}

	public void filter(final String fFilterText) {
	    if (!m_CB_Members.isPopupVisible()) {
	    	m_CB_Members.showPopup();
	    }

	    final List<IClubMember> aFilteredElements= new ArrayList<>();
	    for (int i = 0; i < m_AllMembers.size(); i++) {
	        if (m_AllMembers.get(i).toString().toLowerCase().contains(fFilterText.toLowerCase())) {
	            aFilteredElements.add(m_AllMembers.get(i));
	        }
	    }
	    final DefaultComboBoxModel<IClubMember> model = (DefaultComboBoxModel<IClubMember>) m_CB_Members.getModel();
	    model.removeAllElements();
	    final List<IClubMember> aElementsToAdd = aFilteredElements.size() > 0 ? aFilteredElements : m_AllMembers;
	    if (aFilteredElements.size() > 0) {
	        for (final IClubMember aElementToAdd: aElementsToAdd) {
				model.addElement(aElementToAdd);
			}
	    }
	}
}

// ############################################################################
