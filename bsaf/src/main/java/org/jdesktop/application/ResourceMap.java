package org.jdesktop.application;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jdesktop.application.convert.StringConvertException;
import org.jdesktop.application.convert.ConverterRegistry;
import org.jdesktop.application.convert.ResourceConverter;
import org.jdesktop.application.inject.InjectorRegistry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
   todo - property change notification? These properties are defacto read-only, but perhaps there are use-cases for mutating them once loaded?
          if so, how do we handle reloading of bundles when properties have been changed? Those changes would be lost unless we keep track of them in a special way
   todo - perhaps it is sufficient to just notify when properties have been reloaded, so listeners can get updated values and refresh components, etc.
          also, perhaps we automatically manage this for any Actions created from values in these property bundles, then all menu and button text
          implemented with these Actions would instantly be updated.
   todo - do we allow new bundles to be added to this ResourceMap? if so, and a key in the new bundle already exists, do we overrite with new values,
          or only add keys that are not in the cache (add to top or bottom of list of bundles names).
          do we allow bundles to be removed from this ResourceMap? If so, we'd have to only remove keys that were added to this RM explicitly, not via
          a parent, and then clear any cached values for these keys.




*/


/**
 * @author Rob Ross
 * @version Date: Oct 8, 2009  5:08:44 PM
 */
public class ResourceMap
{


    public ResourceMap(@NotNull List<String> bundleNames) throws MissingResourceException
    {
        this(bundleNames, ResourceMap.class.getClassLoader(), Locale.getDefault());
    }

    public ResourceMap(@NotNull List<String> bundleNames, @NotNull ClassLoader classLoader) throws MissingResourceException
    {
        this(bundleNames, classLoader, Locale.getDefault());
    }

    public ResourceMap(@NotNull List<String> bundleNames, @NotNull Locale locale) throws MissingResourceException
    {
        this(bundleNames, ResourceMap.class.getClassLoader(), locale);
    }

    public ResourceMap(@NotNull List<String> bundleNames, @NotNull ClassLoader classLoader, @NotNull Locale locale) throws MissingResourceException
    {
        if (bundleNames == null || bundleNames.isEmpty())
        {
            throw new IllegalArgumentException("bundleNames argument cannot be null nor empty");
        }
        if (classLoader == null)
        {
            throw new IllegalArgumentException("ClassLoader argument cannot be null");
        }
        if (locale == null)
        {
            throw new IllegalArgumentException("Locale argument cannot be null");
        }

        String bpn = bundlePackageName(bundleNames.get(0));
        for (String bn : bundleNames)
        {
            if (!bpn.equals(bundlePackageName(bn)))
            {
                throw new IllegalArgumentException("Bundles not colocated: '" + bn + "' != '" + bpn + "'");
            }
        }
        this.resourcesDir = bpn.replace(".", "/") + "/";
        this.classLoader = classLoader;
        this.locale = locale;
        this.resourceBundleNames = Collections.<String>unmodifiableList(new ArrayList<String>(bundleNames));
        //this.resourceBundleNames = loadBundles0(bundleNames); //todo - although this could be loaded lazily, I'd rather have this fail fast if bundles can't be found
    }

    //temp - constructors from Original ResourceMap class - we will deprecate these after refactoring ResourceManager and unit tests

    public ResourceMap(ResourceMap parent, ClassLoader classLoader, String... bundleNames)
    {
        this(Arrays.asList(bundleNames), classLoader);
        this.parent = parent;
    }

    public ResourceMap(ResourceMap parent, ClassLoader classLoader, List<String> bundleNames)
    {
        this(bundleNames, classLoader);
        this.parent = parent;
    }

    /**
     * Returns a primitive array of elements obtained by parsing a resource value String. The String must be delimited by commas ",", and contain
     * elements that can all be parsed as the requested type in the componentType argument. If the componentType is not a primitive type, this method
     * throws an IllegalArgumentException. Each component obtained from parsing the Strings between delimeters will be sent to the String converter
     * appropriate for the componentType. Eg, if the componentType is "int.class", then a StringToInteger converter will be used. (Either the ResourceMap's
     * installed converter, or the converter supplied in the ConverterRegistry argument. If the resource String cannot be parsed as an array of the
     * requested type, or if any of the array elements cannot be converterd to the componentType, a LookupException is thrown.
     *
     * @param resourceKey   the key of the resource to be parsed as an array of the componentType
     * @param componentType the primitive type of each component of the returned array
     * @param converters    if null, the ResourceMap's installed ConverterRegistry is used. If not null, the appropriate String to componentType converter from the supplied
     *                      registry is used
     * @return a primitive array composed of componentType elements. If this method successfully parses the String and array components and no exceptions are thrown,
     *         you can safely cast the return value as an array of the componentType. Eg, if componentType is int.class, you can cast the return value to int[].
     * @throws IllegalArgumentException if the componentType is not a primitive type.
     */
    public Object getAsPrimitiveArray(@NotNull String resourceKey, @NotNull Class<?> componentType, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        assertNotNull(componentType, Class.class, "componentType");
        if (!componentType.isPrimitive())
        {
            throw new IllegalArgumentException("Component Type of array must be a primitive.");
        }

        return getAsArray_impl(resourceKey, componentType, converters);
    }


    //gets a resource as an Array of component type specified by the componentType argument
    @SuppressWarnings("unchecked")
    //getAsArray_impl will always return an array of componentType, or throw an exception
    public <T> T[] getAsArray(@NotNull String resourceKey, @NotNull Class<T> componentType, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        assertNotNull(componentType, Class.class, "componentType");
        return (T[]) getAsArray_impl(resourceKey, componentType, converters);
    }

    @Nullable
    public String[] getAsArrayString(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        //all resources are internally stored as strings
        return (String[]) getAsArray(resourceKey, String.class, converters);
    }

    /*
     * Convenience method that calls getAsPrimitiveArray and casts return value to boolean[]
     */
    @Nullable
    public boolean[] getAsArray_boolean(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return (boolean[]) getAsPrimitiveArray(resourceKey, boolean.class, converters);
    }

    /*
     * Convenience method that calls getAsPrimitiveArray and casts return value to int[]
     */
    @Nullable
    public int[] getAsArray_int(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return (int[]) getAsPrimitiveArray(resourceKey, int.class, converters);
    }

    /*
     * Convenience method that calls getAsPrimitiveArray and casts return value to long[]
     */
    @Nullable
    public long[] getAsArray_long(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return (long[]) getAsPrimitiveArray(resourceKey, long.class, converters);
    }

