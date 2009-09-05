package org.jdesktop.application.data;

import java.util.prefs.Preferences;

/**
 * TODO:description
 *
 * @author Sergey A. Malenkov
 */
final class PreferencesStorage implements SessionStorage {

    private final Storage storage;
    private final Preferences preferences;

    PreferencesStorage(Storage storage, Preferences preferences) {
        this.storage = storage;
        this.preferences = preferences;
    }

    public void store(Object object, String name) {
        storage.store(object, preferences.node(name));
    }

    public void restore(Object object, String name) {
        storage.restore(object, preferences.node(name));
    }
}
