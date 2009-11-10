package org.jdesktop.application.inject;

import org.jetbrains.annotations.NotNull;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;


/**
 * @author Rob Ross
 * @version Date: Nov 9, 2009  3:41:39 PM
 */
public class JMenuInjector extends ResourceInjector<JMenu>
{
    public JMenuInjector()
    {
        super(JMenu.class);
    }

    @Override
    public JMenu inject(@NotNull JMenu target, @NotNull ResourceMap properties, boolean recursively)
    {
        assertNotNull(target, JMenu.class, "target");
        assertNotNull(properties, ResourceMap.class, "properties");

        inject_impl(target, properties, recursively);
        return target;
    }

    private void inject_impl(JMenu target, ResourceMap properties, boolean recursively)
    {
        /* Warning: we're bypassing the popupMenu here because
        * JMenu#getPopupMenu creates it; doesn't seem right
        * to do so at injection time.  Unfortunately, this
        * means that attempts to inject the popup menu's
        * "label" property will fail.
        */

        String targetName = getTargetName(target);
        injectProperties(target, targetName, properties);
        if (recursively)
        {
            for (Component child : target.getMenuComponents())
            {
                ResourceInjector<Component> ri = getInjectorRegistry().injectorFor(child);
                //todo - remove println
                //System.out.println(String.format("JMenuInjector : ri for child = %s", ri));
                ri.inject(child, properties, true);
                //inject_impl(child, properties, true);
            }
        }
    }
}
