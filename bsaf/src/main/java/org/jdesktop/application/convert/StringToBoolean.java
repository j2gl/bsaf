package org.jdesktop.application.convert;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;


/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  7:12:50 PM
 */
public class StringToBoolean extends ResourceConverter<String, Boolean>
{
    private static final Set<String> DEFAULT_TRUE_VALUES =
            new HashSet<String>(Arrays.asList("true", "t", "yes", "y", "on", "boolean.true"));
    private final Set<String> trueValues = new HashSet<String>();

    public StringToBoolean(String... trueValues)
    {
        super(String.class, Boolean.class);
        this.trueValues.addAll(Arrays.asList(trueValues));
    }

    public StringToBoolean()
    {
        super(String.class, Boolean.class);
        this.trueValues.addAll(DEFAULT_TRUE_VALUES);
    }

    /**
     * @return a copy of the internal Set of String values that are interpreted as meaing "true". The default values
     *         used by the default constructor are "true", "t", "yes", "y", "on", "boolean.true" but these can be customized by using the constructor
     *         that takes a var-arg list of trueValue Strings, or by calling addTrueValue to add a new value to the set of Strings
     *         that are interpreted as meaing "true". It is recommended you be consistent in your use of boolean String values
     *         in resources, and generally just use either "true" or "false"
     */
    @NotNull
    public Set<String> getTrueValuesSet()
    {
        synchronized (trueValues)
        {
            return new HashSet<String>(trueValues);
        }
    }

    public void addTrueValue(@NotNull String trueValue)
    {
        synchronized (trueValues)
        {
            trueValues.add(trueValue);
        }
    }

    /**
     * @param trueValue String value in trueValue Set that is interpreted as "true". After calling this, resources with this
     *                  value will no longer be interpreted as being "true"
     * @return <tt>true</tt> if this set contained the specified element
     */
    public boolean removeTrueValue(@NotNull String trueValue)
    {
        synchronized (trueValues)
        {
            if (!trueValues.contains(trueValue))
            {
                return false;
            }
            return trueValues.remove(trueValue);
        }
    }

    @NotNull
    public Boolean convert(@NotNull String source, Object... args)
    {
        if (source == null)
        {
            return Boolean.FALSE;
        }
        synchronized(trueValues)
        {
            if (trueValues.contains(source.trim().toLowerCase()))
            {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }
}
