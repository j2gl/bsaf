package org.jdesktop.application.resource;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.TestUtil;

import java.util.Collection;
import java.util.Arrays;



//Unit tests for testing the expression evaluation in property files (variable substitution)
@RunWith(value = Parameterized.class)
public class TestExpressionEval
{
    protected static ResourceMap defaultMap;

    protected String expected;
    protected String resourceKey;
    protected Class exceptionType; //when  not null, we expect the test method to throw an exception and test for this

    public TestExpressionEval(String resourceKey, String expected, Class exceptionType)
    {
        this.expected = expected;
        this.resourceKey = resourceKey;
        this.exceptionType = exceptionType;
    }


    @BeforeClass
    public static void unitSetup()
    {
        defaultMap = new ResourceMap(Arrays.asList(TestUtil.ExpressionsPropPath));
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

    //@SuppressWarnings(value = "unchecked")
    @Test
    public void testEvaluate() throws Exception
    {

        String actual = null;

        if (exceptionType != null)
        {
            try
            {
                actual = defaultMap.getString(resourceKey);
                fail(String.format("getting String resource '%s' should throw %s", resourceKey, exceptionType.getClass().getSimpleName()));

            }
            catch (Exception maybeIgnore)
            {
                if (maybeIgnore.getClass() == exceptionType)//expected
                {
                    //succeeded
                }
                else
                {
                    throw maybeIgnore;
                }
            }
        }
        else
        {
            actual = defaultMap.getString(resourceKey);
            assertEquals(String.format("Evaluating String for resourceKey '%s' should return expected value.", resourceKey), expected, actual);
        }
    }


    @Parameterized.Parameters
    public static Collection<? extends Object[]> data()
    {

        return Arrays.asList(new Object[][]{
                {"hello", "Hello", null},
                {"world", "World", null},
                {"place", "World", null},
                {"helloworld0", "Hello World", null},
                {"helloworld1", "Hello World", null},
                {"helloworld2", "Hello World", null},
                {"helloworld3", "Hello World", null},
                {"escHelloWorld", "${hello} ${world}", null},
                {"escOnly", "${", null},
                {"noSuchVariableKey", "hello ${borf = null}", null},
                {"noClosingBrace", "${hello world", ResourceMap.LookupException.class},
                {"justNull", null, null},
                {"textAndNull", "text and", null},
                {"nullAndText", "and text", null},
                {"escNull", "${null}", null},
                {"nullInMiddle", "text and  in middle", null},

                //testing nested substitutions
                {"fooKey", "foovalue", null},
                {"barKey", "barValue", null},
                {"fooKeyfoovalue", "superdupernested", null},
                {"supernesting", "superdupernested", null},

                {"expr1", "Here is a String", null},
                {"expr2", "Here is a foovalue String", null},
                {"expr3", "Here is a foovalue String barValue", null},
                {"expr4", "Here is a nested ${fooKeyfoovalue} String barValue", null},
                {"expr5", "Here is a nested superdupernested String barValue", null},
                {"systemProp", "current language is "+System.getProperty("user.language"), null},

        });
    }

}