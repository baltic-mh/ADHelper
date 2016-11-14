/**
 * ISingletonWatcher.java
 *
 * Created on 12.03.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.controller;

// ############################################################################
public interface ISingletonWatcher
{
    boolean isConnected();
    String getRemoteInfo();
    void start() throws Exception;
    void stop();
}

// ############################################################################
