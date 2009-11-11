package org.jdesktop.application.inject;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TestUtil;

import javax.swing.*;
import java.util.Arrays;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static junit.framework.Assert.assertSame;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * @author Rob Ross
 * @version Date: Nov 10, 2009  1:34:13 AM
 */
public class TestActionInjector
{

    @RunWith(value = Suite.class)
    @Suite.SuiteClasses(value = {AdHoc.class})
    public static class ASuite
    {
    }

    public static class AdHoc
    {
        @BeforeClass
        public static void unitSetup()
        {
        } // unitSetup()

        @AfterClass
        public static void unitCleanup()
        {
        } // unitCleanup()

        ActionInjector injector;
        ResourceMap map;

        @Before
        public void methodSetup()
        {
            injector = new ActionInjector();
            map = new ResourceMap(Arrays.asList("org.jdesktop.application.inject.resources.ActionInjector"));


        } // methodSetup()

        @After
        public void methodCleanup()
        {
        } // methodCleanup()

        @Test(expected = IllegalArgumentException.class)
        public void testNullTarget()
        {
            injector.inject(null, null, false);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testNullProperties()
        {
            Action  dummy = new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e)
                {

                }
            };
            injector.inject(dummy, null, false);
        }

        @Test
        public void testInjectionReturnsSameTargetInstance()
        {
            assertNotNull(map);
            Action action = new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {

                }
            };
            action.putValue(Action.NAME, "action1");

            Action actual = injector.inject(action, map, false);
            assertSame("inject should return same instance", action, actual);
        }

        @Test
        public void testAction1InjectionProperties()
        {
            Action action1 = new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {

                }
            };
            action1.putValue(Action.NAME, "action1");
            Icon testIcon = new ImageIcon(TestUtil.getTestImage("org/jdesktop/application/inject/resources/black1x1.png"));


            injector.inject(action1, map, false);

            assertEquals("action1 name as expected", "Save As", action1.getValue(Action.NAME));
            assertEquals("action1 mnemonic as expected", 65, action1.getValue(Action.MNEMONIC_KEY));
            assertEquals("action1 DisplayedMnemonicIndex as expected", 5, action1.getValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY));

            assertEquals("action1 accelerator as expected", KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), action1.getValue(Action.ACCELERATOR_KEY));
            assertEquals("action1 command as expected", "SaveAs", action1.getValue(Action.ACTION_COMMAND_KEY));

            assertEquals("action1 short description as expected", "short Description text", action1.getValue(Action.SHORT_DESCRIPTION));
            assertEquals("action1 long description as expected", "long Description text", action1.getValue(Action.LONG_DESCRIPTION));

            TestUtil.assertResourcesEqual("action1 large icon as expected", testIcon,  action1.getValue(Action.LARGE_ICON_KEY));
            TestUtil.assertResourcesEqual("action1 small icon as expected", testIcon, action1.getValue(Action.SMALL_ICON));  
        }

        @Test
        public void testAction2InjectionProperties()
        {
            //these properties use explicit values for the mnemonic  & displayed index
            Action action2 = new AbstractAction()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {

                }
            };
            action2.putValue(Action.NAME, "action2");
            Icon testIcon = new ImageIcon(TestUtil.getTestImage("org/jdesktop/application/inject/resources/black1x1.png"));


            injector.inject(action2, map, false);

            assertEquals("action2 name as expected", "Zippout Pippout", action2.getValue(Action.NAME));
            assertEquals("action2 mnemonic as expected", 80, action2.getValue(Action.MNEMONIC_KEY));
            assertEquals("action2 DisplayedMnemonicIndex as expected", 8, action2.getValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY));

            assertEquals("action2 accelerator as expected", KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), action2.getValue(Action.ACCELERATOR_KEY));
            assertEquals("action2 command as expected", "ZippoutPippout", action2.getValue(Action.ACTION_COMMAND_KEY));

            assertEquals("action2 short description as expected", "short Description text", action2.getValue(Action.SHORT_DESCRIPTION));
            assertEquals("action2 long description as expected", "long Description text", action2.getValue(Action.LONG_DESCRIPTION));

            TestUtil.assertResourcesEqual("action2 large icon as expected", testIcon, action2.getValue(Action.LARGE_ICON_KEY));
            TestUtil.assertResourcesEqual("action2 small icon as expected", testIcon, action2.getValue(Action.SMALL_ICON));

        }
    }





/*    public static void main(String[] args)
    {
        ActionInjector injector;
        ResourceMap map;
        injector = new ActionInjector();
        map = new ResourceMap(Arrays.asList("org.jdesktop.application.inject.resources.ActionInjector"));
        assertNotNull(map);
        Action action = new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

            }
        };
        action.putValue(Action.NAME, "action1");

        Action actual = injector.inject(action, map, false);

    }*/

}
