/**
 * UserSettingsListener.java
 *
 * Created on 02.03.2016
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

import teambaltic.adhelper.model.settings.IUserSettings;
import teambaltic.adhelper.remoteaccess.UserDataDialog;

// ############################################################################
public class UserSettingsListener implements ActionListener
{
    // ------------------------------------------------------------------------
    private final UserDataDialog m_Dialog;
    public UserDataDialog getDialog(){ return m_Dialog; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IUserSettings m_UserSettings;
    public IUserSettings getUserSettings(){ return m_UserSettings; }
    // ------------------------------------------------------------------------

    public UserSettingsListener(final UserDataDialog fDialog, final IUserSettings fUserSettings)
    {
        m_Dialog = fDialog;
        m_UserSettings = fUserSettings;
    }

    @Override
    public void actionPerformed( final ActionEvent fE )
    {
        final IUserSettings aUserSettings = getUserSettings();
        final String aName  = getDialog().getTf_Name().getText();
        aUserSettings.setStringValue( IUserSettings.EKey.NAME, aName );
        final String aEMail = getDialog().getTf_EMail().getText();
        aUserSettings.setStringValue( IUserSettings.EKey.EMAIL, aEMail );
        final Object aSelectedRole = getDialog().getCb_Role().getSelectedItem();
        aUserSettings.setStringValue( IUserSettings.EKey.ROLE, aSelectedRole.toString() );
    }

}

// ############################################################################
