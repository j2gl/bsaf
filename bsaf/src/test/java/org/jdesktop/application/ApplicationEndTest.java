/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */

package org.jdesktop.application;

import junit.framework.TestCase;


/**
 * Verify overriding Application#end() defeats the default call to
 * System.exit().
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ApplicationEndTest extends TestCase {
    private static boolean isAppLaunched = false;

    /* If the JVM were to actually shutdown during the Application#exit call, 
     * we'll throw an Error here.
     */
    private static class ShutdownHookError extends Thread {
        public void run() {
            throw new Error("JVM shutdown unexpectedly");
        }
    }

    public static class EndApplication extends WaitForStartupApplication {
        boolean endCalled = false;

        @Override
        protected void end() {
            endCalled = true;  // default was System.exit(0);
        }
    }

    private EndApplication application() {
        return Application.getInstance(EndApplication.class);
    }

    public ApplicationEndTest(String testName) {
        super(testName);
        if (!isAppLaunched) {
            EndApplication.launchAndWait(EndApplication.class);
            isAppLaunched = true;
            Runtime rt = Runtime.getRuntime();
            Thread hook = new ShutdownHookError();
            rt.addShutdownHook(hook);
            application().exit();
            rt.removeShutdownHook(hook);
        }
    }

    public void testEndCalled() {
        assertTrue(application().endCalled);
    }
}


