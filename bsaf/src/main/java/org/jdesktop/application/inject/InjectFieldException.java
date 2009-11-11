package org.jdesktop.application.inject;


import java.lang.reflect.AccessibleObject;

/**
     * Unchecked exception thrown by {@link #injectFields} when
 * an error occurs while attempting to set a field (a field that
 * had been marked with <tt>&#064;Resource</tt>).
 *
 * @see #injectFields
 */
@SuppressWarnings({"SerializableHasSerializationMethods"})
public class InjectFieldException extends RuntimeException
{
    private static final long serialVersionUID = 1;

    private final AccessibleObject fieldOrMethod;
    private final Object target;
    private final String key;


    /**
     * Constructs an instance of this class with some useful information
     * about the failure.
     *
     * @param msg    the detail message
     * @param field  the Field we were attempting to set
     * @param target the object whose field we were attempting to set
     * @param key    the name of the resource
     */
    public InjectFieldException(String msg, AccessibleObject field, Object target, String key)
    {
        super(String.format("%s: resource %s, field %s, target %s", msg, key, field, target));
        this.fieldOrMethod = field;
        this.target = target;
        this.key = key;
    }

    /**
     * Return the Field whose value couldn't be set.
     *
     * @return the field whose value couldn't be set
     */
    public AccessibleObject getFieldOrMethod()
    {
        return fieldOrMethod;
    }

    /**
     * Return the Object whose Field we were attempting to set
     *
     * @return the Object whose Field we were attempting to set
     */
    public Object getTarget()
    {
        return target;
    }

    /**
     * Returns the type of the name of resource for which lookup failed.
     *
     * @return the resource name
     */
    public String getKey()
    {
        return key;
    }
}
