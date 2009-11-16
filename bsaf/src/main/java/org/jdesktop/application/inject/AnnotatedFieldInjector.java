/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 *
 */

package org.jdesktop.application.inject;

import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;


/**
 *
 * Injects resource properties into an Object where the injectable fields have been annotated. The default annotation is
 * \@Resource, but the annotation type can be specified in one of the overloaded #injectField methods.
 *
 * @author Rob Ross
 * @version Date: Nov 11, 2009  1:40:33 AM
 */
@SuppressWarnings({"InnerClassFieldHidesOuterClassField"})
public class AnnotatedFieldInjector
{
    private static final Logger logger = Logger.getLogger("AnnotatedFieldInjector.class");


    public enum AccessModifier
    {
        PRIVATE(Modifier.PRIVATE, 1), DEFAULT(0, 2), PROTECTED(Modifier.PROTECTED, 3), PUBLIC(Modifier.PUBLIC, 4);

        private final int modifier;
        private final int ordinal;

        private AccessModifier(int modifier, int ordinal)
        {
            this.modifier = modifier;
            this.ordinal = ordinal;
        }

        public int getModifier()
        {
            return modifier;
        }

        public int getOrdinal()
        {
            return ordinal;
        }

        /**
         * Determines which access modifier is attached to the member
         *
         * @param fieldOrMethod a Field or Method for which to obtain the ACCESS enum representing the acces modifier present.
         * @return the ACCESS enum value that represents the access modifier of the field or method in the argument
         * @throws IllegalArgumentException if the argument is not a Field or Method
         */
        static public AccessModifier valueOf(Member fieldOrMethod) throws IllegalArgumentException
        {
            boolean validArgument = (fieldOrMethod instanceof Field) || (fieldOrMethod instanceof Method);
            if (!validArgument)
            {
                throw new IllegalArgumentException(String.format("Argument was: %s but Field or Method expected", fieldOrMethod.getClass().getSimpleName()));
            }
            int modifiers = fieldOrMethod.getModifiers();
            if (Modifier.isPublic(modifiers))
            {
                return PUBLIC;
            }
            else if (Modifier.isProtected(modifiers))
            {
                return PROTECTED;
            }
            else if (Modifier.isPrivate(modifiers))
            {
                return PRIVATE;
            }
            else
            {
                return DEFAULT;
            }
        }


    }

    ;

    //convenience constants
    public static final AccessModifier PRIVATE = AccessModifier.PRIVATE;
    public static final AccessModifier DEFAULT = AccessModifier.DEFAULT;
    public static final AccessModifier PROTECTED = AccessModifier.PROTECTED;
    public static final AccessModifier PUBLIC = AccessModifier.PUBLIC;

    public enum Strategy
    {
        /**
         * Inject fields only
         */
        FIELD,
        /**
         * Inject setter methods only
         */
        METHOD,
        /**
         * Inject either fields or methods, whichever are present. If both are present, uses the setter method
         */
        EITHER;
    }

    ;

    public static final Strategy FIELD = Strategy.FIELD;
    public static final Strategy METHOD = Strategy.METHOD;
    public static final Strategy EITHER = Strategy.EITHER;


    private static final Class<? extends Annotation> DEFAULT_ANNOTATION_TYPE = Resource.class;
    private static final AccessModifier DEFAULT_ACCESS_MODIFIER = PUBLIC;
    private static final Strategy DEFAULT_STRATEGY = EITHER;

    
    private  InjectorState injectorState = new InjectorState();


    private static final class InjectorState
    {
        private @Nullable Class<? extends Annotation> injectAnnotation = DEFAULT_ANNOTATION_TYPE;
        private AccessModifier minimumFieldAccess = DEFAULT_ACCESS_MODIFIER;
        private AccessModifier minimumMethodAccess = DEFAULT_ACCESS_MODIFIER;
        private Strategy strategy = DEFAULT_STRATEGY;

        private InjectorState()
        {
        }

        private InjectorState(AccessModifier minimumFieldAccess, AccessModifier minimumMethodAccess, Strategy strategy, Class<? extends Annotation> injectAnnotation)
        {
            this.minimumFieldAccess = minimumFieldAccess;
            this.minimumMethodAccess = minimumMethodAccess;
            this.strategy = strategy;
            this.injectAnnotation = injectAnnotation;
        }

