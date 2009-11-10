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
 * @version Date: Nov 5, 2009  3:08:09 AM
 */
public class JLabelInjector extends ResourceInjector<JLabel>
{

    public JLabelInjector()
    {
        super(JLabel.class);
    }

    @Override
    public JLabel inject(@NotNull JLabel jLabel, @NotNull ResourceMap resourceMap, boolean recursively)
    {
        assertNotNull(jLabel, JLabel.class, "jLabel");
        assertNotNull(resourceMap, ResourceMap.class, "resourceMap");
        String targetName = getTargetName(jLabel);

        injectProperties(jLabel, targetName, resourceMap);
        return jLabel;
    }

    @Override
    protected void injectProperty(Object target, PropertyDescriptor pd, String key, ResourceMap properties)
    {
        if (!(target instanceof JLabel))
        {
            throw new IllegalArgumentException("First argument must be an AbstractButton");
        }
        JLabel jLabel = (JLabel) target;
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
                        jLabel.setText(value.getText());
                        if (value.getMnemonic() != KeyEvent.VK_UNDEFINED)
                        {
                            jLabel.setDisplayedMnemonic(value.getMnemonic());
                        }
                        if (value.getDisplayedMnemonicIndex() != -1)
                        {
                            jLabel.setDisplayedMnemonicIndex(value.getDisplayedMnemonicIndex());
                        }
                    }
                    else
                    {
                        jLabel.setText(null); //this duplicates the existing injection functionality, but I question if this is really what we should do
                    }
                }
                else
                {
                    Object value = properties.getResourceAs(key, type, null);
                    setter.invoke(jLabel, value);
                }
            }
            catch (Exception e)
            {
                String pdn = pd.getName();
                String msg = "property setter failed";
                RuntimeException re = new PropertyInjectionException(msg, key, jLabel, pdn);
                re.initCause(e);
                throw re;
            }
        }
        else if (type != null)
        {
            String pdn = pd.getName();
            String msg = "no value specified for resource";
            throw new PropertyInjectionException(msg, key, jLabel, pdn);
        }
        else if (setter == null)
        {
            String pdn = pd.getName();
            String msg = "can't set read-only property";
            throw new PropertyInjectionException(msg, key, jLabel, pdn);
        }
    }
}
