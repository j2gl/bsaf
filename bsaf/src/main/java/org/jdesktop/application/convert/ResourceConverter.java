package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;


/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  5:34:30 PM
 */
abstract public class ResourceConverter<S, D>
{
    private final Class<S> sourceClass;
    private final Class<D> destClass;

    protected ResourceConverter(Class<S> sourceClass, Class<D> destClass)
    {
        this.sourceClass = sourceClass;
        this.destClass = destClass;
    }

    public Class<S> getSourceClass()
    {
        return sourceClass;
    }

    public Class<D> getDestClass()
    {
        return destClass;
    }


    abstract public D convert(@NotNull S source, Object... args) throws StringConvertException;


    public String toString(Object obj)
    {
        return (obj == null) ? "null" : obj.toString();
    }


    /* String s is assumed to contain n number substrings separated by
    * commas.  Return a list of those integers or null if there are too
    * many, too few, or if a substring can't be parsed.  The format
    * of the numbers is specified by Double.valueOf().
    */
    protected List<Double> parseDoubles(String s, int n, String errorMsg) throws StringConvertException
    {
        String[] doubleStrings = s.split(",", n + 1);
        if (doubleStrings.length != n)
        {
            throw new StringConvertException(errorMsg, s);
        }
        else
        {
            List<Double> doubles = new ArrayList<Double>(n);
            for (String doubleString : doubleStrings)
            {
                try
                {
                    doubles.add(Double.valueOf(doubleString));
                }
                catch (NumberFormatException e)
                {
                    throw new StringConvertException(errorMsg, s, e);
                }
            }
            return doubles;
        }
    }
}