        public InjectorState(InjectorState injectorState, Class<? extends Annotation> annotationType)
        {
            this(injectorState.minimumFieldAccess, injectorState.minimumMethodAccess, injectorState.strategy, annotationType);
        }

        InjectorState copy()
        {
            return new InjectorState(minimumFieldAccess, minimumMethodAccess, strategy, injectAnnotation);    
        }

        @Override
        public String toString()
        {
            return "AnnotatedFieldInjector{" +
                    "minimumFieldAccess=" + minimumFieldAccess +
                    ", minimumMethodAccess=" + minimumMethodAccess +
                    ", strategy=" + strategy +
                    ", injectAnnotation= @" + injectAnnotation.getName() +
                    '}';
        }
        
        boolean strategyIncludesField()
        {
            return strategy == FIELD || strategy == EITHER;    
        }
        
        boolean strategyIncludesMethod()
        {
            return strategy == METHOD || strategy == EITHER;    
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

            InjectorState that = (InjectorState) o;

            if (injectAnnotation != null ? !injectAnnotation.equals(that.injectAnnotation) : that.injectAnnotation != null)
            {
                return false;
            }
            if (minimumFieldAccess != that.minimumFieldAccess)
            {
                return false;
            }
            if (minimumMethodAccess != that.minimumMethodAccess)
            {
                return false;
            }
            if (strategy != that.strategy)
            {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = injectAnnotation != null ? injectAnnotation.hashCode() : 0;
            result = 31 * result + (minimumFieldAccess != null ? minimumFieldAccess.hashCode() : 0);
            result = 31 * result + (minimumMethodAccess != null ? minimumMethodAccess.hashCode() : 0);
            result = 31 * result + (strategy != null ? strategy.hashCode() : 0);
            return result;
        }
    }
    
    
    /**
     * Default constructor creates an instance with defaults set as:
     * minimumFieldAccess == PUBLIC
     * minimumMethodAccess == PUBLIC
     * Strategy = EITHER
     * injectAnnotation  = Resource.class
     */
    public AnnotatedFieldInjector()
    {
    }

    /**
     * Constructs a new instance, setting initial state according to the arguments.
     *
     * @param accessModifier  PRIVATE, DEFAULT, PROTECTED, or PUBLIC. Sets the miniumum access threshold a field or method
     * must have in order to be injected. For finer granularity, you can set field or method access independently via
     * {@code setMinimumFieldAccess} and {@code setMinimumMethodAccess}
     * @param strategy FIELD, METHOD, or EITHER. When set to FIELD, only fields are used to inject resource values. When
     * set to METHOD, only setter methods are used. If set to EITHER, then either fields or setter methods are potentially used. If
     * both a field and setter method are available for a property and eligible for injection, then the setter method is used. Fields and
     * setter methods are elible for injection depending on the minimum access settings, the actual access modifier of the
     * field or setter method to be injected, and whether or nor an Annotation type has been
     * specified and is present for the field or setter method.
     *
     * @param injectAnnotation if set to null, resources can be injected without fields or setter methods needing to be
     * annotated. Otherwise, this argument specifies the Annotation type that must be present on a field or setter method
     * in order to have its value injected from a resource. The annotation can be of any type. The annotation is not required to
     * have a "key" property, but if a "key" property exists and has a non-null, non-empty value, that value is used as the
     * lookup key into a ResourceMap instead of the normal property key.
     *
     * @see #setMinimumFieldAccess
     * @see #setMinimumMethodAccess
     * @see #setStrategy
     * @see #setInjectAnnotation
     */
    public AnnotatedFieldInjector(@NotNull AccessModifier accessModifier, @NotNull Strategy strategy, @Nullable Class<? extends Annotation> injectAnnotation)
    {
        assertNotNull(accessModifier, AccessModifier.class, "accessModifier");
        assertNotNull(strategy, Strategy.class, "strategy");        
        injectorState = new InjectorState(accessModifier, accessModifier, strategy, injectAnnotation);
    }

