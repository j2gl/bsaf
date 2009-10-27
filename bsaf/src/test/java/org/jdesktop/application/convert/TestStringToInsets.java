package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.awt.*;

public class TestStringToInsets
{
    public TestStringToInsets() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToInsets defaultConverter;

    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToInsets();

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
        Insets expected = new Insets(100, 222, 200, 300);
        Insets actual = defaultConverter.convert(s);
        assertEquals("Insets should reflect String value", expected, actual);
    }

    @Test
    public void testStringWithSpaces1() throws StringConvertException
    {
        String s = "1  , 2,  3,  4";
        Insets expected = new Insets(1, 2, 3, 4);
        Insets actual = defaultConverter.convert(s);
        assertEquals("Insets should reflect String value", expected, actual);
    }

    @Test
    public void testStringWithSpaces2() throws StringConvertException
    {
        String s = "  100  ,   222,    200,   300   ";
        Insets expected = new Insets(100, 222, 200, 300);
        Insets actual = defaultConverter.convert(s);
        assertEquals("Insets should reflect String value", expected, actual);
    }

    @Test
    public void testWithFloats() throws StringConvertException
    {
        String s = "17.234, 29.345, 27.67, 154.23";
        //converter casts these to ints, thus truncating the decimal part
        Insets expected = new Insets(17, 29, 27, 154);
        Insets actual = defaultConverter.convert(s);
        assertEquals("Insets should reflect String value", expected, actual);
    }

    @Test
    public void testWithNegatives() throws StringConvertException
    {
        String s = "-17.234, -29.345, -27.67, -154.23";
        //converter casts these to ints, thus truncating the decimal part
        Insets expected = new Insets(-17, -29, -27, -154);
        Insets actual = defaultConverter.convert(s);
        assertEquals("Insets should reflect String value", expected, actual);
    }
}