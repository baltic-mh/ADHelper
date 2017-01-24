/**
 * BalanceReader.java
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import teambaltic.adhelper.controller.ListProvider;
import teambaltic.adhelper.factories.BalanceFactory;
import teambaltic.adhelper.factories.IItemFactory;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class BalanceReader
{
    private static final Logger sm_Log = Logger.getLogger(BalanceReader.class);

    // ------------------------------------------------------------------------
    private final File m_File;
    public File getFile(){ return m_File; }
    // ------------------------------------------------------------------------

    private final IItemFactory<Balance> m_ItemFactory;

    public BalanceReader( final File fFile )
    {
        m_File = fFile;
        m_ItemFactory = new BalanceFactory();
    }

    public void read(final ListProvider<InfoForSingleMember> fListProvider) throws Exception
    {
        final File aFile = getFile();
        FileUtils.checkFile( aFile );

        final Map<Integer, InfoForSingleMember> aMemberInfoMap = new HashMap<>();

        final List<String>aColumnNames = FileUtils.readColumnNames( aFile );
        final List<String> aAllLines = FileUtils.readAllLines( aFile, 1 );
        for( final String aSingleLine : aAllLines ){
            final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aSingleLine );
            final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
            final int aMemberID = Integer.parseInt( aIDString );
            final InfoForSingleMember aInfo = getMemberInfo( aMemberID, aMemberInfoMap, fListProvider );
            if( aInfo == null ){
                sm_Log.error( String.format( "Mitglied mit der ID %d nicht gefunden!", aMemberID ) );
                continue;
            }
            final Balance aItem = m_ItemFactory.createItem( aMemberID, aAttributes);
            aInfo.addBalance( aItem );
        }

        for( final InfoForSingleMember aInfo : aMemberInfoMap.values() ){
            final int aMemberID    = aInfo.getID();
            final Balance aBalance = aInfo.getBalance();
            aInfo.setDutyCharge( new DutyCharge(aMemberID, aBalance ) );
        }
    }

    private static InfoForSingleMember getMemberInfo(
            final int fMemberID,
            final Map<Integer, InfoForSingleMember> fMemberInfoMap,
            final ListProvider<InfoForSingleMember> fListProvider )
    {
        InfoForSingleMember aInfo = fMemberInfoMap.get( fMemberID );
        if( aInfo == null ){
            aInfo = fListProvider.get( fMemberID );
            if( aInfo != null ){
                fMemberInfoMap.put( fMemberID, aInfo );
            }
        }
        return aInfo;
    }

}

// ############################################################################
