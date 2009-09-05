/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */

package org.jdesktop.application;

import junit.framework.TestCase;

import javax.swing.*;

/**
 * Checks that explicitly defining the Application.lookAndFeel resource
 * to be "system" causes the UIManager.lookAndFeel property to be
 * initialized to the sytem look and feel.
 * This test depends on resources/AppilcationSystemLNF.properties
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ApplicationSystemLNFResourceTest extends TestCase {

    /* Application.lookAndFeel resource is explicity defined to be "system".
     */
    public static class ApplicationSystemLNF extends WaitForStartupApplication {
    }

    private static boolean isAppLaunched = false;

    public ApplicationSystemLNFResourceTest(String testName) {
        super(testName);
        if (!isAppLaunched) {
            ApplicationSystemLNF.launchAndWait(ApplicationSystemLNF.class);
            isAppLaunched = true;
        }
    }

    public void testApplicationLookAndFeelResource() {
        ApplicationContext ctx = Application.getInstance(ApplicationSystemLNF.class).getContext();
        String lnfResource = ctx.getResourceMap().getString("Application.lookAndFeel");
        assertEquals("Application.lookAndFeel resource", "system", lnfResource);
        LookAndFeel lnf = UIManager.getLookAndFeel();
        assertTrue("Look and Feel should be native", lnf.isNativeLookAndFeel());
    }
}


