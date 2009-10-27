package org.jdesktop.application.convert;


import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @author Rob Ross
 * @version Date: Oct 9, 2009  9:14:35 PM
 */
public class StringToKeyStroke extends ResourceConverter<String, KeyStroke>

{
    public StringToKeyStroke()
    {
        super(String.class, KeyStroke.class);
    }

    private static final int PLATFORM_MENU_SHORTCUT_KEYMASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    public KeyStroke convert(@NotNull String s, Object... args) throws StringConvertException
    {
        String lowerCase = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s);

        if (lowerCase.contains("shortcut"))
        {
            int start = lowerCase.indexOf("shortcut");
            int end =   start + "shortcut".length();
            sb.replace(start, end, (PLATFORM_MENU_SHORTCUT_KEYMASK == Event.META_MASK) ? "meta" : "control");
        }
        if (lowerCase.contains("command"))//for Mac OS
        {
            int start = lowerCase.indexOf("command");
            int end = start + "command".length();
            sb.replace(start, end, "meta"); //meta mapped to command key on Mac
        }
        KeyStroke ks = KeyStroke.getKeyStroke(sb.toString());
        if (ks == null)
        {
            //before we give up, see if there are improper capitalization errors. getKeyStroke() wants these exactly
            //capitalized correctly
            ks = maybeFixSyntaxrrors(sb.toString());
        }
        return ks;
    }

    //these keywords need to be lower case to match properly in AWTKeyStroke.getAWTKeyStroke()
    private static final String[] lowerCaseKeywords = {"shift", "control", "ctrl", "meta", "alt", "altGraph",
            "button1", "button2", "button3", "released", "pressed", "typed"};

    private KeyStroke maybeFixSyntaxrrors(String s)
    {
        //all VK keys should be capitalized. Modifiers should be lower case.
        StringBuilder caps = new StringBuilder(s.toUpperCase());

        for (String keyword : lowerCaseKeywords)
        {
            int index = caps.indexOf(keyword.toUpperCase());
            if (index != -1)
            {
                caps.replace(index, keyword.length(), keyword); //replace with lower case version of keyword
            }
        }
        //if "typed" is presenent, restore the character that follows to the original capitalization state
        int index = caps.indexOf("typed");
        if (index != -1)
        {
            int start = index + "typed".length()+1;
            char c = caps.charAt(start);
            while (Character.isWhitespace(c) && start < caps.length()-1)
            {
                c = caps.charAt(++start);
            }
            if (!Character.isWhitespace(c))
            {
                caps.setCharAt(start, s.charAt(start)); //restore the original case of the character
            }
        }
        return KeyStroke.getKeyStroke(caps.toString());
    }
}
