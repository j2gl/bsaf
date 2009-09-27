package org.jdesktop.application;

import java.util.Map;
import java.util.WeakHashMap;

public final class SwingStaticProperty<V> {
    private final Map<ThreadGroup, V> valueMap = new WeakHashMap<ThreadGroup, V>();

    public SwingStaticProperty() {
    }

    public SwingStaticProperty(V value) {
        set(value);
    }

    public synchronized void set(V value) {
        valueMap.put(Thread.currentThread().getThreadGroup(), value);
    }

    public synchronized V get() {
        return valueMap.get(Thread.currentThread().getThreadGroup());
    }
}
