/**
 * ParticipationFactory.java
 *
 * Created on 04.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.factories;

import java.time.LocalDate;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.Participation;
import teambaltic.adhelper.utils.ParseUtils;

// ############################################################################
public abstract class ParticipationFactory<ParticipationType extends Participation>
    implements IItemFactory<Participation>
{
    private static final Logger sm_Log = Logger.getLogger(ParticipationFactory.class);

    // ------------------------------------------------------------------------
    private final String m_ColName_Hours;
    private String getColName_Hours(){ return m_ColName_Hours; }
    // ------------------------------------------------------------------------

    // ========================================================================
    // Abstrakte Methoden:
    abstract
    protected ParticipationType cast(Participation fParticipation);

    abstract
    public ParticipationType createItem(int fID);
    // ========================================================================

    public ParticipationFactory( final String fColName_Hours)
    {
        m_ColName_Hours = fColName_Hours;

    }

    @Override
    public void populateItem( final Participation fParticipation, final Map<String, String> fAttributes )
    {
        final String aDateValue = fAttributes.get( IKnownColumns.DATE );
        final LocalDate aDate = ParseUtils.getDate( aDateValue );
        if( aDate != null ){
            fParticipation.setDate( aDate );
        }

        final String aHoursValue = fAttributes.get( getColName_Hours() );
        try{
            final float aFloatValue  = Float.parseFloat( aHoursValue.replaceAll( ",", "." ) );
            final int   aHoursWorked = Math.round( aFloatValue * 100 );
            fParticipation.setHours( aHoursWorked );
            final ParticipationType aSpecificParticipation = cast( fParticipation );
            populateSpecificAttributes( aSpecificParticipation, fAttributes );
        }catch( final NumberFormatException fEx ){
            sm_Log.warn("Stunden-Angabe ist keine Zahl: "+ aHoursValue );
        }

    }

    public void populateSpecificAttributes( final ParticipationType fSpecificParticipation, final Map<String, String> fAttributes )
    {
        // Default: Keine spezifischen Attribute!
    }

}

// ############################################################################
