
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package examples;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * Initializing {@code @Action} properties from resources.
 * <p>
 * This example is nearly identical to {@link ActionExample1 ActionExample1}.
 * We've just added a a ResourceBundle, 
 * {@code resources/ActionExample2.properties}, that contains
 * resources for the {@code Action's} {@code text}
 * and {@code shortDescription} properties:
 * <pre>
 * setTitle.Action.text = &amp;Set Window Title
 * setTitle.Action.shortDescription = Set the Window's title
 * clearTitle.Action.text = &amp;Clear Window's Title
 * clearTitle.Action.shortDescription = Clear the Window's title
 * </pre>
 * Action resources are automatically loaded from a
 * ResourceBundle with the same name as the actions class, i.e.
 * the class that's passed to 
 * {@link ApplicationContext#getActionMap(Class, Object) getActionMap}.
 * In this case that's just the <code>Application</code> subclass,
 * {@code ActionExample2}.
 * <p>
 * The {@code Action} objects are instances of 
 * {@link application.ApplicationAction ApplicationAction}.
 * See the javadoc for that class for the complete list 
 * of Action properties that are automatically initialized
 * by resources.
 * 
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ActionExample2 extends Application {
    private JFrame appFrame = null;
    private JTextField textField = null;

    @Action public void setTitle() {
	appFrame.setTitle(textField.getText());
    }

    @Action public void clearTitle() {
	appFrame.setTitle("");
    }

    @Override protected void startup() {
	appFrame = new JFrame("");
	textField = new JTextField("<Enter the window title here>");
        textField.setFont(new Font("LucidSans", Font.PLAIN, 32));
	JButton clearTitleButton = new JButton("Set Window Title");
	JButton setTitleButton = new JButton("Clear Window Title");
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
        Application.launch(ActionExample2.class, args);
    }
}
