/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package examples;

import org.jdesktop.application.*;

import javax.swing.*;
import java.awt.*;


/**
 * A trivial (Hello World) example of SingleFrameApplication.  For
 * simplicity's sake, this version doesn't have a resource file.
 * SingleFrameExample2 is a little bit more realistic.
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class SingleFrameExample1 extends SingleFrameApplication {
    @Override
    protected void startup() {
        JLabel label = new JLabel("Hello World");
        label.setFont(new Font("LucidaSans", Font.PLAIN, 32));
		System.out.println("App:"+Application.getInstance());
        show(label);
    }

	@Override
	protected void ready() {
		super.ready();
		System.out.println("Ready:");
	}



    public static void main(String[] args) {
        Launcher.getInstance().launch(SingleFrameExample1.class, args);
    }
}
