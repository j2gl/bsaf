package org.jdesktop.application.inject;

import org.jdesktop.application.ResourceMap;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;
import java.beans.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * @author Rob Ross
 * @version Date: Nov 5, 2009  1:11:40 AM
 */
abstract public class ResourceInjector<T>
{
    private static final Logger logger = Logger.getLogger("ResourceInjector");

    private final Class<T> targetType; //the type of Object for which this injector is designed to work. Also works for subclasses of this type

    private InjectorRegistry registry; //the registry this injector is a part of

    protected ResourceInjector(Class<T> targetType)
    {
        this.targetType = targetType;
    }

    void setRegistry(InjectorRegistry registry)
    {
        this.registry = registry;
    }

    public InjectorRegistry getInjectorRegistry()
    {
        return registry;
    }

    abstract public T inject(@NotNull T target, @NotNull ResourceMap properties, boolean recursively) throws PropertyInjectionException;

    public Class<T> getTargetType()
    {
        return targetType;
    }

    protected static class PropertySetter
    {
        String propName;
        MethodDescriptor methodDescr;
        Class type;

        private PropertySetter(String propName, MethodDescriptor methodDescr, Class type)
        {
            this.propName = propName;
            this.methodDescr = methodDescr;
            this.type = type;
        }
    }

    private List<MethodDescriptor> findDuplicateMethodNames(String methodName, List<MethodDescriptor> mdl)
    {
        List<MethodDescriptor> dupes = new ArrayList<MethodDescriptor>();
        for (MethodDescriptor md : mdl)
        {
            if (md.getMethod().getName().equals(methodName))
            {
                dupes.add(md);
            }
        }
        return dupes;
    }

    //create a single property setter from the different versions of the same setter that takes different arguments.
    //we want to find the setter that takes a single argument. Also, if there are multiple versions with an arg that takes char,
    //and arg that takes int, favor int
    private PropertySetter createPropertySetter(List<MethodDescriptor> mdl)
    {
        if (mdl.size() == 0)
        {
            return null;
        }
        String name = mdl.get(0).getName();
        List<PropertySetter> candidateMethods = new ArrayList<PropertySetter>();
        for (MethodDescriptor md : mdl)
        {
            if (!md.getName().equals(name))
            {
                throw new IllegalArgumentException(String.format("The MethodDescriptor list must contain methods with the same name, %s != %s",md.getName(), name));
            }
            Class<?>[] types = md.getMethod().getParameterTypes();
            if (types.length == 1 ) 
            {
                //this is a single parameter argument
                candidateMethods.add(new PropertySetter(propertyNameFromeSetter(md.getName()), md, types[0]));
            }
        }

        if (candidateMethods.isEmpty())
        {
            return null;
        }
        if (candidateMethods.size() == 1)
        {
            return candidateMethods.get(0);
        }
        //we're just testing a specific situation : are there multiple one-arg setters, with one version taking char, and one taking int?
        //if so, favor the int argument. Otherwise favor an int argument. Otherwise, just pick the first one. We can further refine this method later
        boolean hasCharArg = false;
        boolean hasIntArg = false;
        for (PropertySetter ps : candidateMethods)
        {
            if (ps.type == Character.TYPE)
            {
                hasCharArg = true;
            }
            if (ps.type == Integer.TYPE)
            {
                hasIntArg = true;
            }
        }

        if (hasIntArg) //always favor the int argument if there are multiple methods
        {
            //find the int arg method and return it.
            for (PropertySetter ps : candidateMethods)
            {
                if (ps.type == Integer.TYPE)
                {
                    return ps;
                }
            }
        }

        //no int arg, just return the first one
        return candidateMethods.get(0);



    }

    String propertyNameFromeSetter(String setterName)
    {
        StringBuilder propName = new StringBuilder(setterName.substring(3, setterName.length()));
        propName.setCharAt(0, Character.toLowerCase(propName.charAt(0)));
        return propName.toString();
    }


    protected List<PropertySetter> findPublicSetters(List<MethodDescriptor> mdl)
    {
        Set<MethodDescriptor> processedSet = new HashSet<MethodDescriptor>(mdl.size()); //contains MD that have been processed
        List<PropertySetter> psl = new ArrayList<PropertySetter>(mdl.size());
        for (MethodDescriptor md : mdl)
        {
            if (!processedSet.contains(md) && Modifier.isPublic(md.getMethod().getModifiers()))
            {
                if (md.getMethod().getName().startsWith("set"))
                {
                    //there may be more than one setter with the same name. We want to find the one with a single argument.
                    List<MethodDescriptor> dupes = findDuplicateMethodNames(md.getName(),mdl);
                    if (dupes.size() > 0)
                    {
                        PropertySetter ps = createPropertySetter(dupes);
                        if (ps != null)
                        {
                            psl.add(ps);
                        }
                        processedSet.addAll(dupes); //mark these all as processed
                    }
                }
            }
        }
        return psl;
    }

