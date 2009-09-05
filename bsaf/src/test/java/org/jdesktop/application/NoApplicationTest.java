/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package org.jdesktop.application;

import junit.framework.TestCase;

import javax.swing.*;

/**
 * Check that Application.getInstance() can be used even in
 * situtations where an Application hasn't actually been launched.
 * <p/>
 * This test depends on resources/Basic.properties
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class NoApplicationTest extends TestCase {

    private static class Basic {
        @Action
        public void myAction() {
        }
    }

    public void testGetInstance() {
        Application app = Application.getInstance();
        assertNotNull("Application.getInstance()", app);
        assertNotNull("Application.getInstance().getContext()", app.getContext());
        ApplicationContext ctx = app.getContext();
        assertEquals(app, ctx.getApplication());
        ResourceMap appRM = ctx.getResourceMap();
        assertNotNull("Application.getInstance().getContext().getResourceMap()", appRM);

        /* Verify that a few of the default Application resources are defined */
        String[] resources = {
                "Application.title",
                "Application.vendor",
                "copy.Action.text",
                "quit.Action.text",
        };
        for (String resource : resources) {
            assertNotNull(appRM.getString(resource));
        }

        /* Verify that the default Application actions are defined */
        ActionMap appAM = ctx.getActionMap();
        assertNotNull("Application.getInstance().getContext().getActionMap()", appAM);
        String[] appActionNames = {"quit", "cut", "copy", "paste", "delete"};
        for (String actionName : appActionNames) {
            assertNotNull(appAM.get(actionName));
        }

        /* Check a few entries in the ResourceMap for Basic.class */
        ResourceMap basicRM = ctx.getResourceMap(Basic.class);
        assertNotNull("Application.getInstance().getContext().getResourceMap(Basic.class)", basicRM);
        String aStringResource = "aStringResource";
        assertTrue("containsKey(\"aStringResource\")", basicRM.containsKey(aStringResource));
        assertEquals("getString(\"aStringResource\")", aStringResource, basicRM.getString(aStringResource));
        assertEquals("Hello World", basicRM.getString("aHelloMessage", "World"));

        /* Check that the Basic.class ActionMap is valid */
        Basic basic = new Basic();
        ActionMap basicAM = ctx.getActionMap(Basic.class, basic);
        assertNotNull("Application.getInstance().getContext().getActionMap(Basic.class)", basicAM);
        assertNotNull(basicAM.get("myAction"));
        for (String actionName : appActionNames) {
            assertNotNull(basicAM.get(actionName));
        }
    }

}
