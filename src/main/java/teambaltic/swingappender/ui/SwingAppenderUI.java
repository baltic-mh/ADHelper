/**
 *
 */
package teambaltic.swingappender.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/** Creates a UI to display log messages from a SwingAppender
 * @author pshah
 *
 */
public class SwingAppenderUI {
    //UI attributes
    private final JFrame jframe;
    private JButton startPause; //button for start/pause - toggles
    private JButton stop; //stop button
    private JButton clear; //button to clear the text area
    private JButton search; //search button
    private JTextField searchField; //search field
    private JPanel buttonsPanel; //panel to hold all buttons
    private JTextPane logMessagesDisp; //display area
    private JScrollPane scrollPane;
    //buffer to hold log statements when the UI is set to PAUSE
    private final List<String> logBuffer;
    //flag to indicate if we should display new log events
    private int appState;

    /* Constants */
    public static final String STYLE_REGULAR        = "regular";
    public static final String STYLE_HIGHLIGHTED    = "highlighted";
    public static final String STYLE_WARN           = "warning";
    public static final String STYLE_ERROR          = "error";
    public static final String START = "Start";
    public static final String PAUSE = "Pause";
    public static final String STOP = "Stop";
    public static final String CLEAR = "Klar Schiff";
    public static final int STARTED = 0;
    public static final int PAUSED = 1;
    public static final int STOPPED = 2;

    /**
     * An instance for SwingAppenderUI class. This holds the Singleton.
     */
    private static SwingAppenderUI instance;

    /**
     * Method to get an instance of the this class. This method ensures that
     * SwingAppenderUI is a Singleton using a doule checked locking mechanism.
     * @return An instance of SwingAppenderUI
     */
    public static SwingAppenderUI getInstance() {
//        System.out.println("getting UI Instance");
        if (instance == null) {
            synchronized(SwingAppenderUI.class) {
                if(instance == null) {
                    instance = new SwingAppenderUI();
                }
            }
        }
        return instance;
    }

    /**
     * Private constructer to ensure that this object cannot e instantiated
     * from outside this class.
     */
    private SwingAppenderUI() {
        //set internal attributes
        logBuffer = new ArrayList<>();
        appState = STARTED;

        //create main window
        jframe = new JFrame();
        jframe.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        //initialize buttons
        initButtonsPanel();

        //create text area to hold the log messages
        initMessageDispArea();

        //add components to the contentPane
        jframe.getContentPane().add(BorderLayout.NORTH, buttonsPanel);
        jframe.getContentPane().add(BorderLayout.CENTER, scrollPane);
        jframe.setSize(1200,500);
        jframe.setVisible(true);
    }

    /**Displays the log in the text area unless dispMsg is set to false in which
     * case it adds the log to a buffer. When dispMsg becomes true, the buffer
     * is first flushed and it's contents are displayed in the text area.
     * @param log The log message to be displayed in the text area
     */
    public void doLog(final String log) {
        if(appState == STARTED) {
            try {
            final StyledDocument sDoc = logMessagesDisp.getStyledDocument();
            if(!logBuffer.isEmpty()) {
                System.out.println("flushing buffer");
                final Iterator<String> iter = logBuffer.iterator();
                while(iter.hasNext()) {
                    sDoc.insertString(0, iter.next(), sDoc.getStyle(STYLE_REGULAR));
                    iter.remove();
                }
            }
            if( log.contains( "ERROR" )) {
                sDoc.insertString(sDoc.getLength(), log, sDoc.getStyle(STYLE_ERROR));
            } else if( log.contains( "WARN" )) {
                sDoc.insertString(sDoc.getLength(), log, sDoc.getStyle(STYLE_WARN));
            } else {
                sDoc.insertString(sDoc.getLength(), log, sDoc.getStyle(STYLE_REGULAR));
            }
            logMessagesDisp.setCaretPosition(sDoc.getLength());
            } catch(final BadLocationException ble) {
                System.out.println("Bad Location Exception : " + ble.getMessage());
            }
        }
        else if(appState == PAUSED){
            logBuffer.add(log);
        }
    }

