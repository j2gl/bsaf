package org.jdesktop.application.data;

import java.util.prefs.Preferences;
import javax.swing.JSplitPane;

/**
 * The state manager implementation for the JSplitPane class.
 *
 * @author Sergey A. Malenkov
 */
final class JSplitPaneState implements StateManager<JSplitPane> {

    public void store(JSplitPane pane, Preferences preferences) {
        preferences.putInt("orientation", pane.getOrientation());
        preferences.putInt("location", pane.getUI().getDividerLocation(pane));
    }

    public void restore(JSplitPane pane, Preferences preferences) {
        int orientation = preferences.getInt("orientation", Integer.MIN_VALUE);
        if (orientation == pane.getOrientation()) {
            int location = preferences.getInt("location", Integer.MIN_VALUE);
            if (location != Integer.MIN_VALUE) {
                pane.setDividerLocation(location);
            }
        }
    }
}
