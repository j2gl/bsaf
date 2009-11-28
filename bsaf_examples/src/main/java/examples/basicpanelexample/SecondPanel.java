/*
 * Copyright (C) 2009 Illya V. Yalovyy All rights reserved. Use is
 * subject to license terms.
 */


package examples.basicpanelexample;

import javax.swing.JButton;
import javax.swing.JLabel;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author Illya Yalovyy
 */
public class SecondPanel extends BasicPanel {


	public SecondPanel() {
		ResourceMap resourceMap = getResourceMap();

		add(new JLabel(resourceMap.getString("string1")));
		add(new JLabel(resourceMap.getString("Application.description")));
		add(new JLabel(resourceMap.getString("classTemplate", this.getClass().getSimpleName())));


        add(new JButton(getActions().get("basicAction")));

        add(new JButton(getApplication().getSharedActions().get("sharedAction1")));
        add(new JButton(getApplication().getSharedActions().get("sharedAction2")));
	}

	@Action
	void secondAction() {
		System.out.println("This is an action from the SecondPanel.");
	}
}
