/*
 * Copyright (C) 2013 Kevin Greiner.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
*/

package org.jdesktop.application;

import java.awt.Frame;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.EventObject;
import java.util.Map;
import org.jdesktop.application.session.WindowState;
import org.jdesktop.application.utils.SwingHelper;

/**
 * Verify that you can block a frame's iconified state from its persistent state.
 *
 * @author Kevin Greiner
 */
public class FrameExtendedStatePersistenceTest
{

    public static class FrameExtendedStatePersistenceApplication extends WaitForStartupSFA
    {
        private String sessionFile = "mainFrame.session.xml";
        Object sessionObject = null;

        @Override
        protected void startup()
        {
            SwingHelper.setPersistentExtendedStateMask(getMainFrame(), ~Frame.ICONIFIED);
            
            show(new JLabel("Hello World"));
            super.startup();
            getMainFrame().setExtendedState(Frame.ICONIFIED);
        }

        @Override
        protected void shutdown()
        {
            super.shutdown();
            /* Read the newly persisted state. */
            try
            {
                sessionObject = getContext().getLocalStorage().load(sessionFile);
            }
            catch (IOException e)
            {
                throw new Error("couldn't load " + sessionFile, e);
            }
        }

        // Don't call System.exit(), exitListeners, etc
        @Override
        public void exit(EventObject event)
        {
            shutdown();
        }

    }


    @Before
    public void methodSetup()
    {
        System.err.println("This test generates logger warnings.  Ignore them.");
        FrameExtendedStatePersistenceApplication.launchAndWait(FrameExtendedStatePersistenceApplication.class);
    }

    @Test
    public void testBlockingIconifiedInPersistedState() throws Exception
    {
        final FrameExtendedStatePersistenceApplication app = Application.getInstance(FrameExtendedStatePersistenceApplication.class);
        assertTrue("BadSessionStateApplication started", app.isReady());
        Runnable doExit = new Runnable()
        {
            @Override
            public void run() { app.exit(); }  // override doesn't call System.exit
        };
        SwingUtilities.invokeAndWait(doExit);
        
        Map<String, WindowState> session = (Map<String, WindowState>) app.sessionObject;
        final JFrame mainFrame = app.getMainFrame();
        
        assertTrue((mainFrame.getExtendedState() & Frame.ICONIFIED) != 0);
        assertTrue((session.get(mainFrame.getName()).getFrameState() & Frame.ICONIFIED) == 0);
    }
}