    /*
     * Convenience method that calls getAsPrimitiveArray and casts return value to double[]
     */
    @Nullable
    public double[] getAsArray_double(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return (double[]) getAsPrimitiveArray(resourceKey, double.class, converters);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    synchronized public <T> T getResourceAs(@NotNull String resourceKey, @NotNull Class<T> conversionType, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        assertNotNull(conversionType, Class.class, "conversionType");
        Object value = null;
        value = conversionCache.get(new ConversionCacheKey<T>(resourceKey, conversionType));
        if (value != null)
        {
            //value not null, we found it in the cache
            return (T) maybeCopy(value); //copy/clone if the type is mutable
        }
        else
        {
            //not in cache, get string value from ResourceMap chain
            value = getResource(resourceKey);
        }
        //still null means this key not found in the ResourceMap chain.
        if (value == null)
        {
            return (T) null;
        }

        //convert this String value to the desired type
        value = convertResourceString(resourceKey, (String) value, conversionType, converters);
        conversionCache.put(new ConversionCacheKey<T>(resourceKey, conversionType), value);
        return (T) maybeCopy(value); //copy/clone if the type is mutable
    }

    private final static Set<Class<?>> IMMUTABLE_TYPES = ConverterRegistry.getImmutableTypes();

    private <T> T maybeCopy(T value)
    {
        @SuppressWarnings({"unchecked"})
        Class<T> type = (Class<T>) value.getClass(); //value is type T, so its Class object is a Class<T>, right? Right!

        if (IMMUTABLE_TYPES.contains(type))
        {
            return value; //type is already immutable, no copy needed
        }
        if (type.isArray())
        {

            //todo - remove println
            System.out.println(String.format("value %s is an array type %s", value, type));
            Object[] objArray = (Object[]) value;
            Object[] copyArray = Arrays.copyOf(objArray, objArray.length);
            return (T) copyArray;
        }
        ResourceConverter<String, T> converter = getConverters().converterFor(type);
        if (converter == null)
        {
            throw new IllegalArgumentException(String.format("No resource converter for destination type '%s', value=%s", type, value));
        }
        return converter.copy(value);
    }

    private Object copyArray(Object array)
    {
        Class type = array.getClass();
        if (!type.isArray())
        {
            return array;
        }

        Class compType = type.getComponentType();

        Object arrayCopy = Array.newInstance(compType, Array.getLength(array));
        for (int i = 0, n = Array.getLength(array); i < n; i++)
        {
            Array.set(arrayCopy, i, maybeCopy(Array.get(array, i)));  //deep copy where needed
        }
        return arrayCopy;
    }


    /**
     * @return the ConverterRegistry used by this instance when converting resource strings to objects. If no ConverterRegistry
     *         is explicitly installed, uses the default ConverterRegistry and the default converters.
     */
    @SuppressWarnings({"DoubleCheckedLocking"})
    //with converters volatile, this idiom now works under Java 5 memory model
    public ConverterRegistry getConverters()
    {
        if (converters == null)
        {
            synchronized (converterMonitor)
            {
                if (converters == null)
                {
                    //todo every ResourceMap should get its initial default ConverterRegistry from the ResourceManager
                    // that created it so they all share the same instances. Thus, this situation should only arise if
                    //the caller has manually created this ResourceMap
                    converters = new ConverterRegistry();
                    converters.addDefaultConverters();
                }
            }
        }
        return converters;
    }

    /**
     * Sets the converters used by all ResourceMaps in the chain to this ConverterRegistry in the argument. If the argument is null,
     * the method returns with no action.
     * @param converters the ConverterRegistry to be set for all ResourceMaps in the chain
     */
    public void setConverters(ConverterRegistry converters)
    {
        if (converters != null)
        {
            synchronized (converters)
            {
                getBottommostChild().setConverters0(converters);

            }
        }
    }

    //should only be called on the bottom most child node of a ResourceMap
    private void setConverters0(ConverterRegistry converters)
    {
        synchronized (converters)
        {
            this.converters = converters;
            if (parent != null)
            {
                parent.setConverters0(converters);
            }
        }
    }

    /**
     * @return the InjectorRegistry used by this instance when injecting resource properties into objects. If no InjectorRegistry
     *         is explicitly installed, creates a default InjectorRegistry and uses the default injectors.
     */
    @SuppressWarnings({"DoubleCheckedLocking"})
    //with injectors volatile, this idiom now works under Java 5 memory model
    public InjectorRegistry getInjectors()
    {
        if (injectors == null)
        {
            synchronized (injectorMonitor)
            {
                if (injectors == null)
                {
                    //every ResourceMap should get its initial default InjectorRegistry from the ResourceManager
                    // that created it so they all share the same instances. Thus, this situation should only arise if
                    //the caller has manually created this ResourceMap
                    injectors = new InjectorRegistry();
                    injectors.addDefaultInjectors();
                }
            }
        }
        return injectors;
    }

    /**
     * Sets the injectors used by all ResourceMaps in the chain to be this injector. If the argument is null, the method
     * returns with no action.
     *
     * @param injectors the InjectorRegistry to be used by all ResourceMaps in the chain
     */
    public void setInjectors(InjectorRegistry injectors)
    {
        if (injectors != null)
        {
            synchronized (injectors)
            {
                getBottommostChild().setInjectors0(injectors);
            }
        }
    }

    //should only be called on the bottom most child node of a ResourceMap
    private void setInjectors0(InjectorRegistry injectors)
    {
        synchronized (injectors)
        {
            this.injectors = injectors;
            if (parent != null)
            {
                parent.setInjectors0(injectors);
            }
        }
    }

    /**
     * Returns the names of the ResourceBundles that define the
     * resources contained by this ResourceMap.
     *
     * @return the names of the ResourceBundles in this ResourceMap
     */
    @NotNull
    public synchronized List<String> getBundleNames()
    {
        return resourceBundleNames;
    }

    /**
     * Overloaded version allows caller to specify a ConverterRegisty, containing a string-to-string converter.
     *
     * @param resourceKey the key for the desired String
     * @param converters  if null, the ResoureMap's default converters are used. If not null,looks up the converter to use
     *                    from the object in the argument
     * @return the string value of the resourceKey, converted if a string-to-string converter was provided.
     */
    @Nullable
    public String getAsString(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        //all resources are internally stored as strings
        return getResourceAs(resourceKey, String.class, converters);
    }

    /**
     * @param resourceKey name of property to retrieve as converted to a Boolean type
     * @param converters  optional (null allowed.) A custom ConverterRegistry used if passed in.
     * @return Boolean.TRUE if the named resource is present in the map and has a value of "true", as specified by the converter.
     *         Returns false if the named resource is present in the map and has a value that does not evaluate to true. Returns null
     *         if the resource is not found
     */
    public Boolean getAsBoolean(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Boolean.class, converters);
    }


