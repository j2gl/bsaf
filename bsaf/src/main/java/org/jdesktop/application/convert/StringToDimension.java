package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;
import java.awt.*;


/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  9:17:57 PM
 */
public class StringToDimension extends ResourceConverter<String, Dimension>
{

    public StringToDimension()
    {
        super(String.class, Dimension.class);
    }

    public Dimension convert(@NotNull String source, Object... args) throws StringConvertException
    {
        assertNotNull(source, String.class, "source");
        java.util.List<Double> xy = parseDoubles(source, 2, "Invalid x,y Dimension string");
        Dimension d = new Dimension();
        d.setSize(xy.get(0), xy.get(1));
        return d;
    }

    @Override
    public Dimension copy(Dimension source)
    {
        assertNotNull(source, Dimension.class, "source");
        return new Dimension(source);
    }
}
