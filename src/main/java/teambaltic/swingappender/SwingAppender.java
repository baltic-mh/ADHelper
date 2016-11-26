/**
 *
 */
package teambaltic.swingappender;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import teambaltic.swingappender.ui.SwingAppenderUI;

/**
 * @author kalpak
 *
 */
public class SwingAppender extends AppenderSkeleton {

    /** The appender swing UI. */
    private final SwingAppenderUI appenderUI = SwingAppenderUI.getInstance();

    public SwingAppender() {
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    protected void append(final LoggingEvent event) {
        if (!performChecks()) {
        	return;
        }
        final String logOutput = this.layout.format(event);
        appenderUI.doLog(logOutput);

        if (layout.ignoresThrowable()) {
        	final String[] lines = event.getThrowableStrRep();
			if (lines != null) {
				final int len = lines.length;
				for (int i = 0; i < len; i++) {
					appenderUI.doLog(lines[i]);
					appenderUI.doLog(Layout.LINE_SEP);
				}
			}
		}
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see org.apache.log4j.Appender#close()
	 */
    @Override
    public void close() {
        //Opportunity for the appender ui to do any cleanup.
        /*appenderUI.close();
        appenderUI = null;*/
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#requiresLayout()
     */
    @Override
    public boolean requiresLayout() {
        return true;
    }

    /**
     * Performs checks to make sure the appender ui is still alive.
     *
     * @return
     */
    private boolean performChecks() {
        return !closed && layout != null;
    }
}
