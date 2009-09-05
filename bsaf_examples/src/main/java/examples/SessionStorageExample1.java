/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package examples;

import org.jdesktop.application.Application;
import org.jdesktop.application.Launcher;
import org.jdesktop.application.data.StateManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

/**
 * An example that demonstrates the default support
 * for storing and restoring GUI session state.
 * Try running the application, resizing and moving the main frame,
 * resizing some of the color split panes, changing the selected tab,
 * the widths of columns in the last tab.  When the app is restarted,
 * those GUI features should be restored to the way you left them.
 *
 * @author Sergey A. Malenkov
 */
public class SessionStorageExample1 extends Application {

    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 300;
    private static final int RADIX = 16;
    private static final String[] COLUMNS = { "Name", "Red", "Green", "Blue" }; // NON-NLS: column names
    private static final Font FONT = new Font(null, Font.PLAIN, 24);


    public static void main(String[] args) {
        Launcher.getInstance().launch(SessionStorageExample1.class, args);
    }

    private final JFrame frame = new JFrame(getClass().getSimpleName());

    @Override
    protected void startup() {
        frame.add(BorderLayout.CENTER, createTabbedPane());
        frame.add(BorderLayout.SOUTH, new JTextField());
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                shutdown();
            }
        });
        // add the session state manager for all text fields in the application
        getContext().getStorage().setStateManager(JTextField.class, new StateManager<JTextField>() {
            public void store(JTextField field, Preferences preferences) {
                preferences.put("text.field", field.getText()); // NON-NLS: state key
            }

            public void restore(JTextField field, Preferences preferences) {
                field.setText(preferences.get("text.field", "")); // NON-NLS: state key
            }
        });
        try {
            // restore the session state for the main frame's component tree
            getContext().getStorage().getSessionStorage().restore(frame, "frame"); // NON-NLS: filename
        }
        catch (IOException exception) {
            throw new Error("Unexpected exception", exception);
        }
        frame.setVisible(true);
    }

    @Override
    protected void shutdown() {
        try {
            // store the session state for the main frame's component tree
            getContext().getStorage().getSessionStorage().store(frame, "frame"); // NON-NLS: filename
        }
        catch (IOException exception) {
            throw new Error("Unexpected exception", exception);
        }
    }

    private static JTabbedPane createTabbedPane() {
        JTabbedPane pane = new JTabbedPane();
        pane.add("red", createSplitPane(Color.RED)); // NON-NLS: tab name
        pane.add("blue", createSplitPane(Color.BLUE)); // NON-NLS: tab name
        pane.add("green", createSplitPane(Color.GREEN)); // NON-NLS: tab name
        pane.add("yellow", createSplitPane(Color.YELLOW)); // NON-NLS: tab name
        pane.add("orange", createSplitPane(Color.ORANGE)); // NON-NLS: tab name
        pane.add("pink", createSplitPane(Color.PINK)); // NON-NLS: tab name
        pane.add("table", new JScrollPane(createTable(pane))); // NON-NLS: tab name
        return pane;
    }

    private static JSplitPane createSplitPane(Color color) {
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                         createLabel("darker", color.darker()), // NON-NLS: label text
                                         createLabel("brighter", color.brighter())); // NON-NLS: label text
        pane.setBackground(color);
        pane.setResizeWeight(0.5);
        return pane;
    }

    private static JLabel createLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setBackground(color.darker());
        label.setForeground(Color.WHITE);
        label.setFont(FONT);
        label.setOpaque(true);
        return label;
    }

    private static JTable createTable(final JTabbedPane pane) {
        JTable table = new JTable(new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return COLUMNS[column];
            }

            public int getColumnCount() {
                return COLUMNS.length;
            }

            public int getRowCount() {
                return pane.getTabCount() - 1;
            }

            public Object getValueAt(int row, int column) {
                if (column == 0) {
                    return pane.getTitleAt(row);
                }
                Color color = pane.getComponentAt(row).getBackground();
                switch (column) {
                    case 1:
                        return toString(color.getRed());
                    case 2:
                        return toString(color.getBlue());
                    case 3:
                        return toString(color.getGreen());
                }
                return null;
            }

            private Object toString(int value) {
                return Integer.toString(value, RADIX).toUpperCase(Locale.ENGLISH);
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }
}
