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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.FreeFromDuty;
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
    private final Collection<Balance> m_BalanceList;
    public Collection<Balance> getBalanceList(){ return m_BalanceList; }
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

        clearLists();
        final List<String>aColumnNames = FileUtils.readColumnNames( aFile );
        final List<String> aAllLines = FileUtils.readAllLines( aFile, 1 );
        for( final String aSingleLine : aAllLines ){
            final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aSingleLine );
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

    private void clearLists()
    {
        m_MemberList.clear();
        m_FreeFromDutyList.clear();
        m_BalanceList.clear();
    }

}

// ############################################################################
