/**
 * NetworkUtils.java
 *
 * Created on 14.11.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

// ############################################################################
public final class NetworkUtils
{
    private NetworkUtils(){/**/}

    //====================================================================
    public static String getContentFromURL(final String fURL) throws Exception
    {
        final URL aURL = new URL(fURL);
        return getContentFromURL(aURL);
    }

    //====================================================================
    public static String getContentFromURL(final URL fURL) throws IOException
	{
		final BufferedReader in = new BufferedReader(new InputStreamReader(fURL.openStream()));

		final StringBuffer aSB = new StringBuffer();
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			aSB.append(inputLine).append( System.lineSeparator() );
		}
		in.close();

		return aSB.toString();
	}

}

// ############################################################################
