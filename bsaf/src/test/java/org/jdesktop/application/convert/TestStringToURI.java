package org.jdesktop.application.convert;

import org.junit.*;
import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.net.URI;

public class TestStringToURI
{
    public TestStringToURI() {} // constructor

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    StringToURI defaultConverter;
    @Before
    public void methodSetup()
    {
        defaultConverter = new StringToURI();  
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

    @Test (expected = StringConvertException.class)
    public void testEmtpyString() throws StringConvertException
    {
        String s = "";
        defaultConverter.convert(s);
    }

    @Test
    public void testStringNoProtocal() throws StringConvertException, URISyntaxException
    {
        String s = "www.cnn.com";
        URI expected = new URI("http://www.cnn.com");
        URI actual = defaultConverter.convert(s);

        String msg = String.format("Converter should insert 'http://' in URI for '%s'", s);
        assertEquals(msg, expected, actual);
    }

    @Test
    public void testString2() throws StringConvertException, URISyntaxException
    {
        String s = "http://www.foo.com";
        URI expected = new URI("http://www.foo.com");
        URI actual = defaultConverter.convert(s);

        String msg = String.format("Converter should return expected URI for '%s'", s);
        assertEquals(msg, expected, actual);
    }

    @Test(expected = StringConvertException.class)
    public void testBadString1() throws StringConvertException, URISyntaxException
    {
        String s = "f o o";
        URI expected = new URI("http://www.foo.com");
        URI actual = defaultConverter.convert(s);

        String msg = String.format("Converter should return expected URI for '%s'", s);
        assertEquals(msg, expected, actual);
    }

    @Test(expected = StringConvertException.class)
    public void testBadString2() throws StringConvertException, URISyntaxException
    {
        String s = "http:// www.foo.com"; //bad space
        URI expected = new URI("http://www.foo.com");
        URI actual = defaultConverter.convert(s);

        String msg = String.format("Converter should return expected URI for '%s'", s);
        assertEquals(msg, expected, actual);
    }


    @Test(expected = StringConvertException.class)
    public void testBadString3() throws StringConvertException, URISyntaxException
    {
        String s = "http://www.foo. com"; //bad space
        URI expected = new URI("http://www.foo.com");
        URI actual = defaultConverter.convert(s);

        String msg = String.format("Converter should return expected URI for '%s'", s);
        assertEquals(msg, expected, actual);
    }

    @Test
    public void testBadProtocol() throws StringConvertException, URISyntaxException
    {
        String s = "abc://def.ghi.jkl"; //nonsense, but URI parses it without complaint
        URI actual = defaultConverter.convert(s);
        URI expected = new URI("abc://def.ghi.jkl");

        String msg = String.format("Converter should return expected URI for '%s'", s);
        assertEquals(msg, expected, actual);
    }

}