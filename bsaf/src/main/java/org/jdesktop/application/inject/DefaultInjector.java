package org.jdesktop.application.inject;

import org.jetbrains.annotations.NotNull;
import org.jdesktop.application.ResourceMap;

import java.awt.*;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * A ResourceInjector that can inject properties into any Object.
 *
 * @author Rob Ross
 * @version Date: Nov 6, 2009  12:34:01 AM
 */
public class DefaultInjector extends ResourceInjector<Object>
{
    public DefaultInjector()
    {
        super(Object.class);
    }

    /**
     *
     * @param target
     * @param properties
     * @param recursively ignored for this implementation, since it is unknown if the Object has any kind of container structure
     * @return the target argument
     */
    @Override
    public Object inject(@NotNull Object target, @NotNull ResourceMap properties, boolean recursively) throws PropertyInjectionException
    {
        assertNotNull(target, Object.class, "target");
        assertNotNull(properties, ResourceMap.class, "properties");
        
        String targetName = getTargetName(target);
        injectProperties(target, targetName, properties);
        return target;
    }
}