    /**
     * @return the Annotation type currently used to determine if a resource should be injected into a field or setter method.
     * Returns null if no Annotation type is required for injection.
     * @see #setInjectAnnotation
     */
    @Nullable
    final public Class<? extends Annotation> getInjectAnnotation()
    {
        return injectorState.injectAnnotation;
    }

    /**
     * Specify the Annotation type used to indicate that a field or setter method can be injected with a value from a
     * ResourceMap, if such a property value is found. The default value is \@Resource, but you can supply any
     * Annotation type, and only fields or setter methods annotated with this type will be injected. As with \@Resource,
     * if the Annotation type has a property named "key", with non-empty value, that value will be used as the key
     * into the ResourceMap to find a property for injection. If no "key" property exists or it is empty, the standard
     * property naming scheme is used.
     *
     * @param injectAnnotation the Annotation type that must be present on a field or setter method in order for resource
     * injection to occur on that field or setter method. This argument can be null, in which case no annotations are required.
     * When null, any fields or setter methods with a matching property in the ResourceMap will be automatically injected,
     * whether or not they are annotated, subject to the values set for minimumFieldAccess, minimumMethodAccess, and strategy.
     *
     * @see #setMinimumFieldAccess
     * @see #setMinimumMethodAccess
     * @see #setStrategy
     */
    final public void setInjectAnnotation(@Nullable Class<? extends Annotation> injectAnnotation)
    {
        injectorState.injectAnnotation = injectAnnotation;
    }

    /**
     * @return the minimum access level that a field must have in order to have a resource value injected.
     * @see #setMinimumFieldAccess
     */
    final public AccessModifier getMinimumFieldAccess()
    {
        return injectorState.minimumFieldAccess;
    }

    /**
     * Set the minimum access modifier that a field must have in order to have its value set via property injection. Note that
     * final fields cannot be injected. Also, when injecting non-public fields, if the field  enforces Java language access control,
     * and the underlying field is inaccessible, the method {@code injectFields} throws an InjectFieldException, caused by
     * a  SecurityException.
     *
     * @param fieldAccessModifier an AccessModifier, one of PUBLIC, PROTECTED, DEFAULT, or PRIVATE.
     * This argument specifies the minimum access modifier that must be present on a field for injection to take place.
     * This object's default value is PUBLIC. When PUBLIC, only publicly accessible fields are injected with resource values.
     * If there are no public fields and no suitable setter method is available for a property, the property will not be injected.
     * PUBLIC is the most restrictive level of access control. The least restrictive is PRIVATE. When PRIVATE is used, any field
     * for which a resource exists in the ResourceMap is injected, regardless of the field's access level, since any public, protected,
     * or default fields exceed the PRIVATE access threshold.
     *
     * Note that when PUBLIC is used, the method {@code injectFields}  will never throw a SecurityException. If running with a SecurityManager
     * and using PRIVATE, DEFAULT, or PROTECTED, an InjectFieldException, (caused by a SecurityException) may be thrown
     * if the field is not accessible due to security constraints.
     *
     * @see #setMinimumMethodAccess
     * @see #setInjectAnnotation
     *
     */
    final public void setMinimumFieldAccess(@NotNull AccessModifier fieldAccessModifier)
    {
        assertNotNull(fieldAccessModifier, AccessModifier.class, "fieldAccessModifier");
        injectorState.minimumFieldAccess = fieldAccessModifier;
    }

    /**
     * @return the minimum access level that a setter method must have in order to have a resource value injected.
     * @see #setMinimumMethodAccess
     */
    final public AccessModifier getMinimumMethodAccess()
    {
        return injectorState.minimumMethodAccess;
    }

