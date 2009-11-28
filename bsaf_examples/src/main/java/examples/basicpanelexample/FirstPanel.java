/*
 * Copyright (C) 2009 Illya V. Yalovyy All rights reserved. Use is
 * subject to license terms.
 */


package examples.basicpanelexample;

import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author Illya Yalovyy
 */
public class FirstPanel extends BasicPanel {

	public FirstPanel() {
		ResourceMap resourceMap = getResourceMap();

		JLabel label = new JLabel();
		label.setName("commonLabel");
		add(label);

		label = new JLabel();
		label.setName("firstLabel");
		add(label);

		JButton button = new JButton();
		button.setAction(getActions().get("firstAction"));
		add(button);

		button = new JButton();
		button.setAction(getActions().get("basicAction"));
		add(button);

		button = new JButton();
		button.setAction(getActions().get("globalAction"));
		add(button);

		System.out.println("Keys:"+Arrays.toString(getActions().allKeys()));

		resourceMap.injectComponents(this);


	}

	@Action
	public void firstAction() {
		System.out.println("This is an action from the FirstPanel.");
	}
}
