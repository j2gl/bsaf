package org.jdesktop.application.convert;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import org.jdesktop.application.MnemonicTextValue;

import java.util.Collection;
import java.util.Arrays;
import java.awt.event.KeyEvent;

/**
 * (c) 2009 Rob Ross
 * All rights reserved
 *
 * @author Rob Ross
 * @version Date: Oct 29, 2009  2:41:06 PM
 */


@RunWith(value = Parameterized.class)
public class TestStringToMnemonicTextValue
{
    private String markedText;    //input data
    private MnemonicTextValue expected;
    private Class expectedException; //null if no exception expected to be thrown

    public TestStringToMnemonicTextValue(String markedText, MnemonicTextValue expected, Class expectedException)
    {
        this.markedText = markedText;
        this.expected = expected;
        this.expectedException = expectedException;
    }

    @Parameterized.Parameters
    public static Collection data()
    {
        return Arrays.asList(new Object[][]{
                //test data, expected result, exception type
                {null, new MnemonicTextValue("", KeyEvent.VK_UNDEFINED, -1), IllegalArgumentException.class}, //null input string
                {"", new MnemonicTextValue("", KeyEvent.VK_UNDEFINED, -1), null},
                {"&", new MnemonicTextValue("&", KeyEvent.VK_UNDEFINED, -1), null},
                {"x", new MnemonicTextValue("x", KeyEvent.VK_UNDEFINED, -1), null},
                {"xy", new MnemonicTextValue("xy", KeyEvent.VK_UNDEFINED, -1), null},
                {"xyz", new MnemonicTextValue("xyz", KeyEvent.VK_UNDEFINED, -1), null},
                {"x&", new MnemonicTextValue("x&", KeyEvent.VK_UNDEFINED, -1), null},
                {"x& ", new MnemonicTextValue("x& ", KeyEvent.VK_UNDEFINED, -1), null},
                {"x&\t", new MnemonicTextValue("x&\t", KeyEvent.VK_UNDEFINED, -1), null},
                {"x & y", new MnemonicTextValue("x & y", KeyEvent.VK_UNDEFINED, -1), null},
                {"'&'", new MnemonicTextValue("'&'", KeyEvent.VK_UNDEFINED, -1), null},
                {"foo('&')", new MnemonicTextValue("foo('&')", KeyEvent.VK_UNDEFINED, -1), null},

                //these should generate mnemonic properties
                {"&x & y", new MnemonicTextValue("x & y", KeyEvent.VK_X, 0), null},
                {"x & &y", new MnemonicTextValue("x & y", KeyEvent.VK_Y, 4), null},
                {"&File", new MnemonicTextValue("File", KeyEvent.VK_F, 0), null},
                {"Save &As", new MnemonicTextValue("Save As", KeyEvent.VK_A, 5), null},
                {"Block A&pplication", new MnemonicTextValue("Block Application", KeyEvent.VK_P, 7), null},
                {"E&xit", new MnemonicTextValue("Exit", KeyEvent.VK_X, 1), null},
                
                //underscore should work as well
                {"_x _ y", new MnemonicTextValue("x _ y", KeyEvent.VK_X, 0), null},
                {"x _ _y", new MnemonicTextValue("x _ y", KeyEvent.VK_Y, 4), null},
                {"_File", new MnemonicTextValue("File", KeyEvent.VK_F, 0), null},
                {"Save _As", new MnemonicTextValue("Save As", KeyEvent.VK_A, 5), null},
                {"Block A_pplication", new MnemonicTextValue("Block Application", KeyEvent.VK_P, 7), null},
                {"E_xit", new MnemonicTextValue("Exit", KeyEvent.VK_X, 1), null},
        });
    }

    @Test
    public void testConvertMarkedText() throws StringConvertException
    {
        if (expectedException == null)
        {
            testNoException();
        }
        else if (expectedException == IllegalArgumentException.class)
        {
            testIAException();
        }
    }

    private void testNoException() throws StringConvertException
    {
        StringToMnemonicTextValue converter = new StringToMnemonicTextValue();
        assertEquals(String.format("Converting marked text String '%s' should return appropriate MnemonicTextValue instance", markedText), expected, converter.convert(markedText));

    }

    private void testIAException() throws StringConvertException
    {
        try
        {
            StringToMnemonicTextValue converter = new StringToMnemonicTextValue();
            assertEquals(String.format("Converting marked text String '%s' should return appropriate value instance", markedText), expected, converter.convert(markedText));
            fail(String.format("Excpected IllegalArgumentException to be thrown for marked text String '%s'", markedText));
        }
        catch (IllegalArgumentException expect)
        {
            assertTrue(true);
        }
    }
}
