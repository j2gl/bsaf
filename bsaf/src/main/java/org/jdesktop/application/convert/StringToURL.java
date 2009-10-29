package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;import java.net.URL;
import java.net.URI;


/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  8:37:08 PM
 */
public class StringToURL extends ResourceConverter<String, URL>
{
    public StringToURL()
    {
        super(String.class, URL.class);
    }

    public URL convert(@NotNull String source, Object... args) throws StringConvertException
    {
        assertNotNull(source, String.class, "source");
        String trimmed = source.trim();
        try
        {
            URI temp = new URI(trimmed);
            if (temp.getScheme() ==  null)
            {
                //no scheme, i.e., protocol, included in string, so default to http
                temp = new URI("http://"+ trimmed);
            }
            return temp.toURL();
        }
        catch (Exception e)
        {
            throw new StringConvertException("Invalid URL", source, e);
        }
    }
}
