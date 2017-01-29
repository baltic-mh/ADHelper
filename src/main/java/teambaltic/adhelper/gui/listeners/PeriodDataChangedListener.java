/**
 * PeriodDataChangedListener.java
 *
 * Created on 11.04.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui.listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ADH_DataProvider;
import teambaltic.adhelper.model.PeriodData;

// ############################################################################
public class PeriodDataChangedListener implements ItemListener
{
    private static final Logger sm_Log = Logger.getLogger(PeriodDataChangedListener.class);

    // ------------------------------------------------------------------------
    private final GUIUpdater m_GUIUpdater;
    private GUIUpdater getGUIUpdater(){ return m_GUIUpdater; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final ADH_DataProvider m_DataProvider;
    private ADH_DataProvider getDataProvider(){ return m_DataProvider; }
    // ------------------------------------------------------------------------

    public PeriodDataChangedListener(
            final GUIUpdater fGUIUpdater,
            final ADH_DataProvider fDataProvider)
    {
        m_GUIUpdater            = fGUIUpdater;
        m_DataProvider          = fDataProvider;
    }

    @Override
    public void itemStateChanged( final ItemEvent fEvent )
    {
        if( ItemEvent.SELECTED == fEvent.getStateChange() ) {
            // Item was just selected
            final PeriodData aPeriodData = (PeriodData) fEvent.getItem();
            if( aPeriodData.getPeriod() == null ){
                return;
            }
            try{
                getDataProvider().init( aPeriodData, Integer.parseInt( System.getProperty( "onlyid", "0" ) ) );
                getGUIUpdater().updateGUI( aPeriodData );
            }catch( final Exception fEx ){
                // TODO Auto-generated catch block
                sm_Log.warn("Exception: ", fEx );
            }
        }
    }

}

// ############################################################################
