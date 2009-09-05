/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package examples;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A "Hello World" application with a standard resource bundle.
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ApplicationExample2 extends Application {
    JFrame mainFrame = null;

    @Override
    protected void startup() {
        JLabel label = new JLabel("[label.text resource]", JLabel.CENTER);
        label.setName("label");
        mainFrame = new JFrame();
        mainFrame.setName("mainFrame");
        mainFrame.add(label, BorderLayout.CENTER);
        mainFrame.addWindowListener(new MainFrameListener());
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ResourceMap resourceMap = getContext().getResourceMap(getClass());
        resourceMap.injectComponents(mainFrame);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);  // center the window
        mainFrame.setVisible(true);
    }

    @Override
    protected void shutdown() {
        mainFrame.setVisible(false);
    }

    private class MainFrameListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            exit();
        }
    }

    public static void main(String[] args) {
        Launcher.getInstance().launch(ApplicationExample2.class, args);
    }
}