    protected void injectProperties(Object target, String targetName, ResourceMap properties)
    {
        if (targetName != null)
        {
            //todo - remove println
            //System.out.println(String.format("targetName = %s", targetName));
            /* Optimization: punt early if targetName doesn't
            * appear in any targetName.propertyName resource keys
            */
            boolean matchingResourceFound = false;
            Set<String> keySet = properties.keySet();
            for (String key : keySet)
            {
                int i = key.lastIndexOf(".");
                if ((i != -1) && targetName.equals(key.substring(0, i)))
                {
                    matchingResourceFound = true;
                    break;
                }
            }
            if (!matchingResourceFound)
            {
                return;
            }
            BeanInfo beanInfo = null;
            try
            {
                beanInfo = Introspector.getBeanInfo(target.getClass());
            }
            catch (IntrospectionException e)
            {
                String msg = "introspection failed";
                InjectFieldException ife = new InjectFieldException(msg, null, target, null);
                ife.initCause(e);
                throw ife;
            }

            List<PropertySetter> psl = findPublicSetters(Arrays.<MethodDescriptor>asList(beanInfo.getMethodDescriptors()));

/*            //todo - remove println
            System.out.println(String.format("METHOD DESCRIPTORS: (%s) items",psl.size()));
            for (PropertySetter ps : psl)
            {
                System.out.println(String.format("name=%s, writeMethodName=%s, argType=%s", ps.propName, ps.methodDescr.getName(), ps.type.getSimpleName()));
            }*/


            if ((psl != null) && (psl.size() > 0))
            {
                for (String key : keySet)
                {
                    int i = key.lastIndexOf(".");
                    String keyComponentName = (i == -1) ? null : key.substring(0, i);
                    if (targetName.equals(keyComponentName))
                    {
                        if ((i + 1) == key.length())
                        {
                            /* key has no property name suffix, e.g. "myComponentName."
                        * This is probably a mistake.
                        */
                            String msg = "target resource lacks property name suffix";
                            logger.warning(msg);
                            break;
                        }
                        String propertyName = key.substring(i + 1);
                        boolean matchingPropertyFound = false;
                        for (PropertySetter ps : psl)
                        {
                            if (ps.propName.equals(propertyName))
                            {
                                injectProperty(target, ps, key, properties);
                                matchingPropertyFound = true;
                                break;
                            }
                        }
                        if (!matchingPropertyFound)
                        {
                            String msg = String.format(
                                    "[resource %s] target named %s doesn't have a property named %s",
                                    key, targetName, propertyName);
                            logger.warning(msg);
                        }
                    }
                }
            }
        }
    }

    protected void injectProperty(Object target, PropertySetter ps, String key, ResourceMap properties)
    {
        Method setter = ps.methodDescr.getMethod();
        Class<?> type = ps.type;
        if ((setter != null) && (type != null) && properties.containsKey(key))
        {
            Object value = properties.getResourceAs(key, type, null);
            try
            {
                setter.invoke(target, value);
            }
            catch (Exception e)
            {
                String pdn = ps.propName;
                String msg = "property setter failed";
                InjectFieldException ife = new InjectFieldException(msg, setter, target, key);
                ife.initCause(e);
                throw ife;
            }
        }
        else if (type != null)
        {
            String pdn = ps.propName;
            String msg = "no value specified for resource";
            throw new InjectFieldException(msg, setter, target, key);
        }
        else if (setter == null)
        {
            String pdn = ps.propName;
            String msg = "can't set read-only property";
            throw new InjectFieldException(msg, setter, target, key);
        }
    }

    protected String getTargetName(Object target)
    {
        String name = "";
        if (target instanceof Component)
        {
            name = ((Component) target).getName();
            if (name == null || name.isEmpty())
            {
                name = target.getClass().getSimpleName();
            }
        }
        else
        {
            name = target.getClass().getSimpleName();
        }
        return name;
    }

    
    protected void assertNotNull(Object o, Class type, String paramName)
    {
        if (o == null)
        {
            throw new IllegalArgumentException(String.format("parameter '%s' of type '%s' cannot be null.", paramName, type));
        }
    }
}
