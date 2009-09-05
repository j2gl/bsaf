/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package examples;

import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.EventObject;

/**
 * A SingleFrameApplication example with an exitListener.
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class SingleFrameExample3 extends SingleFrameApplication {
    @Override
    protected void startup() {
        ExitListener maybeExit = new ExitListener() {
            public boolean canExit(EventObject e) {
                Object source = (e != null) ? e.getSource() : null;
                Component owner = (source instanceof Component) ? (Component) source : null;
                int option = JOptionPane.showConfirmDialog(owner, "Really Exit?");
                return option == JOptionPane.YES_OPTION;
            }

            public void willExit(EventObject e) {
            }
        };
        addExitListener(maybeExit);
        JButton button = new JButton();
        button.setName("button");
        button.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        show(button);
    }

    public static void main(String[] args) {
        Launcher.getInstance().launch(SingleFrameExample3.class, args);
    }
}
