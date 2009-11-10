package org.jdesktop.application.inject;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 * <p/>
 * Manages a group of ResourceInjectors and provides services for locating an injector to use for a particular type of object.
 * <p/>
 * It is similar in concept to the ConverterRegistry. One important difference here is that an injector returned may be for a
 * supertype of the target object type. For example, when locating an injector for a JButton instance, an AbstractButtonResourceInjector
 * may be returned if there is no specific JButtonResourceInjector in the registry. In cases where injectors exist for multiple supertypes in the type hierarchy
 * of an object, the most specific injector is returned. For example, there is a ComponentResourceInjector, and an AbstractButtonInjector. If
 * the caller wishes to locate a resource injector for a JButton, both of these injectors would work to inject resources into the JButton, since a JButton
 * is both an AbstractButton and a Component. However, the AbstractButtonResourceInjector is more specific than the ComponentResourceInjector, so the former
 * is returned  in this case.
 * <p/>
 * As with ConverterRegistry, you may create your own injectors subclassed from existing concrete injectors, or from AbstractResourceInjector itself.
 * <p/>
 * To use, call injectorFor, passing the instance to be injected.
 *
 * @author Rob Ross
 * @version Date: Nov 5, 2009  10:37:28 PM
 */
public class InjectorRegistry
{

/*

    notes - if we were going to look for exact matches like we do in ConverterRegistry, we could use a simple Map<Class<?>, ResourceInjector>.
    But, we need a way of not only looking for exact matches, but traversing the Object's family tree
    First, look for an injector for the object's specific type. If found, we're done.
    If not found, look for injectors for the object's supertype. Repeat until an injector is found, or the current type has no super-type, in which
    case return a default injector for an Object.
    As an optimization, we can keep a cache map of previous lookups, so we only have to navigate the supertype hierachy once, and on subsequent
    requests, the ResourceInjector to use for a type can be found by a simple map lookup.

    We'll have to invalidate the cache when we add or remove Injectors.

*/

    public <T> void add(@NotNull ResourceInjector<T> injector)
    {
        assertNotNull(injector, ResourceInjector.class, "injector");

        Class<T> targetType = injector.getTargetType();
        lookupCache.clear(); //the injector may be a different instance for the same targetType, or an injector for an existing
        //key's supertype. Taking a conservative approach here and just rebuild the cache lazily
        registry.put(targetType, injector);
        lookupCache.put(targetType, injector);
        injector.setRegistry(this);
    }

    public void addAll(@NotNull List<ResourceInjector<?>> injectorList)
    {
        assertNotNull(injectorList, List.class, "injectorList");

        for (ResourceInjector<?> injector : injectorList)
        {
            add(injector);
        }
    }

    /**
     * Remove from this InjectorRegistry the ResourceInjectorS in the argument. If the injector is not registered, this
     * method returns false, otherwise it removes the injector and returns true.
     *
     * @param injector the ResourceInjector to remove
     * @return true if the injector was present in the registry and successfully removed. If it was not already registered,
     *         returns false
     */
    synchronized public boolean remove(@NotNull ResourceInjector injector)
    {
        assertNotNull(injector, ResourceInjector.class, "injector");
        if (registry.containsKey(injector.getTargetType()) )
        {
            injector.setRegistry(null);
            registry.remove(injector.getTargetType());
            //removal of one injector could result in may supertypes in the lookupCache being removed as well.
            //It is simpler to just clear it, and rebuild the cache again lazily
            lookupCache.clear();
            return true;
        }
        return false;
    }

    public <T> ResourceInjector<T> injectorFor(T instance)
    {
        Class<?> c = instance.getClass();
        return (ResourceInjector<T>) injectorFor((Class<T>)c);
    }

    public <T> ResourceInjector<T> injectorFor(@NotNull Class<T> targetType)
    {
        //first look for an exact match on the targetType
        if (registry.containsKey(targetType))
        {
            return (ResourceInjector<T>) registry.get(targetType);
        }

        //is the targetType present in the cache?
        if (lookupCache.containsKey(targetType))
        {
            //cache hit!
            //todo - remove println
            //System.out.println(String.format("cache hit in injectorFor : %s",lookupCache.get(targetType)));
            return (ResourceInjector<T>) lookupCache.get(targetType);
        }

        //walk up the type hierarchy, checking each type to see if it's present in the registry. If we find a match,
        //we'll add this mapping to the lookupCache and return the injector
        if (targetType.getSuperclass() != null)
        {
            Class node = targetType.getSuperclass();
            while (node != null)
            {
                if (registry.containsKey(node))
                {
                    //save this mapping in the lookupCache
                    lookupCache.put(node, registry.get(node));
                    //System.out.println(String.format("returning injectorFor : %s", registry.get(node)));
                    return (ResourceInjector<T>) registry.get(node);
                }
                node = node.getSuperclass();
            }
        }

        //at this point there was no injector found, so return default ObjectInjector
        return null;
    }

    /**
     * @return all the injectors registered in this instance
     */
    @NotNull
    public List<ResourceInjector> allInjectors()
    {
        return new ArrayList<ResourceInjector>(registry.values());
    }

    public void addDefaultInjectors()
    {
        ResourceInjector<?>[] defaults = new ResourceInjector[]{
                new DefaultInjector(),
                new ComponentInjector(),
                new AbstractButtonInjector(),
                new ActionInjector(),
                new JLabelInjector(),
                new JMenuInjector(),
        };
        addAll(Arrays.asList(defaults));
        
    }



    private final Map<Class<?>, ResourceInjector<?>> registry = new ConcurrentHashMap<Class<?>, ResourceInjector<?>>(16, .75f, 4);
    private final Map<Class<?>, ResourceInjector<?>> lookupCache = new ConcurrentHashMap<Class<?>, ResourceInjector<?>>(256, .75f, 4);


    protected void assertNotNull(Object o, Class type, String paramName)
    {
        if (o == null)
        {
            throw new IllegalArgumentException(String.format("parameter '%s' of type '%s' cannot be null.", paramName, type));
        }
    }

}
