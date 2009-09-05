/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package org.jdesktop.application;

import junit.framework.TestCase;

import javax.swing.*;


/**
 * No Application.lookAndFeel resource defined, so we default to
 * "system" (unlike the JVM itself) which means to use the
 * system (native) look and feel.
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ApplicationNoLNFResourceTest extends TestCase {

    public static class ApplicationNoLNF extends WaitForStartupApplication {
    }

    private static boolean isAppLaunched = false;

    public ApplicationNoLNFResourceTest(String testName) {
        super(testName);
        if (!isAppLaunched) {
            System.out.println("Starting...");
            ApplicationNoLNF.launchAndWait(ApplicationNoLNF.class);
            isAppLaunched = true;
        }
    }

    public void testApplicationLookAndFeelResource() {
        LookAndFeel lnf = UIManager.getLookAndFeel();
        assertTrue("Look and Feel should be native", lnf.isNativeLookAndFeel());
    }
}
