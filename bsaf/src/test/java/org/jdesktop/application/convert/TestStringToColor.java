package org.jdesktop.application.convert;

import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.awt.*;
import java.util.Collection;
import java.util.Arrays;

public class TestStringToColor
{
    public TestStringToColor() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToColor defaultConverter;

    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToColor();
    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()

    @Test(expected = IllegalArgumentException.class)
    public void testNullString() throws StringConvertException
    {
        String s = null;
        Color actual = defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testEmptyString() throws StringConvertException
    {
        String s = "";
        Color actual = defaultConverter.convert(s);
        //todo - remove println
        System.out.println(String.format("actual color = %s", actual));
    }


    @RunWith(value = Parameterized.class)
    public static class TestStrings
    {
        private Color expected;
        private String testData;

        public TestStrings(Color expected, String testData)
        {
            this.expected = expected;
            this.testData = testData;
        }

        @Parameterized.Parameters
        public static Collection data()
        {

            java.util.List l = Arrays.asList(new Object[][]{
                    //supported formats:
                    //"#RRGGBB",  "#AARRGGBB", "R, G, B", "R, G, B, A"
                    {new Color(0x11, 0x22, 0x33), "#112233"},
                    //todo these two formats are inconsistent in where the Alpha component is. Should we standardize
                    //and make it #RRGGBBAA instead? Or is #AARRGGBB already an industry standard way of specifying it?
                    {new Color(0x11, 0x22, 0x33, 0x44), "#44112233"}, 

                    {new Color(100, 100, 100), "100, 100, 100"},
                    {new Color(1, 2, 3, 4), "1, 2, 3, 4"},


            });
            return l;
        }

        @Test
        public void testConvert() throws StringConvertException
        {
            Color actual = defaultConverter.convert(testData);
            assertEquals(String.format("Converting String '%s' should return expected Color", testData), expected, actual);
        }

        StringToColor defaultConverter;

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToColor();

        } // methodSetup()
    }
}