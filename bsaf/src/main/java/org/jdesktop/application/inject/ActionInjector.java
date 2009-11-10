package org.jdesktop.application.inject;

import org.jetbrains.annotations.NotNull;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.MnemonicTextValue;

import javax.swing.*;
import java.awt.event.KeyEvent;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * @author Rob Ross
 * @version Date: Nov 5, 2009  3:24:31 AM
 */
public class ActionInjector extends ResourceInjector<Action>
{
    public ActionInjector()
    {
        super(Action.class);
    }

    /**
     * Assumes the Action's NAME property has been set to the base name of the group of properties for this action.
     *
     * @param action the Action to inject with values from the ResourceMap in the second argument
     * @param resourceMap the ResourceMap containing the properties used to inject values into the Action
     * @param recursively ignored by this implementation
     * @return the same Action as in the first argument, as modified by injection
     */
    @Override
    public Action inject(@NotNull Action action, @NotNull ResourceMap resourceMap, boolean recursively)
    {
        assertNotNull(action, Action.class, "action");
        assertNotNull(resourceMap, ResourceMap.class, "resourceMap");

        String baseName = (String) action.getValue(Action.NAME);
        if (baseName == null || baseName.isEmpty())
        {
            throw new IllegalArgumentException("Action argument must have its NAME property set to the base name of the resource properties.");
        }
        action.putValue(Action.NAME, null); //this will be set by the "text" property, or to the baseName if no "text" or icon is set

        boolean iconOrNameSpecified = false;  // true if Action's icon/name properties set

        // Action.text => Action.NAME,MNEMONIC_KEY,DISPLAYED_MNEMONIC_INDEX_KEY
        String textKey = baseName + ".Action.text";
        if (resourceMap.containsKey(textKey))
        {
            String text = resourceMap.getAsString(textKey, null);
            if (text != null)
            {
                MnemonicTextValue value = resourceMap.getResourceAs(textKey, MnemonicTextValue.class, null);

                action.putValue(javax.swing.Action.NAME, value.getText());
                if (value.getMnemonic() != KeyEvent.VK_UNDEFINED)
                {
                    action.putValue(Action.MNEMONIC_KEY, value.getMnemonic());
                }
                if (value.getDisplayedMnemonicIndex() != -1)
                {
                    action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, value.getDisplayedMnemonicIndex());
                }
                iconOrNameSpecified = true;
            }
        }
        // Action.mnemonic => Action.MNEMONIC_KEY
        KeyStroke mnemonicKS = resourceMap.getAsKeyStroke(baseName + ".Action.mnemonic", null);
        if (mnemonicKS != null)
        {
            action.putValue(Action.MNEMONIC_KEY, mnemonicKS.getKeyCode());
        }
        // Action.mnemonic => Action.DISPLAYED_MNEMONIC_INDEX_KEY
        Integer index = resourceMap.getAsInteger(baseName + ".Action.displayedMnemonicIndex", null);
        if (index != null)
        {
            action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, index);
        }
        // Action.accelerator => Action.ACCELERATOR_KEY
        KeyStroke key = resourceMap.getAsKeyStroke(baseName + ".Action.accelerator", null);
        if (key != null)
        {
            action.putValue(Action.ACCELERATOR_KEY, key);
        }
        // Action.icon => Action.SMALL_ICON,LARGE_ICON_KEY
        Icon icon = resourceMap.getAsImageIcon(baseName + ".Action.icon", null);
        if (icon != null)
        {
            action.putValue(Action.SMALL_ICON, icon);
            action.putValue(Action.LARGE_ICON_KEY, icon);
            iconOrNameSpecified = true;
        }
        // Action.smallIcon => Action.SMALL_ICON
        Icon smallIcon = resourceMap.getAsImageIcon(baseName + ".Action.smallIcon", null);
        if (smallIcon != null)
        {
            action.putValue(Action.SMALL_ICON, smallIcon);
            iconOrNameSpecified = true;
        }
        // Action.largeIcon => Action.LARGE_ICON
        Icon largeIcon = resourceMap.getAsImageIcon(baseName + ".Action.largeIcon", null);
        if (largeIcon != null)
        {
            action.putValue(Action.LARGE_ICON_KEY, largeIcon);
            iconOrNameSpecified = true;
        }
        // Action.shortDescription => Action.SHORT_DESCRIPTION
        String shortDescription = resourceMap.getAsString(baseName + ".Action.shortDescription", null);
        if (shortDescription != null && !shortDescription.isEmpty())
        {
            action.putValue(Action.SHORT_DESCRIPTION,
                     resourceMap.getAsString(baseName + ".Action.shortDescription", null));
        }
        // Action.longDescription => Action.LONG_DESCRIPTION
        action.putValue(Action.LONG_DESCRIPTION,
                 resourceMap.getAsString(baseName + ".Action.longDescription", null));
        // Action.command => Action.ACTION_COMMAND_KEY
        action.putValue(Action.ACTION_COMMAND_KEY,
                 resourceMap.getAsString(baseName + ".Action.command", null));
        // If no visual was defined for this Action, i.e. no text
        // and no icon, then we default Action.NAME
        if (!iconOrNameSpecified)
        {
            action.putValue(Action.NAME, baseName);
        }
        return action;
    }
}
