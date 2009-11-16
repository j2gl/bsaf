
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */
package org.jdesktop.application;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Standard annotation used to declare fields or methods as injectable. An injectable property can have its value
 * automatically assigned with a value from a property file. (Note that final fields cannot be injected.)
 *  An annotated method must still adhere to JavaBeans
 * naming conventions in order to be egligible for injection; i.e., it must be of the form setXXXX, and take a single
 * argument of a type for which a ResouceConverter exists. Various injector implementations may have different rules
 * for what types of Members are eligible for injection. For example, an implementation may only allow injection
 * on public fields or methods, in which case that injector would ignore a private field or method, even if annotated
 * with this Annotation type. Some implementations may only permit methods to be injected, and so annotated fields
 * would be ignored. Finally, some injector implementations may not even require members to be annotated in order to
 * be injected. See {@code org.jdesktop.application.inject.AnnotatedFieldInjector } for an example of an injector
 * implementation that has various properties to control resource injection.
 *
 * Property values used for injection are obtained from a ResourceMap. The different injector implementation have various
 * strategies for determining the resource key to use to retrieve a property from a ResourceMap. The typical strategy for
 * a java.awt.Component is to set its name property via {@code setName} to some value, which is then used as the prefix
 * for all resources in the ResourceMap that pertain to this object. Then when an injector method is called on the object,
 * all properties in the ResourceMap with keys that begin with this prefix are matched to any fields or methods in the object
 * that match the key, and if the injector settings allow, the fields or methods are injected with the values of the
 * corresponding keys.
 *
 * For example, a JButton's name is set to be "btn1". In a property file, two properties are defined as:
 *
 * btn1.text = OK
 * btn1.enabled = false
 *
 * Assuming these properties have been loaded into a ResourceMap, when inject is called, passing in the JButton object
 * and the ResourceMap, the button's "text" property will be set to "OK", and its "enabled" property will be set to false.
 *
 * For objects that are not Components (and thus have no "name" property), the simple name of the object's class is used
 * as the prefix key. For example, the key prefix of a Point object would be "Point". You could then define these properties:
 * Point.x = 5
 * Point.y = 10
 *
 * and when you called inject for this object, its "x" property would be set to 5 and its "y" property would be set to 10.
 *
 * Note that with this scheme, ALL Point objects would be injected with the same values. However, various versions of the
 * {@code inject} method take a String "prefix" argument that is used by the injector as the prefix key. So you could call
 * inject on two different Point instances, passing in two different prefix strings, and thus use two different sets of
 * properties for the injection. For example,
 *
 * pointA.x=10
 * pointA.y=10
 * pointB.x=20
 * pointB.y=20
 *
 * If inject is called for the first Point using prefix "pointA", and for the second Point with prefix "pointB", then
 * each Point would be assigned the corresponding property values. 
 *
 *
 * @author Rob Ross
 * @author Hans Muller (Hans.Muller@Sun.COM)
 *
 * @see org.jdesktop.application.inject.AnnotatedFieldInjector
 * @see org.jdesktop.application.inject.ResourceInjector
 * @see org.jdesktop.application.convert.ResourceConverter
 * @see org.jdesktop.application.ResourceMap
 */

@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD, ElementType.METHOD})
public @interface Resource {

    String key() default "";
}
