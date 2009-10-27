package org.jdesktop.application;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.net.URL;


/** Various utility methods used by other Test classes
 *
 * @author Rob Ross
 * @version Date: Oct 15, 2009  11:41:09 AM
 */
public class TestUtil
{
    private static final String USER_DIR = System.getProperty("user.dir");
    public static final double EPSILON_DOUBLE = 0.0000000000000001;
    public static final float EPSILON_FLOAT = 0.0000001f;

    public static BufferedImage getTestImage(String filePath)
    {
        URL url = TestUtil.class.getClassLoader().getResource(filePath);
        if (url == null)
        {
            String msg = String.format("Benchmark image not found at path '%s/%s'", USER_DIR, filePath);
            fail(msg);
        }
        BufferedImage bi = null;
        try
        {
            bi = ImageIO.read(url);
        }
        catch (Exception e)
        {
            String msg = String.format("Invalid image path '%s/%s'", USER_DIR, filePath);
            fail(msg);
        }

        return bi;
    }

    //helper method to deal with how to compare certain resource types when they don't have a suitable equals method
    public static void assertResourcesEqual(String msg, Object expected, Object actual)
    {
        Class objClass = expected.getClass();
        //Special case for testing equals with Images, Icons, URLs,
        if (objClass == EmptyBorder.class)
        {
            assertTrue(msg, TestUtil.equal((EmptyBorder) expected, (EmptyBorder) actual));
        }
        else if (objClass == URL.class)
        {
            assertTrue(msg, TestUtil.equal((URL) expected, (URL) actual));
        }
        else if (objClass == BufferedImage.class || objClass == Image.class)
        {
            assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));
        }
        else if (objClass == ImageIcon.class)
        {
            assertTrue(msg, TestUtil.equal((BufferedImage) ((ImageIcon) expected).getImage(), (BufferedImage) ((ImageIcon) actual).getImage()));
        }
        else if (objClass.isArray())
        {
            Class compClass = objClass.getComponentType();
            if (compClass == Integer.TYPE)
            {
                org.junit.Assert.assertArrayEquals(msg, (int[]) expected, (int[]) actual);
            }
            else if (compClass == Boolean.TYPE)
            {
                assertArrayEquals(msg, (boolean[]) expected, (boolean[]) actual);

            }
            else
            {
                org.junit.Assert.assertArrayEquals(msg, (Object[]) expected, (Object[]) actual);
            }

        }
        else
        {
            assertEquals(msg, expected, actual);
        }
    }

    //URL can do a DNS lookup when calling equals, so we avoid that by comparing string values.
    public static boolean equal(URL url1, URL url2)
    {
        if (url1 == url2)
        {
            return true;
        }

        if (url1 == null && url2 == null)
        {
            return true;
        }

        if (url1 == null && url2 != null)
        {
            return false;
        }
        return url1.toString().equals(url2.toString());        
    }

    //EmptyBorder does not override Object.equals, so we test Insets of each for equality
    public static boolean equal(EmptyBorder eb1, EmptyBorder eb2)
    {
        if (eb1 == eb2)
        {
            return true;
        }

        if (eb1 == null && eb2 == null)
        {
            return true;
        }

        if (eb1 == null & eb2 !=null)
        {
            return false;
        }

        return eb1.getBorderInsets().equals(eb2.getBorderInsets());
    }

    //return true if the two buffered images are equal, by comparing a few key fields. Good enough definition of "equality"
    // during unit testing
    public static boolean equal(BufferedImage bi1, BufferedImage bi2)
    {
        if (bi1 == bi2)
        {
            return true; //same instance
        }
        if (bi1 == null && bi2 != null)
        {
            return false;
        }
        if (bi2 == null && bi1 != null)
        {
            return false;
        }

        if (!(bi1.getHeight() == bi2.getHeight()))
        {
            return false;
        }

        if (!(bi1.getWidth() == bi2.getWidth()))
        {
            return false;
        }

        if (!(bi1.getColorModel().equals(bi2.getColorModel())))
        {
            return false;
        }

        if (!(bi1.getType() == bi2.getType()))
        {
            return false;
        }
        return true;
    }

    /**
     * Custom assertion method to compare two boolean arrays for equality
     * @param msg  message to be displayed if an AssertionError is thrown, when arrays are not equal
     * @param expected the expected boolean array
     * @param actual  the actual boolean array
     * @return true if both arrays are the same size and contain the same elements
     */
    public static boolean assertArrayEquals(String msg, boolean[] expected, boolean[] actual)
    {
        if (expected == actual)
        {
            return true;
        }
        if (expected == null && actual != null)
        {
            return false;
        }
        if (expected != null && actual == null)
        {
            return false;
        }

        if (expected.length != actual.length)
        {
            throw new AssertionError(msg + String.format(" Arrays not same size. expected.length=%s, actual.length=%s",expected.length, actual.length));
        }

        for (int i = 0, n = expected.length; i < n; i++)
        {
            if (expected[i] != actual[i])
            {
                throw new AssertionError(msg + String.format(" Arrays differ at index=%d. expected[%d]=%b, actual[%d]=%b", i,i, expected[i], i,actual[i]));
            }
        }
        return true;
    }
}
