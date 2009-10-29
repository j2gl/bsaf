package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;


/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  9:24:10 PM
 */
public class StringToPoint extends ResourceConverter<String, Point>
{

    public StringToPoint()
    {
        super(String.class, Point.class);
    }

    public Point convert(@NotNull String source, Object... args) throws StringConvertException
    {
        assertNotNull(source, String.class, "source");
        List<Double> xy = parseDoubles(source, 2, "Invalid x,y Point string");
        Point p = new Point();
        p.setLocation(xy.get(0), xy.get(1));
        return p;
    }

    public static class StringToPoint2D_Float extends ResourceConverter<String, Point2D.Float>
    {
        public StringToPoint2D_Float()
        {
            super(String.class, Point2D.Float.class);
        }

        public Point2D.Float convert(@NotNull String source, Object... args) throws StringConvertException
        {
            assertNotNull(source, String.class, "source");
            List<Double> xy = parseDoubles(source, 2, "Invalid x,y Point2D.Float string");
            Point2D.Float p = new Point2D.Float();
            p.setLocation(xy.get(0), xy.get(1));
            return p;
        }
    }

    public static class StringToPoint2D_Double extends ResourceConverter<String, Point2D.Double>
    {
        public StringToPoint2D_Double()
        {
            super(String.class, Point2D.Double.class);
        }

        public Point2D.Double convert(@NotNull String source, Object... args) throws StringConvertException
        {
            assertNotNull(source, String.class, "source");
            List<Double> xy = parseDoubles(source, 2, "Invalid x,y Point2D.Double string");
            Point2D.Double p = new Point2D.Double();
            p.setLocation(xy.get(0), xy.get(1));
            return p;
        }
    }
}
