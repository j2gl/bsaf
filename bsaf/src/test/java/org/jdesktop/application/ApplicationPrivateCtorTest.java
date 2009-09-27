
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import junit.framework.*;

/**
 * 
 * Verify that a privileged Application (one that's not running
 * in the secure sandbox) can have a private constructor.
 * 
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ApplicationPrivateCtorTest extends TestCase {

    /* Support for private (not static) inner classes isn't provided
     * by Application.launch() because then we'd have to find a way to
     * pass an instance of the enclosing class along.
     */
    private static class PrivateApplication extends WaitForStartupApplication {
        public boolean ok = false;
        private PrivateApplication() { 
            ok = true; 
        }
    }

    private static boolean isAppLaunched = false;

    public ApplicationPrivateCtorTest(String testName) {
        super(testName);
	if (!isAppLaunched) {
	    PrivateApplication.launchAndWait(PrivateApplication.class);
	    isAppLaunched = true;
	}
    }

    /**
     * Verify that a privileged app use an Application with a private
     * constructor.
     */
    public void testPrivateConstructor() {
	PrivateApplication app = Application.getInstance(PrivateApplication.class);
        assertTrue(app.ok);
    }
}


