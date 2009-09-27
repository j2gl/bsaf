package org.jdesktop.application.data;

import java.util.prefs.Preferences;

/**
 * TODO:description
 *
 * @author Sergey A. Malenkov
 */
public interface StateManager<T> {

    /**
     * Stores the state of the specified {@code object}
     * to the specified {@code preferences}
     * that should be preserved across Application sessions.
     *
     * @param object       the object which state should be stored
     * @param preferences  the preferences for the object
     */
    void store(T object, Preferences preferences);

    /**
     * Restores the state of the specified {@code object}
     * from the specified {@code preferences}.
     *
     * @param object       the object which state should be restored
     * @param preferences  the preferences for the object
     */
    void restore(T object, Preferences preferences);
}
