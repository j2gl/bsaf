package org.jdesktop.application.convert;

import org.junit.*;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Arrays;


public class TestStringToNumber
{
    public TestStringToNumber() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    @Before
    public void methodSetup()
    {
    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()

    @Test(expected = IllegalArgumentException.class)
    public void testNullStringByte() throws StringConvertException
    {
        StringToNumber.StringToByte defaultConverter = new StringToNumber.StringToByte();
        String s = null;
        defaultConverter.convert(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStringShort() throws StringConvertException
    {
        StringToNumber.StringToShort defaultConverter = new StringToNumber.StringToShort();
        String s = null;
        defaultConverter.convert(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStringInteger() throws StringConvertException
    {
        StringToNumber.StringToInteger defaultConverter = new StringToNumber.StringToInteger();
        String s = null;
        defaultConverter.convert(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStringLong() throws StringConvertException
    {
        StringToNumber.StringToLong defaultConverter = new StringToNumber.StringToLong();
        String s = null;
        defaultConverter.convert(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStringFloat() throws StringConvertException
    {
        StringToNumber.StringToFloat defaultConverter = new StringToNumber.StringToFloat();
        String s = null;
        defaultConverter.convert(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStringDouble() throws StringConvertException
    {
        StringToNumber.StringToDouble defaultConverter = new StringToNumber.StringToDouble();
        String s = null;
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testEmptyStringByte() throws StringConvertException
    {
        StringToNumber.StringToByte defaultConverter = new StringToNumber.StringToByte();
        String s = "";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testEmptyStringShort() throws StringConvertException
    {
        StringToNumber.StringToShort defaultConverter = new StringToNumber.StringToShort();
        String s = "";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testEmptyStringInteger() throws StringConvertException
    {
        StringToNumber.StringToInteger defaultConverter = new StringToNumber.StringToInteger();
        String s = "";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testEmptyStringLong() throws StringConvertException
    {
        StringToNumber.StringToLong defaultConverter = new StringToNumber.StringToLong();
        String s = "";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testEmptyStringFloat() throws StringConvertException
    {
        StringToNumber.StringToFloat defaultConverter = new StringToNumber.StringToFloat();
        String s = "";
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testEmptyStringDouble() throws StringConvertException
    {
        StringToNumber.StringToDouble defaultConverter = new StringToNumber.StringToDouble();
        String s = "";
        defaultConverter.convert(s);
    }


    @Test
    public void testPrimativeByteConversion() throws StringConvertException
    {
        StringToNumber.StringToByte defaultConverter = new StringToNumber.StringToByte();
        String s = "42";
        byte expected = (byte)42;
        byte actual = defaultConverter.convert(s);
        assertTrue(String.format("Converstion to primative byte expected=%d, actual=%d", expected, actual), expected == actual);
    }

    @Test
    public void testPrimativeShortConversion() throws StringConvertException
    {
        StringToNumber.StringToShort defaultConverter = new StringToNumber.StringToShort();
        String s = "4765";
        short expected = (short)4765;
        short actual = defaultConverter.convert(s);
        assertTrue(String.format("Converstion to primative short expected=%d, actual=%d", expected, actual), expected == actual);
    }

    @Test
    public void testPrimativeCharConversion() throws StringConvertException
    {
        StringToCharacter defaultConverter = new StringToCharacter();
        String s = "\uABCD";
        char expected = 0xABCD;
        char actual = defaultConverter.convert(s);
        assertTrue(String.format("Converstion to primative char expected=%c, actual=%c", expected, actual), expected == actual);
    }

    @Test
    public void testPrimativeIntConversion() throws StringConvertException
    {
        StringToNumber.StringToInteger defaultConverter = new StringToNumber.StringToInteger();
        String s = "4765";
        int expected = 4765;
        int actual = defaultConverter.convert(s);
        assertTrue(String.format("Converstion to primative int expected=%d, actual=%d",expected, actual), expected == actual);
    }

    @Test
    public void testPrimativeLongConversion() throws StringConvertException
    {
        StringToNumber.StringToLong defaultConverter = new StringToNumber.StringToLong();
        String s = "998877665544332211";
        long expected = 998877665544332211L;
        long actual = defaultConverter.convert(s);
        assertTrue(String.format("Converstion to primative long expected=%d, actual=%d", expected, actual), expected == actual);
    }


    @Test
    public void testPrimativeLongConversion2() throws StringConvertException
    {
        StringToNumber.StringToLong defaultConverter = new StringToNumber.StringToLong();
        String s = "-998877665544332211";
        long expected = -998877665544332211L;
        long actual = defaultConverter.convert(s);
        assertTrue(String.format("Converstion to primative long expected=%d, actual=%d", expected, actual), expected == actual);
    }

    @Test
    public void testPrimativeFloatConversion() throws StringConvertException
    {
        StringToNumber.StringToFloat defaultConverter = new StringToNumber.StringToFloat();
        String s = "4.76512";
        float expected = 4.76512f;
        float actual = defaultConverter.convert(s);
        assertTrue(String.format("Converstion to primative float expected=%f, actual=%f", expected, actual), expected == actual);
    }

    @Test
    public void testPrimativeDoubleConversion() throws StringConvertException
    {
        StringToNumber.StringToDouble defaultConverter = new StringToNumber.StringToDouble();
        String s = "4.7651212345";
        double expected = 4.7651212345;
        double actual = defaultConverter.convert(s);
        assertTrue(String.format("Converstion to primative double expected=%f, actual=%f", expected, actual), expected == actual);
    }

    //shared superclass for all TestStringToFoo classes
    public static class TSSuperclass
    {
        protected Number expected;
        protected String testData;
        protected boolean exceptionExpected; //when true, we expect the test method to throw an exception and test for this

        StringToNumber defaultConverter;

        public TSSuperclass(Number expected, String testData, boolean exceptionExpected)
        {
            this.expected = expected;
            this.testData = testData;
            this.exceptionExpected = exceptionExpected;
        }

        @SuppressWarnings(value = "unchecked")
        @Test
        public void testConvert() throws StringConvertException
        {

            Number actual;

            if (exceptionExpected)
            {
                try
                {                    
                    actual = (Number) defaultConverter.convert(testData);
                    fail(String.format("Converting String '%s' should throw StringConvertException", testData));

                }
                catch (StringConvertException ignore)
                {
                    //expected
                }
            }
            else
            {
                actual = (Number) defaultConverter.convert(testData);
                assertEquals(String.format("Converting String '%s' should return expected %s", testData, actual.getClass().getSimpleName()), expected, actual);
            }

        }
    }

    @RunWith(value = Parameterized.class)
    public static class TestStringToByte extends TSSuperclass
    {
        public TestStringToByte(Byte expected, String testData, boolean exceptionExpected)
        {
            super(expected, testData, exceptionExpected);

        }

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data()
        {

            return   Arrays.asList(new Object[][]{
                    {Byte.MIN_VALUE, "-128", false},
                    {new Byte((byte) -1), "-1", false},
                    {new Byte((byte) 0), "0", false},
                    {new Byte((byte) 1), "1", false},
                    {Byte.MAX_VALUE, "127", false},

                    //these should throw StringConversionExcpetion
                    {Byte.MAX_VALUE, "200", true},
                    {Byte.MAX_VALUE, "-200", true},
                    {Byte.MAX_VALUE, "foo", true},
            });
        }

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToNumber.StringToByte();

        } // methodSetup()
    }

    @RunWith(value = Parameterized.class)
    public static class TestStringToShort extends TSSuperclass
    {
        public TestStringToShort(Short expected, String testData, boolean exceptionExpected)
        {
            super(expected, testData, exceptionExpected);

        }

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data()
        {

            return   Arrays.asList(new Object[][]{
                    {Short.MIN_VALUE, "-32768", false},
                    {new Short((short) -1), "-1", false},
                    {new Short((short) 0), "0", false},
                    {new Short((short) 1), "1", false},
                    {Short.MAX_VALUE, "32767", false},

                    //these should throw StringConversionExcpetion
                    {Short.MAX_VALUE, ""+(Short.MAX_VALUE+1), true},
                    {Short.MIN_VALUE, ""+(Short.MIN_VALUE-1), true},
                    {Short.MAX_VALUE, "foo", true},
            });
        }

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToNumber.StringToShort();

        } // methodSetup()
    }

    @RunWith(value = Parameterized.class)
    public static class TestStringToInteger extends TSSuperclass
    {
        public TestStringToInteger(Integer expected, String testData, boolean exceptionExpected)
        {
            super(expected, testData, exceptionExpected);

        }

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data()
        {

            return   Arrays.asList(new Object[][]{
                    {Integer.MIN_VALUE, ""+ Integer.MIN_VALUE, false},
                    {new Integer( -1), "-1", false},
                    {new Integer( 0), "0", false},
                    {new Integer( 1), "1", false},
                    {Integer.MAX_VALUE, ""+Integer.MAX_VALUE, false},

                    //these should throw StringConversionExcpetion
                    {Integer.MAX_VALUE, "" + (Integer.MAX_VALUE + 1L), true},
                    {Integer.MIN_VALUE, "" + (Integer.MIN_VALUE - 1L), true},
                    {Integer.MAX_VALUE, "foo", true},
            });
        }

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToNumber.StringToInteger();

        } // methodSetup()
    }

    @RunWith(value = Parameterized.class)
    public static class TestStringToLong extends TSSuperclass
    {
        public TestStringToLong(Long expected, String testData, boolean exceptionExpected)
        {
            super(expected, testData, exceptionExpected);

        }

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data()
        {

            return   Arrays.asList(new Object[][]{
                    {Long.MIN_VALUE, "" + Long.MIN_VALUE, false},
                    {new Long(-1), "-1", false},
                    {new Long(0), "0", false},
                    {new Long(1), "1", false},
                    {Long.MAX_VALUE, "" + Long.MAX_VALUE, false},

                    //these should throw StringConversionExcpetion
                    {Long.MAX_VALUE, "1" + Long.MAX_VALUE , true},
                    {Long.MIN_VALUE, "-1" + (Long.MIN_VALUE ), true},
                    {Long.MAX_VALUE, "foo", true},
            });
        }

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToNumber.StringToLong();

        } // methodSetup()
    }

    @RunWith(value = Parameterized.class)
    public static class TestStringToFloat extends TSSuperclass
    {
        public TestStringToFloat(Float expected, String testData, boolean exceptionExpected)
        {
            super(expected, testData, exceptionExpected);

        }

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data()
        {
            return   Arrays.asList(new Object[][]{
                    {Float.NaN, "NaN", false},
                    {Float.NEGATIVE_INFINITY, "-Infinity", false},
                    {Float.POSITIVE_INFINITY, "Infinity", false},
                    {Float.valueOf("0"), "0", false},
                    {Float.valueOf("-0"), "-0", false},
                    {Float.valueOf("1"), "1", false},
                    {Float.valueOf("-1"), "-1", false},
                    {Float.MAX_VALUE, ""+Float.MAX_VALUE, false},
                    {Float.valueOf(-1*Float.MAX_VALUE), "-" + Float.MAX_VALUE, false},
                    {Float.MIN_VALUE, "" + Float.MIN_VALUE, false},
                    {Float.valueOf(-1 * Float.MIN_VALUE), "-" + Float.MIN_VALUE, false},

                    //these should throw StringConversionExcpetion
                    {Float.MAX_VALUE, "foo", true},
            });
        }

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToNumber.StringToFloat();

        } // methodSetup()
    }

    @RunWith(value = Parameterized.class)
    public static class TestStringToDouble extends TSSuperclass
    {
        public TestStringToDouble(Double expected, String testData, boolean exceptionExpected)
        {
            super(expected, testData, exceptionExpected);

        }

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data()
        {
            return   Arrays.asList(new Object[][]{
                    {Double.NaN, "NaN", false},
                    {Double.NEGATIVE_INFINITY, "-Infinity", false},
                    {Double.POSITIVE_INFINITY, "Infinity", false},
                    {Double.valueOf("0"), "0", false},
                    {Double.valueOf("-0"), "-0", false},
                    {Double.valueOf("1"), "1", false},
                    {Double.valueOf("-1"), "-1", false},
                    {Double.MAX_VALUE, "" + Double.MAX_VALUE, false},
                    {Double.valueOf(-1 * Double.MAX_VALUE), "-" + Double.MAX_VALUE, false},
                    {Double.MIN_VALUE, "" + Double.MIN_VALUE, false},
                    {Double.valueOf(-1 * Double.MIN_VALUE), "-" + Double.MIN_VALUE, false},

                    //these should throw StringConversionExcpetion
                    {Double.MAX_VALUE, "foo", true},
            });
        }

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToNumber.StringToDouble();

        } // methodSetup()
    }


}