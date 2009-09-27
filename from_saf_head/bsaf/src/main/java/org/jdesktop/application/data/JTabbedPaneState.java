package org.jdesktop.application.data;

import java.util.prefs.Preferences;
import javax.swing.JTabbedPane;

/**
 * The state manager implementation for the JTabbedPane class.
 *
 * @author Sergey A. Malenkov
 */
final class JTabbedPaneState implements StateManager<JTabbedPane> {

    public void store(JTabbedPane pane, Preferences preferences) {
        preferences.putInt("tab.count", pane.getTabCount());
        preferences.putInt("tab.index", pane.getSelectedIndex());
    }

    public void restore(JTabbedPane pane, Preferences preferences) {
        int count = preferences.getInt("tab.count", Integer.MIN_VALUE);
        if (count == pane.getTabCount()) {
            int index = preferences.getInt("tab.index", Integer.MIN_VALUE);
            if (index != Integer.MIN_VALUE) {
                pane.setSelectedIndex(index);
            }
        }
    }
}
