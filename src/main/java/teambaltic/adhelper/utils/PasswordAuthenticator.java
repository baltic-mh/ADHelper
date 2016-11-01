/**
 * PasswordAuthenticator.java
 *
 * Created on 22.10.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class PasswordAuthenticator extends Authenticator
{
    private final String m_Username;
    private final String m_Password;

    public PasswordAuthenticator(final String fUsername, final String fPassword )
    {
        m_Username = fUsername;
        m_Password = fPassword;
    }
    // Called when password authorization is needed
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        // Return the information (a data holder that is used by Authenticator)
        return new PasswordAuthentication(m_Username, m_Password.toCharArray());

    }

}
//########################################################################
