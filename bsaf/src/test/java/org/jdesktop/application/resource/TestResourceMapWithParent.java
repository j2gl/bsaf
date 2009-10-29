package org.jdesktop.application.resource;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TestUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.Collection;
import java.util.Arrays;
import java.util.Locale;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.KeyEvent;

//This will test the creation of a ResourceMap with a parentMap, to ensure overridden properties are properly retrieved,

//and non-overridden properties are visible through the childMap instance
public class TestResourceMapWithParent
{
    public TestResourceMapWithParent() {} // constructor

    //make sure access through the childMap reference produces overridden values in the childMap bundle, and inherits non-overriden
    //properties from the parentMap
    @RunWith(value = Parameterized.class)
    public static class TestChildMapData
    {
        String resourceKey;
        Class resourceType;
        Object expectedValue;

        public TestChildMapData(String resourceKey, Class resourceTye, Object expectedValue)
        {
            this.resourceKey = resourceKey;
            this.resourceType = resourceTye;
            this.expectedValue = expectedValue;
        }

        static ResourceMap parentMap;
        static ResourceMap childMap;
        static Locale oldLocale;


        @BeforeClass
        public static void unitSetup()
        {
            oldLocale = Locale.getDefault();
            Locale.setDefault(new Locale("")); //needed to force use of unqualified bundles if unit tests run where "es" is default language
            parentMap = new ResourceMap(Arrays.asList(TestUtil.AbstractAppPropPath));
            childMap = new ResourceMap(Arrays.asList(TestUtil.ConcreteAppPropPath));
            childMap.setParent(parentMap);

        } // unitSetup()

        @AfterClass
        public static void unitCleanup()
        {
            Locale.setDefault(oldLocale);
        }


        @Test
        public void testGetResource()
        {
            Object actual = childMap.getResourceAs(resourceKey, resourceType, null);
            String msg = String.format("Getting '%s' should return parent-specific instance", resourceKey);
            TestUtil.assertResourcesEqual(msg, expectedValue, actual);
        }

