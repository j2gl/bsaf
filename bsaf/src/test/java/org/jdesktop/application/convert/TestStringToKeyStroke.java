package org.jdesktop.application.convert;

import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.util.Collection;
import java.util.Arrays;

public class TestStringToKeyStroke
{
    public TestStringToKeyStroke() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToKeyStroke defaultConverter;

    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToKeyStroke();

    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()


    @Test(expected = IllegalArgumentException.class)
    public void testNullString() throws StringConvertException
    {
        String s = null;
        KeyStroke actual = defaultConverter.convert(s);
    }

    @Test
    public void testEmptyString() throws StringConvertException
    {
        String s = "";
        KeyStroke expected = null;
        KeyStroke actual = defaultConverter.convert(s);
        assertEquals("Converting empty String should return null", expected, actual);
    }

    @Test
    public void testBadString() throws StringConvertException
    {
        String s = "12 - a 1 16";

        KeyStroke expected = KeyStroke.getKeyStroke(s);
        KeyStroke actual = defaultConverter.convert(s);
        assertEquals("Converting String badly formatted String should return null", expected, actual);
    }

    @Test
    public void testString1() throws StringConvertException
    {
        String s = "INSERT";

        KeyStroke expected = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
        KeyStroke actual = defaultConverter.convert(s);
        assertEquals("Converting String should return expected KeyStroke", expected, actual);
        assertNotNull(actual);
    }

    @Test
    public void testString2() throws StringConvertException
    {
        String s = "command Q";

        KeyStroke expected = KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.META_MASK);
        KeyStroke actual = defaultConverter.convert(s);
        assertEquals("Converting String should return expected KeyStroke", expected, actual);
    }

    @Test
    public void testString3() throws StringConvertException
    {
        String s = "alt shift released X";

        KeyStroke expected = KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, true);
        KeyStroke actual = defaultConverter.convert(s);
        assertEquals("Converting String should return expected KeyStroke", expected, actual);
    }

    //<modifiers>* (<typedID> | <pressedReleasedID>)
    /*
    modifiers := shift | control | ctrl | meta | alt | altGraph
    typedID := typed <typedKey>
    typedKey := string of length 1 giving Unicode character.
    pressedReleasedID := (pressed | released) key
    key := KeyEvent key code name, i.e. the name following "VK_".


     If typed, pressed or released is not specified, pressed is assumed. Here are some examples:

     "INSERT" => getKeyStroke(KeyEvent.VK_INSERT, 0);
     "control DELETE" => getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK);
     "alt shift X" => getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK);
     "alt shift released X" => getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, true);
     "typed a" => getKeyStroke('a');

     */

    /**
     * Test various combinations of keystroke definitions
     */
    @RunWith(value = Parameterized.class)
    public static class TestStrings
    {
        private final static int platformShortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        private final static int META = KeyEvent.META_DOWN_MASK;
        private final static int ALT = KeyEvent.ALT_DOWN_MASK;
        private final static int CONTROL = KeyEvent.CTRL_DOWN_MASK;
        private final static int SHIFT = KeyEvent.SHIFT_DOWN_MASK;
        
        private KeyStroke expected;
        private String    testData;

        public TestStrings(KeyStroke expected, String testData)
        {
            this.expected = expected;
            this.testData = testData;
        }

        @Parameterized.Parameters
        public static Collection data()
        {
            java.util.List l = Arrays.asList(new Object[][]{

                    {KeyStroke.getKeyStroke(KeyEvent.VK_Q, META), "command Q"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_Q, META), "meta Q"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, ALT), "alt F4"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_N, platformShortcutMask), "shortcut N"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, CONTROL), "control F4"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, CONTROL), "ctrl F4"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, META), "command BACK_QUOTE"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_P, SHIFT), "shift P"},

                    {KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_P, platformShortcutMask | SHIFT), "shortcut shift P"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_Z, META | SHIFT), "command shift Z"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_T, META | ALT | SHIFT), "meta alt shift T"},

                    //test extra spacing
                    {KeyStroke.getKeyStroke(KeyEvent.VK_Q, META), "command   Q"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_Q, META), "meta      Q"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, ALT), "alt        F4"},

                    //test improper capitalization of modifier string
                    {KeyStroke.getKeyStroke(KeyEvent.VK_Q, META), "coMmand Q"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_Q, META), "META Q"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, ALT), "Alt F4"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_N, platformShortcutMask), "ShortCut N"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, CONTROL), "CONTRol F4"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, CONTROL), "CTRL F4"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_P, SHIFT), "SHIFT P"},

                    //test improper capitalization of key code name
                    {KeyStroke.getKeyStroke(KeyEvent.VK_Q, META), "command q"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_Q, META), "meta q"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, ALT), "alt f4"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_N, platformShortcutMask), "shortcut n"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, CONTROL), "control f4"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_F4, CONTROL), "ctrl f4"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_P, SHIFT), "shift p"},
                    {KeyStroke.getKeyStroke(KeyEvent.VK_BACK_QUOTE, META), "command bacK_Quote"},

                    //special handling for "typed" keystroke
                    {KeyStroke.getKeyStroke('A'), "typed A"},
                    {KeyStroke.getKeyStroke('a'), "typed a"},
                    {KeyStroke.getKeyStroke('A'), "tYped A"},
                    {KeyStroke.getKeyStroke('a'), "tYped a"},
                    {KeyStroke.getKeyStroke('a'), "tYped    a"}, //test extra spaces
                    
                    
            });
            return l;
        }

        @Test
        public void testConvert() throws StringConvertException
        {
            KeyStroke actual = defaultConverter.convert(testData);
            assertEquals(String.format("Converting String '%s' should return expected KeyStroke", testData), expected, actual);
        }

        StringToKeyStroke defaultConverter;

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToKeyStroke();

        } // methodSetup()
        
    }


}