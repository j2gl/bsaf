/*
 * Copyright (C) 2009 Illya V. Yalovyy All rights reserved. Use is
 * subject to license terms.
 */

package examples.basicpanelexample;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

/**
 *
 * @author Illya Yalovyy
 */
public class BasicPanel extends JPanel{

	private ApplicationContext context;

	public BasicPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		context = Application.getInstance().getContext();
	}

	@Action
	public void basicAction() {
		System.out.println("This is an action from the BasicPanel.");
	}

	public ApplicationContext getContext() {
		return context;
	}

	protected ResourceMap getResourceMap() {
		return getContext().getResourceManager().getResourceMap
				(this.getClass(), BasicPanel.class);
	}

	protected ActionMap getActions() {
		return getContext().getActionManager().getActionMap(BasicPanel.class, this);
	}
    
    protected ExampleApplication getApplication() {
        return Application.getInstance(ExampleApplication.class);
    }
}
