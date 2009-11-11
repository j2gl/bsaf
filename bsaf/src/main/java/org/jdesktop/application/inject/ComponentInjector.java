package org.jdesktop.application.inject;

import org.jetbrains.annotations.NotNull;
import org.jdesktop.application.ResourceMap;


import java.util.logging.Logger;
import java.awt.*;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * @author Rob Ross
 * @version Date: Nov 5, 2009  1:34:03 AM
 */
public class ComponentInjector extends ResourceInjector<Component>
{
    private static final Logger logger = Logger.getLogger("ComponentInjector");

    public ComponentInjector()
    {
        super(Component.class);
    }


    @Override
    public Component inject(@NotNull Component target, @NotNull ResourceMap properties, boolean recursively) throws PropertyInjectionException
    {
        assertNotNull(target, Component.class, "target");
        assertNotNull(properties, ResourceMap.class, "properties");

        inject_impl(target, properties, recursively);
        return target;
    }


    private Component inject_impl(Component target, ResourceMap properties, boolean recursively)
    {
        String targetName = getTargetName(target);
        injectProperties(target, targetName, properties);
        if (target instanceof Container && recursively)
        {
            Container container = (Container) target;
            for (Component child : container.getComponents())
            {
                ResourceInjector<Component> ri = getInjectorRegistry().injectorFor(child);
                //todo - remove println
               // System.out.println(String.format("ComponentInjector : child = %s, ri = %s",child.getClass().getSimpleName(), ri));
                ri.inject(child,properties,true);
            }
        }
        return target;
    }

}
