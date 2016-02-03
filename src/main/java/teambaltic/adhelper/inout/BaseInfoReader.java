/**
 * BaseInfoReader.java
 *
 * Created on 30.01.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.inout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.HoursWorked;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class BaseInfoReader
{
    private static final Logger sm_Log = Logger.getLogger(BaseInfoReader.class);

    // ------------------------------------------------------------------------
    private final File m_File;
    public File getFile(){ return m_File; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Collection<IClubMember> m_MemberList;
    public Collection<IClubMember> getMemberList(){ return m_MemberList; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Collection<FreeFromDuty> m_FreeFromDutyList;
    public Collection<FreeFromDuty> getFreeFromDutyList(){ return m_FreeFromDutyList; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Collection<HoursWorked> m_HoursWorkedList;
    public Collection<HoursWorked> getHoursWorkedList(){ return m_HoursWorkedList; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final Collection<Balance> m_BalanceList;
    public Collection<Balance> getBlanceList(){ return m_BalanceList; }
    // ------------------------------------------------------------------------

    private final IItemFactory<IClubMember>     m_MemberFactory;
    private final IItemFactory<FreeFromDuty>    m_FreeFromDutyFactory;
    private final IItemFactory<Balance>         m_BalanceFactory;

    public BaseInfoReader(final File fFile)
    {
        m_File = fFile;

        m_MemberList            = new ArrayList<>();
        m_FreeFromDutyList      = new ArrayList<>();
        m_BalanceList           = new ArrayList<>();
        m_HoursWorkedList       = new ArrayList<>();

        m_MemberFactory         = new MemberFactory();
        m_FreeFromDutyFactory   = new FreeFromDutyFactory();
        m_BalanceFactory        = new BalanceFactory();
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

        m_MemberList.clear();
        m_FreeFromDutyList.clear();
        final List<String>aColumnNames = readColumnNames( aFile );
        final List<String> aAllLines = FileUtils.readAllLines( aFile, 1 );
        for( final String aSingleLine : aAllLines ){
            final Map<String, String> aAttributes = makeMap( aColumnNames, aSingleLine );
            final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
            final int aID = Integer.parseInt( aIDString );
            final IClubMember aClubMember = m_MemberFactory.createItem( aID, aAttributes);
            m_MemberList.add( aClubMember );
            final FreeFromDuty aFFD = m_FreeFromDutyFactory.createItem( aID, aAttributes);
            if( aFFD != null ){
                sm_Log.info(aClubMember+": "+ aFFD);
                m_FreeFromDutyList.add( aFFD );
            }
            final Balance aBalance = m_BalanceFactory.createItem( aID, aAttributes);
            if( aBalance != null ){
                sm_Log.info(aClubMember+": Guthaben: "+ aBalance);
                m_BalanceList.add( aBalance );
            }
        }

    }

    private static List<String> readColumnNames( final File fFile )
    {
        final String aFirstLine = FileUtils.readFirstLine( fFile );
        final String[] aColumnNames = aFirstLine.split( ";" );
        final List<String> aAsList = Arrays.asList( aColumnNames );
        return aAsList;
    }

    private static Map<String, String> makeMap(
            final List<String> fColumnNames, final String fSingleLine )
    {
        final Map<String, String> aMap = new HashMap<>();
        final String[] aSplit = fSingleLine.split( ";" );
        for( int aIdx = 0; aIdx < aSplit.length; aIdx++ ){
            final String aString = aSplit[aIdx];
            aMap.put( fColumnNames.get( aIdx ), aString );
        }
        return aMap;
    }


    private FreeFromDuty createFreeFromDuty( final int fMemberID, final List<String> fColumnNames, final String fSingleLine )
    {
        return null;
    }

}

// ############################################################################
