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
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import teambaltic.adhelper.gui.ADH_Application;
import teambaltic.adhelper.gui.UserSettingsDialog;
import teambaltic.adhelper.model.ERole;
import teambaltic.adhelper.model.settings.IUserSettings;

// ############################################################################
public class UserSettingsListener implements ActionListener
{
    private static final Logger sm_Log = Logger.getLogger(UserSettingsListener.class);

    // ------------------------------------------------------------------------
    private final UserSettingsDialog m_Dialog;
    public UserSettingsDialog getDialog(){ return m_Dialog; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final IUserSettings m_UserSettings;
    public IUserSettings getUserSettings(){ return m_UserSettings; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final ADH_Application m_App;
    private ADH_Application getApp(){ return m_App; }
    // ------------------------------------------------------------------------

    public UserSettingsListener(final UserSettingsDialog fDialog, final IUserSettings fUserSettings, final ADH_Application fAppWindow)
    {
        m_Dialog        = fDialog;
        m_UserSettings  = fUserSettings;
        m_App           = fAppWindow;
    }

    @Override
    public void actionPerformed( final ActionEvent fE )
    {
        switch( fE.getActionCommand() ){
            case "Editieren":
                ERole aRole = getUserSettings().getRole();
                if( aRole == null ){
                    aRole = ERole.MITGLIEDERWART;
                }
                getDialog().getCb_Role().setSelectedItem( aRole );
                getDialog().setVisible( true );
                break;

            case "OK":
                acceptUserDataFromDialog();
                break;

            default:
                break;
        }
    }

    private void acceptUserDataFromDialog()
    {
        final IUserSettings aUserSettings = getUserSettings();
        final String aName  = getDialog().getTf_Name().getText();
        aUserSettings.setStringValue( IUserSettings.EKey.NAME, aName );
        final String aEMail = getDialog().getTf_EMail().getText();
        aUserSettings.setStringValue( IUserSettings.EKey.EMAIL, aEMail );
        final Object aSelectedRole = getDialog().getCb_Role().getSelectedItem();
        final ERole aPreviousRole = aUserSettings.getRole();
        aUserSettings.setStringValue( IUserSettings.EKey.ROLE, aSelectedRole.toString() );
        getDialog().setVisible(false);
        final ERole aCurrentRole = aUserSettings.getRole();
        try{
            getUserSettings().writeToFile();
        }catch( final IOException fEx ){
            sm_Log.warn("Exception: ", fEx );
        }
        if( !aCurrentRole.equals( aPreviousRole ) ){
            JOptionPane.showMessageDialog(null,
                    "Die Applikation muss neu gestartet werden!",
                    "Benutzerrolle ge\u00E4ndert!",
                    JOptionPane.WARNING_MESSAGE);
            getApp().shutdown("Benutzerrolle geändert", 0);
        }
    }

}

// ############################################################################
