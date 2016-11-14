/**
 * LocalRemotePathPair.java
 *
 * Created on 05.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.remoteaccess;

import java.nio.file.Path;

// ############################################################################
public class LocalRemotePathPair
{
    // ------------------------------------------------------------------------
    private final Path m_Local;
    public Path getLocal(){ return m_Local; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Path m_Remote;
    public Path getRemote(){ return m_Remote; }
    // ------------------------------------------------------------------------

    public LocalRemotePathPair(final Path fLocal, final Path fRemote)
    {
        m_Local  = fLocal;
        m_Remote = fRemote;
    }

    @Override
    public String toString()
    {
        return String.format( "%s -> %s", getLocal(), getRemote() );
    }
}

// ############################################################################
