package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.*;

import java.awt.*;

public class TestStringToDimension
{
    public TestStringToDimension() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToDimension defaultConverter;
    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToDimension();
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

    @Test (expected= StringConvertException.class)
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

    @Test (expected = StringConvertException.class)
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
        Dimension expected = new Dimension(20,40);
        Dimension actual = defaultConverter.convert(s);
        assertEquals("Dimension should reflect String value", expected, actual);
    }

    @Test
    public void testValidString2() throws StringConvertException
    {
        String s = "100,222"; 
        Dimension expected = new Dimension(100, 222);
        Dimension actual = defaultConverter.convert(s);
        assertEquals("Dimension should reflect String value", expected, actual);
    }

    @Test
    public void testStringWithSpaces1() throws StringConvertException
    {
        String s = "100 , 222";
        Dimension expected = new Dimension(100, 222);
        Dimension actual = defaultConverter.convert(s);
        assertEquals("Dimension should reflect String value", expected, actual);
    }

    @Test
    public void testStringWithSpaces2() throws StringConvertException
    {
        String s = "  100   ,   222  ";
        Dimension expected = new Dimension(100, 222);
        Dimension actual = defaultConverter.convert(s);
        assertEquals("Dimension should reflect String value", expected, actual);
    }

    @Test
    public void testWithFloats() throws StringConvertException
    {
        String s = "17.234, 29.345";
        Dimension expected = new Dimension(18, 30); //Dimension uses Math.ceil in setSize call
        Dimension actual = defaultConverter.convert(s);
        assertEquals("Dimension should reflect String value", expected, actual);
    }

    @Test
    public void testWithNegatives() throws StringConvertException
    {
        String s = "-17.234, -29.345";
        Dimension expected = new Dimension(-17, -29); //Dimension uses Math.ceil in setSize call
        Dimension actual = defaultConverter.convert(s);
        assertEquals("Dimension should reflect String value", expected, actual);
    }
}