    /**
     * Set the minimum access modifier that a setter method must have in order to invoke it for property injection. Note that
     * when injecting via a non-public setter method, if the method  enforces Java language access control,
     * and the underlying method is inaccessible, the method {@code injectFields} throws an InjectFieldException, caused by
     * a  SecurityException.
     *
     * @param methodAccessModifier an AccessModifier, one of PUBLIC, PROTECTED, DEFAULT, or PRIVATE.
     * This argument specifies the minimum access modifier that must be present on a setter method for injection to take place.
     * This object's default value is PUBLIC. When PUBLIC, only publicly accessible setter methods are injected with resource values.
     * If there are no public setter methods and no suitable field is available for a property, the property will not be injected.
     * PUBLIC is the most restrictive level of access control. The least restrictive is PRIVATE. When PRIVATE is used, any setter method
     * for which a resource exists in the ResourceMap is injected, regardless of the method's access level, since any public, protected,
     * or default setter methods exceed the PRIVATE access threshold.
     *
     * Note that when PUBLIC is used, the method {@code injectFields}  will never throw a SecurityException. If running with a SecurityManager
     * and using PRIVATE, DEFAULT, or PROTECTED, an InjectFieldException, (caused by a SecurityException) may be thrown
     * if the setter method is not accessible due to security constraints.
     *
     * @see #setMinimumFieldAccess
     * @see #setInjectAnnotation
     */
    final public void setMinimumMethodAccess(@NotNull AccessModifier methodAccessModifier)
    {
        assertNotNull(methodAccessModifier, AccessModifier.class, "methodAccessModifier");
        injectorState.minimumMethodAccess = methodAccessModifier;
    }

    /**
     * @return the Strategy currently in use by this  instance
     * @see #setStrategy
     */
    final public Strategy getStrategy()
    {
        return injectorState.strategy;
    }

    /**
     * @param strategy Controls how resources are injected into an Object. If FIELD, only object fields are injected.
     * If METHOD, then only setter methods  are used. If EITHER, the property is injected if either a field
     * or setter method for that property is available. If both are present, the setter method will be used.
     */
    final public void setStrategy(@NotNull Strategy strategy)
    {
        assertNotNull(strategy, Strategy.class, "strategy");
        injectorState.strategy = strategy;
    }

    private boolean strategyIncludesMethod()
    {
        return injectorState.strategyIncludesMethod();
    }

