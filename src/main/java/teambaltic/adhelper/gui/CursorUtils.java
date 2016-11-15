/**
 * CursorUtils.java
 *
 * Created on 15.11.2016
 * by <a href="mailto:mhw@teambaltic.de">Mathias-H.&nbsp;Weber&nbsp;(MW)</a>
 *
 * Coole Software - Mein Beitrag im Kampf gegen die Klimaerwärmung!
 *
 * Copyright (C) 2016 Team Baltic. All rights reserved
 */
// ############################################################################
package teambaltic.adhelper.gui;

// ############################################################################
import java.awt.Cursor;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

public class CursorUtils
{
    public interface Cursors {
        Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
        Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    }

    private final static MouseAdapter mouseAdapter = new MouseAdapter() {};

    public static void startWaitCursor(final JComponent component) {
        final RootPaneContainer root =
                (RootPaneContainer)component.getTopLevelAncestor();
              root.getGlassPane().setCursor(Cursors.WAIT_CURSOR);
              root.getGlassPane().addMouseListener(mouseAdapter);
              root.getGlassPane().setVisible(true);
    }

    public static void stopWaitCursor(final JComponent component) {
        final RootPaneContainer root =
                (RootPaneContainer)component.getTopLevelAncestor();
              root.getGlassPane().setCursor(Cursors.DEFAULT_CURSOR);
              root.getGlassPane().removeMouseListener(mouseAdapter);
              root.getGlassPane().setVisible(false);
    }
}
// ############################################################################
