/**
 * AppUpdater.java
 *
 * Created on 22.10.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerw�rmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.panayotis.jupidator.ApplicationInfo;
import com.panayotis.jupidator.UpdatedApplication;
import com.panayotis.jupidator.Updater;
import com.panayotis.jupidator.UpdaterException;
import com.panayotis.jupidator.gui.JupidatorGUI;

import teambaltic.BuildConfig;

// ############################################################################
public class AppUpdater implements UpdatedApplication
{
    private static final Logger LOG = Logger.getLogger(AppUpdater.class);
//    private static Logger LOG;

    public AppUpdater()
    {
        final boolean aTestMode     = isTestMode();
        final String aJupidatorURL  = getJupidatorURL( aTestMode );
        final int aReleaseNumber    = getRelease( aTestMode );

        final String aUserDir = System.getProperty("user.dir");
        final String aVersion = getVersion();
        final ApplicationInfo aApplicationInfo = new ApplicationInfo(aUserDir, aReleaseNumber, aVersion);
        try {
            // Sets the authenticator that will be used by the networking code
            // when a proxy or an HTTP server asks for authentication.
//            Authenticator.setDefault(new PasswordAuthenticator("", "Am6EsadS"));
            final Updater aUpdater = new Updater( aJupidatorURL, aApplicationInfo, this);
            final JupidatorGUI aGUI = aUpdater.getGUI();
            aGUI.setProperty("LogList", "true");
            aUpdater.actionDisplay();
        } catch (final UpdaterException fEx) {
            LOG.error(String.format("Problems to access repository (%s): %s", aJupidatorURL, fEx.getMessage()), fEx );
        }
    }
    /**
     * @see com.panayotis.jupidator.UpdatedApplication#receiveMessage(java.lang.String)
     */
    @Override
    public void receiveMessage(final String fMessage)
    {
        LOG.info(fMessage);
    }

    /**
     * @see com.panayotis.jupidator.UpdatedApplication#requestRestart()
     */
    @Override
    public boolean requestRestart()
    {
        return true;
    }

    private static boolean isTestMode()
    {
        String aTestUpdateProp = System.getProperty("test.update");
        if( aTestUpdateProp == null || aTestUpdateProp.equals("") ) {
            aTestUpdateProp = System.getenv("test.update");
        }
        final boolean aTestCase = aTestUpdateProp != null;
        return aTestCase;
    }

    private static String getVersion()
    {
        return BuildConfig.VERSION;
    }
    private static int getRelease( final boolean fTestMode )
    {
        int aRelease = BuildConfig.RELEASE;
        if( fTestMode ) {
            aRelease -= 1;
        }
        return aRelease;
    }
    private static String getJupidatorURL( final boolean fTestMode )
    {
        String aJupidatorURL = BuildConfig.JUPIDATORURL;
        if( fTestMode ) {
            try {
                final URL aURL = new URL(aJupidatorURL);
                final File aFile = new File( aURL.getFile() );
                final String aBaseName = FilenameUtils.getBaseName( aFile.getName() );
                aJupidatorURL = aJupidatorURL.replaceFirst(aBaseName+"\\.", aBaseName+"-new.");
            } catch (final MalformedURLException fEx) {
                LOG.warn("Nanu?", fEx);
            }
        }
        return aJupidatorURL;
    }

    public static void main( final String[] fArgs)
    {
//        System.setProperty( "log4jfilename", "AppUpdater.log" );
//        LOG = Logger.getLogger(AppUpdater.class);
        new AppUpdater();
    }
}

// ############################################################################