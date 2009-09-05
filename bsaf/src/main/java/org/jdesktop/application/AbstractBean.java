package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

/**
 * An encapsulation of the {@code PropertyChangeSupport} methods.
 * {@code PropertyChangeListener}s are fired on the event dispatching thread.
 */
public class AbstractBean {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this) {
        @Override
        public void firePropertyChange(final PropertyChangeEvent event) {
            if (SwingUtilities.isEventDispatchThread()) {
                super.firePropertyChange(event);
            }
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        firePropertyChange(event);
                    }
                });
            }
        }
    };

    /**
     * Adds a {@code PropertyChangeListener} to the listener list.
     * The same listener object may be added more than once.
     * For each property, the listener will be invoked
     * the number of times it was added for that property.
     * If {@code listener} is null,
     * no exception is thrown and no action is taken.
     *
     * @param listener  the {@code PropertyChangeListener} to be added
     *
     * @see #removePropertyChangeListener(PropertyChangeListener)
     * @see PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Removes a {@code PropertyChangeListener} from the listener list.
     * If {@code listener} was added more than once to the same event source,
     * it will be notified one less time after being removed.
     * If {@code listener} is null, or it was never added,
     * no exception is thrown and no action is taken.
     *
     * @param listener  the {@code PropertyChangeListener} to be removed
     *
     * @see #addPropertyChangeListener(PropertyChangeListener)
     * @see PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener) 
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Adds a {@code PropertyChangeListener} for a specific property.
     * The same listener object may be added more than once.
     * For each property, the listener will be invoked
     * the number of times it was added for that property.
     * If {@code propertyName} or {@code listener} is null,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName  the name of the property to listen on
     * @param listener      the {@code PropertyChangeListener} to be added
     *
     * @see PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes a {@code PropertyChangeListener} for a specific property.
     * If {@code listener} was added more than once
     * to the same event source for the specified property,
     * it will be notified one less time after being removed.
     * If {@code propertyName} or {@code listener} is null,
     * or {@code listener} was never added for the specified property,
     * no exception is thrown and no action is taken.
     *
     * @param propertyName  the name of the property that was listened on
     * @param listener      the {@code PropertyChangeListener} to be removed
     *
     * @see PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Returns an array of all the listeners that were added.
     * If some listeners have been added with a named property,
     * then the returned array will contain
     * corresponding {@code PropertyChangeListenerProxy}.
     *
     * @return all of the {@code PropertyChangeListeners} added
     *         or an empty array if no listeners have been added
     *
     * @see PropertyChangeSupport#getPropertyChangeListeners
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    /**
     * Reports a bound property update to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * The {@code propertyChange} method of corresponding listeners
     * is invoked on the event dispatching thread. No event is fired
     * if old and new values are equal and non-null.
     *
     * @param propertyName  the programmatic name of the property that was changed
     * @param oldValue      the old value of the property
     * @param newValue      the new value of the property
     *
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @see PropertyChangeSupport#firePropertyChange(String, Object, Object)
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fires a property change event to listeners
     * that have been registered to track updates of
     * all properties or a property with the specified name.
     * <p>
     * The {@code propertyChange} method of corresponding listeners
     * is invoked on the event dispatching thread. No event is fired
     * if the given event's old and new values are equal and non-null.
     *
     * @param event  the {@code PropertyChangeEvent} to be fired
     *
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     * @see PropertyChangeSupport#firePropertyChange(PropertyChangeEvent)
     */
    protected void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }
}
