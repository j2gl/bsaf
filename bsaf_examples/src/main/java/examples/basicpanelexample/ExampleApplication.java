/*
 * Copyright (C) 2009 Illya V. Yalovyy All rights reserved. Use is
 * subject to license terms.
 */

package examples.basicpanelexample;

import java.awt.BorderLayout;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 *
 * @author Illya Yalovyy
 */
public class ExampleApplication extends SingleFrameApplication {
    private final ActionsClass sharedActions = new ActionsClass();

    public static void main(String[] args) {
        Application.launch(ExampleApplication.class, args);
    }

	@Override
	protected void startup() {
		JPanel panel = new JPanel(new BorderLayout());

		JPanel panel1 = new FirstPanel();
		JPanel panel2 = new SecondPanel();

		panel.add(panel1, BorderLayout.CENTER);
		panel.add(panel2, BorderLayout.SOUTH);

		show(panel);
	}

	@Action
	public void globalAction() {
		System.out.println("This is a global action");
	}

    public ActionMap getSharedActions() {
        return getContext().getActionMap(sharedActions);
    }
}
