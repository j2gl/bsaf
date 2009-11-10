package org.jdesktop.application;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.HashMap;

/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 *
 * An immutable value class that encapsulates a text String and optional mnemonic key/index information. This class is
 * returned from the StringToTextAndMnemonicSupport ResourceConverter. It can be used to auto inject a text String
 * and mnemonic key/index information into Objects that support these properties, like AbstractButton, Actions, JLabel, etc.
 *
 * The value returned from getText() is guaranteed to be non-null, though it could be an empty string.
 *
 * Method {@code getMnemonic} will return KeyEvent.VK_UNDEFINED if no mnemonic key has been specified. Otherwise, it will return
 * the virtual key code specified as the mnemonic key to use for this property. ( see {@code KeyEvent} for a list of VK_ codes.)
 *
 * Method {@code getDisplayedMnemonicIndex} returns -1 if no mnemonic index has been specified, otherwise it returns the
 * index in the String returned from {@code getText} that has been specified to use to display the mnemonic decoration.
 *
 * @author Rob Ross
 * @version Date: Oct 29, 2009  1:11:25 PM
 *
 * @see java.awt.event.KeyEvent
 */
public class MnemonicTextValue
{
    private final String text;
    private final int    mnemonicKeyCode;
    private final int    mnemonicIndex;


    public MnemonicTextValue(@NotNull String text, int mnemonicKeyCode, int mnemonicIndex)
    {
        assertNotNull(text, String.class, "text");
        this.text = text;
        this.mnemonicKeyCode = mnemonicKeyCode;
        this.mnemonicIndex = mnemonicIndex;
    }

    /**
     * The text String set for this value, intended to be passed as a parameter to Objects that implmenet a {@code setText} method,
     * such as AbstractButton, JLabel, or the NAME property in Action, etc.
     *
     * @return the text String set for this value. WIll never be null.
     */
    @NotNull
    public String getText()
    {
        return text;
    }

    /**
     * Returns the keyboard mnemonic set for this value.
     *
     * @return the virtual key code representing the keyboard mnemonic set for this value.
     * If no mnemonic key has been set, returns KeyEvent.VK_UNDEFINED.
     * @see java.awt.event.KeyEvent
     */
    public int getMnemonic()
    {
        return mnemonicKeyCode;
    }

