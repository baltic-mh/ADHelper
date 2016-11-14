/**
 * WorkEventReader.java
 *
 * Created on 04.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ListProvider;
import teambaltic.adhelper.factories.IItemFactory;
import teambaltic.adhelper.factories.WorkEventFactory;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class WorkEventReader
{
    private static final Logger sm_Log = Logger.getLogger(WorkEventReader.class);

    // ------------------------------------------------------------------------
    private final File m_File;
    public File getFile(){ return m_File; }
    // ------------------------------------------------------------------------

    private final IItemFactory<WorkEvent> m_WorkEventFactory;

    public WorkEventReader( final File fFile )
    {
        m_File = fFile;
        m_WorkEventFactory = new WorkEventFactory();
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

            final WorkEventsAttended aWorkEventsAttended = getCreateWorkEventsAttended( aInfo );
            final WorkEvent aWorkEvent = m_WorkEventFactory.createItem( aID, aAttributes);
            aWorkEventsAttended.addWorkEvent( aWorkEvent );
        }

    }

    private static WorkEventsAttended getCreateWorkEventsAttended( final InfoForSingleMember fInfo )
    {
        WorkEventsAttended aWorkEventsAttended = fInfo.getWorkEventsAttended();
        if( aWorkEventsAttended == null ){
            aWorkEventsAttended = new WorkEventsAttended( fInfo.getID() );
            fInfo.setWorkEventsAttended( aWorkEventsAttended );
        }
        return aWorkEventsAttended;
    }
}

// ############################################################################
