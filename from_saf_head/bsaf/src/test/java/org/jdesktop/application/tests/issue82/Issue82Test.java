/*
* Copyright (C) 2009 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/
package org.jdesktop.application.tests.issue82;

import junit.framework.TestCase;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Launcher;

import javax.swing.*;

/**
 * @author Pavel Porvatov
 */
public class Issue82Test extends TestCase {
    private static final String PAUSE = "pause";

    private static final String CONTINUE = "continue";

    protected void setUp() throws Exception {
        Launcher.getInstance().launch(MyApplication.class, null);

        // Wait application initialization
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
            }
        });
    }

    public void testActionsName() throws Exception {
        ApplicationContext context = Application.getInstance(MyApplication.class).getContext();

        ActionProvider target = new ActionProvider();

        // Change the action name and enablement and print it again
        context.getActionMap(target).get(PAUSE).putValue(Action.NAME, CONTINUE);
        context.getActionMap(target).get(PAUSE).setEnabled(false);

        assertEquals(CONTINUE, context.getActionMap(target).get("pause").getValue(Action.NAME));
        assertFalse(context.getActionMap(target).get("pause").isEnabled());

        // Collect garbage and print the action again
        System.gc();

        assertEquals(CONTINUE, context.getActionMap(target).get("pause").getValue(Action.NAME));
        assertFalse(context.getActionMap(target).get("pause").isEnabled());

        context.removeActionMap(target);

        assertEquals(PAUSE, context.getActionMap(target).get("pause").getValue(Action.NAME));
        assertTrue(context.getActionMap(target).get("pause").isEnabled());
    }

    public static class MyApplication extends Application {
        @Override
        protected void startup() {
        }
    }

    private static class ActionProvider {

        @org.jdesktop.application.Action
        public void pause() {
            // Do nothing.
        }
    }
}
