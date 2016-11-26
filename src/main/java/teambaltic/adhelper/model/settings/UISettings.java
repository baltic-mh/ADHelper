/**
 * UISettings.java
 *
 * Created on 26.11.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.model.settings;

import java.nio.file.Path;

// ############################################################################
public class UISettings extends ASettings<IUISettings.EKey>
    implements IUISettings
{
    public UISettings(final Path fSettingsFile) throws Exception
    {
        super();
        init(fSettingsFile);
    }

    @Override
    protected EKey[] getKeyValues(){ return EKey.values(); }

    @Override
    public int getMainFrame_Width()
    {
        return getIntValue( EKey.MAINFRAME_WIDTH );
    }
    @Override
    public void setMainFrame_Width( final int fValue )
    {
        setIntValue( EKey.MAINFRAME_WIDTH, fValue );
    }

    @Override
    public int getMainFrame_Height()
    {
        return getIntValue( EKey.MAINFRAME_HEIGHT );
    }
    @Override
    public void setMainFrame_Height( final int fValue )
    {
        setIntValue( EKey.MAINFRAME_HEIGHT, fValue );
    }


    @Override
    public int getMainFrame_PosX()
    {
        return getIntValue( EKey.MAINFRAME_POSX );
    }
    @Override
    public void setMainFrame_PosX( final int fValue )
    {
        setIntValue( EKey.MAINFRAME_POSX, fValue );
    }

    @Override
    public int getMainFrame_PosY()
    {
        return getIntValue( EKey.MAINFRAME_POSY );
    }

    @Override
    public void setMainFrame_PosY( final int fValue )
    {
        setIntValue( EKey.MAINFRAME_POSY, fValue );
    }

}

// ############################################################################
