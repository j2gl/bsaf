/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package examples;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Launcher;

import javax.swing.*;
import java.awt.*;

/**
 * {@code @Action} basics.
 * <p/>
 * A trivial {@code @Action} example: the buttons set/clear the Frame's title:
 * <pre>
 * public class ActionExample1 extends Application {
 *     &#064;Action public void setTitle() {
 *         appFrame.setTitle(textField.getText());
 *     }
 *     &#064;Action public void clearTitle() {
 *         appFrame.setTitle("");
 *     }
 *     // ...
 * }
 * </pre>
 * The only wrinkle worth noting is that the Action objects we've
 * created are going to call the methods on <i>this</i> object. So
 * when we lookup the ActionMap for this class, we have to pass along
 * the {@code ActionExample1} instance as well:
 * <pre>
 * ApplicationContext ac = ApplicationContext.getInstance();
 * ActionMap actionMap = ac.getActionMap(getClass(), <i>this</i>);
 * setTitleButton.setAction(actionMap.get("setTitle"));
 * clearTitleButton.setAction(actionMap.get("clearTitle"));
 * </pre>
 * Since our {@code @Actions} have been defined in the {@code Application}
 * subclass itself, we can use the no-argument version of {@code getActionMap()},
 * which returns the {@code ActionMap} for the application:
 * <pre>
 * ApplicationContext ac = ApplicationContext.getInstance();
 * ActionMap actionMap = ac.getActionMap();
 * setTitleButton.setAction(actionMap.get("setTitle"));
 * clearTitleButton.setAction(actionMap.get("clearTitle"));
 * </pre>
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ActionExample1 extends Application {
    private JFrame appFrame = null;
    private JTextField textField = null;

    @Action
    public void setTitle() {
        appFrame.setTitle(textField.getText());
    }

    @Action
    public void clearTitle() {
        appFrame.setTitle("");
    }

    @Override
    protected void startup() {
        appFrame = new JFrame("");
        textField = new JTextField("<Enter the window title here>");
        textField.setFont(new Font("LucidSans", Font.PLAIN, 32));
        JButton clearTitleButton = new JButton();
        JButton setTitleButton = new JButton();
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(setTitleButton);
        buttonPanel.add(clearTitleButton);
        appFrame.add(textField, BorderLayout.CENTER);
        appFrame.add(buttonPanel, BorderLayout.SOUTH);

        /* Lookup up the Actions for this Application 
          * and bind them to the GUI controls.
          */
        ActionMap actionMap = getContext().getActionMap();
        setTitleButton.setAction(actionMap.get("setTitle"));
        textField.setAction(actionMap.get("setTitle"));
        clearTitleButton.setAction(actionMap.get("clearTitle"));

        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.pack();
        appFrame.setLocationRelativeTo(null);
        appFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Launcher.getInstance().launch(ActionExample1.class, args);
    }
}
