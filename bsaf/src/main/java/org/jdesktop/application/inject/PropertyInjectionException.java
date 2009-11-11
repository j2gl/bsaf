package org.jdesktop.application.inject;


import java.awt.*;

/**
 * Unchecked exception thrown by {@link ComponentInjector#inject}
 * when a property value specified by
 * a resource can not be set.
 *
 * @see ResourceInjector#inject
 */
public class PropertyInjectionException extends RuntimeException
{
    private static final long serialVersionUID = 1;

    private final String key;
    private final Component component;
    private final String propertyName;


    /**
     * Constructs an instance of this class with some useful information
     * about the failure.
     *
     * @param msg          the detail message
     * @param key          the name of the resource
     * @param component    the component whose property couldn't be set
     * @param propertyName the name of the component property
     */
    public PropertyInjectionException(String msg, String key, Component component, String propertyName)
    {
        super(String.format("%s: resource %s, property %s, component %s", msg, key, propertyName, component));
        this.key = key;
        this.component = component;
        this.propertyName = propertyName;
    }

    /**
     * Returns the the name of resource whose value was to be used to set the property
     *
     * @return the resource name
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Returns the component whose property could not be set
     *
     * @return the component
     */
    public Component getComponent()
    {
        return component;
    }

    /**
     * Returns the the name of property that could not be set
     *
     * @return the property name
     */
    public String getPropertyName()
    {
        return propertyName;
    }
}
