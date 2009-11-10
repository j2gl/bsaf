package org.jdesktop.application.resource;

import org.junit.*;
import static org.junit.Assert.*;
import org.jetbrains.annotations.NotNull;

import org.jdesktop.application.convert.*;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TestUtil;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.KeyEvent;
import java.net.*;

import java.io.IOException;

import org.jdesktop.application.resource.abstractapp.AbstractApplication;

//Basic tests of creating and loading a single ResourceMap instance, with one bundle and no parentMap
public class TestResourceMap1
{

    public TestResourceMap1() {} // constructor

    static Locale oldLocale;
    @BeforeClass
    public static void unitSetup()
    {
        oldLocale = Locale.getDefault();
        Locale.setDefault(new Locale("")); //needed to force use of unqualified bundles if unit tests run where "es" is default language
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
        Locale.setDefault(oldLocale);
    } // unitCleanup()

    AbstractApplication application = new AbstractApplication();
    ResourceMap defaultMap = new ResourceMap(Arrays.asList(TestUtil.AbstractAppPropPath));
    ResourceConverter[] defaultConverters;

    @Before
    public void methodSetup()
    {
        defaultConverters = new ResourceConverter[]{
                new StringToBoolean(),
                new StringToCharacter(),
                new StringToColor(),
                new StringToDimension(),
                new StringToEmptyBorder(),
                new StringToFont(),
                new StringToImage.StringToBufferedImage(),
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
    public void testLoadTopClassResources()
    {
        //this class has no superclass
        assertNotNull("Creating new ResourceMap should return non-null instance", defaultMap);
    }

    @Test
    public void testResourcesExist()
    {
        //test that the resources known to be in the file were actually loaded
        String[] keys = {"AbstractApplication", "Application.title", "Application.field1", "Application.field2"};
        for (String key : keys)
        {
            assertTrue(String.format("Resource named '%s' exists", key), defaultMap.containsKey(key));
        }
    }

    @Test
    public void testResourceDoesNotExist()
    {
        String key = "SpecializedApplication"; //from a different property file, not supposed to be loaded/visible
        assertFalse(String.format("key %s not present in reaource file", key), defaultMap.containsKey(key));
    }


    @Test
    public void testResourcesValues()
    {
        //test that the resources values loaded correctly
        String[] keys = {"AbstractApplication", "Application.title", "Application.field1", "Application.field2"};
        String[] values = {"AbstractApplication", "AbstractApplication.properties title", "AbstractApplication.properties field1", "AbstractApplication.properties field2"};
        for (int i = 0; i < keys.length; i++)
        {
            assertEquals(String.format("Resource %s=%s", keys[i], values[i]), values[i], defaultMap.getAsString(keys[i], null));

        }
    }

    @Test
    public void testNullParent()
    {
        assertNull("Top level of chain should not have parentMap", defaultMap.getParent());
    }

    @Test
    public void testDefaultClassloader()
    {
        ClassLoader actual = defaultMap.getClassLoader();
        ClassLoader expected = defaultMap.getClass().getClassLoader();
        assertEquals(String.format("Default classloader should be same as that which loaded ResourceManager class"), expected, actual);
    }

    @Test
    public void testDefaultConverterList()
    {
        ConverterRegistry cr = defaultMap.getConverters();
        List<ResourceConverter> converters = cr.allConverters();
        List<Class> cls = new ArrayList<Class>();
        for (ResourceConverter converter : converters)
        {
            cls.add(converter.getClass());
        }
        Collections.sort(cls, new Comparator<Class>()
        {
            public int compare(Class o1, Class o2)
            {
                return o1.getCanonicalName().compareTo(o2.getCanonicalName());
            }
        });

        for (ResourceConverter converter : defaultConverters)
        {
            if (!cls.contains(converter.getClass()))
            {
                fail(String.format("Converter %s not found in ResourceMap's ConverterRegisty as expected!", converter));
            }
        }
    }

    @Test
    public void testKeySet()
    {
        ResourceBundle bundle = ResourceBundle.getBundle(TestUtil.AbstractAppPropPath);
        Set<String> expectedSet = bundle.keySet();
        Set<String> actualSet = defaultMap.keySet();
        assertEquals(String.format("keySet should contain all resource keys"), expectedSet, actualSet);
      //  System.out.println(String.format("%s", Arrays.toString(actualSet.toArray())));
    }

    @Test
    public void testResDirectory()
    {
        String expected = "org/jdesktop/application/resource/abstractapp/resources/";
        String actual = defaultMap.getResourcesDir();
        assertEquals(String.format("Resource directory name correctly parsed"), expected, actual);
    }

    //Test conversion accessors - this isn't exhaustive, since we do test the converters in a separate test suite



    @Test
    public void testGetAs_Boolean()
    {
        Boolean expected = Boolean.TRUE;
        Boolean actual = defaultMap.getAsBoolean("AbstractApplication.boolean", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_boolean()
    {
        boolean expected = true;
        boolean actual = defaultMap.getAsBoolean("AbstractApplication.boolean", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAsBufferedImage1() throws IOException
    {
        String filePath = defaultMap.getResourcesDir() + "duke.png";
        BufferedImage expected = TestUtil.getTestImage(filePath);
        BufferedImage actual = defaultMap.getAsBufferedImage("AbstractApplication.image1", null);

        String msg = String.format("Buffered image should match duke.gif '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));
    }

    @Test
    public void testGetAsBufferedImage2() throws IOException
    {
        String filePath = defaultMap.getResourcesDir() + "images/duke.gif";
        BufferedImage expected = TestUtil.getTestImage(filePath);
        BufferedImage actual = defaultMap.getAsBufferedImage("AbstractApplication.image2", null);

        String msg = String.format("Buffered image should match duke.gif '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));
    }

    @Test
    public void testGetAsBufferedImage3() throws IOException
    {
        String filePath = defaultMap.getResourcesDir() + "images/smallbabies.jpeg";
        BufferedImage expected = TestUtil.getTestImage(filePath);
        BufferedImage actual = defaultMap.getAsBufferedImage("AbstractApplication.image3", null);

        String msg = String.format("Buffered image should match smallbabies.jpeg '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));
    }

    @Test
    public void testGetAsBufferedImage4() throws IOException
    {
        String filePath = defaultMap.getResourcesDir() + "images/javalogo52x88.gif";
        BufferedImage expected = TestUtil.getTestImage(filePath);
        //uses absolute reference
        BufferedImage actual = defaultMap.getAsBufferedImage("AbstractApplication.image4", null);

        String msg = String.format("Buffered image should match avalogo52x88.gif '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));
    }

    @Test
    public void testGetAs_Image1()
    {
        String filePath = defaultMap.getResourcesDir() + "duke.png";
        Image expected = TestUtil.getTestImage(filePath);
        Image actual = defaultMap.getAsBufferedImage("AbstractApplication.image1", null);

        String msg = String.format("Image should match duke.png '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));
    }

    @Test
    public void testGetAs_Image2()
    {
        String filePath = defaultMap.getResourcesDir() + "images/duke.gif";
        Image expected = TestUtil.getTestImage(filePath);
        Image actual = defaultMap.getAsBufferedImage("AbstractApplication.image2", null);

        String msg = String.format("Image should match duke.gif '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));
    }


    @Test
    public void testGetAs_Image3()
    {
        String filePath = defaultMap.getResourcesDir() + "images/smallbabies.jpeg";
        Image expected = TestUtil.getTestImage(filePath);
        Image actual = defaultMap.getAsBufferedImage("AbstractApplication.image3", null);

        String msg = String.format("Image should match smallbabies.jpeg '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));;
    }

    @Test
    public void testGetAs_Image4() throws IOException
    {
        String filePath = defaultMap.getResourcesDir() + "images/javalogo52x88.gif";
        Image expected = TestUtil.getTestImage(filePath);
        //uses absolute reference
        Image actual = defaultMap.getAsBufferedImage("AbstractApplication.image4", null);

        String msg = String.format("Image should match avalogo52x88.gif '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));
    }

    @Test
    public void testGetAs_ImageIcon1()
    {
        String filePath = defaultMap.getResourcesDir() + "duke.png";
        ImageIcon expected = new ImageIcon(TestUtil.getTestImage(filePath));
        ImageIcon actual = (ImageIcon) defaultMap.getAsImageIcon("AbstractApplication.image1", null);

        String msg = String.format("Image should match duke.png '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected.getImage(), (BufferedImage) actual.getImage()));
    }

    @Test
    public void testGetAs_ImageIcon2()
    {
        String filePath = defaultMap.getResourcesDir() + "images/duke.gif";
        ImageIcon expected = new ImageIcon(TestUtil.getTestImage(filePath));
        ImageIcon actual = (ImageIcon) defaultMap.getAsImageIcon("AbstractApplication.image2", null);

        String msg = String.format("Image should match duke.gif '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected.getImage(), (BufferedImage) actual.getImage()));
    }

    @Test
    public void testGetAs_ImageIcon3()
    {
        String filePath = defaultMap.getResourcesDir() + "images/smallbabies.jpeg";
        ImageIcon expected = new ImageIcon(TestUtil.getTestImage(filePath));
        ImageIcon actual = (ImageIcon) defaultMap.getAsImageIcon("AbstractApplication.image3", null);

        String msg = String.format("Image should match smallbabies.jpeg '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected.getImage(), (BufferedImage) actual.getImage()));
    }

    @Test
    public void testGetAs_ImageIcon4()
    {
        String filePath = defaultMap.getResourcesDir() + "images/javalogo52x88.gif";
        ImageIcon expected = new ImageIcon(TestUtil.getTestImage(filePath));
        ImageIcon actual = (ImageIcon) defaultMap.getAsImageIcon("AbstractApplication.image4", null);

        String msg = String.format("ImageIcom should match javalogo52x88.gif '%s'", filePath);
        assertTrue(msg, TestUtil.equal((BufferedImage) expected.getImage(), (BufferedImage) actual.getImage()));
    }


    @Test
    public void testGetAs_Character()
    {
        Character expected = 'h';
        Character actual = defaultMap.getAsCharacter("AbstractApplication.character", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_character()
    {
        char expected = 'h';
        char actual = defaultMap.getAsCharacter("AbstractApplication.character", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Color1()
    {
        Color expected = new Color(10,20,30,40);
        Color actual = defaultMap.getAsColor("AbstractApplication.color1", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Color2()
    {
        Color expected = new Color(50,60,70);
        Color actual = defaultMap.getAsColor("AbstractApplication.color2", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Color3()
    {
        Color expected = new Color(0xFF, 0xAA, 0xCD);
        Color actual = defaultMap.getAsColor("AbstractApplication.color3", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Color4()
    {
        Color expected = new Color(0xBB, 0xCC, 0xDD, 0xAA);
        Color actual = defaultMap.getAsColor("AbstractApplication.color4", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Dimension()
    {
        Dimension expected = new Dimension(-144, 2356);
        Dimension actual = defaultMap.getAsDimension("AbstractApplication.dimension", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Double()
    {
        Double expected = Math.PI;
        Double actual = defaultMap.getAsDouble("AbstractApplication.double", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_double()
    {
        double expected = Math.PI;
        double actual = defaultMap.getAsDouble("AbstractApplication.double", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual, TestUtil.EPSILON_DOUBLE);
    }

    @Test
    public void testGetAs_emptyBorder()
    {
        EmptyBorder expected = new EmptyBorder(5,6,7,8);
        EmptyBorder actual = defaultMap.getAsEmptyBorder("AbstractApplication.emptyBorder", null);
        //assertEquals(String.format("Resource converted correctly"), expected.getBorderInsets(), actual.getBorderInsets());
        assertTrue(String.format("Resource converted correctly"), TestUtil.equal(expected, actual));

    }

    @Test
    public void testGetAs_Float()
    {
        Float expected = new Float((float)(Math.PI));
        Float actual = defaultMap.getAsFloat("AbstractApplication.float", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_float()
    {
        float expected = (float) (Math.PI);
        float actual = defaultMap.getAsFloat("AbstractApplication.float", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual, TestUtil.EPSILON_FLOAT);
    }

    @Test
    public void testGetAs_Font()
    {
        Font expected = new Font("Arial",Font.PLAIN, 12);
        Font actual = defaultMap.getAsFont("AbstractApplication.font", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }


    @Test
    public void testGetAs_Insets()
    {
        Insets expected = new Insets(9,8,7,6);
        Insets actual = defaultMap.getAsInsets("AbstractApplication.insets", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }


    @Test
    public void testGetAs_Integer()
    {
        Integer expected = new Integer(56789234);
        Integer actual = defaultMap.getAsInteger("AbstractApplication.integer", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_integer()
    {
        int expected = 56789234;
        int actual = defaultMap.getAsInteger("AbstractApplication.integer", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_KeyStroke()
    {
        KeyStroke expected = KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK);
        KeyStroke actual = defaultMap.getAsKeyStroke("AbstractApplication.keystroke", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }


    @Test
    public void testGetAs_Long()
    {
        Long expected = new Long(Long.MAX_VALUE);
        Long actual = defaultMap.getAsLong("AbstractApplication.long", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_long()
    {
        long expected = Long.MAX_VALUE;
        long actual = defaultMap.getAsLong("AbstractApplication.long", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_long2()
    {
        long expected = Long.MAX_VALUE;
        long actual = defaultMap.getResourceAs("AbstractApplication.long",long.class, null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Point()
    {
        Point expected = new Point(55,66);
        Point actual = defaultMap.getAsPoint("AbstractApplication.point", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Point2D_Double()
    {
        Point2D.Double expected = new Point2D.Double(55.14156, 66.14156);
        Point2D.Double actual = defaultMap.getAsPoint2D_Double("AbstractApplication.point2d_double", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Point2D_Float()
    {
        Point2D.Float expected = new Point2D.Float(77.14156f, 88.14156f);
        Point2D.Float actual = defaultMap.getAsPoint2D_Float("AbstractApplication.point2d_float", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Rectangle()
    {
        Rectangle expected = new Rectangle(55, 66, 100, 200);
        Rectangle actual = defaultMap.getAsRectangle("AbstractApplication.rectangle", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Rectangle2D_Double()
    {
        Rectangle2D.Double expected = new Rectangle2D.Double(12.345678, 23.45678, 567.123456, 789.23456);
        Rectangle2D.Double actual = defaultMap.getAsRectangle2D_Double("AbstractApplication.rectangle2d_double", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Rectangle2D_Float()
    {
        Rectangle2D.Float expected = new Rectangle2D.Float(55.123f, 66.321f, 100.765f, 200.876f);
        Rectangle2D.Float actual = defaultMap.getAsRectangle2D_Float("AbstractApplication.rectangle2d_float", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_Short()
    {
        Short expected = new Short((short)876);
        Short actual = defaultMap.getAsShort("AbstractApplication.short", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }



    @Test
    public void testGetAs_short()
    {
        short expected = (short) 876;
        short actual = defaultMap.getAsShort("AbstractApplication.short", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }


    @Test
    public void testGetAs_URI() throws URISyntaxException
    {
        URI expected = new URI("http://java.sun.com");
        URI actual = defaultMap.getAsURI("AbstractApplication.uri", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetAs_URL() throws MalformedURLException
    {
        URL expected = new URL("http://java.sun.com");
        URL actual = defaultMap.getAsURL("AbstractApplication.url", null);
        //URL does DNS lookup in equals, so let's avoid that
        //assertEquals(String.format("Resource converted correctly"), expected.toString(), actual.toString());
        assertTrue(String.format("Resource converted correctly"), TestUtil.equal(expected, actual));
    }

    @Test
    public void testGetAs_String1()
    {
        String expected = "tootie frooty, oh rooty!";
        String actual = defaultMap.getAsString("AbstractApplication.string", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }


    @Test
    public void testGetAs_String2()
    {
        //test overridden method (without a converter argument)
        String expected = "tootie frooty, oh rooty!";
        String actual = defaultMap.getAsString("AbstractApplication.string", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testGetKeyWithNoValue1()
    {
        //getting a key with no value should return null and not throw any exceptions
        String actual = defaultMap.getAsString("AbstractApplication.novalue", null);
        assertEquals("getting resourceKey with no value should return null,",null, actual);
    }

    @Test
    public void testGetKeyWithNoValue2()
    {
        //getting a key with no value should return null and not throw any exceptions
        String actual = defaultMap.getAsString("AbstractApplication.novalue", null);
        assertNull("getting resourceKey with no value should return null", actual);
    }


    @Test
    public void testLoadWithCustomClassLoader1()
    {
        ClassLoader expected = new URLClassLoader(new URL[0]);
        ResourceMap rm = new ResourceMap(Arrays.asList(TestUtil.AbstractAppPropPath), expected);

        ClassLoader actual = rm.getClassLoader();
        assertSame("ClassLoader used in ResourceMap constructor should be returned by getter method",expected, actual);

        assertNotSame("ResourceMap classloader should be different than JUnit runner", getClass().getClassLoader(),actual);

    }

    @Test
    public void testLoadWithCustomClassLoader2()
    {
        ClassLoader cl = new URLClassLoader(new URL[0], getClass().getClassLoader());
        ResourceMap rm = new ResourceMap(Arrays.asList(TestUtil.AbstractAppPropPath), cl);

        Integer expected = new Integer(56789234);
        Integer actual = rm.getAsInteger("AbstractApplication.integer", null);
        assertEquals(String.format("Resource with custom ClassLoader converted correctly"), expected, actual);

    }


    @Test (expected = ResourceMap.LookupException.class)
    public void testGetAs_badString()
    {
        Double expected = Math.PI;
        Double actual = defaultMap.getAsDouble("AbstractApplication.dimension", null);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }


    //test some custom converters
    @Test
    public void testCustomConverters1()
    {
        class CustomStringConverter extends ResourceConverter<String, String>
        {
            CustomStringConverter()
            {
                super(String.class, String.class);
            }

            public String convert(String source, Object... args) throws StringConvertException
            {
                return "I am a custom String converter. Original String was :"+source;
            }
        }

        class CustomIntegerConverter extends ResourceConverter<String, Integer>
        {
            CustomIntegerConverter()
            {
                super(String.class, Integer.class);
            }

            public Integer convert(String source, Object... args) throws StringConvertException
            {
                return 42; //always convert source to number 42, of course!
            }

        }

        ConverterRegistry customRegistry = new ConverterRegistry();
        customRegistry.addAll(Arrays.<ResourceConverter>asList(new CustomStringConverter(), new CustomIntegerConverter()));

        String expectedStr = "I am a custom String converter. Original String was :tootie frooty, oh rooty!";
        String actualStr = defaultMap.getAsString("AbstractApplication.string", customRegistry);
        assertEquals(String.format("Resource converted correctly"), expectedStr, actualStr);

        Integer expected = new Integer(42);
        Integer actual = defaultMap.getAsInteger("AbstractApplication.integer", customRegistry);
        assertEquals(String.format("Resource converted correctly"), expected, actual);
    }

    @Test
    public void testLoadMultipleBundles()
    {
        //test explicitly loading more than one bundle file
        ResourceMap rm = new ResourceMap(Arrays.asList(TestUtil.ExpressionsPropPath, TestUtil.ActionsPropsPath));

        //spot check a few props from each, and verify that items in Expressions.properties shadow items in Actions.properties
        assertEquals("Correct value from Expressions.properties not loaded.","Hello", rm.getAsString("hello", null));
        assertEquals("Correct value from Expressions.properties not loaded.", "apple", rm.getAsString("appleKey",  null));
        assertEquals("Correct value from Actions.properties not loaded.", "Select Window", rm.getAsString("selectWindow.Action.text", null));
        assertEquals("Correct value from Actions.properties not loaded.", "Create new paint document", rm.getAsString("newDocument.Action.shortDescription", null));
        //in both files. The value in Expressions.properties should be returned
        assertEquals("Value from Expressions.properties should shadow value from Actions.properties", "foo", rm.getAsString("WindowMenu.title", null));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testLoadMultiBundlesNotColocated()
    {
        ResourceMap rm = new ResourceMap(Arrays.asList(TestUtil.ExpressionsPropPath, "doesNotExist"));
    }

    //todo - interim behavior with current app framework allows non-existent bundle names to be supplied to a ResourceMap consturctor
    //eventually, should throw exception, but for backwards-compatibility allow
    @Test //(expected = MissingResourceException.class)
    public void testLoadMultiBundlesWithBadBundleName()
    {
        ResourceMap rm = new ResourceMap(Arrays.asList(TestUtil.ExpressionsPropPath, "org.jdesktop.application.resource.resources.doesNotExist"));
    }
    
}
