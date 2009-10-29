package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;import javax.swing.border.EmptyBorder;
import java.util.List;


/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  9:31:33 PM
 */
public class StringToEmptyBorder extends ResourceConverter<String, EmptyBorder>
{
    public StringToEmptyBorder()
    {
        super(String.class, EmptyBorder.class);
    }

    public EmptyBorder convert(@NotNull String source, Object... args) throws StringConvertException
    {
        assertNotNull(source, String.class, "source");
        List<Double> tlbr = parseDoubles(source, 4, "invalid top,left,bottom,right EmptyBorder string");
        return new EmptyBorder(tlbr.get(0).intValue(), tlbr.get(1).intValue(), tlbr.get(2).intValue(), tlbr.get(3).intValue());
    }

}
