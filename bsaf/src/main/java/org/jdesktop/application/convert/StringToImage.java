package org.jdesktop.application.convert;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.net.URL;
import java.awt.image.BufferedImage;
import java.awt.*;

import org.jetbrains.annotations.NotNull;


/**
 *
 *
 * @author Rob Ross
 * @version Date: Oct 9, 2009  9:11:54 PM
 */
public class StringToImage extends ResourceConverter<String, Image>
{

    public StringToImage()
    {
        super(String.class, Image.class);
    }


    public Image convert(@NotNull String resourcePath, Object... args) throws StringConvertException
    {
        checkArgs(resourcePath, args);
        ClassLoader classloader = (ClassLoader) args[0];
        return loadBufferedImage(resourcePath, classloader);
    }


    public static class StringToIcon extends ResourceConverter<String, Icon>
    {
        public StringToIcon()
        {
            super(String.class, Icon.class);
        }

        public Icon convert(@NotNull String resourcePath, Object... args) throws StringConvertException
        {
            checkArgs(resourcePath, args);
            ClassLoader classloader = (ClassLoader) args[0];
            return new ImageIcon(loadBufferedImage(resourcePath, classloader));
        }
    }

    public static class StringToImageIcon extends ResourceConverter<String, ImageIcon>
    {
        public StringToImageIcon()
        {
            super(String.class, ImageIcon.class);
        }

        public ImageIcon convert(@NotNull String resourcePath, Object... args) throws StringConvertException
        {
            checkArgs(resourcePath, args);
            ClassLoader classloader = (ClassLoader) args[0];
            return new ImageIcon(loadBufferedImage(resourcePath, classloader));
        }
    }


    public static class StringToBufferedImage extends ResourceConverter<String, BufferedImage>
    {
        public StringToBufferedImage()
        {
            super(String.class, BufferedImage.class);
        }

        public BufferedImage convert(@NotNull String resourcePath, Object... args) throws StringConvertException
        {
            checkArgs(resourcePath, args);
            ClassLoader classloader = (ClassLoader) args[0];
            return loadBufferedImage(resourcePath, classloader);
        }
    }

    protected static void checkArgs(String resourcePath, Object[] args) throws IllegalArgumentException
    {

        if (resourcePath == null || resourcePath.isEmpty())
        {
            String s = resourcePath == null ? "null" : "empty string";
            throw new IllegalArgumentException("Expected resource path as first argument, but found "+s);
        }
        if (args == null || args.length == 0 || !(args[0] instanceof ClassLoader))
        {
            throw new IllegalArgumentException("Expected ClassLoader as first var-argument");
        }

    }

    protected static BufferedImage loadBufferedImage(@NotNull String resourcePath, @NotNull ClassLoader classloader)
            throws StringConvertException
    {
        //ClassLoader.getResource() requires the full path to a resource, unlike Class.getResource(), which
        //can also specify a location relative to the Class' path.
        URL url = classloader.getResource(resourcePath);
        if (url == null)
        {
            String msg = String.format("No image found at path \"%s\"", resourcePath);
            throw new StringConvertException(msg, resourcePath);
        }
        BufferedImage bi = null;
        try
        {
            bi = ImageIO.read(url);
        }
        catch (Exception e)
        {
            String msg = String.format("Invalid image path \"%s\"", resourcePath);
            throw new StringConvertException(msg, resourcePath, e);
        }
        return bi;
    }
}