    /**
     * Returns the character, as an index, that the look and feel should
     * provide decoration for as representing the mnemonic character.
     *
     * @return index representing mnemonic character. Returns -1 if no index has been set
     */
    public int getDisplayedMnemonicIndex()
    {
        return mnemonicIndex;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof MnemonicTextValue))
        {
            return false;
        }

        MnemonicTextValue that = (MnemonicTextValue) o;

        if (mnemonicIndex != that.mnemonicIndex)
        {
            return false;
        }
        if (mnemonicKeyCode != that.mnemonicKeyCode)
        {
            return false;
        }
        if (text != null ? !text.equals(that.text) : that.text != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + mnemonicKeyCode;
        result = 31 * result + mnemonicIndex;
        return result;
    }

    @Override
    public String toString()
    {
        return "MnemonicTextValue{" +
                "text='" + text + '\'' +
                ", mnemonicKeyCode=" + mnemonicKeyCode +
                ", mnemonicIndex=" + mnemonicIndex +
                '}';
    }

    /**
     * Return the VK constant name for the given key code
     * @param keyCode an int representing a virtual key code for which to return its symbolic constant name
     * @return the name of the virtual keycode for the given int keyCode value.
     * @see java.awt.event.KeyEvent
     */
    public static String getVKName(int keyCode)
    {
        return map.get(keyCode);    
    }

    private void assertNotNull(Object o, Class type, String paramName)
    {
        if (o == null)
        {
            throw new IllegalArgumentException(String.format("parameter '%s' of type '%s' cannot be null.", paramName, type));
        }
    }

    private static final Map<Integer, String> map = new HashMap<Integer, String>(256);
    static
    {
        map.put((int)'\n', "VK_ENTER");
        map.put((int)'\b', "VK_BACK_SPACE");
        map.put((int)'\t', "VK_TAB");
        map.put(0x03, "VK_CANCEL");
        map.put(0x0C, "VK_CLEAR");
        map.put(0x10, "VK_SHIFT");
        map.put(0x11, "VK_CONTROL");
        map.put(0x12, "VK_ALT");
        map.put(0x13, "VK_PAUSE");
        map.put(0x14, "VK_CAPS_LOCK");
        map.put(0x1B, "VK_ESCAPE");
        map.put(0x20, "VK_SPACE");
        map.put(0x21, "VK_PAGE_UP");
        map.put(0x22, "VK_PAGE_DOWN");
        map.put(0x23, "VK_END");
        map.put(0x24, "VK_HOME");
        map.put(0x25, "VK_LEFT");
        map.put(0x26, "VK_UP");
        map.put(0x27, "VK_RIGHT");
        map.put(0x28, "VK_DOWN");
        map.put(0x2C, "VK_COMMA");
        map.put(0x2D, "VK_MINUS");
        map.put(0x2E, "VK_PERIOD");
        map.put(0x2F, "VK_SLASH");
        map.put(0x30, "VK_0");
        map.put(0x31, "VK_1");
        map.put(0x32, "VK_2");
        map.put(0x33, "VK_3");
        map.put(0x34, "VK_4");
        map.put(0x35, "VK_5");
        map.put(0x36, "VK_6");
        map.put(0x37, "VK_7");
        map.put(0x38, "VK_8");
        map.put(0x39, "VK_9");
        map.put(0x3B, "VK_SEMICOLON");
        map.put(0x3D, "VK_EQUALS");
        map.put(0x41, "VK_A");
        map.put(0x42, "VK_B");
        map.put(0x43, "VK_C");
        map.put(0x44, "VK_D");
        map.put(0x45, "VK_E");
        map.put(0x46, "VK_F");
        map.put(0x47, "VK_G");
        map.put(0x48, "VK_H");
        map.put(0x49, "VK_I");
        map.put(0x4A, "VK_J");
        map.put(0x4B, "VK_K");
        map.put(0x4C, "VK_L");
        map.put(0x4D, "VK_M");
        map.put(0x4E, "VK_N");
        map.put(0x4F, "VK_O");
        map.put(0x50, "VK_P");
        map.put(0x51, "VK_Q");
        map.put(0x52, "VK_R");
        map.put(0x53, "VK_S");
        map.put(0x54, "VK_T");
        map.put(0x55, "VK_U");
        map.put(0x56, "VK_V");
        map.put(0x57, "VK_W");
        map.put(0x58, "VK_X");
        map.put(0x59, "VK_Y");
        map.put(0x5A, "VK_Z");
        map.put(0x5B, "VK_OPEN_BRACKET");
        map.put(0x5C, "VK_BACK_SLASH");
        map.put(0x5D, "VK_CLOSE_BRACKET");
        map.put(0x60, "VK_NUMPAD0");
        map.put(0x61, "VK_NUMPAD1");
        map.put(0x62, "VK_NUMPAD2");
        map.put(0x63, "VK_NUMPAD3");
        map.put(0x64, "VK_NUMPAD4");
        map.put(0x65, "VK_NUMPAD5");
        map.put(0x66, "VK_NUMPAD6");
        map.put(0x67, "VK_NUMPAD7");
        map.put(0x68, "VK_NUMPAD8");
        map.put(0x69, "VK_NUMPAD9");
        map.put(0x6A, "VK_MULTIPLY");
        map.put(0x6B, "VK_ADD");
        map.put(0x6C, "VK_SEPARATOR");
        map.put(0x6D, "VK_SUBTRACT");
        map.put(0x6E, "VK_DECIMAL");
        map.put(0x6F, "VK_DIVIDE");
        map.put(0x90, "VK_NUM_LOCK");
        map.put(0x91, "VK_SCROLL_LOCK");
        map.put(0x70, "VK_F1");
        map.put(0x71, "VK_F2");
        map.put(0x72, "VK_F3");
        map.put(0x73, "VK_F4");
        map.put(0x74, "VK_F5");
        map.put(0x75, "VK_F6");
        map.put(0x76, "VK_F7");
        map.put(0x77, "VK_F8");
        map.put(0x78, "VK_F9");
        map.put(0x79, "VK_F10");
        map.put(0x7A, "VK_F11");
        map.put(0x7B, "VK_F12");
        map.put(0xF000, "VK_F13");
        map.put(0xF001, "VK_F14");
        map.put(0xF002, "VK_F15");
        map.put(0xF003, "VK_F16");
        map.put(0xF004, "VK_F17");
        map.put(0xF005, "VK_F18");
        map.put(0xF006, "VK_F19");
        map.put(0xF007, "VK_F20");
        map.put(0xF008, "VK_F21");
        map.put(0xF009, "VK_F22");
        map.put(0xF00A, "VK_F23");
        map.put(0xF00B, "VK_F24");
        map.put(0x9A, "VK_PRINTSCREEN");
        map.put(0x9B, "VK_INSERT");
        map.put(0x9C, "VK_HELP");
        map.put(0x9D, "VK_META");
        map.put(0xC0, "VK_BACK_QUOTE");
        map.put(0xDE, "VK_QUOTE");
        map.put(0xE0, "VK_KP_UP");
        map.put(0xE1, "VK_KP_DOWN");
        map.put(0xE2, "VK_KP_LEFT");
        map.put(0xE3, "VK_KP_RIGHT");
        map.put(0x80, "VK_DEAD_GRAVE");
        map.put(0x81, "VK_DEAD_ACUTE");
        map.put(0x82, "VK_DEAD_CIRCUMFLEX");
        map.put(0x83, "VK_DEAD_TILDE");
        map.put(0x84, "VK_DEAD_MACRON");
        map.put(0x85, "VK_DEAD_BREVE");
        map.put(0x86, "VK_DEAD_ABOVEDOT");
        map.put(0x87, "VK_DEAD_DIAERESIS");
        map.put(0x88, "VK_DEAD_ABOVERING");
        map.put(0x89, "VK_DEAD_DOUBLEACUTE");
        map.put(0x8a, "VK_DEAD_CARON");
        map.put(0x8b, "VK_DEAD_CEDILLA");
        map.put(0x8c, "VK_DEAD_OGONEK");
        map.put(0x8d, "VK_DEAD_IOTA");
        map.put(0x8e, "VK_DEAD_VOICED_SOUND");
        map.put(0x8f, "VK_DEAD_SEMIVOICED_SOUND");
        map.put(0x96, "VK_AMPERSAND");
        map.put(0x97, "VK_ASTERISK");
        map.put(0x98, "VK_QUOTEDBL");
        map.put(0x99, "VK_LESS");
        map.put(0xa0, "VK_GREATER");
        map.put(0xa1, "VK_BRACELEFT");
        map.put(0xa2, "VK_BRACERIGHT");
        map.put(0x0200, "VK_AT");
        map.put(0x0201, "VK_COLON");
        map.put(0x0202, "VK_CIRCUMFLEX");
        map.put(0x0203, "VK_DOLLAR");
        map.put(0x0204, "VK_EURO_SIGN");
        map.put(0x0205, "VK_EXCLAMATION_MARK");
        map.put(0x0206, "VK_INVERTED_EXCLAMATION_MARK");
        map.put(0x0207, "VK_LEFT_PARENTHESIS");
        map.put(0x0208, "VK_NUMBER_SIGN");
        map.put(0x0209, "VK_PLUS");
        map.put(0x020A, "VK_RIGHT_PARENTHESIS");
        map.put(0x020B, "VK_UNDERSCORE");
        map.put(0x020C, "VK_WINDOWS");
        map.put(0x020D, "VK_CONTEXT_MENU");
        map.put(0x0018, "VK_FINAL");
        map.put(0x001C, "VK_CONVERT");
        map.put(0x001D, "VK_NONCONVERT");
        map.put(0x001E, "VK_ACCEPT");
        map.put(0x001F, "VK_MODECHANGE");
        map.put(0x0015, "VK_KANA");
        map.put(0x0019, "VK_KANJI");
        map.put(0x00F0, "VK_ALPHANUMERIC");
        map.put(0x00F1, "VK_KATAKANA");
        map.put(0x00F2, "VK_HIRAGANA");
        map.put(0x00F3, "VK_FULL_WIDTH");
        map.put(0x00F4, "VK_HALF_WIDTH");
        map.put(0x00F5, "VK_ROMAN_CHARACTERS");
        map.put(0x0100, "VK_ALL_CANDIDATES");
        map.put(0x0101, "VK_PREVIOUS_CANDIDATE");
        map.put(0x0102, "VK_CODE_INPUT");
        map.put(0x0103, "VK_JAPANESE_KATAKANA");
        map.put(0x0104, "VK_JAPANESE_HIRAGANA");
        map.put(0x0105, "VK_JAPANESE_ROMAN");
        map.put(0x0106, "VK_KANA_LOCK");
        map.put(0x0107, "VK_INPUT_METHOD_ON_OFF");
        map.put(0xFFD1, "VK_CUT");
        map.put(0xFFCD, "VK_COPY");
        map.put(0xFFCF, "VK_PASTE");
        map.put(0xFFCB, "VK_UNDO");
        map.put(0xFFC9, "VK_AGAIN");
        map.put(0xFFD0, "VK_FIND");
        map.put(0xFFCA, "VK_PROPS");
        map.put(0xFFC8, "VK_STOP");
        map.put(0xFF20, "VK_COMPOSE");
        map.put(0xFF7E, "VK_ALT_GRAPH");
        map.put(0xFF58, "VK_BEGIN");
        map.put(0x0, "VK_UNDEFINED");
    }
}
