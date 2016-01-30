/**
 * ADHelperApplication.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.view;

import javax.swing.UIManager;

import teambaltic.adhelper.utils.Log4J;

// ############################################################################
public class ADHelperApplication
{

    //====================================================================
    public static void main(final String[] args)
    {
        System.setProperty( "appname", "ADHelper" );
        Log4J.initLog4J();
        try {
            UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
        } catch (final Exception e) {
            // Likely PlasticXP is not in the class path; ignore.
        }
//        new LabelChanger().createAndShowGUI();
    }

    //====================================================================
}

// ############################################################################
