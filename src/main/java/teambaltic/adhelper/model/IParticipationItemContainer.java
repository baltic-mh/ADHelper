/**
 * IParticipationItemContainer.java
 *
 * Created on 09.03.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

import java.util.List;

// ############################################################################
public interface IParticipationItemContainer<ParticipationType extends Participation>
{
    List<ParticipationType> getParticipationList();

    void add( ParticipationType fParticipationItem );
    void remove( ParticipationType fParticipationItem );

}

// ############################################################################
