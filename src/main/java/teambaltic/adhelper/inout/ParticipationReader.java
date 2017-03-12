/**
 * ParticipationReader.java
 *
 * Created on 11.03.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ListProvider;
import teambaltic.adhelper.factories.ParticipationFactory;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.IParticipationItemContainer;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.Participation;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public abstract class ParticipationReader<ParticipationType extends Participation>
{
    private static final Logger sm_Log = Logger.getLogger(ParticipationReader.class);

    // ------------------------------------------------------------------------
    private final File m_File;
    public File getFile(){ return m_File; }
    // ------------------------------------------------------------------------

    private final ParticipationFactory<ParticipationType> m_ItemFactory;

    abstract
    protected IParticipationItemContainer<ParticipationType> getCreateParticipationItemContainer( final InfoForSingleMember fInfo );

    public ParticipationReader( final File fFile, final ParticipationFactory<ParticipationType> fParticipationFactory)
    {
        m_File = fFile;
        m_ItemFactory = fParticipationFactory;
    }

    public void read(final ListProvider<InfoForSingleMember> fListProvider) throws Exception
    {
        final File aFile = getFile();
        FileUtils.checkFile( aFile );

        final List<String>aColumnNames = FileUtils.readColumnNames( aFile );
        final List<String> aAllLines = FileUtils.readAllLines( aFile, 1 );
        for( final String aSingleLine : aAllLines ){
            if( aSingleLine.isEmpty() ){
                continue;
            }
            final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aSingleLine );
            final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
            final int aID = Integer.parseInt( aIDString );
            final InfoForSingleMember aInfo = fListProvider.get( aID );
            if( aInfo == null ){
                sm_Log.error( String.format( "Mitglied mit der ID %d nicht gefunden!", aID ) );
                continue;
            }

            final IParticipationItemContainer<ParticipationType> aItemContainer = getCreateParticipationItemContainer( aInfo );
            final ParticipationType aParticipationItem = m_ItemFactory.createItem( aID );
            m_ItemFactory.populateItem(aParticipationItem, aAttributes);
            aItemContainer.add( aParticipationItem );
        }

    }

}

// ############################################################################
