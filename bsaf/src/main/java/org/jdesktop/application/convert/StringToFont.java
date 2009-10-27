package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;import java.awt.*;

/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  8:45:47 PM
 */
public class StringToFont extends ResourceConverter<String, Font>
{

    public StringToFont()
    {
        super(String.class, Font.class);
    }

    /* Just delegates to Font.decode.
    * Typical string is: face-STYLE-size, for example "Arial-PLAIN-12"
    */
    public Font convert(@NotNull String source, Object... args) throws StringConvertException
    {
        return Font.decode(source);
    }
}
