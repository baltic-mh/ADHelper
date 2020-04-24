/**
 * AppUpdater.java
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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        if( aJupidatorURL == null ){
            return;
        }
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
            LOG.error(String.format("Probleme beim Zugriff auf den Update-Server (%s): %s", aJupidatorURL, fEx.getMessage()) );
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
        String aJupidatorURL = getJupidatorURL();
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

    private static String getJupidatorURL()
    {
        final String aURLStr = BuildConfig.URL_FOR_FILEWITH_JUPIDATORURL;
        LOG.info("Lese UpdateURL von: "+aURLStr);
        String aContentFromURL;
        try{
            aContentFromURL = NetworkUtils.getContentFromURL( aURLStr );
            if( aContentFromURL.isEmpty() ) {
            	LOG.warn(String.format( "Konnte Inhalt nicht lesen von URL: %s", aURLStr));
            	return null;
            }
        }catch( final Exception fEx ){
            LOG.warn(String.format( "Konnte Inhalt nicht lesen von URL: %s", aURLStr), fEx );
            return null;
        }
        final List<String> aNonCommentLines = getNonCommentLines( aContentFromURL );
        if( aNonCommentLines.size() == 0 ){
            LOG.warn(String.format( "Datei '%s' enthält keine JupidatorURL: %s", aURLStr, aContentFromURL ));
            return null;
        }
        if( aNonCommentLines.size() > 1 ){
            LOG.warn(String.format( "Datei '%s' enthält mehr als eine Nicht-Kommentarzeile: %s", aURLStr, aContentFromURL ));
        }
        final String aJupidatorURLStr = aNonCommentLines.get( 0 );
        try{
            new URL(aJupidatorURLStr);
        }catch( final MalformedURLException fEx ){
            LOG.error(String.format( "Vermutlich kein URL: %s - %s", fEx.getMessage(), aJupidatorURLStr  ) );
            return null;
        }
        LOG.info("UpdateURL: "+aJupidatorURLStr);
        return aJupidatorURLStr;
    }

    static List<String> getNonCommentLines( final String fMultiLineString )
    {
        final String[] aAllLines = fMultiLineString.split( "\\n" );
        final List<String> aNonCommentLines = new ArrayList<>();
        for( final String aLine : aAllLines ){
            final String aTrimmedLine = aLine.trim();
            if( aTrimmedLine.startsWith( "#" ) ){
                continue;
            }
            aNonCommentLines.add( aTrimmedLine );
        }
        return aNonCommentLines;
    }

}

// ############################################################################
