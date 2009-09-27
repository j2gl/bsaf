
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import junit.framework.*;


/**
 * The Application.lookAndFeel resource defined as "default", which 
 * indicates that we should load the JVM default look and feel.
 * That's metal on all platforms except OSX, where it's the native L&F.
 * 
 * This test depends on resources/AppilcationDefaultLNF.properties
 * 
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ApplicationDefaultLNFResourceTest extends TestCase {

    public static class ApplicationDefaultLNF extends WaitForStartupApplication {
    }

    private static boolean isAppLaunched = false;

    public ApplicationDefaultLNFResourceTest(String testName) {
        super(testName);
	if (!isAppLaunched) {
	    ApplicationDefaultLNF.launchAndWait(ApplicationDefaultLNF.class);
	    isAppLaunched = true;
	}
    }

    public void testApplicationLookAndFeelResource() {
	LookAndFeel lnf = UIManager.getLookAndFeel();
	String osName = System.getProperty("os.name");
	if ((osName != null) && (osName.toLowerCase().startsWith("mac os x"))) {
	    assertTrue("OSX default L&F is native", lnf.isNativeLookAndFeel());
	}
	else {
	    String defaultLNFName = UIManager.getCrossPlatformLookAndFeelClassName();
	    assertFalse("not native L&F", lnf.isNativeLookAndFeel());
	    assertEquals("cross platform L&F name", defaultLNFName, lnf.getClass().getName());
	}
    }
}
