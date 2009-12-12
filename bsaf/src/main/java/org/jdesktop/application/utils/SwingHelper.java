/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

package org.jdesktop.application.utils;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

/**
 *
 * @author etf
 */
public final class SwingHelper {

    private SwingHelper() {
    }

    public static Rectangle computeVirtualGraphicsBounds() {
        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs =
                ge.getScreenDevices();
        for (GraphicsDevice gd : gs) {
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            virtualBounds = virtualBounds.union(gc.getBounds());
        }
        return virtualBounds;
    }

    public static boolean isResizable(Window window) {
        boolean resizable = true;
        if (window instanceof Frame) {
            resizable = ((Frame) window).isResizable();
        } else if (window instanceof Dialog) {
            resizable = ((Dialog) window).isResizable();
        }
        return resizable;
    }

    public static Point defaultLocation(Window window) {
        GraphicsConfiguration gc =
                window.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        Insets insets = window.getToolkit().getScreenInsets(gc);
        int x = bounds.x + insets.left;
        int y = bounds.y + insets.top;
        return new Point(x, y);
    }
}
