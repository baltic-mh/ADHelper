/**
 * InvoicingPeriodFolderFilter.java
 *
 * Created on 16.02.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;

// ############################################################################
public class InvoicingPeriodFolderFilter implements FilenameFilter
{

    public static final String sm_SplitRegex = "\\s*-\\s*";
    public static final String sm_MatchRegex = "\\d{4}-\\d{2}-\\d{2} - \\d{4}-\\d{2}-\\d{2}";

    // ------------------------------------------------------------------------
    private final String m_FileName_Finished;
    private String getFileName_Finished(){ return m_FileName_Finished; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final String m_FileName_Uploaded;
    private String getFileName_Uploaded(){ return m_FileName_Uploaded; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final boolean m_RejectIfExists_Finished;
    private boolean isRejectIfExists_Finished(){ return m_RejectIfExists_Finished; }
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    private final boolean m_RejectIfExists_Uploaded;
    private boolean isRejectIfExists_Uploaded(){ return m_RejectIfExists_Uploaded; }
    // ------------------------------------------------------------------------

    public static InvoicingPeriodFolderFilter createFilter_All()
    {
        return new InvoicingPeriodFolderFilter( false, null, false, null );
    }

    public static InvoicingPeriodFolderFilter createFilter_NotFinished(
            final String fFileName_Finished)
    {
        return new InvoicingPeriodFolderFilter( true, fFileName_Finished, false, null );
    }

    public static InvoicingPeriodFolderFilter createFilter_FinishedButNotUploaded(
            final String fFileName_Finished,
            final String fFileName_Uploaded)
    {
        return new InvoicingPeriodFolderFilter(
                false, fFileName_Finished,
                true,  fFileName_Uploaded );
    }

    public static InvoicingPeriodFolderFilter createFilter_FinishedAndUploaded(
            final String fFileName_Finished,
            final String fFileName_Uploaded)
    {
        return new InvoicingPeriodFolderFilter(
                false, fFileName_Finished,
                false, fFileName_Uploaded );
    }

    private InvoicingPeriodFolderFilter(
            final boolean fRejectIfExists_Finished,
            final String fFileName_Finished,
            final boolean fRejectIfExists_Uploaded,
            final String fFileName_Uploaded)
    {
        assertLegalArgumentCombination(fRejectIfExists_Finished, fFileName_Finished);
        assertLegalArgumentCombination(fRejectIfExists_Uploaded, fFileName_Uploaded);
        m_RejectIfExists_Finished   = fRejectIfExists_Finished;
        m_FileName_Finished         = fFileName_Finished;
        m_RejectIfExists_Uploaded   = fRejectIfExists_Uploaded;
        m_FileName_Uploaded         = fFileName_Uploaded;
    }

    @Override
    public boolean accept( final File fDir, final String fName )
    {
        final Path aDir = fDir.toPath().resolve( fName );
        if( !Files.isDirectory( aDir ) ){
            return false;
        }
        final boolean aMatches = fName.matches( sm_MatchRegex );
        if( !aMatches ){
            return false;
        }

        if( reject( aDir, getFileName_Finished(), isRejectIfExists_Finished() ) ){
            return false;
        }

        if( reject( aDir, getFileName_Uploaded(), isRejectIfExists_Uploaded() ) ){
            return false;
        }

        return true;
    }

    private static boolean reject( final Path aDir, final String aFileName, final boolean aRejectIfExists )
    {
        if( aFileName != null ){
            final Path aFile = aDir.resolve( aFileName );
            if( aRejectIfExists ){
                if( Files.exists( aFile )){
                    return true;
                }
            } else {
                if( !Files.exists( aFile )){
                    return true;
                }
            }
        }
        return false;
    }

    private static void assertLegalArgumentCombination(
            final boolean fRejectIfExists, final String fFileName )
    {
        if( fRejectIfExists && fFileName == null ){
            throw new IllegalArgumentException( "Reject if exists is true but filename not provided" );
        }
    }

}

// ############################################################################
