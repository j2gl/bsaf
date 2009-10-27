package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.assertEquals;

import javax.swing.border.EmptyBorder;

/**
 * EmptyBorder does not implement a custom equals method, so to compare EmptyBorders we get the Insets
 */
public class TestStringToEmptyBorder
{
    public TestStringToEmptyBorder() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToEmptyBorder defaultConverter;

    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToEmptyBorder();

    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()


    @Test(expected = IllegalArgumentException.class)
    public void testNullString() throws StringConvertException
    {
        String s = null;
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testEmptyString() throws StringConvertException
    {
        String s = "";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testBadString() throws StringConvertException
    {
        String s = "foo";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testBadPartialString1() throws StringConvertException
    {
        String s = "20";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testBadPartialString2() throws StringConvertException
    {
        String s = "20,";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testBadPartialString3() throws StringConvertException
    {
        String s = "20,40,";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testBadPartialString4() throws StringConvertException
    {
        String s = "20,   40  , 40";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testBadPartialString5() throws StringConvertException
    {
        String s = "20,   40  b";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testBadPartialString6() throws StringConvertException
    {
        String s = "a, b";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testBadPartialString7() throws StringConvertException
    {
        String s = "20,40, 50,";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testBadPartialString8() throws StringConvertException
    {
        String s = "20,40, 50,  a";
        defaultConverter.convert(s);
    }



    @Test
    public void testValidString1() throws StringConvertException
    {
        String s = "100,222,200,300";
        EmptyBorder expected = new EmptyBorder(100, 222, 200, 300);
        EmptyBorder actual = defaultConverter.convert(s);
        assertEquals("EmptyBorder should reflect String value", expected.getBorderInsets(), actual.getBorderInsets());
    }

    @Test
    public void testStringWithSpaces1() throws StringConvertException
    {
        String s = "1  , 2,  3,  4";
        EmptyBorder expected = new EmptyBorder(1, 2, 3, 4);
        EmptyBorder actual = defaultConverter.convert(s);
        assertEquals("EmptyBorder should reflect String value", expected.getBorderInsets(), actual.getBorderInsets());
    }

    @Test
    public void testStringWithSpaces2() throws StringConvertException
    {
        String s = "  100  ,   222,    200,   300   ";
        EmptyBorder expected = new EmptyBorder(100, 222, 200, 300);
        EmptyBorder actual = defaultConverter.convert(s);
        assertEquals("EmptyBorder should reflect String value", expected.getBorderInsets(), actual.getBorderInsets());
    }

    @Test
    public void testWithFloats() throws StringConvertException
    {
        String s = "17.234, 29.345, 27.67, 154.23";
        EmptyBorder expected = new EmptyBorder(17, 29, 27, 154);//converter uses intValue(), truncates decimal parts
        EmptyBorder actual = defaultConverter.convert(s);
        assertEquals("EmptyBorder should reflect String value", expected.getBorderInsets(), actual.getBorderInsets());
    }

    @Test
    public void testWithNegatives() throws StringConvertException
    {
        String s = "-17.234, -29.345, -27.67, -154.23";
        EmptyBorder expected = new EmptyBorder(-17, -29, -27, -154); //converter uses intValue(), truncates decimal parts
        EmptyBorder actual = defaultConverter.convert(s);
        assertEquals("EmptyBorder should reflect String value", expected.getBorderInsets(), actual.getBorderInsets());
    }
}