package org.jdesktop.application.inject;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TestUtil;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static junit.framework.Assert.assertSame;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * @author Rob Ross
 * @version Date: Nov 10, 2009  5:51:35 PM
 */
public class TestJLabelInjector
{
    @RunWith(value = Suite.class)
    @Suite.SuiteClasses(value = {AdHoc.class, TestInjectedValues.class})
    public static class ASuite{}

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

        JLabelInjector injector;
        ResourceMap map;

        @Before
        public void methodSetup()
        {
            injector = new JLabelInjector();
             map = new ResourceMap(Arrays.asList("org.jdesktop.application.inject.resources.JLabelInjector"));
        } // methodSetup()

        @Test(expected = IllegalArgumentException.class)
        public void testNullTarget()
        {
            injector.inject(null, null, false);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testNullProperties()
        {
            JLabel jLabel = new JLabel();
            injector.inject(jLabel, null, false);
        }

        @Test
        public void testInjectionReturnsSameTargetInstance()
        {
            assertNotNull(map);
            JLabel jLabel = new JLabel();
            jLabel.setName("jLabel");
            JLabel actual = injector.inject(jLabel, map, false);
            assertSame("inject should return same instance", jLabel, actual);
        }

        @Test
        public void testJLabel2InjectionProperties()
        {
            JLabel jLabel = new JLabel();
            jLabel.setName("jLabel2");
            Icon testIcon = new ImageIcon(TestUtil.getTestImage("org/jdesktop/application/inject/resources/black1x1.png"));
            jLabel.setIcon(testIcon); //resource property should set this to null.

            injector.inject(jLabel, map, false);

            assertEquals("jLabel2 text as expected", "Exit", jLabel.getText());
            assertEquals("jLabel2 displayedMnemonic as expected", 88, jLabel.getDisplayedMnemonic());
            assertEquals("jLabel2 DisplayedMnemonicIndex as expected", 1, jLabel.getDisplayedMnemonicIndex());

            assertEquals("jLabel2 location as expected", new Point(50, 60), jLabel.getLocation());
            assertEquals("jLabel2 size as expected", new Dimension(20, 20), jLabel.getSize());

            assertNull("jLabel2 icon is null", jLabel.getIcon());
        }
    }

    //test that each injected property is correctly set
    @RunWith(value = Parameterized.class)
    public static class TestInjectedValues
    {
        static JLabelInjector injector;
        static ResourceMap map;
        static JLabel jLabel;
        static Icon testIcon;

        @BeforeClass
        public static void unitSetup()
        {

            injector = new JLabelInjector();
            map = new ResourceMap(Arrays.asList("org.jdesktop.application.inject.resources.JLabelInjector"));
            jLabel = new JLabel();
            jLabel.setName("jLabel");
            injector.inject(jLabel, map, false);

        }

        String propName;
        Object expectedValue;
        String getterName;

        public TestInjectedValues(String propName, Object expectedValue, String getterName)
        {
            this.propName = propName;
            this.expectedValue = expectedValue;
            this.getterName = getterName;
        }

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data() throws IOException
        {
            testIcon = new ImageIcon(TestUtil.getTestImage("org/jdesktop/application/inject/resources/black1x1.png"));
            return Arrays.asList(new Object[][]{
                    //property name, expected value, getter name (for reflection)
                    {"disabledIcon", testIcon, "getDisabledIcon"},
                    {"displayedMnemonic", 65, "getDisplayedMnemonic"},
                    {"displayedMnemonicIndex", 6, "getDisplayedMnemonicIndex"},
                    {"horizontalAlignment", 4, "getHorizontalAlignment"},
                    {"horizontalTextPosition", 0, "getHorizontalTextPosition"},
                    {"icon", testIcon, "getIcon"},
                    {"iconTextGap", 16, "getIconTextGap"},
                    {"text", "Applejack", "getText"},
                    {"verticalAlignment", 3, "getVerticalAlignment"},
                    {"verticaTextPosition", 1, "getVerticalTextPosition"},

                    //JComponent Properties
                    {"alignmentX", 0.17f, "getAlignmentX"},
                    {"alignmentY", 0.82345f, "getAlignmentY"},
                    {"autoscrolls", true, "getAutoscrolls"},
                    {"background", new Color(127, 127, 127), "getBackground"},
                    {"debugGraphicsOptions", 2, "getDebugGraphicsOptions"},
                    {"doubleBuffered", false, "isDoubleBuffered"},
                    {"enabled", false, "isEnabled"},
                    {"font", new Font("Arial", Font.ITALIC, 13), "getFont"},
                    {"foreground", new Color(50, 60, 70), "getForeground"},
                    {"inheritsPopupMenu", true, "getInheritsPopupMenu"},

                    {"maximumSize", new Dimension(400, 400), "getMaximumSize"},
                    {"minimumSize", new Dimension(200, 200), "getMinimumSize"},
                    {"preferredSize", new Dimension(300, 300), "getPreferredSize"},
                    {"opaque", false, "isOpaque"},
                    {"requestFocusEnabled", false, "isRequestFocusEnabled"},
                    {"toolTipText", "This is the JLabel tooltip text string", "getToolTipText"},
                    {"verifyInputWhenFocusTarget", false, "getVerifyInputWhenFocusTarget"},
                    {"visible", true, "isVisible"},
                    {"focusCycleRoot", true, "isFocusCycleRoot"},
                    {"focusTraversalPolicyProvider", true, "isFocusTraversalPolicyProvider"},
                    {"bounds", new Rectangle(5, 10, 15, 20), "getBounds"},
                    {"focusable", false, "isFocusable"},
                    {"focusTraversalKeysEnabled", true, "getFocusTraversalKeysEnabled"},
                    {"ignoreRepaint", true, "getIgnoreRepaint"},
                    {"location", new Point(5, 10), "getLocation"},
                    {"size", new Dimension(15, 20), "getSize"},
                    {"name", "modified JLabel name", "getName"},

            });
        }

        @Test
        public void textPropValue() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
        {
            Class expectedType = expectedValue.getClass();

            Method getter = jLabel.getClass().getMethod(getterName);
            Object actualValue = getter.invoke(jLabel);

            if (expectedValue instanceof Icon)
            {
                TestUtil.assertResourcesEqual(String.format("Injection should set property '%s' to expected value", propName), expectedValue, actualValue);
            }
            else
            {
                assertEquals(String.format("Injection should set property '%s' to expected value", propName), expectedValue, actualValue);
            }


        }
    }
}
