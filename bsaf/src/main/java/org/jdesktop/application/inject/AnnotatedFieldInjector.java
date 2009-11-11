package org.jdesktop.application.inject;

import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import java.lang.reflect.*;
import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * Injects resource properties into an Object where the injectable fields have been annotated. The default annotation is
 * \@Resource, but the annotation type can be specified in one of the overloaded #injectField methods.
 *
 * @author Rob Ross
 * @version Date: Nov 11, 2009  1:40:33 AM
 */
public class AnnotatedFieldInjector
{
    private static final Logger logger = Logger.getLogger("AnnotatedFieldInjector.class");

    /**
     * Set each field with a <tt>&#064;Resource</tt> annotation in the target object,
     * to the value of a resource whose name is the simple name of the target
     * class followed by "." followed by the name of the field.  If the
     * key <tt>&#064;Resource</tt> parameter is specified, then a resource with that name
     * is used instead.  Array valued fields can also be initialized
     * with resources whose names end with "[index]".  For example:
     * <pre>
     * class MyClass {
     *   &#064;Resource String sOne;
     *   &#064;Resource(key="sTwo") String s2;
     *   &#064;Resource int[] numbers = new int[2];
     * }
     * </pre>
     * Given the previous class and the following resource file:
     * <pre>
     * MyClass.sOne = One
     * sTwo = Two
     * MyClass.numbers[0] = 10
     * MyClass.numbers[1] = 11
     * </pre>
     * Then <tt>injectFields(new MyClass())</tt> would initialize the MyClass
     * <tt>sOne</tt> field to "One", the <tt>s2</tt> field to "Two", and the
     * two elements of the numbers array to 10 and 11.
     * <p/>
     * If <tt>target</tt> is null an IllegalArgumentException is
     * thrown.  If an error occurs during resource lookup, then an
     * unchecked LookupException is thrown.  If a target field marked
     * with <tt>&#064;Resource</tt> can't be set, then an unchecked
     * InjectFieldException is thrown.
     *
     * @param target the object whose fields will be initialized
     * @throws LookupException          if an error occurs during lookup or string conversion
     * @throws InjectFieldException     if a field can't be set
     * @throws IllegalArgumentException if target is null
     * @see #getObject
     */
    public void injectFields(Object target, ResourceMap resourceMap)
    {
        injectFields_impl(target, resourceMap, target.getClass().getSimpleName(), Resource.class);
    }

    public void injectFields(Object target, ResourceMap resourceMap, String resourcePrefix)
    {
        injectFields_impl(target, resourceMap,  resourcePrefix, Resource.class);
    }

    public void injectFields(Object target, ResourceMap resourceMap, String resourcePrefix, Class<? extends Annotation> annotationType)
    {
        injectFields_impl(target, resourceMap, resourcePrefix, annotationType);
    }


