package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.*;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class TestStringToURL
{
    public TestStringToURL() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToURL defaultConverter;
    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToURL();

    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()


    //Note : URL actually does a DNS lookup if you call equals() (Talk about leaky abstractions! So we try to compare
    //strings for equality. This is sufficient for these unit tests which are just verifying that the String argument
    //is parsed correctly and returns a correct URL representation

    @Test (expected = IllegalArgumentException.class)
    public void testNullString() throws StringConvertException
    {
        String s = null;
        defaultConverter.convert(s);
    }

    @Test (expected = StringConvertException.class)
    public void testEmtpyString() throws StringConvertException
    {
        String s = "";
        defaultConverter.convert(s);
    }

    @Test
    public void testStringNoProtocal() throws StringConvertException, MalformedURLException, URISyntaxException
    {
        String s = "www.cnn.com";
        URL expected = new URL("http://www.cnn.com");
        URL actual = defaultConverter.convert(s);

        String msg = String.format("Converter should insert 'http://' in URL for '%s'",s);
        assertEquals(msg, expected.toString(), actual.toString());
    }

    @Test
    public void testString2() throws StringConvertException, MalformedURLException
    {
        String s = "http://www.foo.com";
        URL expected = new URL("http://www.foo.com");
        URL actual = defaultConverter.convert(s);

        String msg = String.format("Converter should return expected URL for '%s'", s);
        assertEquals(msg, expected.toString(), actual.toString());
    }

    @Test (expected = StringConvertException.class)
    public void testBadString1() throws StringConvertException, MalformedURLException
    {
        String s = "f o o";
        URL expected = new URL("http://www.foo.com");
        URL actual = defaultConverter.convert(s);

        String msg = String.format("Converter should return expected URL for '%s'", s);
        assertEquals(msg, expected.toString(), actual.toString());
    }

    @Test(expected = StringConvertException.class)
    public void testBadString2() throws StringConvertException, MalformedURLException
    {
        String s = "http:// www.foo.com"; //bad space
        URL expected = new URL("http://www.foo.com");
        URL actual = defaultConverter.convert(s);

        String msg = String.format("Converter should return expected URL for '%s'", s);        
        assertEquals(msg, expected.toString(), actual.toString());
    }


    @Test(expected = StringConvertException.class)
    public void testBadString3() throws StringConvertException, MalformedURLException
    {
        String s = "http://www.foo. com"; //bad space
        URL expected = new URL("http://www.foo.com");
        URL actual = defaultConverter.convert(s);

        String msg = String.format("Converter should return expected URL for '%s'", s);
        assertEquals(msg, expected.toString(), actual.toString());
    }

    @Test (expected = StringConvertException.class)
    public void testBadProtocol() throws StringConvertException
    {
        String s = "abc://def.ghi.jkl"; //nonsense
        URL actual = defaultConverter.convert(s);
    }

}