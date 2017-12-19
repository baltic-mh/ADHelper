/**
 * Adjustment.java
 *
 * Created on 06.03.2017
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2017 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model;

// ############################################################################
public class Adjustment extends Participation
{
    // ------------------------------------------------------------------------
    private String m_Comment;
    public String getComment(){ return m_Comment; }
    public void setComment( final String fComment ) { m_Comment = fComment; }
    // ------------------------------------------------------------------------

    public Adjustment( final int fMemberID )
    {
        super( fMemberID );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( m_Comment == null ) ? 0 : m_Comment.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if( this == obj )
            return true;
        if( !super.equals( obj ) )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        final Adjustment other = (Adjustment) obj;
        if( m_Comment == null ){
            if( other.m_Comment != null )
                return false;
        }else if( !m_Comment.equals( other.m_Comment ) )
            return false;
        return true;
    }

}

// ############################################################################
