package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ArrayList;
import java.awt.*;

public class TestConverterRegistry
{
    public TestConverterRegistry() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    ConverterRegistry converters;
    ResourceConverter[] knownConverters;

    @Before
    public void methodSetup()
    {
        converters = new ConverterRegistry();
        knownConverters = new ResourceConverter[]{
                new StringToBoolean(),
                new StringToCharacter(),
                new StringToColor(),
                new StringToDimension(),
                new StringToEmptyBorder(),
                new StringToFont(),
                new StringToImage(),
                new StringToImage.StringToBufferedImage(),
                new StringToImage.StringToIcon(),
                new StringToImage.StringToImageIcon(),
                new StringToInsets(),
                new StringToKeyStroke(),
                new StringToNumber.StringToByte(),
                new StringToNumber.StringToDouble(),
                new StringToNumber.StringToFloat(),
                new StringToNumber.StringToInteger(),
                new StringToNumber.StringToLong(),
                new StringToNumber.StringToShort(),
                new StringToPoint(),
                new StringToPoint.StringToPoint2D_Double(),
                new StringToPoint.StringToPoint2D_Float(),
                new StringToRectangle(),
                new StringToRectangle.StringToRectangle2D_Double(),
                new StringToRectangle.StringToRectangle2D_Float(),
                new StringToURI(),
                new StringToURL(),
        };
    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()

    @Test
    public void testEmptyRegistry()
    {
        List<ResourceConverter> convList = converters.convertersFor(String.class);
        assertTrue("ConverterRegistry should not contain any converters when instantiated", convList.isEmpty());
    }

    @Test
    public void testAddDefaultConverters()
    {
        converters.addDefaultConverters();
        List<ResourceConverter> convList = converters.convertersFor(String.class);
        assertTrue("ConverterRegistry should contain converters after calling addDefaultConverters()", !convList.isEmpty());
    }

    @Test
    public void testAllConverters()
    {
        converters.addDefaultConverters();
        List<ResourceConverter> convList = converters.allConverters();
        ResourceConverter[] actual = (ResourceConverter[]) convList.toArray(new ResourceConverter[0]);
        ResourceConverter[] expected = knownConverters;

        //we can't test for equality since it's implemented as ==, so we compare the Classes of each object in both arrays
        Class[] actualClasses = new Class[actual.length];
        for (int i = 0, n = actualClasses.length; i < n; i++)
        {
            actualClasses[i] = actual[i].getClass();
        }
        //actual has been created from a Map implemention, so sorting is indeterminate
        Arrays.sort(actualClasses, new Comparator<Class>()
        {
            public int compare(Class o1, Class o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Class[] expectedClasses = new Class[expected.length];
        for (int i = 0, n = expectedClasses.length; i < n; i++)
        {
            expectedClasses[i] = expected[i].getClass();
        }

        assertArrayEquals("allConverters() should return all registered converters", expectedClasses, actualClasses);        
    }

    @Test
    public void testEnumerateDefaultConverters()
    {
        converters.addDefaultConverters();
        List<ResourceConverter> convList = converters.convertersFor(String.class);
        ResourceConverter[] actual = (ResourceConverter[]) convList.toArray(new ResourceConverter[0]);
        ResourceConverter[] expected = knownConverters;

        //we can't test for equality since it's implemented as ==, so we compare the Classes of each object in both arrays
        Class[] actualClasses = new Class[actual.length];
        for (int i = 0, n = actualClasses.length; i < n; i++)
        {
            actualClasses[i] = actual[i].getClass();
        }
        //actual has been created from a Map implemention, so sorting is indeterminate
        Arrays.sort(actualClasses,new Comparator<Class>()
        {
            public int compare(Class o1, Class o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Class[] expectedClasses = new Class[expected.length];
        for (int i = 0, n = expectedClasses.length; i < n; i++)
        {
            expectedClasses[i] = expected[i].getClass();
        }

        assertArrayEquals("addDefaultConverters should add all known default converters", expectedClasses, actualClasses);
    }

    @Test
    public void testConverterForNotFound()
    {
        //have not yet called addDefaultConverters, so this converter is not registered
        ResourceConverter actual = converters.converterFor(Boolean.class);
        assertNull("Calling convertersFor for an unregistered type should return null",actual);
    }

    @Test
    public void testConverterForString1()
    {
        converters.addDefaultConverters();
        Class expected = StringToBoolean.class;
        Class actual   = converters.converterFor(Boolean.class).getClass();
        assertEquals("Calling converterFor(Boolean.class) should return StringToBoolean converter", expected, actual);
    }

    @Test
    public void testConverterForString2()
    {
        converters.addDefaultConverters();
        Class expected = StringToBoolean.class;
        Class actual = converters.converterFor(String.class, Boolean.class).getClass();
        assertEquals("Calling converterFor should return StringToBoolean converter", expected, actual);
    }

    @Test
    public void testRemoveConverter()
    {
        converters.addDefaultConverters();
        ResourceConverter actual = converters.converterFor(Boolean.class);
        assertNotNull("Calling convertersFor for an registered type should not return null", actual);
        converters.remove(actual);
        actual = converters.converterFor(Boolean.class);
        assertNull("After removing a converter, getting it should return null", actual);        
    }

    @Test
    public void testAddConverter()
    {
        //converters initially empty
        converters.add(new StringToColor());
        ResourceConverter actual = converters.converterFor(Color.class);
        assertNotNull("Getting converter for a registered type should not return null", actual);
    }

    @Test
    public void testAddAll()
    {
        ResourceConverter[] rcA =
                new ResourceConverter[]{new StringToBoolean(),new StringToCharacter(),new StringToColor(),};
        ResourceConverter rc;

        rc = converters.converterFor(Boolean.class);
        assertNull("Calling convertersFor for an unregistered type should return null", rc);
        rc = converters.converterFor(Character.class);
        assertNull("Calling convertersFor for an unregistered type should return null", rc);
        rc = converters.converterFor(Color.class);
        assertNull("Calling convertersFor for an unregistered type should return null", rc);

        converters.addAll(new ArrayList< ResourceConverter >(Arrays.asList(rcA)));
        Class expected;
        Class actual;

        expected = StringToBoolean.class;
        actual = converters.converterFor(String.class, Boolean.class).getClass();
        assertEquals("Calling converterFor should return StringToBoolean converter", expected, actual);
        expected = StringToCharacter.class;
        actual = converters.converterFor(String.class, Character.class).getClass();
        assertEquals("Calling converterFor should return StringToCharacter converter", expected, actual);
        expected = StringToColor.class;
        actual = converters.converterFor(String.class, Color.class).getClass();
        assertEquals("Calling converterFor should return StringToColor converter", expected, actual);

        //we didn't add this one, so we shouldn not find it
        rc = converters.converterFor(Rectangle.class);
        assertNull("Getting converter for unregistered type should return null", rc);
    }

    @Test
    public void testConvertersFor_empty()
    {
        List<ResourceConverter> rcl = converters.convertersFor(String.class);
        assertTrue("There should be no converters in a new ConverterRegistry", rcl.isEmpty());
    }

    @Test
    public void testConvertersForString()
    {
        converters.addDefaultConverters();
        List<ResourceConverter> rcl = converters.convertersFor(String.class);
        assertTrue("There should be String converters after adding default", !rcl.isEmpty());

        //we already tested that each converter is present as expected in testEnumerateDefaultConverters
    }

    @Test
    public void testgetConverterForPrimativeBoolean()
    {
        converters.addDefaultConverters();
        ResourceConverter actual = converters.converterFor(boolean.class);
        assertTrue("There should be a converter for primative boolean", (actual != null && actual.getClass() == StringToBoolean.class));
    }

    @Test
    public void testgetConverterForPrimativeCharacter()
    {
        converters.addDefaultConverters();
        ResourceConverter actual = converters.converterFor(char.class);
        assertTrue("There should be a converter for primative char", (actual != null && actual.getClass() == StringToCharacter.class));
    }

    @Test
    public void testgetConverterForPrimativeByte()
    {
        converters.addDefaultConverters();
        ResourceConverter actual = converters.converterFor(byte.class);
        assertTrue("There should be a converter for primative byte", (actual != null && actual.getClass() == StringToNumber.StringToByte.class));
    }

    @Test
    public void testgetConverterForPrimativeShort()
    {
        converters.addDefaultConverters();
        ResourceConverter actual = converters.converterFor(short.class);
        assertTrue("There should be a converter for primative short", (actual != null && actual.getClass() == StringToNumber.StringToShort.class));
    }



    @Test
    public void testgetConverterForPrimativeInt()
    {
        converters.addDefaultConverters();
        ResourceConverter actual = converters.converterFor(int.class);
        assertTrue("There should be a converter for primative int", (actual != null && actual.getClass() == StringToNumber.StringToInteger.class));
    }

    @Test
    public void testgetConverterForPrimativeLong()
    {
        converters.addDefaultConverters();
        ResourceConverter actual = converters.converterFor(long.class);
        assertTrue("There should be a converter for primative long", (actual != null && actual.getClass() == StringToNumber.StringToLong.class));
    }

    @Test
    public void testgetConverterForPrimativeFloat()
    {
        converters.addDefaultConverters();
        ResourceConverter actual = converters.converterFor(float.class);
        assertTrue("There should be a converter for primative float", (actual != null && actual.getClass() == StringToNumber.StringToFloat.class));
    }

    @Test
    public void testgetConverterForPrimativeDouble()
    {
        converters.addDefaultConverters();
        ResourceConverter actual = converters.converterFor(double.class);
        assertTrue("There should be a converter for primative double", (actual != null && actual.getClass() == StringToNumber.StringToDouble.class));
    }




}