package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.IOException;

public class TestStringToImage
{
    public TestStringToImage() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()


    StringToImage.StringToBufferedImage defaultConverter;

    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToImage.StringToBufferedImage();

    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()

    @Test(expected = IllegalArgumentException.class)
    public void testNullString() throws StringConvertException
    {
        String s = null;

        Image actual = defaultConverter.convert(s);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullClassLoader() throws StringConvertException
    {
        String s = "";
        ClassLoader cl = null;

        Image actual = defaultConverter.convert(s, (ClassLoader) cl);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyString() throws StringConvertException
    {
        String s = "";
        ClassLoader cl = getClass().getClassLoader();

        Image actual = defaultConverter.convert(s, cl);
    }

    @Test(expected = StringConvertException.class)
    public void testBadString() throws StringConvertException
    {
        String s = "foo";
        ClassLoader cl = getClass().getClassLoader();

        Image actual = defaultConverter.convert(s, cl);
    }

    private static BufferedImage getTestImage(String imagePath) throws IOException
    {
        ClassLoader cl = TestStringToImage.class.getClassLoader();
        URL url = cl.getResource(imagePath);
        BufferedImage bi = ImageIO.read(url);
        return bi;
    }

    //return true if the two buffered images are equal, for comparing during unit tests
    private static boolean equal(BufferedImage bi1, BufferedImage bi2)
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

    @Test
    public void testString1() throws StringConvertException, IOException
    {
        String s = "org/jdesktop/application/convert/resources/guitar.png";
        ClassLoader cl = getClass().getClassLoader();

        Image actual = defaultConverter.convert(s, cl);
        Image expected = getTestImage(s);

        String msg = String.format("Should load BufferedImage for '%s'", s);
        assertTrue(msg, equal((BufferedImage) expected, (BufferedImage) actual));
    }

    @Test(expected = StringConvertException.class)
    public void testRelativePath() throws StringConvertException, IOException
    {
        String s = "guitar.png"; //although this works with Class.getResource(), it fails on ClassLoader.getResource()
        ClassLoader cl = getClass().getClassLoader();

        Image actual = defaultConverter.convert(s, cl);
        Image expected = getTestImage(s);

        String msg = String.format("Should load BufferedImage for '%s'", s);
        assertTrue(msg, equal((BufferedImage) expected, (BufferedImage) actual));
    }

    public static class TestStringToIcon
    {
        StringToImage.StringToImageIcon defaultConverter;

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToImage.StringToImageIcon();
        } // methodSetup()

        @Test(expected = IllegalArgumentException.class)
        public void testNullString() throws StringConvertException
        {
            String s = null;

            Icon actual = defaultConverter.convert(s);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testNullClassLoader() throws StringConvertException
        {
            String s = "";
            ClassLoader cl = null;

            Icon actual = defaultConverter.convert(s, (ClassLoader) cl);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testEmptyString() throws StringConvertException
        {
            String s = "";
            ClassLoader cl = getClass().getClassLoader();

            Icon actual = defaultConverter.convert(s, cl);
        }

        @Test(expected = StringConvertException.class)
        public void testBadString() throws StringConvertException
        {
            String s = "foo";
            ClassLoader cl = getClass().getClassLoader();

            Icon actual = defaultConverter.convert(s, cl);
        }

        @Test
        public void testString1() throws StringConvertException, IOException
        {
            String s = "org/jdesktop/application/convert/resources/guitar.png";
            ClassLoader cl = getClass().getClassLoader();

            Icon actual = defaultConverter.convert(s, cl); //we know the implementation is an ImageIcon, image is a BufferedImage
            Image image = getTestImage(s);
            ImageIcon expected = new ImageIcon(image);

            String msg = String.format("Should load BufferedImage for '%s'", s);
            assertTrue(msg, equal((BufferedImage) expected.getImage(), (BufferedImage) ((ImageIcon)actual).getImage()));
        }

        @Test(expected = StringConvertException.class)
        public void testRelativePath() throws StringConvertException, IOException
        {
            String s = "guitar.png"; //although this works with Class.getResource(), it fails on ClassLoader.getResource()
            ClassLoader cl = getClass().getClassLoader();

            Icon actual = defaultConverter.convert(s, cl);
            Image expected = getTestImage(s);

            String msg = String.format("Should load BufferedImage for '%s'", s);
            assertTrue(msg, equal((BufferedImage) expected, (BufferedImage) actual));
        }

    }

    public static class TestStringToBufferedImage
    {
        StringToImage.StringToBufferedImage defaultConverter;

        @Before
        public void methodSetup()
        {
            defaultConverter = new StringToImage.StringToBufferedImage();

        } // methodSetup()

        @Test(expected = IllegalArgumentException.class)
        public void testNullString() throws StringConvertException
        {
            String s = null;

            BufferedImage actual = defaultConverter.convert(s);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testNullClassLoader() throws StringConvertException
        {
            String s = "";
            ClassLoader cl = null;

            BufferedImage actual = defaultConverter.convert(s, (ClassLoader) cl);
        }

        @Test(expected = IllegalArgumentException.class)
        public void testEmptyString() throws StringConvertException
        {
            String s = "";
            ClassLoader cl = getClass().getClassLoader();

            BufferedImage actual = defaultConverter.convert(s, cl);
        }

        @Test(expected = StringConvertException.class)
        public void testBadString() throws StringConvertException
        {
            String s = "foo";
            ClassLoader cl = getClass().getClassLoader();

            BufferedImage actual = defaultConverter.convert(s, cl);
        }

        @Test
        public void testString1() throws StringConvertException, IOException
        {
            String s = "org/jdesktop/application/convert/resources/guitar.png";
            ClassLoader cl = getClass().getClassLoader();

            BufferedImage actual = defaultConverter.convert(s, cl);
            BufferedImage expected = getTestImage(s);

            String msg = String.format("Should load BufferedImage for '%s'", s);
            assertTrue(msg, equal((BufferedImage) expected, (BufferedImage) actual));
        }

        @Test(expected = StringConvertException.class)
        public void testRelativePath() throws StringConvertException, IOException
        {
            String s = "guitar.png"; //although this works with Class.getResource(), it fails on ClassLoader.getResource()
            ClassLoader cl = getClass().getClassLoader();

            BufferedImage actual = defaultConverter.convert(s, cl);
            BufferedImage expected = getTestImage(s);

            String msg = String.format("Should load BufferedImage for '%s'", s);
            assertTrue(msg, equal((BufferedImage) expected, (BufferedImage) actual));
        }
    }

}