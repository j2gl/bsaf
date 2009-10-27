package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.awt.*;

public class TestStringToPoint
{
    public TestStringToPoint() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToPoint defaultConverter;
    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToPoint();
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

    @Test
    public void testValidString1() throws StringConvertException
    {
        String s = "20,40";
        Point expected = new Point(20, 40);
        Point actual = defaultConverter.convert(s);
        assertEquals("Point should reflect String value", expected, actual);
    }

    @Test
    public void testValidString2() throws StringConvertException
    {
        String s = "100,222";
        Point expected = new Point(100, 222);
        Point actual = defaultConverter.convert(s);
        assertEquals("Point should reflect String value", expected, actual);
    }

    @Test
    public void testStringWithSpaces1() throws StringConvertException
    {
        String s = "100 , 222";
        Point expected = new Point(100, 222);
        Point actual = defaultConverter.convert(s);
        assertEquals("Point should reflect String value", expected, actual);
    }

    @Test
    public void testStringWithSpaces2() throws StringConvertException
    {
        String s = "  100   ,   222  ";
        Point expected = new Point(100, 222);
        Point actual = defaultConverter.convert(s);
        assertEquals("Point should reflect String value", expected, actual);
    }

    @Test
    public void testWithFloats() throws StringConvertException
    {
        String s = "17.234, 29.345";
        //Point uses Math.floor in setLocation call, but also adds .5 to x, y. In effect, it rounds up half-way between integers,
        //i.e., using standard rounding rules
        Point expected = new Point(17, 29); 
        Point actual = defaultConverter.convert(s);
        assertEquals("Point should reflect String value", expected, actual);
    }

    @Test
    public void testWithFloats2() throws StringConvertException
    {
        String s = "17.534, 29.645"; //should round up
        //Point uses Math.floor in setLocation call, but also adds .5 to x, y. In effect, it rounds up half-way between integers,
        //i.e., using standard rounding rules
        Point expected = new Point(18, 30);
        Point actual = defaultConverter.convert(s);
        assertEquals("Point should reflect String value", expected, actual);
    }

    @Test
    public void testWithNegatives() throws StringConvertException
    {
        String s = "-17.234, -29.345";
        Point expected = new Point(-17, -29);
        Point actual = defaultConverter.convert(s);
        assertEquals("Point should reflect String value", expected, actual);
    }

    @Test
    public void testWithNegatives2() throws StringConvertException
    {
        String s = "-17.534, -29.645";
        Point expected = new Point(-18, -30);
        Point actual = defaultConverter.convert(s);
        assertEquals("Point should reflect String value", expected, actual);
    }
}