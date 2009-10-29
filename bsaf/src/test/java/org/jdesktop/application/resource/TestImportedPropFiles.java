package org.jdesktop.application.resource;

import org.junit.*;
import static org.junit.Assert.*;
import org.jdesktop.application.ResourceMap;

import java.util.List;
import java.util.Arrays;

//tests that imported= resource key is behaving correctly and loading in additional resource bundles

public class TestImportedPropFiles
{
    public TestImportedPropFiles() {} // constructor


    static ResourceMap defaultManager = new ResourceMap(Arrays.asList("org.jdesktop.application.resource.resources.SpecializedApplication"));

    @BeforeClass
    public static void unitSetup()
    {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup()
    {
    } // unitCleanup()

    @Before
    public void methodSetup()
    {
    } // methodSetup()

    @After
    public void methodCleanup()
    {
    } // methodCleanup()


    @Test
    public void testBundleNames()
    {
        List<String> expected = Arrays.asList( "org.jdesktop.application.resource.resources.SpecializedApplication",
                                               "org.jdesktop.application.resource.resources.Expressions",
                                               "org.jdesktop.application.resource.resources.Actions");

        //difference between SAF and my code - these are lazily loaded in SAF, so we have to force a lookup to load all bundles
        defaultManager.getAsString("foo", null);

        List<String> actual = defaultManager.getBundleNames();

        //todo - remove println
        //System.out.println(String.format("actual bundle names: %s", actual));
        assertArrayEquals("Bundle names should match import key in SpecializedApplication",expected.toArray(), actual.toArray());
    }

    @Test
    public void testMainFileProperty()
    {
        String expected = "SpecializedApplication";
        String actual = defaultManager.getAsString("SpecializedApplication", null);
        assertEquals("Property value from main bundle should match expected.", expected, actual);
    }

    @Test
    public void testPropOverride1()
    {
        String expected = "Hola!";
        String actual = defaultManager.getAsString("hello", null);
        assertEquals("Property value from ExprEval should be overriden by main propfile value.", expected, actual);
    }

    @Test
    public void testPropOverride2()
    {
        String expected = "La Venta Sin Titulo";
        String actual = defaultManager.getAsString("UNTITLED_WINDOW_NAME", null);
        assertEquals("Property value from ExprEval should be overriden by main propfile value.", expected, actual);
    }

    @Test
    public void testOverridenSubstitution1()
    {
        //substitution expression is loaded from ExprEval, but value for key "hello" is overriden in  SpecializedApplication
        //helloworld2 = ${hello} ${world}
        String expected = "Hola! World";
        String actual = defaultManager.getAsString("helloworld2", null);
        assertEquals("Substitution value from ExprEval should be overriden by main propfile value.", expected, actual);
    }

    @Test
    public void testOverridenSubstitution2()
    {
        //expr4 = Here is a nested \\${fooKey${fooKey}} String ${barKey}
        String expected = "Here is a nested ${fooKeyHere is a String} String barValue";
        String actual = defaultManager.getAsString("expr4", null);
        assertEquals("Substitution value from ExprEval should be overriden by main propfile value.", expected, actual);

    }

    @Test (expected = ResourceMap.LookupException.class)
    public void testCircularDependency1()
    {
        String expected = "bazKey";
        String actual = defaultManager.getAsString("bazKey", null);
        assertEquals("bazKey is defined circularly", expected, actual);
    }
    @Test(expected = ResourceMap.LookupException.class)
    public void testCircularDependency2()
    {
        String expected = "expr6";
        String actual = defaultManager.getAsString("expr6", null);
        assertEquals("Substitution value from ExprEval should be overriden by main propfile value.", expected, actual);
    }

    @Test
    public void testLongExpr()
    {
        //longKey
        String expected = "green Hola! La Venta Sin Titulo Here is a String Here is a nested ${fooKeyHere is a String = null = null} String barValue";
        String actual = defaultManager.getAsString("longKey", null);
        assertEquals("long expression should be correctly parsed", expected, actual);
    }

}