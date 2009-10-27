package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;import java.net.URI;
import java.net.URISyntaxException;


/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  8:37:19 PM
 */
public class StringToURI extends ResourceConverter<String, URI>
{
    public StringToURI()
    {
        super(String.class, URI.class);
    }

    public URI convert(@NotNull String source, Object... args) throws StringConvertException
    {
        String trimmed = source.trim();
        try
        {
            URI temp = new URI(trimmed);
            if (temp.getScheme() == null)
            {
                temp = new URI("http://"+trimmed);
            }
            return temp;
        }
        catch (URISyntaxException e)
        {
            throw new StringConvertException("Invalid URI", source, e);
        }
    }
}
