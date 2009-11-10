package org.jdesktop.application.inject;

import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.MnemonicTextValue;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * @author Rob Ross
 * @version Date: Nov 5, 2009  2:52:35 AM
 */
public class AbstractButtonInjector extends ResourceInjector<AbstractButton>
{
    public AbstractButtonInjector()
    {
        super(AbstractButton.class);
    }

    /**
     * @param target
     * @param properties
     * @param recursively ignored for this injector implementation
     * @return
     */
    @Override
    public AbstractButton inject(@NotNull AbstractButton button, @NotNull ResourceMap resourceMap, boolean recursively)
    {
        assertNotNull(button, AbstractButton.class, "button");
        assertNotNull(resourceMap, ResourceMap.class, "resourceMap");
        String targetName = getTargetName(button);

        injectProperties(button, targetName, resourceMap);
        return null;
    }

    @Override
    protected void injectProperty(Object target, PropertyDescriptor pd, String key, ResourceMap properties)
    {
        if (!(target instanceof AbstractButton))
        {
            throw new IllegalArgumentException("First argument must be an AbstractButton");
        }
        AbstractButton button = (AbstractButton) target;
        Method setter = pd.getWriteMethod();
        Class<?> type = pd.getPropertyType();
        if ((setter != null) && (type != null) && properties.containsKey(key))
        {
            String propertyName = pd.getName();
            try
            {
                if ("text".equals(propertyName))
                {
                    MnemonicTextValue value = properties.getResourceAs(key, MnemonicTextValue.class, null);
                    if (value != null)
                    {
                        button.setText(value.getText());
                        if (value.getMnemonic() != KeyEvent.VK_UNDEFINED)
                        {
                            button.setMnemonic(value.getMnemonic());
                        }
                        if (value.getDisplayedMnemonicIndex() != -1)
                        {
                            button.setDisplayedMnemonicIndex(value.getDisplayedMnemonicIndex());
                        }
                    }
                    else
                    {
                        button.setText(null); //this duplicates the existing injection functionality, but I question if this is really what we should do
                    }
                }
                else
                {
                    Object value = properties.getResourceAs(key, type, null);
                    setter.invoke(button, value);
                }
            }
            catch (Exception e)
            {
                String pdn = pd.getName();
                String msg = "property setter failed";
                RuntimeException re = new PropertyInjectionException(msg, key, button, pdn);
                re.initCause(e);
                throw re;
            }
        }
        else if (type != null)
        {
            String pdn = pd.getName();
            String msg = "no value specified for resource";
            throw new PropertyInjectionException(msg, key, button, pdn);
        }
        else if (setter == null)
        {
            String pdn = pd.getName();
            String msg = "can't set read-only property";
            throw new PropertyInjectionException(msg, key, button, pdn);
        }
    }
}
