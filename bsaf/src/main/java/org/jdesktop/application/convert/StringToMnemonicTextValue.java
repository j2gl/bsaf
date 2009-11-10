package org.jdesktop.application.convert;

import org.jdesktop.application.MnemonicTextValue;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;


/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * @author Rob Ross
 * @version Date: Oct 29, 2009  1:58:03 PM
 */
public class StringToMnemonicTextValue extends ResourceConverter<String, MnemonicTextValue>
{

    public StringToMnemonicTextValue()
    {
        super(String.class, MnemonicTextValue.class);
    }

    @Override
    public MnemonicTextValue convert(@NotNull String markedText, Object... args) throws StringConvertException
    {
        assertNotNull(markedText, String.class, "markedText");
        String text = markedText;
        int mnemonicIndex = -1;
        int mnemonicKey = KeyEvent.VK_UNDEFINED;
        // TBD: mnemonic marker char should be an application resource
        int markerIndex = mnemonicMarkerIndex(markedText, '&');
        if (markerIndex == -1)
        {
            markerIndex = mnemonicMarkerIndex(markedText, '_');
        }
        if (markerIndex != -1)
        {
            text = text.substring(0, markerIndex) + text.substring(markerIndex + 1);
            mnemonicIndex = markerIndex;
            CharacterIterator sci = new StringCharacterIterator(markedText, markerIndex);
            mnemonicKey = mnemonicKey(sci.next());
        }
        return new MnemonicTextValue(text, mnemonicKey, mnemonicIndex);
    }

    private int mnemonicMarkerIndex(String s, char marker)
    {
        if ((s == null) || (s.length() < 2))
        {
            return -1;
        }
        CharacterIterator sci = new StringCharacterIterator(s);
        int i = 0;
        while (i != -1)
        {
            i = s.indexOf(marker, i);
            if (i != -1)
            {
                sci.setIndex(i);
                char c1 = sci.previous();
                sci.setIndex(i);
                char c2 = sci.next();
                boolean isQuote = (c1 == '\'') && (c2 == '\'');
                boolean isSpace = Character.isWhitespace(c2);
                if (!isQuote && !isSpace && (c2 != CharacterIterator.DONE))
                {
                    return i;
                }
            }
            if (i != -1)
            {
                i += 1;
            }
        }
        return -1;
    }

    /* A general purpose way to map from a char to a KeyCode is needed.  An
    * AWT RFE has been filed:
    * http://bt2ws.central.sun.com/CrPrint?id=6559449
    * CR 6559449 java/classes_awt Support for converting from char to KeyEvent VK_ keycode
    */
    private int mnemonicKey(char c)
    {
        int vk = (int) c;
        if ((vk >= 'a') && (vk <= 'z'))
        {
            vk -= ('a' - 'A');
        }
        return vk;
    }
}