    private boolean strategyIncludesField()
    {
        return injectorState.strategyIncludesMethod();
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
     * @param target      the object whose fields will be initialized.
     * @param resourceMap the ResourceMap containing the properties to use for injection
     * @throws InjectFieldException     if a field can't be set
     * @throws IllegalArgumentException if target is null or an array
     */
    public void injectFields(@NotNull Object target, @NotNull ResourceMap resourceMap)
    {
        injectFields_impl(target, resourceMap, target.getClass().getSimpleName(), getInjectAnnotation());
    }

    public void injectFields(@NotNull Object target, @NotNull ResourceMap resourceMap, @NotNull String resourcePrefix)
    {
        injectFields_impl(target, resourceMap, resourcePrefix, getInjectAnnotation());
    }

    public void injectFields(@NotNull Object target, @NotNull ResourceMap resourceMap, @Nullable Class<? extends Annotation> annotationType)
    {
        injectFields_impl(target, resourceMap, target.getClass().getSimpleName(), annotationType);
    }

    public void injectFields(@NotNull Object target, @NotNull ResourceMap resourceMap, @NotNull String resourcePrefix, @Nullable Class<? extends Annotation> annotationType)
    {
        injectFields_impl(target, resourceMap, resourcePrefix, annotationType);
    }

    @Override
    public String toString()
    {
        return injectorState.toString();
    }

    private final Map<InjectorCacheEntry.InjectorCacheKey, InjectorCacheEntry> cacheMap = new HashMap<InjectorCacheEntry.InjectorCacheKey, InjectorCacheEntry>();

    private static class InjectorCacheEntry
    {
        private final Class<?> targetType;
        private final InjectorState state;
        private final List<MemberLookupEntry<? extends Member>> members;


        private InjectorCacheEntry(Class<?> targetType, InjectorState state, Class<? extends Annotation> annotationType)
        {
            this.targetType = targetType;
            this.state = new InjectorState(state, annotationType);
            members = new ArrayList<MemberLookupEntry<? extends Member>>();
        }

        public void addMember(MemberLookupEntry<? extends Member> member)
        {
            if (!members.contains(member))
            {
                members.add(member);
            }
        }

        public Class<?> getTargetType()
        {
            return targetType;
        }

        public InjectorState getState()
        {
            return state;
        }

        public InjectorCacheKey generateKey()
        {
            return new InjectorCacheKey(getTargetType(), getState());
        }

        private static class InjectorCacheKey
        {
            private final Class<?> targetType;
            private final InjectorState state;


            private InjectorCacheKey(Class<?> targetType, InjectorState state)
            {
                this.targetType = targetType;
                this.state = state;
            }

            public InjectorState getState()
            {
                return state;
            }

            public Class<?> getTargetType()
            {
                return targetType;
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

                InjectorCacheKey that = (InjectorCacheKey) o;

                if (state != null ? !state.equals(that.getState()) : that.getState() != null)
                {
                    return false;
                }
                if (targetType != null ? !targetType.equals(that.getTargetType()) : that.getTargetType() != null)
                {
                    return false;
                }

                return true;
            }

            @Override
            public int hashCode()
            {
                int result = targetType != null ? targetType.hashCode() : 0;
                result = 31 * result + (state != null ? state.hashCode() : 0);
                return result;
            }
        }
    }

    private void injectFields_impl(Object target, ResourceMap resourceMap, String keyPrefix, Class<? extends Annotation> annotationType)
    {
        assertNotNull(target, Object.class, "target");
        assertNotNull(resourceMap, ResourceMap.class, "resourceMap");
        assertNotNull(keyPrefix, String.class, "resourcePrefix");
        Class<?> targetType = target.getClass();
        if (targetType.isArray())
        {
            throw new IllegalArgumentException("array target");
        }

        //if we have previously injected this type of class, we can use the cached Members to know what can be injected based
        //on the current state of this injector
        InjectorCacheEntry cacheEntry = cacheMap.get(new InjectorCacheEntry.InjectorCacheKey(targetType, new InjectorState(injectorState, annotationType)));
        if (cacheEntry != null)
        {
            injectMembers(cacheEntry, target, keyPrefix, resourceMap);
            //todo refactor this method to have a single exit point
            return;
        }
        else
        {
            cacheEntry = new InjectorCacheEntry(targetType, injectorState,annotationType);
            cacheMap.put(cacheEntry.generateKey(), cacheEntry);
        }

        List<Field> eligibleFields = Collections.emptyList();
        Map<String, MemberLookupEntry<Field>> fieldMap = Collections.emptyMap();
        if (strategyIncludesField())
        {
            eligibleFields = getEligibleFields(targetType, annotationType);
            fieldMap = generateMemberMap(eligibleFields, keyPrefix, annotationType );
/*            //todo - remove println
            System.out.println(String.format("elgfield = %d, fieldMap = %s", eligibleFields.size(), fieldMap.size()));
            for (Field ef : eligibleFields)
            {
                System.out.println(String.format("field: %s", ef.getName()));
            }
            System.out.println(String.format("fieldMap:"));
            for (Map.Entry<String, MemberLookupEntry<Field>> es : fieldMap.entrySet())
            {
                System.out.println(String.format("%s, %s", es.getKey(), es.getValue()));
            }*/
        }

        List<Method> eligibleMethods = Collections.emptyList();
        Map<String, MemberLookupEntry<Method>> methodMap = Collections.emptyMap();
        if (strategyIncludesMethod())
        {
            eligibleMethods = getEligibleMethods(targetType, annotationType);
            methodMap = generateMemberMap(eligibleMethods, keyPrefix, annotationType);
/*            //todo - remove println
            System.out.println(String.format("eligibleMethods = %d, methodMap = %s", eligibleMethods.size(), methodMap.size()));
            for (Method em : eligibleMethods)
            {
                System.out.println(String.format("method: %s", em.getName()));
            }
            for (Map.Entry<String, MemberLookupEntry<Method>> es : methodMap.entrySet())
            {
                System.out.println(String.format("%s, %s", es.getKey(), es.getValue()));    
            }*/
        }

        if (strategyIncludesField())
        {
            for (Map.Entry<String, MemberLookupEntry<Field>> en : fieldMap.entrySet())
            {
                String fieldKey = en.getKey();
                MemberLookupEntry<Field> field = en.getValue();
                
                if ((injectorState.strategy == EITHER) && (methodMap.get(fieldKey) != null))
                {
                    //this  property has a setter, and so, we'll the setter instead of the field
                    MemberLookupEntry<Method> setter = methodMap.get(fieldKey);
                   
                    if (!field.annotationKey.isEmpty() && !setter.annotationKey.isEmpty() && (!field.annotationKey.equals(setter.annotationKey)))
                    {
                        //both the field and method have a value for "key" property, but they are not the same. We'll log the issue, but use the
                        //methdod's version for lookup
                        logger.warning(String.format("Annotation @%s 'key' property differs between \nfield %s.%s  and  " +
                                "\nmethod %s.%s\nfield:  key=%s\nmethod: key=%s   - method version will be used."
                                , annotationType.getName(), field.members.get(0).getDeclaringClass().getName(),field.propertyName,
                                  setter.members.get(0).getDeclaringClass().getName(), setter.methodName, field.annotationKey,setter.annotationKey));
                    }
                    
                   
                    if (setter.annotationKey.isEmpty() && !field.annotationKey.isEmpty())
                    {
                        //if the field has defined an Annotation "key" property, but the setter has not, use the field's key definition
                        setter.annotationKey = field.annotationKey; //when setter is invoked, it will use this key to look in the ResourceMap
                    }
                    logger.fine(String.format("skipping injection of eligible field '%s' in favor of method '%s'", field.propertyName, setter.methodName));
                    continue;
                }
                else
                {
                    //no setter, just a field
                    cacheEntry.addMember(field);
                   // injectField(field.members.get(0), target, field.getResourceKey(keyPrefix), resourceMap);
                }
             
            }
        }

        if (strategyIncludesMethod())
        {
            for (Map.Entry<String, MemberLookupEntry<Method>> en : methodMap.entrySet())
            {
                MemberLookupEntry<Method> methodEntry = en.getValue();
                Method m = findBestMethodForInjection(methodEntry);
                if (m != null)
                {
                    cacheEntry.addMember(methodEntry);
                    //injectMethod(m,target, methodEntry.getResourceKey(keyPrefix),resourceMap );
                }
            }
        }

        injectMembers(cacheEntry, target, keyPrefix, resourceMap);
    }

    private void injectMembers(InjectorCacheEntry cacheEntry, Object target, String keyPrefix, ResourceMap resourceMap)
    {
        for (MemberLookupEntry member : cacheEntry.members)
        {
            if (Field.class == member.getEntryType())
            {
                Field field = (Field) member.members.get(0);
                injectField(field, target, member.getResourceKey(keyPrefix), resourceMap);
            }
            else if (Method.class == member.getEntryType())
            {

                Method m = findBestMethodForInjection(member);
                if (m != null)
                {
                    injectMethod(m, target, member.getResourceKey(keyPrefix), resourceMap);
                }
            }
        }
    }


    private Method findBestMethodForInjection(MemberLookupEntry<Method> methodEntry)
    {
        if (methodEntry.members.isEmpty())
        {
            return null; // no methods
        }
        List<Method> methods = methodEntry.members;
        if (methods.size() == 1)
        {
            return methods.get(0);
        }

        //we're just testing a specific situation : are there multiple one-arg setters, with one version taking int
        //if so, favor the int argument. Otherwise, just pick the first one. We can further refine this method later
        for (Method m : methods)
        {
            Class<?>[] types = m.getParameterTypes();
            if (types.length != 1)
            {
                //shouldn't happen, but this is a problem
                continue;
            }
            if (types[0] == Integer.TYPE)
            {
                return m; //this method takes an int argument
            }
        }

        //no int arg, just return the first method for now
        //later, maybe we compare the argument types to the list of ResourceConverter types, and choose one of those over a type
        //we do not have a converter for
        return methods.get(0);
    }

    //generates a map of method name to Method object to make finding a setter easier
    private <T extends Member & AnnotatedElement> Map<String, MemberLookupEntry<T>> generateMemberMap(List<T> eligibleMembers, String keyPrefix, @Nullable Class<? extends Annotation> annotationType)
    {
        Map<String, MemberLookupEntry<T>> map = new HashMap<String, MemberLookupEntry<T>>(eligibleMembers.size());
        for (T m : eligibleMembers)
        {
            String mapKey;
            if (m instanceof Method)
            {
                mapKey = keyPrefix + "." + propertyNameFromeSetter(m.getName());
            }
            else
            {
                mapKey = keyPrefix + "." + m.getName();
            }

            MemberLookupEntry<T> entry = map.get(mapKey);
            if (entry == null)
            {
                //not in map yet
                map.put(mapKey, new MemberLookupEntry<T>(m, annotationType, keyPrefix));
            }
            else
            {
                //already in map, so we're adding an overloaded version of the method
                entry.addMember(m);
            }
        }
        return map;
    }

    private String propertyNameFromeSetter(String setterName)
    {
        StringBuilder propName = new StringBuilder(setterName.substring(3, setterName.length()));
        propName.setCharAt(0, Character.toLowerCase(propName.charAt(0)));
        return propName.toString();
    }

    private class MemberLookupEntry<T extends Member & AnnotatedElement>
    {
        String methodName; //name of setter method, if Member is a Method, otherwise same as propertyName
        String propertyName; //the JavaBean property name, i.e., either the Field name, or the setter name minus the "set" part
        String annotationKey = ""; //if no annotation, or annotation has no "key" value, this will be the empty string
        List<T> members = new ArrayList<T>();//methods may be overloaded
        Class<?> entryType;

        private MemberLookupEntry(T m, Class<? extends Annotation> annotationType, String keyPrefix)
        {
            entryType = m.getClass();
            this.methodName = m.getName();
            members.add(m);
            if (m instanceof Field)
            {
                propertyName = m.getName();
            }
            else
            {
                propertyName = propertyNameFromeSetter(methodName);
            }
            if (m.getAnnotation(annotationType) != null)
            {
                annotationKey = getValueFromAnnotationProperty(m.getAnnotation(annotationType), "key", String.class);
            }
        }

        /**
         *
         * @param keyPrefix the prefix used for a  set of properties in a ResourceMap. Normally equals the name property
         * of a Component type object, or the simple name of the target Object's class.
         * @return the key used to find the property value in a ResourceMap. If annotationKey is not empty,
         * then resourceKey=annotationKey. Otherwise, resourceKey = keyPrefix + "." + propertyName
         */
        public String getResourceKey(String keyPrefix)
        {
            if (!annotationKey.isEmpty())
            {
                return annotationKey;
            }
            else
            {
                return  keyPrefix + "." + propertyName;
            }
        }

        public Class<?> getEntryType()
        {
            return entryType;
        }

        public void addMember(T m)
        {
            if (!members.contains(m))
            {
                members.add(m);
            }
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            for (T m : members)
            {
                sb.append(m.getName()).append(" ");
            }
            return "MemberLookupEntry{" +
                    "propertyName='" + propertyName + '\'' +
                    ", methodName='" + methodName + '\'' +
                    ", annotationKey='" + annotationKey + '\'' +
                    ", members= {" + sb.toString() + "} " +
                    '}';
        }
    }

    /**
     * @param targetType     the Class of the Object being injected
     * @param annotationType if not null, we only add those Methods annotated with this Annotation type
     * @return the set of all methods that could potentially be injected based on the current values of
     *         strategy, accessType, and injectableAnnotation
     */
    private List<Method> getEligibleMethods(Class<?> targetType, Class<? extends Annotation> annotationType)
    {
        List<Method> methods = new ArrayList<Method>();
        AccessModifier access = getMinimumMethodAccess();

        List<Method> mL = new ArrayList<Method>();
        if (access == PUBLIC)
        {
            Method[] mA = targetType.getMethods(); //all public methods for this Class and its superclasses
            mL.addAll(Arrays.asList(mA));
        }
        else
        {
            //have to traverse the Class hierarchy to get all methods
            Class<?> node = targetType;
            while (node != null)
            {
                Method[] mA = node.getDeclaredMethods();
                //we need to filter out any methods that have a weaker access modifier than accessType
                mL.addAll(filterOutWeakerAccess(mA, access));
                node = node.getSuperclass();
            }
        }

        if (annotationType != null)
        {
            for (Method m : mL)
            {
                if (!(m.getName().startsWith("set") && m.getName().length() > 3))
                {
                    continue; //it's not a setter method
                }
                if (m.getAnnotation(annotationType) != null)
                {
                    //annotation is present
                    //we only want methods with a single parameter
                    if (m.getParameterTypes().length == 1)
                    {
                        methods.add(m);
                    }
                }
            }
        }
        else
        {
            //no annotation is set so all fields are eligible for injection
            for (Method m : mL)
            {
                if (!(m.getName().startsWith("set") && m.getName().length() > 3))
                {
                    continue; //it's not a setter method
                }
                //we only want methods with a single parameter
                if (m.getParameterTypes().length == 1)
                {
                    methods.add(m);
                }
            }
        }
        return methods;
    }


    /**
     * @param targetType     the Class of the Object being injected
     * @param annotationType if not null, we only add those Fields annotated with this Annotation type
     * @return the set of all fields that could potentially be injected based on the current values of
     *         strategy, accessType, and injectableAnnotation
     */
    private List<Field> getEligibleFields(Class<?> targetType, @Nullable Class<? extends Annotation> annotationType)
    {
        List<Field> fields = new ArrayList<Field>();
        AccessModifier access = getMinimumFieldAccess();

        List<Field> fL = new ArrayList<Field>();
        if (access == PUBLIC)
        {
            Field[] fA = targetType.getFields(); //all public fields for this Class and its superclasses
            fL.addAll(Arrays.asList(fA));
        }
        else
        {
            //have to traverse the Class hierarchy to get all fields
            Class<?> node = targetType;
            while (node != null)
            {
                Field[] fA = node.getDeclaredFields();
                //we need to filter out any fields that have a weaker access modifier than accessType
                fL.addAll(filterOutWeakerAccess(fA, access));
                node = node.getSuperclass();
            }
        }

        if (annotationType != null)
        {
            for (Field f : fL)
            {
                if (f.getAnnotation(annotationType) != null)
                {
                    //required annotation is present
                    if (fieldNotFinal(f))
                    {
                        fields.add(f);
                    }
                    else
                    {
                        //Annotation present on a final field!
                        logger.log(Level.WARNING, String.format("final Field '%s' in class %s is annotated with %s, " +
                                "but final cannot be modified", f.getName(), f.getDeclaringClass().getName(), annotationType.getName()));
                    }
                }
            }
        }
        else
        {
            //no annotation is set so all non-final fields are eligible for injection
            for (Field f : fL)
            {
                if (fieldNotFinal(f))
                {
                    fields.add(f);
                }
            }
        }
        return fields;
    }

    //return true only if the argument field is not marked as final
    private boolean fieldNotFinal(Field field)
    {
        return ! Modifier.isFinal(field.getModifiers());
    }


    //filters out of the argument array those members with weaker access modifiers than the access argument and returns
    //result as a list
    private <T extends Member> List<T> filterOutWeakerAccess(T[] members, AccessModifier access)
    {
        List<T> ml = new ArrayList<T>();

        for (T member : members)
        {
            if (accessThresholdIsMet(access, member))
            {
                ml.add(member);
            }
        }
        return ml;
    }

    private boolean accessThresholdIsMet(AccessModifier minimumLevel, Member memberToCheck)
    {
        if (AccessModifier.valueOf(memberToCheck).getOrdinal() >= minimumLevel.getOrdinal())
        {
            return true;
        }
        return false;
    }


    @SuppressWarnings({"UnusedDeclaration"})
    private void injectFields_implOLD(Object target, ResourceMap resourceMap, String resourcePrefix, Class<? extends Annotation> annotationType)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("null target");
        }
        Class<?> targetType = target.getClass();
        if (targetType.isArray())
        {
            throw new IllegalArgumentException("array target");
        }
        String keyPrefix = resourcePrefix + ".";
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
                //don't care, return empty string
            }
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
            Object value = resourceMap.getResourceAs(key, type);
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

    protected void assertNotNull(Object o, Class type, String paramName)
    {
        if (o == null)
        {
            throw new IllegalArgumentException(String.format("parameter '%s' of type '%s' cannot be null.", paramName, type));
        }
    }
}
