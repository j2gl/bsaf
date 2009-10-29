package org.jdesktop.application.resource;

import org.junit.*;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TestUtil;

import java.util.Arrays;
import java.awt.*;


//Unit tests for various methods to get array properties
public class TestGetArrays
{
    public TestGetArrays() {} // constructor

    static ResourceMap defaultMap = new ResourceMap(Arrays.asList(TestUtil.AbstractAppPropPath));

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

    @Test
    public void testGetStringArrayEmptyString()
    {
        String key = "AbstractApplication.array.string.empty";
        String[] expected = null;
        String[] actual = defaultMap.getAsArrayString(key, null);
        Assert.assertArrayEquals(String.format("String array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGetStringArrayOneElement()
    {
        String key = "AbstractApplication.array.string.one";
        String[] expected = {"one"};
        String[] actual = defaultMap.getAsArrayString(key, null);
        Assert.assertArrayEquals(String.format("String array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGetStringArray()
    {   String key = "AbstractApplication.array.string";
        String[] expected = {"one", "two", "three"};
        String[] actual = defaultMap.getAsArrayString(key, null);
        Assert.assertArrayEquals(String.format("String array for key='%s' not retrieved.",key), expected, actual);
    }



    @Test
    public void testGetStringArrayTrailingComma()
    {
        String key = "AbstractApplication.array.string.lastComma";
        String[] expected = {"one", "two", "three"};
        String[] actual = defaultMap.getAsArrayString(key, null);
        Assert.assertArrayEquals(String.format("String array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGetStringArrayMissingElement()
    {
        String key = "AbstractApplication.array.string.missing";
        String[] expected = {"one", "", "three"};
        String[] actual = defaultMap.getAsArrayString(key, null);
        Assert.assertArrayEquals(String.format("String array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGet_int_ArrayEmpty()
    {
        String key = "AbstractApplication.array.int.empty";
        int[] expected = null;
        int[] actual = defaultMap.getAsArray_int(key, null);
        Assert.assertArrayEquals(String.format("int array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGet_int_Array()
    {
        String key = "AbstractApplication.array.int";
        int[] expected = {1,2,3};
        int[] actual = defaultMap.getAsArray_int(key, null);
        Assert.assertArrayEquals(String.format("int array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test (expected = ResourceMap.LookupException.class)
    public void testGet_int_ArrayMissingElement()
    {
        String key = "AbstractApplication.array.int.missing";
        defaultMap.getAsArray_int(key, null);
    }



    @Test
    public void testGet_long_ArrayEmpty()
    {
        String key = "AbstractApplication.array.long.empty";
        int[] expected = null;
        int[] actual = defaultMap.getAsArray_int(key, null);
        Assert.assertArrayEquals(String.format("int array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGet_long_Array()
    {
        String key = "AbstractApplication.array.long";
        int[] expected = {1, 2, 3};
        int[] actual = defaultMap.getAsArray_int(key, null);
        Assert.assertArrayEquals(String.format("int array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test(expected = ResourceMap.LookupException.class)
    public void testGet_long_ArrayMissingElement()
    {
        String key = "AbstractApplication.array.long.missing";
        defaultMap.getAsArray_int(key, null);
    }

    @Test
    public void testGet_double_Array()
    {
        String key = "AbstractApplication.array.double";
        double[] expected = {1.1, -2.22, 3.333};
        double[] actual = defaultMap.getAsArray_double(key, null);
        Assert.assertArrayEquals(String.format("double array for key='%s' not retrieved.", key), expected, actual, TestUtil.EPSILON_DOUBLE);
    }

    @Test(expected = ResourceMap.LookupException.class)
    public void testGet_double_ArrayMissingElement()
    {
        String key = "AbstractApplication.array.double.missing";
        double[] expected = {1.1, 2.22, 3.333};
        double[] actual = defaultMap.getAsArray_double(key, null);
        Assert.assertArrayEquals(String.format("double array for key='%s' not retrieved.", key), expected, actual, TestUtil.EPSILON_DOUBLE);
    }

    @Test
    public void testGet_double_ArrayEmpty()
    {
        String key = "AbstractApplication.array.double.empty";
        double[] expected = null;
        double[] actual = defaultMap.getAsArray_double(key, null);
        Assert.assertArrayEquals(String.format("double array for key='%s' not retrieved.", key), expected, actual, TestUtil.EPSILON_DOUBLE);
    }


    @Test
    public void testGet_boolean_Array()
    {
        String key = "AbstractApplication.array.boolean";
        boolean[] expected = {true,false,true,false};
        boolean[] actual = defaultMap.getAsArray_boolean(key, null);
        TestUtil.assertArrayEquals(String.format("boolean array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGet_boolean_ArrayOne()
    {
        String key = "AbstractApplication.array.boolean.one";
        boolean[] expected = {true};
        boolean[] actual = defaultMap.getAsArray_boolean(key, null);
        TestUtil.assertArrayEquals(String.format("boolean array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGet_boolean_ArrayMissingElement()
    {
        String key = "AbstractApplication.array.boolean.missing";
        boolean[] expected = {true, true, false, true};
        boolean[] actual = defaultMap.getAsArray_boolean(key, null);
        TestUtil.assertArrayEquals(String.format("boolean array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGet_boolean_ArrayEmpty()
    {
        String key = "AbstractApplication.array.boolean.empty";
        boolean[] expected = null;
        boolean[] actual = defaultMap.getAsArray_boolean(key, null);
        TestUtil.assertArrayEquals(String.format("boolean array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testCallGetPrimitiveWithObjectType()
    {
        String key = "AbstractApplication.array.boolean";
        boolean[] expected = {true, false, true, false};
        boolean[] actual = (boolean[]) defaultMap.getAsPrimitiveArray(key, Boolean.class, null);
        TestUtil.assertArrayEquals(String.format("boolean array for key='%s' not retrieved.", key), expected, actual);
        //todo - remove println
        // System.out.println(String.format("actualArray=%s", Arrays.toString(actual)));
    }

    //getting ints as byte[] using the low-level getAsPrimitiveArray method
    @Test
    public void testGet_byte_ArrayEmpty()
    {
        String key = "AbstractApplication.array.int.empty";
        byte[] expected = null;
        byte[] actual = (byte[]) defaultMap.getAsPrimitiveArray(key, byte.class, null);
        Assert.assertArrayEquals(String.format("byte array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGet_byte_Array()
    {
        String key = "AbstractApplication.array.int";
        byte[] expected = {1, 2, 3};
        byte[] actual = (byte[]) defaultMap.getAsPrimitiveArray(key, byte.class, null);
        Assert.assertArrayEquals(String.format("byte array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test(expected = ResourceMap.LookupException.class)
    public void testGet_byte_ArrayMissingElement()
    {
        String key = "AbstractApplication.array.int.missing";
        defaultMap.getAsPrimitiveArray(key, byte.class, null);
    }


    @Test
    public void testGet_Point_Array()
    {
        String key = "AbstractApplication.array.point";
        Point[] expected = { new Point(22,33), new Point(66,77), new Point(99, 78)};
        Point[] actual = (Point[]) defaultMap.getAsArray(key, Point.class, null);
        Assert.assertArrayEquals(String.format("Point array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGet_Point_ArrayEmpty()
    {
        String key = "AbstractApplication.array.point.empty";
        Point[] expected = null;
        Point[] actual = (Point[]) defaultMap.getAsArray(key, Point.class, null);
        Assert.assertArrayEquals(String.format("Point array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test (expected = ResourceMap.LookupException.class)
    public void testGet_Point_ArrayMissing()
    {
        String key = "AbstractApplication.array.point.missing";
        Point[] expected = {new Point(22, 33), new Point(99, 78)};
        Point[] actual = (Point[]) defaultMap.getAsArray(key, Point.class, null);
        Assert.assertArrayEquals(String.format("Point array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test (expected = ResourceMap.LookupException.class)
    public void testGet_Point_Array_badPoint()
    {
        String key = "AbstractApplication.array.point.badPoint";
        Point[] expected = {new Point(22, 33), new Point(99, 78)};
        Point[] actual = (Point[]) defaultMap.getAsArray(key, Point.class, null);
        Assert.assertArrayEquals(String.format("Point array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test(expected = ResourceMap.LookupException.class)
    public void testGet_Point_Array_badPoint2()
    {
        String key = "AbstractApplication.array.point.badPoint2";
        Point[] expected = {new Point(22, 33), new Point(99, 78)};
        Point[] actual = (Point[]) defaultMap.getAsArray(key, Point.class, null);
        Assert.assertArrayEquals(String.format("Point array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test
    public void testGet_Rect_Array()
    {
        String key = "AbstractApplication.array.rect";
        Rectangle[] expected = {new Rectangle(1,2,3,4), new Rectangle(5,6,7,8), new Rectangle(9,10,11,12)};
        Rectangle[] actual = (Rectangle[]) defaultMap.getAsArray(key, Rectangle.class, null);
        Assert.assertArrayEquals(String.format("Rectangle array for key='%s' not retrieved.", key), expected, actual);
        //todo - remove println
        //System.out.println(String.format("actualArray=%s", Arrays.toString(actual)));
    }

    @Test
    public void testGet_Rect_ArrayEmpty()
    {
        String key = "AbstractApplication.array.rect.empty";
        Rectangle[] expected = null;
        Rectangle[] actual = (Rectangle[]) defaultMap.getAsArray(key, Rectangle.class, null);
        Assert.assertArrayEquals(String.format("Rectangle array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test(expected = ResourceMap.LookupException.class)
    public void testGet_Rect_ArrayMissing()
    {
        String key = "AbstractApplication.array.rect.missing";
        Rectangle[] expected = {new Rectangle(1, 2, 3, 4), new Rectangle(5, 6, 7, 8), new Rectangle(9, 10, 11, 12)};
        Rectangle[] actual = (Rectangle[]) defaultMap.getAsArray(key, Rectangle.class, null);
        Assert.assertArrayEquals(String.format("Rectangle array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test(expected = ResourceMap.LookupException.class)
    public void testGet_Rect_Array_badRect()
    {
        String key = "AbstractApplication.array.rect.badRect";
        Rectangle[] expected = {new Rectangle(1, 2, 3, 4), new Rectangle(5, 6, 7, 8), new Rectangle(9, 10, 11, 12)};
        Rectangle[] actual = (Rectangle[]) defaultMap.getAsArray(key, Rectangle.class, null);
        Assert.assertArrayEquals(String.format("Rectangle array for key='%s' not retrieved.", key), expected, actual);
    }

    @Test (expected = ResourceMap.LookupException.class)
    public void testGet_Rect_Array_badRect2()
    {
        String key = "AbstractApplication.array.rect.badRect2";
        Rectangle[] expected = {new Rectangle(1, 2, 3, 4), new Rectangle(5, 6, 7, 8), new Rectangle(9, 10, 11, 12)};
        Rectangle[] actual = (Rectangle[]) defaultMap.getAsArray(key, Rectangle.class, null);
        Assert.assertArrayEquals(String.format("Rectangle array for key='%s' not retrieved.", key), expected, actual);
    }
}