package org.jdesktop.application.data;

import java.awt.Window;
import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import java.applet.Applet;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

/**
 * TODO:description
 *
 * @author Sergey A. Malenkov
 */
public class Storage {

    private final String name;
    private final String vendor;

    private final Map<Class, StateManager> managers = new HashMap<Class, StateManager>();

    private LocalStorage local;
    private SessionStorage session;

    public Storage(String name, String vendor) {
        this.name = name;
        this.vendor = vendor;

        managers.put(Window.class, new WindowState());
        managers.put(JSplitPane.class, new JSplitPaneState());
        managers.put(JTabbedPane.class, new JTabbedPaneState());
        managers.put(JTable.class, new JTableState());
    }

    /**
     * Looks up a {@link StateManager} object for the specified {@code object}.
     * If the specified {@code object} is a {@link JComponent}
     * that contains a client property for the {@link StateManager} class,
     * this property will be returned as a {@link StateManager} object.
     * It allows to register a {@link StateManager} object
     * for a single Swing component by setting its client property.
     * For example:
     * <pre>
     * myJComponent.putClientProperty(StateManager.class, myStateHolder);
     * </pre>
     * If the specified {@code object} is a {@link StateManager},
     * it will be returned as a {@link StateManager} object.
     * It allows to provide needed functionality
     * in a custom Swing component directly.
     * If a {@link StateManager} object is registered
     * for the type of the specified {@code object}
     * or for one of its superclasses, it will be returned.
     * This method returns {@code null}
     * if the specified {@code object} is {@code null} or
     * an appropriate {@link StateManager} object is not found.
     *
     * @param object  the object to look up an appropriate {@link StateManager} object
     * @return an appropriate {@link StateManager} object or {@code null}
     *
     * @see #setStateManager
     * @see JComponent#putClientProperty
     */
    public final StateManager getStateManager(Object object) {
        if (object != null) {
            if (object instanceof JComponent) {
                JComponent component = (JComponent) object;
                Object property = component.getClientProperty(StateManager.class);
                if (property instanceof StateManager) {
                    return (StateManager) property;
                }
            }
            if (object instanceof StateManager) {
                return (StateManager) object;
            }
            Class<?> type = (object instanceof Class)
                    ? (Class) object
                    : object.getClass();

            while (type != null) {
                StateManager manager = managers.get(type);
                if (manager != null) {
                    return manager;
                }
                type = type.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Registers a {@link StateManager} object
     * for the specified {@code type}.
     * If {@code type} is {@code null},
     * no exception is thrown and no action is taken.
     *
     * @param type     the class to which {@code manager} applies
     * @param manager  the {@link StateManager} object to register,
     *                 or {@code null} to cancel registration
     *
     * @see #getStateManager
     */
    public final void setStateManager(Class<?> type, StateManager manager) {
        if (type != null) {
            if (manager != null) {
                managers.put(type, manager);
            }
            else {
                managers.remove(type);
            }
        }
    }

    public LocalStorage getLocalStorage() {
        if (local == null) {
            try {
                local = new PersistenceStorage();
            }
            catch (Exception exception) {
                local = new FileSystemStorage(name, vendor);
            }
        }
        return local;
    }

    public void setLocalStorage(LocalStorage storage) {
        local = storage;
    }

    public SessionStorage getSessionStorage() {
        if (session == null) {
            try {
                session = new PreferencesStorage(this, Preferences.userRoot().node(vendor).node(name));
            }
            catch (SecurityException disabled) {
                try {
                    session = new PreferencesStorage(this, new PersistencePreferences().node(vendor).node(name));
                }
                catch (Exception unavailable) {
                    session = new PreferencesStorage(this, new MemoryPreferences());
                }
            }
        }
        return session;
    }

    public void setSessionStorage(SessionStorage storage) {
        session = storage;
    }

    void store(Object object, Preferences preferences) {
        StateManager manager = getStateManager(object);
        if (manager != null) {
            if (object instanceof Component) {
                StringBuilder path = getPathBuilder((Component) object);
                if (path != null) {
                    manager.store(object, preferences.node(path.toString()));
                }
            }
            else {
                manager.store(object, preferences);
            }
        }
        if (object instanceof Container) {
            Container container = (Container) object;
            for (Component component : container.getComponents()) {
                store(component, preferences);
            }
        }
    }

    void restore(Object object, Preferences preferences) {
        StateManager manager = getStateManager(object);
        if (manager != null) {
            if (object instanceof Component) {
                StringBuilder path = getPathBuilder((Component) object);
                if (path != null) {
                    manager.restore(object, preferences.node(path.toString()));
                }
            }
            else {
                manager.restore(object, preferences);
            }
        }
        if (object instanceof Container) {
            Container container = (Container) object;
            for (Component component : container.getComponents()) {
                restore(component, preferences);
            }
        }
    }

    /**
     * Return a string that uniquely identifies this component, or null
     * if Component component doesn't have a name per getComponentName().  The
     * pathname is basically the name of all of the components, starting
     * with component, separated by "/".  This path is the reverse of what's
     * typical, the first path element is component's name, rather than the name
     * of component's root Window or Applet.  That way pathnames can be
     * distinguished without comparing much of the string.  The names
     * of intermediate components *can* be null, we substitute
     * "[type][z-order]" for the name.  Here's an example:
     *
     * JFrame myFrame = new JFrame();
     * JPanel p = new JPanel() {};  // anonymous JPanel subclass
     * JButton myButton = new JButton();
     * myButton.setName("myButton");
     * p.add(myButton);
     * myFrame.add(p);
     *
     * getPath(myButton) =>
     * "myButton/AnonymousJPanel0/null.contentPane/null.layeredPane/JRootPane0/myFrame"
     *
     * Notes about name usage in AWT/Swing: JRootPane (inexplicably) assigns
     * names to it's children (layeredPane, contentPane, glassPane);
     * all AWT components lazily compute a name.  If we hadn't assigned the
     * JFrame a name, it's name would have been "frame0".
     */
    private static StringBuilder getPathBuilder(Component component) {
        if (component == null) {
            return new StringBuilder();
        }
        String name = component.getName();
        if ((component instanceof Window) || (component instanceof Applet)) {
            return (name != null)
                    ? new StringBuilder(name)
                    : null;
        }
        Container parent = component.getParent();
        StringBuilder sb = getPathBuilder(parent);
        if (sb == null) {
            return null;
        }
        if (sb.length() > 0) {
            sb.append('/');
        }
        if (name != null) {
            return sb.append(name);
        }
        if (parent == null) {
            return null;
        }
        int order = parent.getComponentZOrder(component);
        if (order < 0) {
            return null;
        }
        Class type = component.getClass();
        if (type.isAnonymousClass()) {
            sb.append("anonymous");
            type = type.getSuperclass();
        }
        return sb.append(type.getSimpleName()).append(order);
    }
}
