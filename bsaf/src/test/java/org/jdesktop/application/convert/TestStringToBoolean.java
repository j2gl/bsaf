package org.jdesktop.application.convert;

import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Arrays;
import java.util.Set;

public class TestStringToBoolean
{
    public TestStringToBoolean() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToBoolean defaultConverter;

    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToBoolean();


    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()

    @Test(expected = IllegalArgumentException.class)
    public void testNullString()
    {
        String s = null;

        Boolean expected = Boolean.FALSE;
        Boolean actual = defaultConverter.convert(s);
        assertEquals("Converting null String should throw exception", expected, actual);
    }

    @Test
    public void testEmptyString()
    {
        String s = "";

        Boolean expected = Boolean.FALSE;
        Boolean actual = defaultConverter.convert(s);
        assertEquals("Converting empty String should return false", expected, actual);
    }

    @Test
    public void testAddTrueValue()
    {
        String s = "false"; //known to not be an element in the trueValues set

        assertEquals("String 'false' should return Boolea.FALSE", Boolean.FALSE, defaultConverter.convert(s));

        //now add this as a "true" value
        defaultConverter.addTrueValue(s);
        assertEquals("After adding 'false' should return Boolea.TRUE", Boolean.TRUE, defaultConverter.convert(s));
    }

    @Test
    public void testRemoveTrueValue()
    {
        StringToBoolean converter = new StringToBoolean("foo", "bar", "baz");
        assertEquals("'foo' should return Boolean.TRUE", Boolean.TRUE, converter.convert("foo"));
        assertEquals("'bar' should return Boolean.TRUE", Boolean.TRUE, converter.convert("bar"));
        assertEquals("'baz' should return Boolean.TRUE", Boolean.TRUE, converter.convert("baz"));

        boolean result = converter.removeTrueValue("bar");
        assertTrue("Removing 'bar' should return 'true'", result);

        assertEquals("'foo' should return Boolean.TRUE", Boolean.TRUE, converter.convert("foo"));
        assertEquals("'bar' was removed, should return Boolean.FALSE", Boolean.FALSE, converter.convert("bar"));
        assertEquals("'baz' should return Boolean.TRUE", Boolean.TRUE, converter.convert("baz"));

        result = converter.removeTrueValue("bar");
        assertFalse("'bar' was already removed - should return 'false'", result);
    }

    @Test
    public void testCustomTrueValueConstuctor()
    {
        StringToBoolean converter = new StringToBoolean("foo", "bar");
        assertEquals("'foo' should return Boolean.TRUE", Boolean.TRUE, converter.convert("foo"));
        assertEquals("'bar' should return Boolean.TRUE", Boolean.TRUE, converter.convert("bar"));
        assertEquals("'baz' should return Boolean.FALSE", Boolean.FALSE, converter.convert("baz"));
    }

    @Test
    public void testTrueValueSetGetter()
    {
        StringToBoolean converter = new StringToBoolean("foo", "bar", "baz");

        Set<String> trueSet = converter.getTrueValuesSet();

        assertTrue("foo should be in the trueValueSet", trueSet.contains("foo"));
        assertTrue("bar should be in the trueValueSet", trueSet.contains("bar"));
        assertTrue("baz should be in the trueValueSet", trueSet.contains("baz"));

        //mutate our local copy, and then compare again. our changes should not affect the internal set
        trueSet.remove("bar");
        assertTrue("bar was removed from the local trueValueSet", !trueSet.contains("bar"));

        Set<String> freshSet = converter.getTrueValuesSet();
        assertTrue("bar should still be in the converter's trueValueSet", freshSet.contains("bar"));

    }

    @RunWith(value= Parameterized.class)
    public static class TestDataValues
    {

        private Boolean expected;
        private String  testData;

        @Parameterized.Parameters
        public static Collection data(){

            return Arrays.asList(new Object[][]{
                    {Boolean.TRUE, "true"}, //expected result, test data
                    {Boolean.TRUE, "t"},
                    {Boolean.TRUE, "yes"},
                    {Boolean.TRUE, "y"},
                    {Boolean.TRUE, "on"},
                    {Boolean.TRUE, "TRUE"},
                    {Boolean.TRUE, "T"},
                    {Boolean.TRUE, "YeS"},
                    {Boolean.TRUE, "Y"},
                    {Boolean.TRUE, "oN"},
                    {Boolean.TRUE, "Boolean.TRUE"},
                    {Boolean.TRUE, "BOOLEAN.true"},

                    {Boolean.FALSE, "false"},
                    {Boolean.FALSE, "FALSE"},
                    {Boolean.FALSE, "no"},
                    {Boolean.FALSE, "N"},
                    {Boolean.FALSE, ""},

            });

        }

        public TestDataValues(Boolean expected, String testData)
        {
            this.expected = expected;
            this.testData = testData;
        }

        @Test
        public void testConvertString()
        {
            StringToBoolean converter = new StringToBoolean();
            assertEquals(String.format("Converting boolean String '%s' should return appropriate Boolean instance", testData), expected, converter.convert(testData));
        }
    }
}