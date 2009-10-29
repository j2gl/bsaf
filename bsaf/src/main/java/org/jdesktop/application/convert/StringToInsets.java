package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;import java.awt.*;
import java.util.List;


/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  9:29:16 PM
 */
public class StringToInsets extends ResourceConverter<String, Insets>
{
    public StringToInsets()
    {
        super(String.class, Insets.class);
    }

    public Insets convert(@NotNull String source, Object... args) throws StringConvertException
    {
        assertNotNull(source, String.class, "source");
        List<Double> tlbr = parseDoubles(source, 4, "Invalid top,left,bottom,right Insets string");
        return new Insets(tlbr.get(0).intValue(), tlbr.get(1).intValue(), tlbr.get(2).intValue(), tlbr.get(3).intValue());
    }
}
