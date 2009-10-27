package org.jdesktop.application.convert;

/**
 *   @author Hans Muller
 */
public class StringConvertException extends Exception
{
    private final String badString;

    private String maybeShorten(String s)
    {
        int n = s.length();
        return (n < 128) ? s : s.substring(0, 128) + "...[" + (n - 128) + " more characters]";
    }

    public StringConvertException(String message, String badString, Throwable cause)
    {
        super(message, cause);
        this.badString = maybeShorten(badString);
    }

    public StringConvertException(String message, String badString)
    {
        super(message);
        this.badString = maybeShorten(badString);
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(super.toString());
        sb.append(" string: \"");
        sb.append(badString);
        sb.append("\"");
        return sb.toString();
    }
}
