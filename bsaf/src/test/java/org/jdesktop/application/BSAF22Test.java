package org.jdesktop.application;

import java.beans.Beans;
import junit.framework.TestCase;

/**
 *
 * @author Illya Yalovyy
 */
public class BSAF22Test extends TestCase {

    public static class ActionObject {
        public int n=0;
        @Action
        public void actionMethod() {
            n++;
        }
    }

    /**
     * Inside a visual UI editor Application is never launched,
     * but it could be called for actionMap or ResourceMap.
     */
    public void testApplicationWithoutLaunch() {
        Beans.setDesignTime(false);
        Application app = Application.getInstance(WaitForStartupApplication.class);
        assertNull(app); // Application has to be launched first

        Beans.setDesignTime(true);
        app = Application.getInstance(WaitForStartupApplication.class);
        assertNotNull(app);
        assertEquals(WaitForStartupApplication.class, app.getClass());
        ApplicationContext ac = app.getContext();
        assertNotNull(ac);

        ActionObject ao = new ActionObject();

        assertNotNull(ac.getActionMap(ao));
        assertNotNull(ac.getResourceMap(ao.getClass()));

    }
}
