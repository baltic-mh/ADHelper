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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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
        final URLConnection aConnection = fURL.openConnection();
        final InputStream aContent = aConnection.getInputStream();
        final InputStreamReader aInput = new InputStreamReader(aContent);
        final StringBuffer aSB = new StringBuffer();
        while( aInput.ready() ) {
            final char[] aCharBuf = new char[1024];
            // char[] cbuf, int offset, int length
            final int aNumCharsRead = aInput.read(aCharBuf, 0, aCharBuf.length);
            if(aNumCharsRead > 0) {
                aSB.append(aCharBuf, 0, aNumCharsRead);
            }
        }
        return aSB.toString();
    }

}

// ############################################################################
