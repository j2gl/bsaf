package org.jdesktop.application.inject;

import org.junit.*;
import org.junit.runners.Suite;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TestUtil;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.awt.*;
import java.io.IOException;

import static junit.framework.Assert.assertSame;


/**
 * @author Rob Ross
 * @version Date: Nov 9, 2009  7:33:16 PM
 */
public class TestAbstractButtonInjector
{
    @RunWith(value = Suite.class)
    @Suite.SuiteClasses(value ={TestAbstractButtonInjector.AdHoc.class, TestInjectedValues.class  })
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

        AbstractButtonInjector injector;
        ResourceMap map;

        @Before
        public void methodSetup()
        {
            injector = new AbstractButtonInjector();
            map = new ResourceMap(Arrays.asList("org.jdesktop.application.inject.resources.AbstractButtonInjector"));

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
            JButton button = new JButton();
            injector.inject(button, null, false);
        }

        @Test
        public void testInjectionReturnsSameTargetInstance()
        {
            assertNotNull(map);
            JButton button = new JButton();
            button.setName("jLabel");
            JButton actual = (JButton) injector.inject(button, map, false);
            assertSame("inject should return same instance", button, actual);
        }

        @Test
        public void testButton2InjectionProperties()
        {
            JButton button = new JButton();
            button.setName("button2");
            Icon testIcon = new ImageIcon(TestUtil.getTestImage("org/jdesktop/application/inject/resources/black1x1.png"));
            button.setIcon(testIcon); //resource property should set this to null.

            injector.inject(button, map, false);

            assertEquals("Button2 text as expected", "Exit", button.getText());
            assertEquals("Button2 mnemonic as expected", 88, button.getMnemonic());
            assertEquals("Button2 DisplayedMnemonicIndex as expected", 1, button.getDisplayedMnemonicIndex());

            assertEquals("Button2 location as expected", new Point(50,60), button.getLocation());
            assertEquals("Button2 size as expected", new Dimension(20,20), button.getSize());

            assertNull("Button2 icon is null", button.getIcon());
        }
    }


    //test that each injected property is correctly set
    @RunWith(value = Parameterized.class)
    public static class TestInjectedValues
    {
         static AbstractButtonInjector injector;
         static ResourceMap map;
         static JButton button;
         static Icon testIcon;

        @BeforeClass
        public static void unitSetup()
        {

            injector = new AbstractButtonInjector();
            map = new ResourceMap(Arrays.asList("org.jdesktop.application.inject.resources.AbstractButtonInjector"));
            button = new JButton();
            button.setName("button");
            injector.inject(button, map, false);

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
                    {"defaultCapable", false, "isDefaultCapable"},
                    {"actionCommand", "This is the ActionCommand string", "getActionCommand"},
                    {"borderPainted", false, "isBorderPainted"},
                    {"contentAreaFilled", false, "isContentAreaFilled"},
                    {"disabledIcon", testIcon, "getDisabledIcon"},
                    {"disabledSelectedIcon", testIcon, "getDisabledSelectedIcon"},
                    {"enabled", false, "isEnabled"},
                    {"focusPainted", false, "isFocusPainted"},
                    {"hideActionText", true, "getHideActionText"},
                    {"horizontalAlignment", 4, "getHorizontalAlignment"},
                    {"horizontalTextPosition", 0, "getHorizontalTextPosition"},
                    {"icon", testIcon, "getIcon"},
                    {"iconTextGap", 16, "getIconTextGap"},
                    {"margin", new Insets(1,2,3,4), "getMargin"},
                    {"displayedMnemonicIndex", 6, "getDisplayedMnemonicIndex"},
                    {"mnemonic", 65, "getMnemonic"},
                    {"text", "Applejack", "getText"},
                    {"verticalAlignment", 3, "getVerticalAlignment"},
                    {"verticaTextPosition", 1, "getVerticalTextPosition"},
                    {"alignmentX", 0.17f, "getAlignmentX"},
                    {"alignmentY", 0.82345f, "getAlignmentY"},
                    {"autoscrolls", true, "getAutoscrolls"},
                    {"background", new Color(127,127,127), "getBackground"},
                    {"debugGraphicsOptions", 2, "getDebugGraphicsOptions"},
                    {"doubleBuffered", false, "isDoubleBuffered"},
                    {"font", new Font("Arial",Font.PLAIN,  12), "getFont"},
                    {"foreground", new Color(50,60,70), "getForeground"},
                    {"inheritsPopupMenu", true, "getInheritsPopupMenu"},

                    {"maximumSize", new Dimension(400,400), "getMaximumSize"},
                    {"minimumSize", new Dimension(200,200), "getMinimumSize"},
                    {"preferredSize", new Dimension(300, 300), "getPreferredSize"},
                    {"opaque", false, "isOpaque"},
                    {"requestFocusEnabled", false, "isRequestFocusEnabled"},
                    {"toolTipText", "This is the tooltip text string", "getToolTipText"},
                    {"verifyInputWhenFocusTarget", false, "getVerifyInputWhenFocusTarget"},
                    {"visible", true, "isVisible"},
                    {"focusCycleRoot", true, "isFocusCycleRoot"},
                    {"focusTraversalPolicyProvider", true, "isFocusTraversalPolicyProvider"},
                    {"bounds", new Rectangle(5,10,15,20), "getBounds"},
                    {"focusable", false, "isFocusable"},
                    {"focusTraversalKeysEnabled", true, "getFocusTraversalKeysEnabled"},
                    {"ignoreRepaint", true, "getIgnoreRepaint"},
                    {"location", new Point(5, 10), "getLocation"},
                    {"size", new Dimension(15, 20), "getSize"},
                    {"name", "foo bar name String", "getName"},

            });
        }

        @Test
        public void textPropValue() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
        {
            Class expectedType = expectedValue.getClass();

            Method getter = button.getClass().getMethod(getterName);
            Object actualValue = getter.invoke(button);

            if (expectedValue instanceof Icon)
            {
                TestUtil.assertResourcesEqual(String.format("Injection should set property '%s' to expected value", propName),expectedValue, actualValue);
            }
            else
            {
                assertEquals(String.format("Injection should set property '%s' to expected value", propName), expectedValue, actualValue);
            }

            
        }
    }


}
