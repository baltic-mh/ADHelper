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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.WorkEvent;
import teambaltic.adhelper.model.WorkEventsAttended;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class WorkEventReader
{
//    private static final Logger sm_Log = Logger.getLogger(WorkEventReader.class);

    // ------------------------------------------------------------------------
    private final File m_File;
    public File getFile(){ return m_File; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Map<Integer, WorkEventsAttended> m_WorkEventsAttendedMap;
    public Collection<WorkEventsAttended> getWorkEventsAttendedList(){ return m_WorkEventsAttendedMap.values(); }
    // ------------------------------------------------------------------------


    private final IItemFactory<WorkEvent> m_WorkEventFactory;

    public WorkEventReader( final File fFile )
    {
        m_File = fFile;

        m_WorkEventsAttendedMap = new HashMap<>();

        m_WorkEventFactory      = new WorkEventFactory();
    }

    public void read() throws Exception
    {
        final File aFile = getFile();
        if( !aFile.exists() ){
            throw new Exception("File does not exist: "+aFile.getPath());
        }
        if( !aFile.isFile() ){
            throw new Exception("File is no regular file: "+aFile.getPath());
        }
        if( !aFile.canRead() ){
            throw new Exception("Cannot read file: "+aFile.getPath());
        }

        m_WorkEventsAttendedMap.clear();
        final List<String>aColumnNames = FileUtils.readColumnNames( aFile );
        final List<String> aAllLines = FileUtils.readAllLines( aFile, 1 );
        for( final String aSingleLine : aAllLines ){
            final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aSingleLine );
            final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
            final int aID = Integer.parseInt( aIDString );
            final Integer aIDInteger = Integer.valueOf( aID );
            final WorkEventsAttended aWorkEventsAttended = getCreateWorkEventsAttended( aIDInteger );
            final WorkEvent aWorkEvent = m_WorkEventFactory.createItem( aID, aAttributes);
            aWorkEventsAttended.addWorkEvent( aWorkEvent );
        }

    }

    private WorkEventsAttended getCreateWorkEventsAttended( final Integer aIDInteger )
    {
        WorkEventsAttended aWorkEventsAttended = m_WorkEventsAttendedMap.get( aIDInteger );
        if( aWorkEventsAttended == null ){
            aWorkEventsAttended = new WorkEventsAttended( aIDInteger.intValue() );
            m_WorkEventsAttendedMap.put( aIDInteger, aWorkEventsAttended );
        }
        return aWorkEventsAttended;
    }
}

// ############################################################################
