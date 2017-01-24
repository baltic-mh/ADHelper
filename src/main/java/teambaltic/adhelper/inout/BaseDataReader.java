/**
 * BaseDataReader.java
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import teambaltic.adhelper.controller.ListProvider;
import teambaltic.adhelper.factories.FreeFromDutySetFactory;
import teambaltic.adhelper.factories.IItemFactory;
import teambaltic.adhelper.factories.MemberFactory;
import teambaltic.adhelper.model.DutyCharge;
import teambaltic.adhelper.model.FreeFromDutySet;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.model.InfoForSingleMember;
import teambaltic.adhelper.utils.FileUtils;

// ############################################################################
public class BaseDataReader
{
//    private static final Logger sm_Log = Logger.getLogger(BaseDataReader.class);

    private static final Comparator<? super IClubMember> COMPARATOR =
            new Comparator<IClubMember>(){
                @Override
                public int compare( final IClubMember fClubMember1, final IClubMember fClubMember2 )
                {
                    return( fClubMember1.getName().compareTo( fClubMember2.getName() ) );
                }
            };

    // ------------------------------------------------------------------------
    private final File m_File;
    public File getFile(){ return m_File; }
    // ------------------------------------------------------------------------

    private final IItemFactory<IClubMember>     m_MemberFactory;
    private final IItemFactory<FreeFromDutySet> m_FFDSetFactory;


    public BaseDataReader(final File fFile)
    {
        m_File = fFile;

        m_MemberFactory = new MemberFactory();
        m_FFDSetFactory = new FreeFromDutySetFactory();
    }

    public List<IClubMember> read(final ListProvider<InfoForSingleMember> fListProvider) throws Exception
    {
        final File aFile = getFile();
        FileUtils.checkFile( aFile );

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

        aAllMembers.sort( COMPARATOR );
        return aAllMembers;

    }

    public void populateInfoForSingleMember(
            final InfoForSingleMember fInfo,
            final Map<String, String> fAttributes )
    {
        final int aID = fInfo.getID();
        final IClubMember aClubMember = m_MemberFactory.createItem( aID, fAttributes);
        fInfo.setMember( aClubMember );
        final FreeFromDutySet aFFDSet = m_FFDSetFactory.createItem( aID, fAttributes);
        fInfo.setFreeFromDutySet( aFFDSet );
        fInfo.setDutyCharge( new DutyCharge(aID, null ) );
    }

}

// ############################################################################
