/**
 * TestUtils.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import org.apache.log4j.Logger;

// ############################################################################
public final class TestUtils
{
    private static final Logger sm_Log = Logger.getLogger(TestUtils.class);

    private TestUtils(){/**/}

    public static long logMethodStart()
    {
        return logMethodStart( getMethodName(3) );
    }

    public static long logMethodStart(final String fMethodName)
    {
        sm_Log.info( String.format( "Starting '%s'", fMethodName ) );
        return System.currentTimeMillis();
    }

    public static void logMethodEnd( final long fStartTime )
    {
        logMethodEnd( fStartTime, getMethodName(3) );
    }

    public static void logMethodEnd( final long fStartTime, final String fMethodName )
    {
        final long aTimeElapsed = System.currentTimeMillis() - fStartTime;
        sm_Log.info( String.format( "Finished '%s' - time elapsed: %d msecs",
                fMethodName, aTimeElapsed ) );
    }
    public static String getMethodName()
    {
        return getMethodName( 2 );
    }

    private static String getMethodName(final int fStacktraceIdx)
    {
        final Exception aException = new Exception();
        final StackTraceElement[] aStackTrace = aException.getStackTrace();
        final String aMethodName = aStackTrace[fStacktraceIdx].getMethodName();
        return aMethodName;
    }

}

// ############################################################################
