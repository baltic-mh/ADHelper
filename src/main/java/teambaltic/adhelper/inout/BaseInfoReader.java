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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import teambaltic.adhelper.controller.ListProvider;
import teambaltic.adhelper.model.Balance;
import teambaltic.adhelper.model.FreeFromDuty;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class BaseInfoReader
{
//    private static final Logger sm_Log = Logger.getLogger(BaseInfoReader.class);

    // ------------------------------------------------------------------------
    private final File m_File;
    public File getFile(){ return m_File; }
    // ------------------------------------------------------------------------

    private final IItemFactory<IClubMember>     m_MemberFactory;
    private final IItemFactory<FreeFromDuty>    m_FreeFromDutyFactory;
    private final IItemFactory<Balance>         m_BalanceFactory;

    public BaseInfoReader(final File fFile)
    {
        m_File = fFile;

        m_MemberFactory         = new MemberFactory();
        m_FreeFromDutyFactory   = new FreeFromDutyFactory();
        m_BalanceFactory        = new BalanceFactory();
    }

    public Collection<IClubMember> read(final ListProvider<InfoForSingleMember> fListProvider) throws Exception
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

        final List<IClubMember> aAllMembers = new ArrayList<>();
        final List<String>aColumnNames = FileUtils.readColumnNames( aFile );
        final List<String> aAllLines = FileUtils.readAllLines( aFile, 1 );
        for( final String aSingleLine : aAllLines ){
            final Map<String, String> aAttributes = FileUtils.makeMap( aColumnNames, aSingleLine );
            final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
            final int aID = Integer.parseInt( aIDString );
            InfoForSingleMember aInfo = fListProvider.get( aID );
            if( aInfo == null ){
                aInfo = new InfoForSingleMember( aID );
                fListProvider.add( aInfo );
            }
            populateInfoForSingleMember( aInfo, aAttributes );
            aAllMembers.add( aInfo.getMember() );
        }

        final Comparator<? super IClubMember> aComp = new Comparator<IClubMember>(){
            @Override
            public int compare( final IClubMember fClubMember1, final IClubMember fClubMember2 )
            {
                return( fClubMember1.getName().compareTo( fClubMember2.getName() ) );
            }

        };
        aAllMembers.sort( aComp );
        return aAllMembers;

    }

    public void populateInfoForSingleMember(
            final InfoForSingleMember fInfo,
            final Map<String, String> fAttributes )
    {
        final int aID = fInfo.getID();
        final IClubMember aClubMember = m_MemberFactory.createItem( aID, fAttributes);
        fInfo.setMember( aClubMember );
        final FreeFromDuty aFFD = m_FreeFromDutyFactory.createItem( aID, fAttributes);
        if( aFFD != null ){
            fInfo.setFreeFromDuty( aFFD );
        }
        final Balance aBalance = m_BalanceFactory.createItem( aID, fAttributes);
        if( aBalance != null ){
            fInfo.setBalance( aBalance );
        }
    }

}

// ############################################################################
