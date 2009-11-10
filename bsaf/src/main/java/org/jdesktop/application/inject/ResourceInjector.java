package org.jdesktop.application.inject;

import org.jdesktop.application.ResourceMap;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.logging.Logger;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.awt.*;


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

    abstract public T inject(@NotNull T target, @NotNull ResourceMap properties, boolean recursively);

    public Class<T> getTargetType()
    {
        return targetType;
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
                ResourceMap.InjectFieldException ife = new ResourceMap.InjectFieldException(msg, null, target, null);
                ife.initCause(e);
                throw ife;
            }
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            if ((pds != null) && (pds.length > 0))
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
                        for (PropertyDescriptor pd : pds)
                        {
                            if (pd.getName().equals(propertyName))
                            {
                                injectProperty(target, pd, key, properties);
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

    protected void injectProperty(Object target, PropertyDescriptor pd, String key, ResourceMap properties)
    {
        Method setter = pd.getWriteMethod();
        Class<?> type = pd.getPropertyType();
        if ((setter != null) && (type != null) && properties.containsKey(key))
        {
            Object value = properties.getResourceAs(key, type, null);
            try
            {
                setter.invoke(target, value);
            }
            catch (Exception e)
            {
                String pdn = pd.getName();
                String msg = "property setter failed";
                ResourceMap.InjectFieldException ife = new ResourceMap.InjectFieldException(msg, setter, target, key);
                ife.initCause(e);
                throw ife;
            }
        }
        else if (type != null)
        {
            String pdn = pd.getName();
            String msg = "no value specified for resource";
            throw new ResourceMap.InjectFieldException(msg, setter, target, key);
        }
        else if (setter == null)
        {
            String pdn = pd.getName();
            String msg = "can't set read-only property";
            throw new ResourceMap.InjectFieldException(msg, setter, target, key);
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
