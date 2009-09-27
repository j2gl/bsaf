/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package examples;

import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Launcher;

import javax.swing.*;
import org.jdesktop.application.Application;

/**
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class SingleFrameExample2 extends SingleFrameApplication {
    @Override
    protected void startup() {
		JButton b = new JButton(getContext().getActionMap().get("print"));
		show(b);
    }

    public static void main(String[] args) {
        Launcher.getInstance().launch(SingleFrameExample2.class, args);
    }

	@org.jdesktop.application.Action
	public void print() {
		System.out.println("Action!"+Application.getInstance());
	}
}
