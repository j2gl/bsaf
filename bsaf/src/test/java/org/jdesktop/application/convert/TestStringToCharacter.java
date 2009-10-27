package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.*;

public class TestStringToCharacter
{
    public TestStringToCharacter() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToCharacter defaultConverter;
    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToCharacter();
    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()


    @Test(expected = IllegalArgumentException.class)
    public void testNullString() throws StringConvertException
    {
        String s = null;
        defaultConverter.convert(s);
    }

    @Test(expected = StringConvertException.class)
    public void testEmptyString() throws StringConvertException
    {
        String s = "";
        defaultConverter.convert(s);
    }

    @Test
    public void testValidString() throws StringConvertException
    {
        String s = "foo";
        Character expected = 'f';
        assertEquals("Conversion of 'foo' should return 'f'", expected, defaultConverter.convert(s));
    }


}