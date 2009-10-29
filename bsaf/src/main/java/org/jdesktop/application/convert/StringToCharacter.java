package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;


/**
 * @author Rob Ross
 * @version Date: Oct 10, 2009  12:42:59 AM
 */
public class StringToCharacter  extends ResourceConverter<String, Character>
{
    public StringToCharacter()
    {
        super(String.class, Character.class);
    }

    /**
     *
     * @param source must not be null nor empty.
     * @param args
     * @return the first character of the String argument as a Character object
     * @throws StringConvertException if source is empty
     * @throws IllegalArgumentException if source is null
     */
    public Character convert(@NotNull String source, Object... args) throws StringConvertException, IllegalArgumentException
    {
        assertNotNull(source, String.class, "source");
        Character c = null;

        try
        {
            c = source.trim().charAt(0);
        }
        catch (Exception e)
        {
            String message = "Could not convert String argument to Character";
            StringConvertException sce = new StringConvertException(message,source,e);
            throw sce;
        }
        return c;
    }
}
