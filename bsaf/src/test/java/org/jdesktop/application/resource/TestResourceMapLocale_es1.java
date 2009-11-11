package org.jdesktop.application.resource;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
import org.jdesktop.application.ResourceMap;

import java.util.Collection;
import java.util.Arrays;
import java.util.Locale;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.KeyEvent;

import org.jdesktop.application.TestUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

//tests to verify a single ResourceMap, with no parentMap, loaded when Locale is es, returns correct values from
//the _es bundle
@RunWith(value = Parameterized.class)
public class TestResourceMapLocale_es1
{
    String resourceKey;
    Class resourceType;
    Object expectedValue;

    public TestResourceMapLocale_es1(String resourceKey, Class resourceTye, Object expectedValue)
    {
        this.resourceKey = resourceKey;
        this.resourceType = resourceTye;
        this.expectedValue = expectedValue;
    }

    static Locale oldLocale;
    static ResourceMap spanishMap;

    @BeforeClass
    public static void unitSetup()
    {
       oldLocale = Locale.getDefault();
        //load with spanish local
        Locale.setDefault(new Locale("es"));
        spanishMap = new ResourceMap(Arrays.asList(TestUtil.AbstractAppPropPath));
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
        Locale.setDefault(oldLocale);
    } // unitCleanup()



    @Before
    public void methodSetup()
    {
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void testGetResource()
    {
        Object actual = spanishMap.getResourceAs(resourceKey, resourceType, null);
        String msg = String.format("Getting '%s' should return locale-specific instance", resourceKey);
        TestUtil.assertResourcesEqual(msg, expectedValue, actual);
    }


    @Parameterized.Parameters
    public static Collection<? extends Object[]> data() throws URISyntaxException, MalformedURLException
    {
        //we don't test image loading, because we're just testing that we get locale-specific values from the bundle

        return Arrays.asList(new Object[][]{
                {"AbstractApplication", String.class, "AbstractApplication_es"},
                {"Application.title", String.class, "AbstractApplication.properties title_es"},
                {"Application.field1", String.class, "AbstractApplication.properties field1_es"},
                {"Application.field2", String.class, "AbstractApplication.properties field2_es"},
                {"AbstractApplication.boolean", Boolean.class, Boolean.FALSE},
                {"AbstractApplication.character", Character.class, 'e'},
                {"AbstractApplication.color1", Color.class, new Color(40,30,20,10)},
                {"AbstractApplication.color2", Color.class, new Color(7,6,5)},
                {"AbstractApplication.color3", Color.class, new Color(0xAB, 0xCD, 0xEF)},
                {"AbstractApplication.color4", Color.class, new Color(0x00, 0xAA, 0xBB, 0xFF)},
                {"AbstractApplication.dimension", Dimension.class, new Dimension(10,10)},
                {"AbstractApplication.double", Double.class, 6.2},
                {"AbstractApplication.emptyBorder", EmptyBorder.class, new EmptyBorder(1,2,3,4)},
                {"AbstractApplication.float", Float.class, 6.2f},
                {"AbstractApplication.font", Font.class, new Font("Helvetica", Font.BOLD, 36)},
                {"AbstractApplication.insets", Insets.class, new Insets(1,2,3,4)},
                {"AbstractApplication.integer", Integer.class, new Integer(123)},
                {"AbstractApplication.keystroke", KeyStroke.class, KeyStroke.getKeyStroke(KeyEvent.VK_W,  KeyEvent.META_DOWN_MASK)},
                {"AbstractApplication.long", Long.class, new Long(422L)},

                {"AbstractApplication.point", Point.class, new Point(33,44)},
                {"AbstractApplication.point2d_double", Point2D.Double.class, new Point2D.Double(22.14156, 33.14156)},
                {"AbstractApplication.point2d_float", Point2D.Float.class, new Point2D.Float(22.14156f, 33.14156f)},
                {"AbstractApplication.rectangle", Rectangle.class, new Rectangle(10,10,20,20)},
                {"AbstractApplication.rectangle2d_double", Rectangle2D.Double.class, new Rectangle2D.Double(10.345678, 10.45678, 200.123456, 321.23456)},
                {"AbstractApplication.rectangle2d_float", Rectangle2D.Float.class, new Rectangle2D.Float(10.345678f, 10.45678f, 200.123456f, 321.23456f)},
                {"AbstractApplication.short", Short.class, new Short((short) 19)},
                {"AbstractApplication.uri", URI.class, new URI("http://java.net")},
                {"AbstractApplication.url", URL.class, new URL("http://java.net")},
                {"AbstractApplication.string", String.class, "yo dice el toote froote!"},
                //these properties are NOT in the _es bundle, so we should get them from the default bundle
                {"AbstractApplication.image1", String.class, "duke.png"},
                {"AbstractApplication.image2", String.class, "images/duke.gif"},
                {"AbstractApplication.image4", String.class, "/org/jdesktop/application/resource/abstractapp/resources/images/javalogo52x88.gif"},
        });
    }
}