    /**creates a panel to hold the buttons
     */
    private void initButtonsPanel() {
        buttonsPanel = new JPanel();
        startPause = new JButton(PAUSE);
        startPause.addActionListener(new StartPauseActionListener());
        stop = new JButton(STOP);
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                appState = STOPPED;
                startPause.setText(START);
            }
        });
        clear = new JButton(CLEAR);
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
                logMessagesDisp.setText("");
            }
        });

        searchField = new JTextField(25);
        search = new JButton("Suchen");
        search.addActionListener(new SearchActionListener());
        buttonsPanel.add(startPause);
        buttonsPanel.add(stop);
        buttonsPanel.add(clear);
        buttonsPanel.add(searchField);
        buttonsPanel.add(search);


    }

    /**Creates a scrollable text area
     */
    private void initMessageDispArea() {
        logMessagesDisp = new JTextPane();
        scrollPane = new JScrollPane(logMessagesDisp);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //add styles
        final StyledDocument sDoc = logMessagesDisp.getStyledDocument();
        final Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        final Style s1 = sDoc.addStyle(STYLE_REGULAR, def);
        StyleConstants.setFontFamily(def, "SansSerif");

        final Style s2 = sDoc.addStyle(STYLE_HIGHLIGHTED, s1);
        StyleConstants.setBackground(s2, Color.CYAN);

        final Style s3 = sDoc.addStyle(STYLE_ERROR, s1);
        StyleConstants.setBackground(s3, new Color(255, 0, 0));

        final Style s4 = sDoc.addStyle(STYLE_WARN, s1);
        StyleConstants.setBackground(s4, new Color(255, 200, 0));

    }

    /**************** inner classes *************************/

    /**Accepts and responds to action events generated by the startPause
     * button.
     */
    class StartPauseActionListener implements ActionListener {
        /**Toggles the value of the startPause button. Also toggles
         * the value of dispMsg.
         * @param evt The action event
         */
        @Override
        public void actionPerformed(final ActionEvent evt) {
            final JButton srcButton = (JButton)evt.getSource();
            if(srcButton.getText().equals(START)) {
                srcButton.setText(PAUSE);
                appState = STARTED;
            }
            else if(srcButton.getText().equals(PAUSE)) {
                appState = PAUSED;
                srcButton.setText(START);
            }
        }
    }

    class SearchActionListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent evt) {
            final JButton srcButton = (JButton)evt.getSource();
            if(!"Suchen".equals(srcButton.getText())) {
                return;
            }
            System.out.println("Highlighting search results");
            final String searchTerm = searchField.getText();
            final String allLogText = logMessagesDisp.getText();
            int startIndex = 0;
            int selectionIndex=-1;
            final Highlighter hLighter = logMessagesDisp.getHighlighter();
            //clear all previous highlightes
            hLighter.removeAllHighlights();
            final DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
            while((selectionIndex = allLogText.indexOf(searchTerm, startIndex)) != -1) {
                startIndex = selectionIndex + searchTerm.length();
                try {
                    final int newLines = getNumberOfNewLinesTillSelectionIndex(allLogText, selectionIndex);
                    hLighter.addHighlight(selectionIndex-newLines, (selectionIndex+searchTerm.length()-newLines), highlightPainter);
                } catch(final BadLocationException ble) {
                    System.out.println("Bad Location Exception: " + ble.getMessage());
                }
            }
        }

        private int getNumberOfNewLinesTillSelectionIndex(final String allLogText, final int selectionIndex) {
            int numberOfNewlines = 0;
            int pos = 0;
            while((pos = allLogText.indexOf("\n", pos))!=-1 && pos <= selectionIndex) {
                numberOfNewlines++;
                pos++;
            }
            return numberOfNewlines;
        }

    }

    public void close() {
        // clean up code for UI goes here.
        jframe.setVisible(false);
    }

    public void show(){
        jframe.setVisible(true);
    }
}