        static BufferedImage image1;
        static Image image2, image3;
        static Icon icon;

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data() throws URISyntaxException, MalformedURLException
        {
            image1 = TestUtil.getTestImage("org/jdesktop/application/resource/abstractapp/resources/duke.png");
            image2 = TestUtil.getTestImage("org/jdesktop/application/resource/abstractapp/resources/images/duke.gif");
            image3 = TestUtil.getTestImage("org/jdesktop/application/resource/concrete/resources/images/Bean24.gif");

            icon = new ImageIcon(TestUtil.getTestImage("org/jdesktop/application/resource/abstractapp/resources/images/javalogo52x88.gif"));

            assertNotNull(image1);
            assertNotNull(image2);
            assertNotNull(image3);
            assertNotNull(icon);

            return Arrays.asList(new Object[][]{
                    {"AbstractApplication", String.class, "AbstractApplication"},
                    {"ConcreteApplication", String.class, "ConcreteApplication"},

                    {"Application.title", String.class, "ConcreteApplication.properties title"},
                    {"Application.field1", String.class, "ConcreteApplication.properties field1"},
                    {"Application.field2", String.class, "ConcreteApplication.properties field2"},

                    {"AbstractApplication.boolean", Boolean.class, Boolean.TRUE},
                    {"AbstractApplication.character", Character.class, 'h'},
                    {"AbstractApplication.color1", Color.class, new Color(10, 20, 30, 40)},
                    {"AbstractApplication.color2", Color.class, new Color(50, 60, 70)},
                    {"AbstractApplication.color3", Color.class, new Color(0xFF, 0xAA, 0xCD)},
                    {"AbstractApplication.color4", Color.class, new Color(0xBB, 0xCC, 0xDD, 0xAA)},
                    {"AbstractApplication.dimension", Dimension.class, new Dimension(-144, 2356)},
                    {"AbstractApplication.double", Double.class, 3.141592653589793},
                    {"AbstractApplication.emptyBorder", EmptyBorder.class, new EmptyBorder(5, 6, 7, 8)},
                    {"AbstractApplication.float", Float.class, 3.1415927f},
                    {"AbstractApplication.font", Font.class, new Font("Arial", Font.PLAIN, 12)},
                    {"AbstractApplication.insets", Insets.class, new Insets(9, 8, 7, 6)},
                    {"AbstractApplication.integer", Integer.class, new Integer(56789234)},
                    {"AbstractApplication.keystroke", KeyStroke.class, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK)},
                    {"AbstractApplication.long", Long.class, new Long(9223372036854775807L)},
                    {"AbstractApplication.point", Point.class, new Point(55, 66)},
                    {"AbstractApplication.point2d_double", Point2D.Double.class, new Point2D.Double(55.14156, 66.14156)},
                    {"AbstractApplication.point2d_float", Point2D.Float.class, new Point2D.Float(77.14156f, 88.14156f)},
                    {"AbstractApplication.rectangle", Rectangle.class, new Rectangle(55, 66, 100, 200)},
                    {"AbstractApplication.rectangle2d_double", Rectangle2D.Double.class, new Rectangle2D.Double(12.345678, 23.45678, 567.123456, 789.23456)},
                    {"AbstractApplication.rectangle2d_float", Rectangle2D.Float.class, new Rectangle2D.Float(55.123f, 66.321f, 100.765f, 200.876f)},
                    {"AbstractApplication.short", Short.class, new Short((short) 19)},
                    {"AbstractApplication.uri", URI.class, new URI("http://java.net")},
                    {"AbstractApplication.url", URL.class, new URL("http://java.net")},
                    {"AbstractApplication.string", String.class, "Overriden value in ConcreteApplication.properties"},
                    {"AbstractApplication.image1", BufferedImage.class, image1},
                    {"AbstractApplication.image2", BufferedImage.class, image2},
                    {"AbstractApplication.image3", BufferedImage.class, image3},
                    {"AbstractApplication.image4", ImageIcon.class, icon},
            });
        }
    }

    //make sure access through the parentMap reference still produces the values from the parentMap's bundle
    @RunWith(value = Parameterized.class)
    public static class TestParentMapData
    {

        String resourceKey;
        Class resourceType;
        Object expectedValue;

        public TestParentMapData(String resourceKey, Class resourceTye, Object expectedValue)
        {
            this.resourceKey = resourceKey;
            this.resourceType = resourceTye;
            this.expectedValue = expectedValue;
        }

        static ResourceMap parentMap;
        static ResourceMap childMap;
        static Locale oldLocale;


        @BeforeClass
        public static void unitSetup()
        {
            oldLocale = Locale.getDefault();
            Locale.setDefault(new Locale("")); //needed to force use of unqualified bundles if unit tests run where "es" is default language
            parentMap = new ResourceMap(Arrays.asList(TestUtil.AbstractAppPropPath));
            childMap = new ResourceMap(Arrays.asList(TestUtil.ConcreteAppPropPath));
            childMap.setParent(parentMap);

        } // unitSetup()

        @AfterClass
        public static void unitCleanup()
        {
            Locale.setDefault(oldLocale);
        }

        @Test
        public void testGetResource()
        {
            Object actual = parentMap.getResourceAs(resourceKey, resourceType, null);
            String msg = String.format("Getting '%s' should return parent-specific instance", resourceKey);
            TestUtil.assertResourcesEqual(msg, expectedValue, actual);
        }

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data() throws URISyntaxException, MalformedURLException
        {

            return Arrays.asList(new Object[][]{
                    {"AbstractApplication", String.class, "AbstractApplication"},
                    {"Application.title", String.class, "AbstractApplication.properties title"},
                    {"Application.field1", String.class, "AbstractApplication.properties field1"},
                    {"Application.field2", String.class, "AbstractApplication.properties field2"},

                    {"AbstractApplication.boolean", Boolean.class, Boolean.TRUE},
                    {"AbstractApplication.character", Character.class, 'h'},
                    {"AbstractApplication.color1", Color.class, new Color(10, 20, 30, 40)},
                    {"AbstractApplication.color2", Color.class, new Color(50, 60, 70)},
                    {"AbstractApplication.color3", Color.class, new Color(0xFF, 0xAA, 0xCD)},
                    {"AbstractApplication.color4", Color.class, new Color(0xBB, 0xCC, 0xDD, 0xAA)},
                    {"AbstractApplication.dimension", Dimension.class, new Dimension(-144, 2356)},
                    {"AbstractApplication.double", Double.class, 3.141592653589793},
                    {"AbstractApplication.emptyBorder", EmptyBorder.class, new EmptyBorder(5, 6, 7, 8)},
                    {"AbstractApplication.float", Float.class, 3.1415927f},
                    {"AbstractApplication.font", Font.class, new Font("Arial", Font.PLAIN, 12)},
                    {"AbstractApplication.insets", Insets.class, new Insets(9, 8, 7, 6)},
                    {"AbstractApplication.integer", Integer.class, new Integer(56789234)},
                    {"AbstractApplication.keystroke", KeyStroke.class, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK)},
                    {"AbstractApplication.long", Long.class, new Long(9223372036854775807L)},
                    {"AbstractApplication.point", Point.class, new Point(55, 66)},
                    {"AbstractApplication.point2d_double", Point2D.Double.class, new Point2D.Double(55.14156, 66.14156)},
                    {"AbstractApplication.point2d_float", Point2D.Float.class, new Point2D.Float(77.14156f, 88.14156f)},
                    {"AbstractApplication.rectangle", Rectangle.class, new Rectangle(55, 66, 100, 200)},
                    {"AbstractApplication.rectangle2d_double", Rectangle2D.Double.class, new Rectangle2D.Double(12.345678, 23.45678, 567.123456, 789.23456)},
                    {"AbstractApplication.rectangle2d_float", Rectangle2D.Float.class, new Rectangle2D.Float(55.123f, 66.321f, 100.765f, 200.876f)},
                    {"AbstractApplication.short", Short.class, new Short((short) 876)},
                    {"AbstractApplication.uri", URI.class, new URI("http://java.sun.com")},
                    {"AbstractApplication.url", URL.class, new URL("http://java.sun.com")},
                    {"AbstractApplication.string", String.class, "tootie frooty, oh rooty!"},
                    {"AbstractApplication.image1", String.class, "duke.png"},
                    {"AbstractApplication.image2", String.class, "images/duke.gif"},
                    {"AbstractApplication.image3", String.class, "images/smallbabies.jpeg"},
                    {"AbstractApplication.image4", String.class, "/org/jdesktop/application/resource/abstractapp/resources/images/javalogo52x88.gif"},
            });
        }
    }

    /**
     * Same test as TestChildMapData but using _es Local bundles for parent and child
     */
    @RunWith(value = Parameterized.class)
    public static class TestChildUsingLocale_es
    {
        String resourceKey;
        Class resourceType;
        Object expectedValue;

        public TestChildUsingLocale_es(String resourceKey, Class resourceTye, Object expectedValue)
        {
            this.resourceKey = resourceKey;
            this.resourceType = resourceTye;
            this.expectedValue = expectedValue;
        }

        static ResourceMap parentMap;
        static ResourceMap childMap;
        static Locale oldLocale;

        @BeforeClass
        public static void unitSetup()
        {
            oldLocale = Locale.getDefault();
            Locale.setDefault(new Locale("es"));
            if (parentMap == null)
            {
                parentMap = new ResourceMap(Arrays.asList(TestUtil.AbstractAppPropPath));
            }
            if (childMap == null)
            {
                childMap = new ResourceMap(Arrays.asList(TestUtil.ConcreteAppPropPath));
                childMap.setParent(parentMap);
            }

        } // unitSetup()

        @AfterClass
        public static void unitCleanup()
        {
            Locale.setDefault(oldLocale);
        } // unitCleanup()

        @Test
        public void testGetResource()
        {
            Object actual = childMap.getResourceAs(resourceKey, resourceType, null);
            String msg = String.format("Getting '%s' should return child or locale-specific instance", resourceKey);
            TestUtil.assertResourcesEqual(msg, expectedValue, actual);
        }

        static BufferedImage image1;
        static Image image2, image3;
        static Icon icon;

        @Parameterized.Parameters
        public static Collection<? extends Object[]> data() throws URISyntaxException, MalformedURLException
        {
            image1 = TestUtil.getTestImage("org/jdesktop/application/resource/concrete/resources/images/bell.gif");
            image2 = TestUtil.getTestImage("org/jdesktop/application/resource/abstractapp/resources/images/duke.gif");
            image3 = TestUtil.getTestImage("org/jdesktop/application/resource/concrete/resources/images/Bean24_es.png");

            icon = new ImageIcon(TestUtil.getTestImage("org/jdesktop/application/resource/abstractapp/resources/images/javalogo52x88.gif"));

            assertNotNull(image1);
            assertNotNull(image2);
            assertNotNull(image3);
            assertNotNull(icon);

            return Arrays.asList(new Object[][]{
                    {"AbstractApplication", String.class, "AbstractApplication_es"},
                    {"ConcreteApplication", String.class, "ConcreteApplication_es"},

                    {"Application.title", String.class, "ConcreteApplication.properties title_es"},
                    {"Application.field1", String.class, "ConcreteApplication.properties field1_es"},
                    {"Application.field2", String.class, "ConcreteApplication.properties field2_es"},

                    {"AbstractApplication.boolean", Boolean.class, Boolean.FALSE},
                    {"AbstractApplication.character", Character.class, 'e'},
                    {"AbstractApplication.color1", Color.class, new Color(40, 30, 20, 10)},
                    {"AbstractApplication.color2", Color.class, new Color(7, 6, 5)},
                    {"AbstractApplication.color3", Color.class, new Color(0xAB, 0xCD, 0xEF)},
                    {"AbstractApplication.color4", Color.class, new Color(0x00, 0xAA, 0xBB, 0xFF)},
                    {"AbstractApplication.dimension", Dimension.class, new Dimension(10, 10)},
                    {"AbstractApplication.double", Double.class, 6.2},
                    {"AbstractApplication.emptyBorder", EmptyBorder.class, new EmptyBorder(1, 2, 3, 4)},
                    {"AbstractApplication.float", Float.class, 6.2f},
                    {"AbstractApplication.font", Font.class, new Font("Helvetica", Font.BOLD, 36)},
                    {"AbstractApplication.insets", Insets.class, new Insets(1, 2, 3, 4)},
                    {"AbstractApplication.integer", Integer.class, new Integer(123)},
                    {"AbstractApplication.keystroke", KeyStroke.class, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.META_DOWN_MASK)},
                    {"AbstractApplication.long", Long.class, new Long(422L)},
                    {"AbstractApplication.point", Point.class, new Point(33, 44)},
                    {"AbstractApplication.point2d_double", Point2D.Double.class, new Point2D.Double(22.14156, 33.14156)},
                    {"AbstractApplication.point2d_float", Point2D.Float.class, new Point2D.Float(22.14156f, 33.14156f)},
                    {"AbstractApplication.rectangle", Rectangle.class, new Rectangle(10, 10, 20, 20)},
                    {"AbstractApplication.rectangle2d_double", Rectangle2D.Double.class, new Rectangle2D.Double(10.345678, 10.45678, 200.123456, 321.23456)},
                    {"AbstractApplication.rectangle2d_float", Rectangle2D.Float.class, new Rectangle2D.Float(10.345678f, 10.45678f, 200.123456f, 321.23456f)},
                    {"AbstractApplication.short", Short.class, new Short((short) 19)},
                    {"AbstractApplication.uri", URI.class, new URI("http://java.net")},
                    {"AbstractApplication.url", URL.class, new URL("http://java.net")},
                    {"AbstractApplication.string", String.class, "Spanish version of overriden value in ConcreteApplication_es.properties"},
                    {"AbstractApplication.image1", BufferedImage.class, image1},
                    {"AbstractApplication.image2", BufferedImage.class, image2},
                    {"AbstractApplication.image3", BufferedImage.class, image3},
                    {"AbstractApplication.image4", ImageIcon.class, icon},
            });
        }
    }
}