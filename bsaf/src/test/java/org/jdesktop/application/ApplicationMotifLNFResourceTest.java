/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import junit.framework.*;

/**
 * Checks that by defining the Application.lookAndFeel resource
 * to be "com.sun.java.swing.plaf.motif.MotifLookAndFeel" causes 
 * the UIManager.lookAndFeel property to be initialized to the 
 * Motif L&F.
 * 
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ApplicationMotifLNFResourceTest extends TestCase {

    /* Application.lookAndFeel resource is explicity defined to be "system".
     */
    public static class ApplicationMotifLNF extends WaitForStartupApplication {
    }

    private static boolean isAppLaunched = false;

    public ApplicationMotifLNFResourceTest(String testName) {
        super(testName);
	if (!isAppLaunched) {
	    ApplicationMotifLNF.launchAndWait(ApplicationMotifLNF.class);
	    isAppLaunched = true;
	}
    }

    public void testApplicationLookAndFeelResource() {
	ApplicationContext ctx = Application.getInstance(ApplicationMotifLNF.class).getContext();
	String lnfResource = ctx.getResourceMap().getString("Application.lookAndFeel");
	assertEquals("Application.lookAndFeel resource", "com.sun.java.swing.plaf.motif.MotifLookAndFeel", lnfResource);
	LookAndFeel lnf = UIManager.getLookAndFeel(); 
        @SuppressWarnings("all") // ... MotifLookAndFeel is Sun proprietary API and may be removed in a future release
        Class motifLNFClass = com.sun.java.swing.plaf.motif.MotifLookAndFeel.class;
	assertSame("UIManager.getLookAndFeel().getClass", motifLNFClass, lnf.getClass());
    }
}

