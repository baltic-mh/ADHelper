/**
 * MemberReader.java
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import teambaltic.adhelper.model.ClubMember;
import teambaltic.adhelper.model.IClubMember;
import teambaltic.adhelper.model.IKnownColumns;
import teambaltic.adhelper.utils.FileUtils;
import teambaltic.adhelper.utils.ParseUtils;

// ############################################################################
public class MemberReader
{
    private static final Logger sm_Log = Logger.getLogger(MemberReader.class);

    private final static Set<String> IGNORED_COLUMNS = new HashSet<>();
    static {
        IGNORED_COLUMNS.add( IKnownColumns.MEMBERID );
        IGNORED_COLUMNS.add( IKnownColumns.PLZ );
        IGNORED_COLUMNS.add( IKnownColumns.ORT );
        IGNORED_COLUMNS.add( IKnownColumns.ANREDE );
        IGNORED_COLUMNS.add( IKnownColumns.STREET );
        IGNORED_COLUMNS.add( IKnownColumns.BEITRAGSART );
    }


    public static Collection<IClubMember> readFrom(final File fFile) throws Exception
    {
        if( !fFile.exists() ){
            throw new Exception("File does not exist: "+fFile.getPath());
        }
        if( !fFile.isFile() ){
            throw new Exception("File is no regular file: "+fFile.getPath());
        }
        if( !fFile.canRead() ){
            throw new Exception("Cannot read file: "+fFile.getPath());
        }

        final Collection<IClubMember> aClubMembers = new ArrayList<>();
        final List<String>aColumnNames = readColumnNames( fFile );
        final List<String> aAllLines = FileUtils.readAllLines( fFile, 1 );
        for( final String aSingleLine : aAllLines ){
            final IClubMember aClubMember = createClubMember(aColumnNames, aSingleLine);
            aClubMembers.add( aClubMember );
        }
        return aClubMembers;
    }

    private static List<String> readColumnNames( final File fFile )
    {
        final String aFirstLine = FileUtils.readFirstLine( fFile );
        final String[] aColumnNames = aFirstLine.split( ";" );
        final List<String> aAsList = Arrays.asList( aColumnNames );
        return aAsList;
    }

    private static IClubMember createClubMember(
            final List<String> fColumnNames, final String fSingleLine )
    {
        final Map<String, String> aAttributes = makeMap( fColumnNames, fSingleLine );
        final String aIDString = aAttributes.get( IKnownColumns.MEMBERID );
        final int aID = Integer.parseInt( aIDString );
        final ClubMember aCM = new ClubMember( aID );
        populate( aCM, aAttributes );
        return aCM;
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

    private static void populate(
            final ClubMember fCM, final Map<String, String> fAttributes )
    {
        String aNachname = "";
        String aVorname  = "";
        for( final Entry<String, String> aEntry : fAttributes.entrySet() ){
            final String aKey = aEntry.getKey();
            if( ignoreColumn(aKey) ){
                continue;
            }
            final String aValue = aEntry.getValue();
            switch( aKey ){
                case IKnownColumns.NAME:
                    aNachname = aValue;
                    break;
                case IKnownColumns.FIRSTNAME:
                    aVorname = aValue;
                    break;
                case IKnownColumns.BIRTHDAY:
                    final LocalDate aBirthday = ParseUtils.getDate( aValue );
                    if( aBirthday != null ){
                        fCM.setBirthday( aBirthday );
                    }
                    break;
                case IKnownColumns.EINTRITT:
                    final LocalDate aEintritt = ParseUtils.getDate( aValue );
                    if( aEintritt != null ){
                        fCM.setMemberSince( aEintritt );
                    }
                    break;
                case IKnownColumns.AUSTRITT:
                    final LocalDate aAustritt = ParseUtils.getDate( aValue );
                    if( aAustritt != null ){
                        fCM.setMemberUntil( aAustritt );
                    }
                    break;
                case IKnownColumns.LINKID:
                    final Integer aLink = ParseUtils.getInteger( aValue );
                    if( aLink != null ){
                        fCM.setLinkID( aLink.intValue() );
                    }
                    break;
                default:
                    sm_Log.warn("Unknown column: "+aKey);
            }
        }
        fCM.setName( String.format("%s %s", aVorname, aNachname) );
    }

    private static boolean ignoreColumn( final String fColumn )
    {

        if( IGNORED_COLUMNS.contains( fColumn ) ){
            return true;
        }
        if( fColumn.startsWith( IKnownColumns.GUTHABEN_PREFIX ) ){
            return true;
        }
        return false;
    }

}

// ############################################################################
