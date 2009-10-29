package org.jdesktop.application.convert;


import org.jetbrains.annotations.NotNull;
import java.awt.*;


/**
 * An improved version of Color.decode() that supports colors
 * with an alpha channel and comma separated RGB[A] values.
 * Legal format for color resources are:
 * "#RRGGBB",  "#AARRGGBB", "R, G, B", "R, G, B, A"
 * Thanks to Romain Guy for the code.
 *  @author Hans Muller 
 *  @author Rob Ross
 *  @version Date: Oct 9, 2009  8:48:14 PM
 */
public class StringToColor extends ResourceConverter<String, Color>
{
    public StringToColor()
    {
        super(String.class, Color.class);
    }

    public Color convert(@NotNull String source, Object... args) throws StringConvertException
    {
        assertNotNull(source, String.class, "source");
        Color color = null;
        if (source.startsWith("#"))
        {
            switch (source.length())
            {
                // RGB/hex color
                case 7:
                    color = Color.decode(source);
                    break;
                // ARGB/hex color
                //todo these two formats are inconsistent in where the Alpha component is. Should we standardize
                //and make it #RRGGBBAA instead? Or is #AARRGGBB already an industry standard way of specifying it?
                case 9:
                    int alpha = Integer.decode(source.substring(0, 3));
                    int rgb = Integer.decode("#" + source.substring(3));
                    color = new Color(alpha << 24 | rgb, true);
                    break;
                default:
                    throw new StringConvertException("Invalid #RRGGBB or #AARRGGBB color string", source);
            }
        }
        else
        {
            String[] parts = source.split(",");
            if (parts.length < 3 || parts.length > 4)
            {
                throw new StringConvertException("Invalid R, G, B[, A] color string", source);
            }
            try
            {
                // with alpha component
                if (parts.length == 4)
                {
                    int r = Integer.parseInt(parts[0].trim());
                    int g = Integer.parseInt(parts[1].trim());
                    int b = Integer.parseInt(parts[2].trim());
                    int a = Integer.parseInt(parts[3].trim());
                    color = new Color(r, g, b, a);
                }
                else
                {
                    int r = Integer.parseInt(parts[0].trim());
                    int g = Integer.parseInt(parts[1].trim());
                    int b = Integer.parseInt(parts[2].trim());
                    color = new Color(r, g, b);
                }
            }
            catch (NumberFormatException e)
            {
                throw new StringConvertException("Invalid R, G, B[, A] color string", source, e);
            }
        }
        return color;
    }


    private void error(String msg, String s, Exception e) throws StringConvertException
    {
        throw new StringConvertException(msg, s, e);
    }

    private void error(String msg, String s) throws StringConvertException
    {
        error(msg, s, null);
    }
}
