package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.awt.geom.Point2D;

public class TestStringToPoint2D_Double
{
    public TestStringToPoint2D_Double() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToPoint.StringToPoint2D_Double defaultConverter;

    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToPoint.StringToPoint2D_Double();
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
        Point2D.Double expected = new Point2D.Double(20, 40);
        Point2D.Double actual = defaultConverter.convert(s);
        assertEquals("Point2D.Double should reflect String value", expected, actual);
    }

    @Test
    public void testValidString2() throws StringConvertException
    {
        String s = "100,222";
        Point2D.Double expected = new Point2D.Double(100, 222);
        Point2D.Double actual = defaultConverter.convert(s);
        assertEquals("Point2D.Double should reflect String value", expected, actual);
    }

    @Test
    public void testStringWithSpaces1() throws StringConvertException
    {
        String s = "100 , 222";
        Point2D.Double expected = new Point2D.Double(100, 222);
        Point2D.Double actual = defaultConverter.convert(s);
        assertEquals("Point2D.Double should reflect String value", expected, actual);
    }

    @Test
    public void testStringWithSpaces2() throws StringConvertException
    {
        String s = "  100   ,   222  ";
        Point2D.Double expected = new Point2D.Double(100, 222);
        Point2D.Double actual = defaultConverter.convert(s);
        assertEquals("Point2D.Double should reflect String value", expected, actual);
    }

    @Test
    public void testWithFloats() throws StringConvertException
    {
        String s = "17.234, 29.345";
        Point2D.Double expected = new Point2D.Double();
        expected.setLocation(17.234, 29.345);
        Point2D.Double actual = defaultConverter.convert(s);
        assertEquals("Point2D.Double should reflect String value", expected, actual);
    }

    @Test
    public void testWithFloats2() throws StringConvertException
    {
        String s = "17.534, 29.645"; //should round up
        Point2D.Double expected = new Point2D.Double();
        expected.setLocation(17.534, 29.645);
        Point2D.Double actual = defaultConverter.convert(s);
        assertEquals("Point2D.Double should reflect String value", expected, actual);
    }

    @Test
    public void testWithNegatives() throws StringConvertException
    {
        String s = "-17.234, -29.345";
        Point2D.Double expected = new Point2D.Double();
        expected.setLocation(-17.234, -29.345);
        Point2D.Double actual = defaultConverter.convert(s);
        assertEquals("Point2D.Double should reflect String value", expected, actual);
    }

    @Test
    public void testWithNegatives2() throws StringConvertException
    {
        String s = "-17.534, -29.645";
        Point2D.Double expected = new Point2D.Double();
        expected.setLocation(-17.534, -29.645);
        Point2D.Double actual = defaultConverter.convert(s);
        assertEquals("Point2D.Double should reflect String value", expected, actual);
    }
}