package org.jdesktop.application.data;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.prefs.Preferences;
import javax.swing.JFrame;

/**
 * The state manager implementation for the Window class.
 *
 * @author Sergey A. Malenkov
 */
final class WindowState implements StateManager<Window> {

    public void store(Window window, Preferences preferences) {
        preferences.putInt("screen.count", getScreenCount());
        putBounds(preferences, "screen.", getScreenBounds(window));

        int state = Frame.NORMAL;
        if (window instanceof Frame) {
            Frame frame = (Frame) window;
            state = frame.getExtendedState();
        }
        Rectangle bounds = window.getBounds();
        if (window instanceof JFrame) {
            JFrame frame = (JFrame) window;
            // If this is a JFrame created by FrameView and it's been maximized,
            // retrieve the frame's normal (not maximized) bounds.  More info:
            // see FrameStateListener#windowStateChanged in FrameView.
            if (0 != (state & Frame.MAXIMIZED_BOTH)) {
                Object property = frame.getRootPane().getClientProperty("WindowState.normalBounds");
                if (property instanceof Rectangle) {
                    bounds = (Rectangle) property;
                }
            }
        }
        putBounds(preferences, "window.", bounds);
        preferences.putInt("frame.state", state);
    }

    public void restore(Window window, Preferences preferences) {
        if (!window.isLocationByPlatform()) {
            int count = preferences.getInt("screen.count", Integer.MIN_VALUE);
            if (count == getScreenCount()) {
                if (isScreenBoundsValid(window, getBounds(preferences, "screen."))) {
                    Rectangle bounds = getBounds(preferences, "window.");
                    if (bounds != null) {
                        if (isResizable(window)) {
                            window.setBounds(bounds);
                        }
                        else {
                            window.setLocation(bounds.x, bounds.y);
                        }
                    }
                }
            }
            if (window instanceof Frame) {
                int state = preferences.getInt("frame.state", Integer.MAX_VALUE);
                if (state != Integer.MAX_VALUE) {
                    Frame frame = (Frame) window;
                    frame.setExtendedState(state);
                }
            }
        }
    }

    private static Rectangle getBounds(Preferences preferences, String prefix) {
        int x = preferences.getInt(prefix + "x", Integer.MIN_VALUE);
        if (x == Integer.MIN_VALUE) {
            return null;
        }
        int y = preferences.getInt(prefix + "y", Integer.MIN_VALUE);
        if (y == Integer.MIN_VALUE) {
            return null;
        }
        int width = preferences.getInt(prefix + "width", Integer.MIN_VALUE);
        if (width == Integer.MIN_VALUE) {
            return null;
        }
        int height = preferences.getInt(prefix + "height", Integer.MIN_VALUE);
        if (height == Integer.MIN_VALUE) {
            return null;
        }
        return new Rectangle(x, y, width, height);
    }

    private static void putBounds(Preferences preferences, String prefix, Rectangle bounds) {
        preferences.putInt(prefix + "x", bounds.x);
        preferences.putInt(prefix + "y", bounds.y);
        preferences.putInt(prefix + "width", bounds.width);
        preferences.putInt(prefix + "height", bounds.height);
    }

    private static int getScreenCount() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
    }

    private static Rectangle getScreenBounds(Window window) {
        GraphicsConfiguration gc = window.getGraphicsConfiguration();
        return (gc == null) ? null : gc.getBounds();
    }

    private static boolean isScreenBoundsValid(Window window, Object bounds) {
        return (bounds instanceof Rectangle) && bounds.equals(getScreenBounds(window));
    }

    private static boolean isResizable(Window window) {
        if (window instanceof Frame) {
            Frame frame = (Frame) window;
            return frame.isResizable();
        }
        if (window instanceof Dialog) {
            Dialog dialog = (Dialog) window;
            return dialog.isResizable();
        }
        return true;
    }
}