    private void injectFields_impl(Object target, ResourceMap resourceMap, String resourcePrefix, Class<? extends Annotation> annotationType)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("null target");
        }
        Class targetType = target.getClass();
        if (targetType.isArray())
        {
            throw new IllegalArgumentException("array target");
        }
        String keyPrefix = targetType.getSimpleName() + ".";
        for (Field field : targetType.getDeclaredFields())
        {
            Annotation annotation = field.getAnnotation(annotationType);
            if (annotation != null)
            {
                String rKey = getValueFromAnnotationProperty(annotation, "key", String.class);
                String key = (rKey.length() > 0) ? rKey : keyPrefix + field.getName();
                injectField(field, target, key, resourceMap);
            }
        }
        logger.log(Level.FINE, String.format("injectFields called for %s", target));
        for (Method method : targetType.getMethods())
        {
            String methodName = method.getName();
            if (methodName.startsWith("set") && method.getParameterTypes().length == 1 && methodName.length() > 3)
            {
                //this is a setter method
                StringBuilder propName = new StringBuilder(methodName.substring(3));
                propName.setCharAt(0, Character.toLowerCase(propName.charAt(0)));

                Annotation annotation = method.getAnnotation(annotationType);
                if (annotation != null)
                {
                    String rKey = getValueFromAnnotationProperty(annotation, "key", String.class);
                    String key = (rKey.length() > 0) ? rKey : keyPrefix + propName;
                    injectMethod(method, target, key, resourceMap);
                }

            }
        }
    }

    //Try to extract the value from a property named propertyName in the argument annotation. If there is no such property
    //in the annotation, or the value is null, this method returns the empty String.
    protected String getValueFromAnnotationProperty(Annotation annotation, String propertyName, Class<?> propertyType)
    {

        Method m = null;
        try
        {
            m = annotation.getClass().getMethod(propertyName);
            if (propertyType == m.getReturnType())
            {
                return (String) m.invoke(annotation);
            }
        }
        catch (NoSuchMethodException e)
        {
            //no property with this name, so we'll return the empty string from this method
        }
        catch (IllegalAccessException e)
        {
            try
            {
                m.setAccessible(true);
                return (String) m.invoke(annotation);
            }
            catch (Exception ignore)
            {
            }
        }
        catch (InvocationTargetException e)
        {
            //don't care, return empty string
        }
        catch (Exception e)
        {
            //don't care, return empty string
        }

        return "";
    }


    private void injectField(Field field, Object target, String key, ResourceMap resourceMap)
    {
        Class<?> type = field.getType();
        if (type.isArray())
        {
            type = type.getComponentType();
            Pattern p = Pattern.compile(key + "\\[([\\d]+)\\]");  // matches key[12]
            List<String> arrayKeys = new ArrayList<String>();
            for (String arrayElementKey : resourceMap.keySet())
            {
                Matcher m = p.matcher(arrayElementKey);
                if (m.matches())
                {
                    /* field's value is an array, arrayElementKey is a resource
                  * name of the form "MyClass.myArray[12]" and m.group(1)
                  * matches the array index.  Set the index element
                  * of the field's array to the value of the resource.
                  */
                    Object value = resourceMap.getResourceAs(arrayElementKey, type, null);
                    if (!field.isAccessible())
                    {
                        field.setAccessible(true);
                    }
                    try
                    {
                        int index = Integer.parseInt(m.group(1));
                        Array.set(field.get(target), index, value);
                    }
                    /* Array.set throws IllegalArgumentException, ArrayIndexOutOfBoundsException
                  * field.get throws IllegalAccessException(Checked), IllegalArgumentException
                  * Integer.parseInt throws NumberFormatException (Checked)
                  */
                    catch (Exception e)
                    {
                        String msg = "unable to set array element";
                        InjectFieldException ife = new InjectFieldException(msg, field, target, key);
                        ife.initCause(e);
                        throw ife;
                    }
                }
            }
        }
        else
        {  // field is not an array
            Object value = resourceMap.getResourceAs(key, type, null);
            if (value != null)
            {
                if (!field.isAccessible())
                {
                    field.setAccessible(true);
                }
                try
                {
                    field.set(target, value);
                }
                /* Field.set throws IllegalAccessException, IllegalArgumentException,
             * ExceptionInInitializerError
             */
                catch (Exception e)
                {
                    String msg = "unable to set field's value";
                    InjectFieldException ife = new InjectFieldException(msg, field, target, key);
                    ife.initCause(e);
                    throw ife;
                }
            }
        }
    }

    private void injectMethod(Method method, Object target, String key, ResourceMap resourceMap)
    {
        Class<?> paramType = method.getParameterTypes()[0];
        logger.log(Level.FINE, String.format("method : %s : injecting %s of type %s into %s", method, key, paramType, target));
        Object value = resourceMap.getResourceAs(key, paramType, null);
        if (value != null)
        {
            if (!method.isAccessible())
            {
                method.setAccessible(true);
            }
            try
            {
                method.invoke(target, value);
            }
            /* Method.invoke throws IllegalAccessException, InvocationTargetException,
            */
            catch (Exception e)
            {
                String msg = "unable to call setter method";
                InjectFieldException ife = new InjectFieldException(msg, method, target, key);
                ife.initCause(e);
                throw ife;
            }
        }

    }
}
