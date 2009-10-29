package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;


/* 
 * @author Rob Ross
 * @version Date: Oct 9, 2009  9:26:52 PM
 */
public class StringToRectangle extends ResourceConverter<String, Rectangle>
{
    public StringToRectangle()
    {
        super(String.class, Rectangle.class);
    }

    public Rectangle convert(@NotNull String source, Object... args) throws StringConvertException
    {
        assertNotNull(source, String.class, "source");
        List<Double> xywh = parseDoubles(source, 4, "Invalid x,y,width,height Rectangle string");
        Rectangle r = new Rectangle();
        r.setFrame(xywh.get(0), xywh.get(1), xywh.get(2), xywh.get(3));
        return r;
    }

    public static class StringToRectangle2D_Float extends ResourceConverter<String, Rectangle2D.Float>
    {
        public StringToRectangle2D_Float()
        {
            super(String.class, Rectangle2D.Float.class);
        }

        public Rectangle2D.Float convert(@NotNull String source, Object... args) throws StringConvertException
        {
            assertNotNull(source, String.class, "source");
            List<Double> xywh = parseDoubles(source, 4, "Invalid x,y,width,height Rectangle2D.Float string");
            Rectangle2D.Float r = new Rectangle2D.Float();
            r.setFrame(xywh.get(0), xywh.get(1), xywh.get(2), xywh.get(3));
            return r;
        }
    }

    public static class StringToRectangle2D_Double extends ResourceConverter<String, Rectangle2D.Double>
    {
        public StringToRectangle2D_Double()
        {
            super(String.class, Rectangle2D.Double.class);
        }

        public Rectangle2D.Double convert(@NotNull String source, Object... args) throws StringConvertException
        {
            assertNotNull(source, String.class, "source");
            List<Double> xywh = parseDoubles(source, 4, "Invalid x,y,width,height Rectangle2D.Double string");
            Rectangle2D.Double r = new Rectangle2D.Double();
            r.setFrame(xywh.get(0), xywh.get(1), xywh.get(2), xywh.get(3));
            return r;
        }
    }
}
