package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.*;

import java.awt.*;

public class TestStringToFont
{
    public TestStringToFont() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToFont defaultConverter;

    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToFont();

    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()


    @Test(expected = IllegalArgumentException.class)
    public void testNullString() throws StringConvertException
    {
        String s = null;
        Font actual = defaultConverter.convert(s);
    }

    @Test
    public void testEmptyString() throws StringConvertException
    {
        String s = "";
        Font expected = new Font("", Font.PLAIN, 12);
        Font actual = defaultConverter.convert(s);
        assertEquals("Converting empty String should return default Font", expected, actual);
    }

     //face-STYLE-size, for example "Arial-PLAIN-12"

    @Test
    public void testBadString() throws StringConvertException
    {
        String s = "12 - a 1 16";

        Font expected = new Font("12 - a 1", Font.PLAIN, 16);
        Font actual = defaultConverter.convert(s);
        assertEquals("Converting String should return expected Font", expected, actual);
    }

    @Test
    public void testString1() throws StringConvertException
    {
        String s = "Arial-PLAIN-12";

        Font expected = new Font("Arial", Font.PLAIN, 12);
        Font actual = defaultConverter.convert(s);
        assertEquals("Converting String should return expected Font", expected, actual);
    }

    @Test
    public void testString2() throws StringConvertException
    {
        String s = "Helvetica-italic-24";

        Font expected = new Font("Helvetica", Font.ITALIC, 24);
        Font actual = defaultConverter.convert(s);
        assertEquals("Converting String should return expected Font", expected, actual);
    }

}