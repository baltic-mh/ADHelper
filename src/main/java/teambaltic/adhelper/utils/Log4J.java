/**
 * Log4J.java
 *
 * Created on 29.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

//############################################################################
public final class Log4J
{

    private Log4J() {/**/}

    public static void initLog4J()
    {
        final Properties aProperties = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream("log4j.properties");
        } catch (final FileNotFoundException fEx) { /**/
        }
        if (is == null) {
            is = Log4J.class.getResourceAsStream("log4j.properties");
            if (is == null) {
                is = Log4J.class.getResourceAsStream("/log4j.properties");
                if (is == null) {
                    BasicConfigurator.configure();
                    Logger.getRootLogger().error("Datei log4j.properties nicht gefunden!");
                    return;
                }
            }
        }
        try {
            aProperties.load(is);
            PropertyConfigurator.configure(aProperties);
        } catch (final Throwable fEx) {
            System.err.println("Could not load log4j properties! "+ fEx.getMessage());
        }
    }

    public static void reset()
    {
        BasicConfigurator.resetConfiguration();
        LogManager.resetConfiguration();
    }
}
//############################################################################