    public Character getAsCharacter(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Character.class, converters);
    }

    public Color getAsColor(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Color.class, converters);
    }

    public Dimension getAsDimension(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Dimension.class, converters);
    }

    public EmptyBorder getAsEmptyBorder(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, EmptyBorder.class, converters);
    }

    public Font getAsFont(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Font.class, converters);
    }

    /**
     * @param resourceKey the resource key who's value represnts a relative or absolute path to a resource that can be
     *                    loaded as an image.
     * @param converters  optional (null allowed.) A custom ConverterRegistry used if passed in.
     * @return a BufferedImage loaded from the value of the resource key in the first argument
     */
    public BufferedImage getAsBufferedImage(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, BufferedImage.class, converters);
    }

    /**
     * @param resourceKey the resource key who's value represnts a relative or absolute path to a resource that can be
     *                    loaded as an image.
     * @param converters  optional (null allowed.) A custom ConverterRegistry used if passed in.
     * @return an ImageIcon created with an image loaded from the value of the resource key in the first argument
     */
    public ImageIcon getAsImageIcon(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, ImageIcon.class, converters);
    }

    public Insets getAsInsets(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Insets.class, converters);
    }

    public KeyStroke getAsKeyStroke(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, KeyStroke.class, converters);
    }

    public Byte getAsByte(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Byte.class, converters);
    }

    public Double getAsDouble(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Double.class, converters);
    }

    public Float getAsFloat(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Float.class, converters);
    }

    public Integer getAsInteger(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Integer.class, converters);
    }

    public Long getAsLong(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Long.class, converters);
    }

    public Short getAsShort(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Short.class, converters);
    }


    public Point getAsPoint(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Point.class, converters);
    }

    public Point2D.Double getAsPoint2D_Double(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Point2D.Double.class, converters);
    }

    public Point2D.Float getAsPoint2D_Float(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Point2D.Float.class, converters);
    }

    public Rectangle getAsRectangle(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Rectangle.class, converters);
    }

    public Rectangle2D.Double getAsRectangle2D_Double(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Rectangle2D.Double.class, converters);
    }

    public Rectangle2D.Float getAsRectangle2D_Float(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, Rectangle2D.Float.class, converters);
    }

    public URI getAsURI(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, URI.class, converters);
    }

    public URL getAsURL(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, URL.class, converters);
    }

    public MnemonicTextValue getAsMnemonicText(@NotNull String resourceKey, @Nullable ConverterRegistry converters)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        return getResourceAs(resourceKey, MnemonicTextValue.class, converters);
    }

    /**
     * Returns true if this resourceMap or its parent (recursively) contains
     * the specified key.
     *
     * @param resourceKey property key to be checked
     * @return true if this resourceMap or its parent contains the specified key.
     * @see #getParent
     * @see #keySet
     */
    public synchronized boolean containsKey(@NotNull String resourceKey)
    {
        assertNotNull(resourceKey, String.class, "resourceKey");
        if (getResourceMap().containsKey(resourceKey))
        {
            return true;
        }
        if (parent == null)
        {
            return false;
        }
        else
        {
            return parent.containsKey(resourceKey);
        }
    }


    public String getResourcesDir()
    {
        return resourcesDir;
    }

    /**
     * @return the ClassLoader used to load the resource bundle(s) of this ResourceMap instance
     */
    @NotNull
    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    /**
     * @return the Locale used to load all ResourceBundles in this ResourceMap.
     */
    @NotNull
    public Locale getLocale()
    {
        synchronized (localeChangeMonitor)
        {
            return locale;
        }
    }

    /**
     * Changes the Locale used by this ResourceMap when loading ResourceBundles. This method will cause all bundles
     * to be re-loaded using the new Locale, and propigates the change in Locale to all ResourceMaps that are part of
     * its chain, either as a parent or child, if such a chain exists. When this method
     * returns, subsequent calls to any property getters will use the property values appropriate to the new Locale.
     *
     * @param newLocale if newLocale is the same as the current Locale, this method simply returns. If different,
     *                  the conversion cache is cleared, and all ResourceBundles are reloaded using the new Locale. The new Locale change
     *                  will be propogated up the chain if any parent ResourceMaps exist.
     */
    public void setLocale(@NotNull Locale newLocale)
    {
        assertNotNull(newLocale, String.class, "newLocale");
        synchronized (localeChangeMonitor)
        {
            if (newLocale.equals(locale))
            {
                return;
            }
            getBottommostChild().setLocale_impl(newLocale);
        }
    }

    //we need a static variable as a monitor object since multiple ResourceMaps in the chain may be involved in a Locale change
    //and we need to protect access across this entire transaction
    private static final Object localeChangeMonitor = new Object();


    private void setLocale_impl(Locale newLocale)
    {
        synchronized (localeChangeMonitor)
        {
            if (parent != null)
            {
                parent.setLocale_impl(newLocale);
            }
            conversionCache.clear();
            //current implementation includes all keys from this ResourceMap and its parent(s), so we must clear this
            //set as well
            resourceKeys = null;
            Locale oldLocale = locale;
            this.locale = newLocale;
            //noinspection AccessToStaticFieldLockedOnInstance
            logger.log(Level.FINE, String.format("Setting Locale to '%s' for ResourceMap for dir=%s", locale, getResourcesDir()));
            loadBundles0(resourceBundleNames);

            if (child == null)
            {
                //child map is responsible for firing the notification event, so that only one is sent on behalf of the whole chain
                pcs.firePropertyChange("locale", oldLocale, newLocale);
            }
        }
    }


    /**
     * @return an unmodifiable set of all keys in this resource map, including keys from parent ResourceMaps.
     */
    @NotNull
    public synchronized Set<String> keySet()
    {
        if (resourceKeys == null)
        {
            //lazily created
            resourceKeys = new HashSet<String>(getResourceMap().size());
            if (parent != null)
            {
                resourceKeys.addAll(parent.keySet());
            }
            resourceKeys.addAll(getResourceMap().keySet());
            resourceKeys = (Set<String>) Collections.unmodifiableSet(resourceKeys);
        }
        return resourceKeys;
    }

    /**
     * @return A new copy of the Set of all the keys in this instance only. To get all keys from this instance and all parent instances,
     *         call keySet().
     */
    public Set<String> getKeysInThisMap()
    {
        return new HashSet<String>(getResourceMap().keySet());
    }


    /**
     * Makes the parent of this ResourceMap the instance in the argument
     *
     * @param parentMap a ResourceMap to become the new parent of this instance. Null allowed.
     */
    public synchronized void setParent(@Nullable ResourceMap parentMap)
    {
        if (this.parent != parentMap) //do nothing if parentMap is not changing
        {
            //any cached resources that were created by inherited resource strings from the previous parentMap may now
            //be invalid, so to be safe we have to clear the cache.
            clearCache();
            //current implementation includes all keys from this ResourceMap and its parentMap(s), so we must clear this
            //set as well
            resourceKeys = null;
            if (parent != null)
            {
                //if we currently have a parent, that parent is being removed as our parent, and thus it no longer has a child
                parent.setChild(null);
            }

            parent = parentMap;
            if (parent != null)
            {
                parent.setChild(this);//we are the child of our new parentMap.
            }
        }

    }

    @Nullable
    public synchronized ResourceMap getParent()
    {
        return parent;
    }


    //although child is currently not used, we may at some point wish to know something about our children, if any.
    private void setChild(ResourceMap childMap)
    {
        child = childMap;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private ResourceMap getChild()
    {
        return child;
    }

    //navigate the ResourceMap chain and find the bottommost, i.e. leaf or base, ResourceMap, which has no child itself
    private ResourceMap getBottommostChild()
    {
        ResourceMap currentNode = this;
        while (currentNode.getChild() != null)
        {
            currentNode = currentNode.getChild();
        }
        return currentNode;
    }

    /**
     * Makes the conversion cache for this ResourceMap instance empty.
     * This is not a transitive operation, i.e., any parent ResourceMapS are unaffected
     * After clearing the cache, any subsequent resource lookups will cause a new instance for that resource and type to
     * be created, which will then be added back into the cache.
     */
    synchronized public void clearCache()
    {
        conversionCache.clear();
    }

    /**
     * ResourceMaps can be reloaded when calling setLocale, so any set properties, including "platform",
     * will be deleted. If it is desired for a ResourceMap to hold properties set by user during program execution,
     * the ResourceManger should have to be refactored to contain a private ResourceMap that does
     * not get reloaded and serves as the map to always check first
     *
     * @see ResourceManager#setPlatform
     * @deprecated to set platform see {@code ResourceManager.setPlatform() }
     */
    @Deprecated
    protected void putResource(@NotNull String resourceKey, Object resource)
    {
        //todo - this may invalidate any cached conversions for this key if it already exists. Clear the entire cache?
        //or just search for this resource and clear those entries? How often does a resource get written, and is this
        //something we want to support? Generally properties files are read-only
        getResourceMap().put(resourceKey, resource);
    }

    // Property Listener support. Right now I only intend to fire changes on "locale" so observers can re-load their resources with the new Locale bundle.
    //If the ResourceMap is part of a chain, we delegate all property handling the the bottom of the chain, i.e., the RM with no child.
    private PropertyChangeSupport pcs = new PropertyChangeSupport(ResourceMap.class);

    synchronized public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if (child != null)
        {
            child.addPropertyChangeListener(listener);
        }
        else
        {
            pcs.addPropertyChangeListener(listener);
        }
    }

    synchronized public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if (child != null)
        {
            child.removePropertyChangeListener(listener);
        }
        else
        {
            pcs.removePropertyChangeListener(listener);
        }
    }

    synchronized public PropertyChangeListener[] getPropertyChangeListeners()
    {
        if (child != null)
        {
            return child.getPropertyChangeListeners();
        }
        else
        {
            return pcs.getPropertyChangeListeners();
        }
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if (child != null)
        {
            child.addPropertyChangeListener(propertyName, listener);
        }
        else
        {
            pcs.addPropertyChangeListener(propertyName, listener);
        }
    }


    synchronized public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if (child != null)
        {
            child.removePropertyChangeListener(propertyName, listener);
        }
        else
        {
            pcs.removePropertyChangeListener(propertyName, listener);
        }
    }


    synchronized public PropertyChangeListener[] getPropertyChangeListeners(String propertyName)
    {
        if (child != null)
        {
            return child.getPropertyChangeListeners(propertyName);
        }
        else
        {
            return pcs.getPropertyChangeListeners(propertyName);
        }
    }


    public static class LookupException extends RuntimeException
    {
        private final Class type;
        private String key;
        private final StringBuilder strBuilder = new StringBuilder();

        /**
         * Constructs an instance of this class with some useful information
         * about the failure.
         *
         * @param msg  the detail message
         * @param type the type of the resource
         * @param key  the name of the resource
         */
        public LookupException(String msg, String key, Class type)
        {
            super(msg);
            this.key = key;
            this.type = type;
        }

        /**
         * Returns the type of the resource for which lookup failed.
         *
         * @return the resource type
         */
        public Class getType()
        {
            return type;
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

        void setKey(String key)
        {
            this.key = key;
        }

        public void appendMessage(String s)
        {
            strBuilder.append(s);
        }

        /**
         * Overridden to append additional information, if any, that was added to the message via the appendMessage() method
         *
         * @return
         */
        @Override
        public String getLocalizedMessage()
        {
            if (strBuilder == null || strBuilder.length() == 0)
            {
                return getMessage() + String.format(" resource='%s', type=%s", key, type);
            }
            else
            {
                return getMessage() + String.format(" resource='%s', type=%s%s", key, type, strBuilder.toString());
            }

        }
    }

    protected void assertNotNull(Object o, Class type, String paramName)
    {
        if (o == null)
        {
            throw new IllegalArgumentException(String.format("parameter '%s' of type '%s' cannot be null.", paramName, type));
        }
    }


    private static final String VAR_START_TOKEN = "${";
    private static final String VAR_END_TOKEN = "}";

    private static final int MAX_EVAL_RECURSE_DEPTH = 12; //to detect circular references in expressions
    private static final ThreadLocal<Integer> recurseDepth =
            new ThreadLocal<Integer>()
            {
                @Override
                protected Integer initialValue()
                {
                    return 0;
                }
            };

    //examine expr for any expressions that need to be evaluated by performing variable substitution. These will be in the
    //form of ${foo} . There may be zero or more of these expressions, and they might be nested, eg:
    // ${foo}.${bar}
    // ${outer${inner}}
    private String evaluateStringExpression(String expr)
    {

        String s = doVariableSubstitution(expr);

        if (s != null)
        {
            if (s.contains("\\${"))

            {
                //there were escaped tokens, remove the meta-backslash
                s = s.replace("\\${", "${");
            }
            s = s.trim();
        }

        return s;
    }

    private String doVariableSubstitution(String expr)
    {
        recurseDepth.set(recurseDepth.get() + 1);
        //todo - remove println
        //System.out.println(String.format("recurse depth=%s, expr=%s", recurseDepth.get(), expr));
        try
        {
            if (recurseDepth.get() > MAX_EVAL_RECURSE_DEPTH)
            {
                //simple way of dealing with circular references, don't let recurse depth exceed MAX, which is more than plenty for normal evaluation
                String msg = String.format("Circular reference detected in expression. Giving up on '%s' : cannot evaluate.", expr);
                throw new LookupException(msg, "", String.class);
            }

            if (!expr.contains("${"))
            {
                return expr;
            }


            int startPos = expr.indexOf(VAR_START_TOKEN); //can't be -1, we just tested this above
            while (startPos > 0 && expr.charAt(startPos - 1) == '\\')
            {
                startPos = expr.indexOf(VAR_START_TOKEN, startPos + 1);
                if (startPos == -1)
                {
                    //all start tokens were escaped, nothing to substitute
                    return expr;
                }
            }

            if ("${null}".equalsIgnoreCase(expr))
            {
                return null;
            }

            //if there is a nested expression we should find VAR_START_TOKEN before VAR_END_TOKEN
            int nextStartToken = expr.indexOf(VAR_START_TOKEN, startPos + 1);
            int nextEndToken = expr.indexOf(VAR_END_TOKEN, startPos + 1);
            if (nextStartToken == -1 && nextEndToken == -1)
            {
                //error, expression not properly terminated
                String msg = String.format("Expression '%s' : no closing brace.", expr);
                throw new LookupException(msg, "", String.class);
            }

            StringBuilder sb = new StringBuilder(expr.substring(0, startPos)); //add everything before the start token
            if (nextStartToken == -1 || (nextEndToken < nextStartToken))
            {
                //not nested
                String key = expr.substring(startPos + 2, nextEndToken);
                String subValue = null;
                if ("null".equalsIgnoreCase(key))
                {
                    //special keyword used for actual null value
                    subValue = null;
                }
                else
                {
                    subValue = getAsString(key, null);
                    if (subValue == null)
                    {
                        //didn't find a value for this key in the ResourceMap chain, see if we find it in System.properties
                        subValue = System.getProperty(key);
                    }
                    if (subValue == null)
                    {
                        //still cannot find a value for this key, so keep the original key name, and add =null as a debugging measure for the caller
                        subValue = "\\${" + key + " = null}"; //todo - alternately, we could just append a blank instead of "null" here
                    }
                    sb.append(subValue); //do substitution
                }

                //process remaining string recursively
                String remaining = null;
                if (expr.length() > nextEndToken)
                {
                    remaining = doVariableSubstitution(expr.substring(nextEndToken + 1, expr.length()));
                }
                else
                {
                    remaining = expr.substring(nextEndToken + 1);
                }
                if (remaining.contains(VAR_START_TOKEN))
                {
                    remaining = doVariableSubstitution(remaining);
                }
                sb.append(remaining);
            }
            else
            {
                //nested, recursively drill down
                String result = expr.substring(startPos, nextStartToken) + doVariableSubstitution(expr.substring(nextStartToken, expr.length()));
                if (result.contains(VAR_START_TOKEN))
                {
                    result = doVariableSubstitution(result);
                }
                sb.append(doVariableSubstitution(result));
            }

            return sb.toString();
        }
        finally
        {
            recurseDepth.set(recurseDepth.get() - 1);
        }
    }

    private synchronized Map<String, Object> getResourceMap()
    {
        if (resourceMap == null)
        {
            resourceBundleNames = loadBundles0(resourceBundleNames); //now lazily loaded, but still not sure I want to do it this way
        }
        return resourceMap;
    }

    private static final Logger logger = Logger.getLogger("ResourceMap.class");

    private ResourceMap parent; //if not null, this ResourceMap looks to this map when it can't find a resource
    private ResourceMap child;  //if not null, we are the parent of this instance
    private Locale locale;
    private final ClassLoader classLoader;


    private List<String> resourceBundleNames; //the names of all the resource bundles loaded in this ResourceMap

    private volatile Map<String, Object> resourceMap;
    private Set<String> resourceKeys;

    //we cache converted resources here, and we look here first when requests for resources are made
    // Note : ALL operations on this reference MUST be synchronized, currently by the monitor for this instance of
    //ResourceMap., i.e., using the default "synchronzied" modifier on methods that contain this reference.
    //Remember, even a get() mutates this object, as it updates the last access time
    private volatile Map<ConversionCacheKey, Object> conversionCache = new LRUCacheMap();

    private static final int MAX_CACHE_ENTRIES = 500; //todo this can be tuned later

    //implementation of a LinkedHashMap to override removeEldestEntry and provide a LRU strategy for managing
    //memory associated with the cached resource conversions

    @SuppressWarnings({"SerializableHasSerializationMethods"})
    private static class LRUCacheMap extends LinkedHashMap<ConversionCacheKey, Object>
    {
        private static final long serialVersionUID = -5244281154183701786L;

        private LRUCacheMap()
        {
            super(MAX_CACHE_ENTRIES, .75f, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<ConversionCacheKey, Object> eldest)
        {
            boolean shouldUnload = size() > MAX_CACHE_ENTRIES;
            if (shouldUnload)
            {
                //todo - remove println
                System.out.println(String.format("* * * removing resource from cache: %s, value=%s ", eldest.getKey(), eldest.getValue()));
/*                long fm = Runtime.getRuntime().freeMemory();
                long mm = Runtime.getRuntime().maxMemory();
                long tm = Runtime.getRuntime().totalMemory();
                System.out.println(String.format("free mem = %s, total mem = %s, max mem = %s", fm, tm, mm));*/
            }
            return shouldUnload;
        }
    }


    private volatile ConverterRegistry converters; //converters used by this ResourceMap when converting resources to objects
    private final Object converterMonitor = new Object(); //used to synchronize changes to converter registry instance
    private volatile InjectorRegistry injectors = null; //injectors used by this ResourceMap when injecting resources into objects
    private final Object injectorMonitor = new Object(); //used to synchronize changes to injector registry instance

    private final String resourcesDir; //the directory from which this ResourceMap's property bundle(s) was(were) loaded


    //compound key type for looking up cached resources by name and target type
    private static final class ConversionCacheKey<T>
    {
        private final String resourceName;
        private final Class<T> conversionClass;

        private ConversionCacheKey(@NotNull String resourceName, @NotNull Class<T> conversionClass)
        {
            this.resourceName = resourceName;
            if (conversionClass.isPrimitive())
            {
                //change primitive type to wrapper type
                this.conversionClass = convertPrimativeType(conversionClass);
            }
            else
            {
                this.conversionClass = conversionClass;
            }
        }

        @Override
        public String toString()
        {
            return "ConversionCacheKey{" +
                    "resourceName='" + resourceName + '\'' +
                    ", conversionClass=" + conversionClass +
                    '}';
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            ConversionCacheKey that = (ConversionCacheKey) o;

            if (conversionClass != null ? !conversionClass.equals(that.conversionClass) : that.conversionClass != null)
            {
                return false;
            }
            if (resourceName != null ? !resourceName.equals(that.resourceName) : that.resourceName != null)
            {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = resourceName != null ? resourceName.hashCode() : 0;
            result = 31 * result + (conversionClass != null ? conversionClass.hashCode() : 0);
            return result;
        }

    }

    private final Set<String> loadBundlesSet = new HashSet<String>(32); //used by loadBundles to make sure we only load a bundle once
    private final List<String> loadBundlesList = new ArrayList<String>(32);

    private synchronized List<String> loadBundles0(List<String> bundleNames)
    {
        Map<String, Object> tempMap = new HashMap<String, Object>(1000);
        loadBundlesSet.clear();
        loadBundlesList.clear();
        loadBundles(bundleNames, tempMap);
        resourceMap = new ConcurrentHashMap<String, Object>(tempMap);
        Collections.reverse(loadBundlesList); //these were added in order loaded, which is reverse priority order
        return Collections.unmodifiableList(new ArrayList<String>(loadBundlesList));
    }

    //load all the resource bundles named in the bundleNames list. Lists are loaded in reverse order, so bundles
    //at the top of the list shadow bundles at the bottom of the list
    @SuppressWarnings({"AccessToStaticFieldLockedOnInstance"})
    private synchronized void loadBundles(List<String> bundleNames, Map<String, Object> tempMap)
    {
        for (int i = bundleNames.size() - 1; i >= 0; i--)
        {
            String bundleName = bundleNames.get(i);
            if (loadBundlesSet.contains(bundleName))
            {
                logger.log(Level.FINE, String.format("Skipping ResourceBundle named '%s' because it has already been loaded", bundleName));
            }
            else
            {
                logger.log(Level.FINE, String.format("loading ResourceBundle named %s", bundleName));
                try
                {
                    ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, classLoader);
                    //any import key present?
                    if (bundle.containsKey("import"))
                    {
                        String value = (String) bundle.getString("import");
                        if (value != null && !value.isEmpty())
                        {
                            String[] addlFiles = value.split("[,;]");
                            if (addlFiles != null && addlFiles.length > 0)
                            {
                                for (int j = 0, n = addlFiles.length; j < n; j++)
                                {
                                    addlFiles[j] = getResourcesDir() + addlFiles[j].trim(); //prepend resources directory name
                                    addlFiles[j] = addlFiles[j].replace("/", ".");
                                }
                                //load these imported bundles recursively before the parent bundle
                                loadBundles(Arrays.asList(addlFiles), tempMap);
                            }
                        }
                    }
                    Enumeration<String> keys = bundle.getKeys();
                    while (keys.hasMoreElements())
                    {
                        String key = keys.nextElement();
                        tempMap.put(key, bundle.getObject(key));
                    }
                    loadBundlesList.add(bundleName);
                    loadBundlesSet.add(bundleName);


                }
                catch (MissingResourceException ignore)
                {
                    /* bundleName is just a location to check, it's not
                    * guaranteed to name a ResourceBundle
                    */
                    //todo - not sure about this ignore.   I think the ResourceManager should make sure the property file actually
                    //exists before supplying it as a parameter to the ResourceMap constructor. Then the ResourceMap can just diligently
                    //try to load what it was given and fail if it can't find it.
                    //throw ignore;
                }
            }
        }
    }


    //shared implementation for getAsPrimitiveArray and getAsArray. Those public methods have signatures to facilitate compile-time
    //type checking and serve as a facade to this method
    private Object getAsArray_impl(@NotNull String resourceKey, @NotNull Class<?> componentType, @Nullable ConverterRegistry converters)
    {
        Object value = null;

        Class<?> arrayClass = Array.newInstance(componentType, 0).getClass();
        //The ConversionCache supports primitive array types as well as Reference array types, but we can't specify primitive
        //component types as a parameterized (Generic) type, so we have to use wildcards in the Class type. "arrayClass" will
        //always be an array of the type supplied in the argument, so this statement is safe
        @SuppressWarnings("unchecked")
        ConversionCacheKey cacheKey = new ConversionCacheKey(resourceKey, arrayClass);
        value = conversionCache.get(cacheKey);
        if (value != null)
        {
            //value not null, we found it in the cache
            //noinspection unchecked
            return copyArray(value);
        }
        else
        {
            //not in cache, get string value from ResourceMap chain
            value = getResource(resourceKey);
        }
        //still null means this key not found in the ResourceMap chain.
        if (value == null)
        {
            return null;
        }
        String strValue = (String) value;// all resources are stored in ResourceBundle as a String
        String delimeter = ","; //default delimeter is a comma
        if (strValue.contains(";") && componentType != String.class)
        {
            //if we are not creating a String array, see if the String value contains any semi-colons. These will delimit
            //elements that are not strings. This will allow us to store arrays of Points, for example, as a string resource. Each
            //point has two elements separated by a comma, but each point would be delimeted by a semi-colon
            delimeter = ";";
        }
        //convert this String value to the desired  array type

        String[] elements = ((String) value).split(delimeter);
        if (elements == null || elements.length == 0)
        {
            return null;
        }
        String lastElement = elements[elements.length - 1];
        //special case to handle allowing a final comma in an array string, to match what Java syntax allows
        int upperBound = (lastElement == null || lastElement.trim().isEmpty()) ? elements.length - 1 : elements.length;
        //create the Array that will be returned
        Object array = Array.newInstance(componentType, upperBound);

        for (int i = 0; i < upperBound; i++)
        {
            String element = elements[i].trim();
            try
            {
                Object o = convertResourceString(resourceKey, element, componentType, converters);
                Array.set(array, i, o);
            }
            catch (LookupException le)
            {
                le.appendMessage("[" + i + "]"); //provide information about which array element failed
                throw le;
            }
        }

        conversionCache.put(cacheKey, array);
        return copyArray(array);
    }


    @SuppressWarnings("unchecked")
    private static <T> Class<T> convertPrimativeType(Class<T> type)
    {
        Class<?> converted = type;
        if (type == Boolean.TYPE)
        { converted = Boolean.class; }
        else if (type == Character.TYPE)
        { converted = Character.class; }
        else if (type == Byte.TYPE)
        { converted = Byte.class; }
        else if (type == Short.TYPE)
        { converted = Short.class; }
        else if (type == Integer.TYPE)
        { converted = Integer.class; }
        else if (type == Long.TYPE)
        { converted = Long.class; }
        else if (type == Float.TYPE)
        { converted = Float.class; }
        else if (type == Double.TYPE)
        { converted = Double.class; }

        return (Class<T>) converted;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    private <T> T convertResourceString(@NotNull String resourceKey, @NotNull String strValue, @NotNull Class<T> conversionType, @Nullable ConverterRegistry converters)
    {
        T obj = null;
        ResourceConverter<String, T> stringConverter;
        if (converters == null)
        {
            //no converters explicitly supplied, so use our own
            converters = getConverters();
        }

        stringConverter = converters.converterFor(conversionType);
        if (stringConverter != null)
        {
            try
            {
                //For image types, we need to supply the ClassLoader of the loaded bundles
                Object[] args;
                if (conversionType == Icon.class || conversionType == ImageIcon.class ||
                        conversionType == Image.class || conversionType == BufferedImage.class)
                {
                    args = new Object[]{getClassLoader()};
                    strValue = resourcePath(resourceKey, strValue);
                }
                else
                {
                    args = null;
                }
                obj = stringConverter.convert(strValue, args);
            }
            catch (StringConvertException e)
            {
                String msg = String.format("Failed to convert String '%s' to %s.", strValue, conversionType.getSimpleName());
                LookupException lfe = new LookupException(msg, resourceKey, conversionType);
                lfe.initCause(e);
                throw lfe;
            }
        }
        else if (conversionType == String.class || conversionType.isAssignableFrom(strValue.getClass()))
        {
            //don't error out if we are trying to do a string-to-string conversion, and no string converter was found. Just pass back
            //the original string unchanged

            obj = (T) strValue;
        }
        else
        {
            String msg = "No StringConverter for required type";
            throw new LookupException(msg, resourceKey, conversionType);
        }
        return obj;
    }

    /* If path doesn't have a leading "/" then the resourcesDir
    * is prepended, otherwise the leading "/" is removed.
    */
    private String resourcePath(@NotNull String resourceKey, @NotNull String path)
    {
        String rPath = path;
        if (path == null)
        {
            rPath = null;
        }
        else if (path.startsWith("/"))
        {
            rPath = (path.length() > 1) ? path.substring(1) : null;
        }
        else
        {
            rPath = resourceMapForKey(resourceKey).getResourcesDir() + path;
        }
        return rPath;
    }


    /**
     * If this ResourceMap has no parent, simply check the internal resourceMap for this key, and if found, return
     * a reference to this instance. If the reousrceKey is not found, returns null. If this ResourceMap is part of a chain,
     * i.e., has a parent and possibly grand-parents, etc, then this method searches up the chain, starting with itself, and
     * returns the first ResourceMap in the chain that contains this resourceKey, or null if the key is not found.
     *
     * @param resourceKey the key of the resource property to locate
     * @return the first ResourceMap in the chain that contains the resource in the argument. Returns null if the argument
     *         is not contained in the ResourceMap chain
     */
    @Nullable
    private synchronized ResourceMap resourceMapForKey(@NotNull String resourceKey)
    {
        if (getResourceMap().containsKey(resourceKey))
        {
            return this;
        }
        else if (parent != null)
        {
            return parent.resourceMapForKey(resourceKey);
        }
        else
        {
            //neither this instance nor its parent chain contains the key.
            return null;
        }

    }

    @Nullable
    private synchronized Object getResource(@NotNull String resourceKey)
    {
        Object obj = getResourceMap().get(resourceKey);
        if (obj == null && parent != null)
        {
            obj = parent.getResource(resourceKey);
        }
        if (obj == null)
        {
            return null;
        }
        // Treat empty string as null here as a convenience
        //so a LookupException does not get thrown
        //todo this might be controversial. Should an empty string be allowed as a valid resource value? It does make
        //sense if the resource actually represents a String, but in all other contexts, what does parsing an empty string
        //mean? Null would be the safe default, consistent across the board. One could also make the argument that an empty
        //string should represent the "default" state of an object, e.g., Point(0,0), Rectangle(0,0,0,0). But this might be
        //assuming too much. Also, some converted types cannot return any meaningful value from an empty string, like URL,
        //BufferedImage, etc.
        if (obj.getClass() == String.class && ((String) obj).isEmpty())
        {
            return null;
        }

        //apply any variable substitutions here.
        if (obj.getClass() == String.class)
        {
            String s = (String) obj;
            try
            {
                s = evaluateStringExpression(s.trim());
            }
            catch (LookupException le)
            {
                //insert name of resource key
                le.setKey(resourceKey);
                //recurseDepth.set(0); //reset for next time
                throw le;
            }
            obj = s;
        }

        return obj;
    }


    private String bundlePackageName(String bundleName)
    {
        int i = bundleName.lastIndexOf(".");
        return (i == -1) ? "" : bundleName.substring(0, i);
    }


    //-------------------------------------
    //    D E B U G G I N G
    //debugging
    public void printlnResourceMap()
    {
        System.out.println(String.format("***** ResourceMap for %s", resourcesDir));
        for (String s : getResourceMap().keySet())
        {
            System.out.println(String.format("key=%s, value=%s", s, getResourceMap().get(s)));
        }
    }

    public synchronized void printlnParentChain()
    {
        System.out.println(String.format("I'm a ResourceMap for %s, my parent=%s", resourcesDir, parent));
        if (parent != null)
        {
            parent.printlnParentChain();
        }
    }


    // - - - -M I G R A T I O N
    // -------------------------------
    //These methods from the original ResourceMap are included here so existing code doesn't break. They are all deprecated, and
    // will be removed at some point in the near future

    /**
     * @see #getResourceAs(String, Class, ConverterRegistry)
     * @deprecated use one of the {@code getAsXXX} convenience methods. For the low level API you can call {@code getResourceAs()}.
     */
    @SuppressWarnings({"unchecked"})
    @Deprecated
    public Object getObject(@NotNull String key, @NotNull Class type)
    {
        assertNotNull(key, String.class, "resourceKey");
        assertNotNull(type, Class.class, "type");
        return getResourceAs(key, type, null);
    }

    /**
     * Deprecated. Formatting of message strings will be the caller's responsibilty. ResourceMap just vends resource properties, converts types,
     * and performs variable substitution. Further processing is outside scope of class responsibilities. You can also install a custom StringToString ResourceConverter
     * in this chain's converter registry that does this kind of processing
     *
     * @see #getAsString
     * @deprecated use {@code getAsString()}
     */
    @Deprecated
    public String getString(String key, Object... args)
    {
        if (args.length == 0)
        {
            return getAsString(key, null);
        }
        else
        {
            String format = getAsString(key, null);
            return (format == null) ? null : String.format(format, args);
        }
    }

    /**
     * @see #getAsBoolean
     * @deprecated use {@code getAsBoolean}
     */
    @Deprecated
    public Boolean getBoolean(String key)
    {
        return getAsBoolean(key, null);
    }

    /**
     * @see #getAsInteger
     * @deprecated use {@code getAsInteger}
     */
    @Deprecated
    public Integer getInteger(String key)
    {
        return getAsInteger(key, null);
    }

    /**
     * @see #getAsLong
     * @deprecated use {@code getAsLong}
     */
    @Deprecated
    public Long getLong(String key)
    {
        return getAsLong(key, null);
    }

    /**
     * @see #getAsShort
     * @deprecated use {@code getAsShort}
     */
    @Deprecated
    public Short getShort(String key)
    {
        return getAsShort(key, null);
    }

    /**
     * @see #getAsByte
     * @deprecated use {@code getAsByte}
     */
    @Deprecated
    public Byte getByte(String key)
    {
        return getAsByte(key, null);
    }

    /**
     * @see #getAsFloat
     * @deprecated use {@code getAsFloat}
     */
    @Deprecated
    public Float getFloat(String key)
    {
        return getAsFloat(key, null);
    }

    /**
     * @see #getAsDouble
     * @deprecated use {@code getAsDouble}
     */
    @Deprecated
    public Double getDouble(String key)
    {
        return getAsDouble(key, null);
    }

    /**
     * @see #getAsImageIcon
     * @deprecated this method is redundant and will not be replaced. Use {@code getAsImageIcon}, since ImageIcon implements Icon
     */
    @Deprecated
    public Icon getIcon(String key)
    {
        return getAsImageIcon(key, null);
    }

    /**
     * @see
     * @deprecated
     */
    @Deprecated
    public ImageIcon getImageIcon(String key)
    {
        return getAsImageIcon(key, null);
    }

    /**
     * @see #getAsFont
     * @deprecated use {@code getAsFont}
     */
    @Deprecated
    public Font getFont(String key)
    {
        return getAsFont(key, null);
    }

    /**
     * @see #getAsColor
     * @deprecated use {@code getAsColor}
     */
    @Deprecated
    public Color getColor(String key)
    {
        return getAsColor(key, null);
    }

    /**
     * @see #getAsKeyStroke
     * @deprecated use {@code getAsKeyStroke}
     */
    @Deprecated
    public KeyStroke getKeyStroke(String key)
    {
        return getAsKeyStroke(key, null);
    }

    /**
     * @see #getAsKeyStroke
     * @see KeyStroke#getKeyCode()
     * @deprecated this will not be replaced. Call {@code getAsKeyStroke} and on that object you can call {@code getKeyCode}
     */
    @Deprecated
    public Integer getKeyCode(String key)
    {
        KeyStroke ks = getAsKeyStroke(key, null);
        return (ks != null) ? new Integer(ks.getKeyCode()) : null;
    }



    //Injection relatd code, copied from original ResourceMap implementation.
    //todo - refactor, extract this into separate classes and make injectors pluggable
    /**
     * Set each property in <tt>target</tt> to the value of
     * the resource named <tt><i>componentName</i>.propertyName</tt>,
     * where  <tt><i>componentName</i></tt> is the value of the
     * target component's name property, i.e. the value of
     * <tt>target.getName()</tt>.  The type of the resource must
     * match the type of the corresponding property.  Properties
     * that aren't defined by a resource aren't set.
     * <p/>
     * For example, given a button configured like this:
     * <pre>
     * myButton = new JButton();
     * myButton.setName("myButton");
     * </pre>
     * And a ResourceBundle properties file with the following
     * resources:
     * <pre>
     * myButton.text = Hello World
     * myButton.foreground = 0, 0, 0
     * myButton.preferredSize = 256, 256
     * </pre>
     * Then <tt>injectComponent(myButton)</tt> would initialize
     * myButton's text, foreground, and preferredSize properties
     * to <tt>Hello World</tt>, <tt>new Color(0,0,0)</tt>, and
     * <tt>new Dimension(256,256)</tt> respectively.
     * <p/>
     * This method calls {@link #getResourceAs} to look up resources
     * and it uses {@link Introspector#getBeanInfo} to find
     * the target component's properties.
     * <p/>
     * If target is null an IllegalArgumentException is thrown.  If a
     * resource is found that matches the target component's name but
     * the corresponding property can't be set, an (unchecked) {@link
     * PropertyInjectionException} is thrown.
     *
     * @param target the Component to inject
     * @throws LookupException            if an error occurs during lookup or string conversion
     * @throws PropertyInjectionException if a property specified by a resource can't be set
     * @throws IllegalArgumentException   if target is null
     * @see #injectComponents
     * @see #getResourceAs
     * @see @see org.jdesktop.application.convert.ConverterRegistry#converterFor
     */
    public void injectComponent(Component target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("null target");
        }
        getInjectors().injectorFor(target).inject(target, this, false);
    }


    /**
     * Applies {@link #injectComponent} to each Component in the
     * hierarchy with root <tt>root</tt>.
     *
     * @param root the root of the component hierarchy
     * @throws PropertyInjectionException if a property specified by a resource can't be set
     * @throws IllegalArgumentException   if target is null
     * @see #injectComponent
     */
    public void injectComponents(Component root)
    {
        getInjectors().injectorFor(root).inject(root, this, true);
    }


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
    public void injectFields(Object target)
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
            Resource resource = field.getAnnotation(Resource.class);
            if (resource != null)
            {
                String rKey = resource.key();
                String key = (rKey.length() > 0) ? rKey : keyPrefix + field.getName();
                injectField(field, target, key);
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

                Resource resource = method.getAnnotation(Resource.class);
                if (resource != null)
                {
                    String rKey = resource.key();
                    String key = (rKey.length() > 0) ? rKey : keyPrefix + propName;
                    injectMethod(method, target, key);
                }

            }
        }
    }


    /**
     * Unchecked exception thrown by {@link #injectFields} when
     * an error occurs while attempting to set a field (a field that
     * had been marked with <tt>&#064;Resource</tt>).
     *
     * @see #injectFields
     */
    public static class InjectFieldException extends RuntimeException
    {
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

    private void injectField(Field field, Object target, String key)
    {
        Class type = field.getType();
        if (type.isArray())
        {
            type = type.getComponentType();
            Pattern p = Pattern.compile(key + "\\[([\\d]+)\\]");  // matches key[12]
            List<String> arrayKeys = new ArrayList<String>();
            for (String arrayElementKey : keySet())
            {
                Matcher m = p.matcher(arrayElementKey);
                if (m.matches())
                {
                    /* field's value is an array, arrayElementKey is a resource
                  * name of the form "MyClass.myArray[12]" and m.group(1)
                  * matches the array index.  Set the index element
                  * of the field's array to the value of the resource.
                  */
                    Object value = getObject(arrayElementKey, type);
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
            Object value = getObject(key, type);
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

    private void injectMethod(Method method, Object target, String key)
    {
        Class paramType = method.getParameterTypes()[0];
        logger.log(Level.FINE, String.format("method : %s : injecting %s of type %s into %s", method, key, paramType, target));
        Object value = getObject(key, paramType);
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
