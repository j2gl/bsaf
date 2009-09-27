
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package examples;

import org.jdesktop.application.SingleFrameApplication;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * 
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class SingleFrameExample2 extends SingleFrameApplication {
    @Override protected void startup() {
        JLabel label = new JLabel();
	label.setName("label");
        show(label);
    }
    public static void main(String[] args) {
        launch(SingleFrameExample2.class, args);
    }
}
