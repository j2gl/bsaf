package org.jdesktop.application.resource;


import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TestUtil;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.util.Collection;
import java.util.Arrays;
import java.util.Locale;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.MalformedURLException;


//Testing that retreived recources from a single ResourceMap with no parentMap,
// are placed in the cache, and returned from the cache on subsequent calls
@RunWith(value = Parameterized.class)
public class TestResourceMapCaching1
{
    String resourceKey;
    Class resourceType;
    Object expectedValue;

    public TestResourceMapCaching1(String resourceKey, Class resourceTye, Object expectedValue)
    {
        this.resourceKey = resourceKey;
        this.resourceType = resourceTye;
        this.expectedValue = expectedValue;
    }

    static BufferedImage image1;
    static Image         image2;
    static Icon          icon;


    static ResourceMap defaultMap;
    static Locale oldLocale;

    @BeforeClass
    public static void unitSetup()
    {
        oldLocale = Locale.getDefault();
        Locale.setDefault(new Locale("")); //needed to force use of unqualified bundles if unit tests run where "es" is default language
        defaultMap = new ResourceMap(Arrays.asList(AllTests.AbstractAppPropPath));
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
        Locale.setDefault(oldLocale);
    }

    @Before
    public void methodSetup()
    {

    } // methodSetup()


    private Object getResource()
    {
        Object value =  null;
        if (expectedValue.getClass().isArray())
        {
            if (expectedValue.getClass().getComponentType().isPrimitive())
            {
                value = defaultMap.getAsPrimitiveArray(resourceKey, resourceType, null);
            }
            else
            {
                value = defaultMap.getAsArray(resourceKey, resourceType, null);
            }            
        }
        else
        {
            value = defaultMap.getResourceAs(resourceKey, resourceType, null);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSecondGetIsFromCache()
    {
        //prime cache with an entry
        defaultMap.getResourceAs("AbstractApplication.long", Long.class, null);


        //first get of resource should place it in the cache
        Object expected = getResource();
        assertNotNull(String.format("When getting resrouceKey='%s' expected:%s",resourceKey, expectedValue), expected);

        String msg = String.format("Expected value of key='%s' not returned", resourceKey);
        //Special case for testing equals with Images, Icons, URLs,
        TestUtil.assertResourcesEqual(msg, expectedValue, expected);

        //called a second time, should return same instance
        Object actual = getResource();
        msg = String.format("Getting '%s' second time should return cached instance", resourceKey);
        assertSame(msg, expected, actual);

        //clearing cache should cause new instances to be returned
        defaultMap.clearCache();
        if (expected != expectedValue && expectedValue.getClass() != String.class)
        {
            //some of these resource values are cached by Java libraries themselves, like Fonts, KeyStrokes, etc. So we will always
            //retreive the same instance whenever we reference them. But for those instances that are created new each time,
            //we can see if the instance we get after the cache is cleared is different than before, verifying the cache has
            //created a new instance
            msg = String.format("After clearing the cache, a new instance of '%s' should be returned.", resourceKey);
            actual = getResource();

            assertNotSame(msg, expected, actual);
        }
    }

    @Parameterized.Parameters
    public static Collection<? extends Object[]> data() throws URISyntaxException, MalformedURLException
    {
        image1 = TestUtil.getTestImage("org/jdesktop/application/resource/abstractapp/resources/duke.png");
        image2 = TestUtil.getTestImage("org/jdesktop/application/resource/abstractapp/resources/images/duke.gif");
        icon = new ImageIcon(TestUtil.getTestImage("org/jdesktop/application/resource/abstractapp/resources/images/smallbabies.jpeg"));

        assertNotNull(image1);
        assertNotNull(image2);
        assertNotNull(icon);
        
        return Arrays.asList(new Object[][]{
                {"AbstractApplication.boolean", Boolean.class, Boolean.TRUE},
                {"AbstractApplication.character", Character.class, 'h'},
                {"AbstractApplication.color1", Color.class, new Color(10,20,30,40)},
                {"AbstractApplication.color2", Color.class, new Color(50,60,70)},
                {"AbstractApplication.color3", Color.class, new Color(0xFF, 0xAA, 0xCD)},
                {"AbstractApplication.color4", Color.class, new Color(0xBB, 0xCC, 0xDD, 0xAA)},
                {"AbstractApplication.dimension", Dimension.class, new Dimension(-144, 2356)},
                {"AbstractApplication.double", Double.class, 3.141592653589793},
                {"AbstractApplication.emptyBorder", EmptyBorder.class, new EmptyBorder(5,6,7,8)},
                {"AbstractApplication.float", Float.class, 3.1415927f},
                {"AbstractApplication.font", Font.class, new Font("Arial", Font.PLAIN, 12)},
                {"AbstractApplication.insets", Insets.class, new Insets(9,8,7,6)},
                {"AbstractApplication.integer", Integer.class, new Integer(56789234)},
                {"AbstractApplication.keystroke", KeyStroke.class, KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK)},
                {"AbstractApplication.long", Long.class, new Long(9223372036854775807L)},

                {"AbstractApplication.point", Point.class, new Point(55,66)},
                {"AbstractApplication.point2d_double", Point2D.Double.class, new Point2D.Double(55.14156, 66.14156)},
                {"AbstractApplication.point2d_float", Point2D.Float.class, new Point2D.Float(77.14156f, 88.14156f)},
                {"AbstractApplication.rectangle", Rectangle.class, new Rectangle(55, 66, 100, 200)},
                {"AbstractApplication.rectangle2d_double", Rectangle2D.Double.class, new Rectangle2D.Double(12.345678, 23.45678, 567.123456, 789.23456)},
                {"AbstractApplication.rectangle2d_float", Rectangle2D.Float.class, new Rectangle2D.Float(55.123f, 66.321f, 100.765f, 200.876f)},
                {"AbstractApplication.short", Short.class, new Short((short)876)},
                {"AbstractApplication.uri", URI.class, new URI("http://java.sun.com")},
                {"AbstractApplication.url", URL.class, new URL("http://java.sun.com")},
                {"AbstractApplication.string", String.class, "tootie frooty, oh rooty!"},
                {"AbstractApplication.image1", BufferedImage.class, image1},
                {"AbstractApplication.image2", BufferedImage.class, image2},
                {"AbstractApplication.image3", ImageIcon.class, icon},

                //arrays
                {"AbstractApplication.array.string", String.class, new String[]{"one", "two", "three"}},
                {"AbstractApplication.array.int", int.class, new int[]{1,2,3}},
                {"AbstractApplication.array.boolean", boolean.class, new boolean[]{true, false, true, false}},

        });
    }
}