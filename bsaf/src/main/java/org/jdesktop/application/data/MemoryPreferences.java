package org.jdesktop.application.data;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * Preferences implementation to avoid {@code NullPointerException}.
 * All of the preferences will be lost on application restart.
 *
 * @author Sergey A. Malenkov
 * @see java.util.prefs.Preferences
 */
final class MemoryPreferences extends AbstractPreferences {

    private static final String[] EMPTY_ARRAY = new String[0];

    private final Map<String, Object> map = new HashMap<String, Object>();

    MemoryPreferences() {
        super(null, "");
    }

    private MemoryPreferences(MemoryPreferences parent, String name) {
        super(parent, name);
    }

    @Override
    public boolean isUserNode() {
        return true;
    }

    @Override
    protected String[] keysSpi() throws BackingStoreException {
        String[] array = new String[map.size()];
        return map.keySet().toArray(array);
    }

    @Override
    protected String getSpi(String key) {
        Object value = map.get(key);
        return (value instanceof String)
                ? (String) value
                : null;
    }

    @Override
    protected void removeSpi(String key) {
        map.remove(key);
    }

    @Override
    protected void putSpi(String key, String value) {
        map.put(key, value);
    }

    @Override
    protected String[] childrenNamesSpi() throws BackingStoreException {
        return EMPTY_ARRAY;
    }

    @Override
    protected AbstractPreferences childSpi(String name) {
        return new MemoryPreferences(this, name);
    }

    @Override
    protected void removeNodeSpi() throws BackingStoreException {
    }

    @Override
    protected void flushSpi() throws BackingStoreException {
    }

    @Override
    protected void syncSpi() throws BackingStoreException {
    }
}
