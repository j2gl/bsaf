package org.jdesktop.application.resource.locale;

import org.junit.*;
import static org.junit.Assert.*;
import org.jdesktop.application.ResourceMap;

import java.util.Arrays;
import java.util.Locale;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class TestLocaleChange
{
    public TestLocaleChange() {} // constructor


    static ResourceMap defaultLocaleMap;
    static ResourceMap esLocaleMap;

    @BeforeClass
    public static void unitSetup()
    {
        //todo - remove println
        System.out.println(String.format("user.language=%s, default locale = %s", System.getProperty("user.language"), Locale.getDefault()));

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


    private ResourceMap createDefaultMap()
    {
        Locale defaultLocale = new Locale(""); //should force use of non-qualified resource bundles
        ResourceMap parent = new ResourceMap(Arrays.asList("org.jdesktop.application.resource.locale.parent.resources.Parent"), defaultLocale);
        ResourceMap child = new ResourceMap(Arrays.asList("org.jdesktop.application.resource.locale.child.resources.Child"), defaultLocale);
        child.setParent(parent);
        //force loading
        child.getAsString("foo", null);
        return child;
    }

    private ResourceMap createSpanishMap()
    {
        ResourceMap parent = new ResourceMap(Arrays.asList("org.jdesktop.application.resource.locale.parent.resources.Parent"), new Locale("es"));
        ResourceMap child = new ResourceMap(Arrays.asList("org.jdesktop.application.resource.locale.child.resources.Child"), new Locale("es"));
        child.setParent(parent);
        //force loading
        child.getAsString("foo", null);
        return child;
    }

    private boolean checkDefaultLocaleProps(ResourceMap rm)
    {
        String[] expectedPBundles = {"org.jdesktop.application.resource.locale.parent.resources.Parent",
                "org.jdesktop.application.resource.locale.parent.resources.Foo",
                "org.jdesktop.application.resource.locale.parent.resources.Bar"};
        assertArrayEquals("Expect all Parent resource bundles to be loaded", expectedPBundles, rm.getParent().getBundleNames().toArray());

        String[] expectedCBundles = {"org.jdesktop.application.resource.locale.child.resources.Child",
                "org.jdesktop.application.resource.locale.child.resources.Foo",
                "org.jdesktop.application.resource.locale.child.resources.Bar"};
        assertArrayEquals("Expect all Parent resource bundles to be loaded", expectedCBundles, rm.getBundleNames().toArray());

        //always exists for all locales:
        assertEquals("Correct property value not found.", "Parent.default", rm.getAsString("Parent.default", null));
        assertEquals("Correct property value not found.", "Child.default", rm.getAsString("Child.default", null));


        //these properties exist for Parent & Child, but will be shadowed by the es locale
        //Parent:
        assertEquals("Correct property Parent value not found.", "Parent.default", rm.getAsString("Parent.default",null));
        assertEquals("Correct property Parent value not found.", "parentKey1.default", rm.getAsString("parentKey1", null));
        assertEquals("Correct property Parent value not found.", "parentKey2.default", rm.getAsString("parentKey2", null));
        //Child:
        assertEquals("Correct property Child value not found.", "Child.default", rm.getAsString("Child.default", null));
        assertEquals("Correct property Child value not found.", "childKey1.default", rm.getAsString("childKey1", null));
        assertEquals("Correct property Child value not found.", "childKey2.default", rm.getAsString("childKey2", null));

        //shadowed by both Child and es locale
        assertEquals("Shared property value not properly shadowed.", "Child.key1.default", rm.getAsString("key1", null));
        assertEquals("Shared property value not properly shadowed.", "Child.key2.default", rm.getAsString("key2", null));

        return true;
    }

    private boolean checkSpanishLocaleProps(ResourceMap rm)
    {
        String[] expectedPBundles = {"org.jdesktop.application.resource.locale.parent.resources.Parent",
                "org.jdesktop.application.resource.locale.parent.resources.Foo",
                "org.jdesktop.application.resource.locale.parent.resources.Bar"};
        assertArrayEquals("Expect all Parent resource bundles to be loaded", expectedPBundles, rm.getParent().getBundleNames().toArray());

        String[] expectedCBundles = {"org.jdesktop.application.resource.locale.child.resources.Child",
                "org.jdesktop.application.resource.locale.child.resources.Foo",
                "org.jdesktop.application.resource.locale.child.resources.Bar"};
        assertArrayEquals("Expect all Parent resource bundles to be loaded", expectedCBundles, rm.getBundleNames().toArray());

        //always exists for all locales:
        assertEquals("Correct property value not found.", "Parent.default", rm.getAsString("Parent.default", null));
        assertEquals("Correct property value not found.", "Child.default", rm.getAsString("Child.default", null));

        //only exists for Spanish locale
        assertEquals("Correct property value not found.", "Parent.es", rm.getAsString("Parent.es", null));
        assertEquals("Correct property value not found.", "Child.es", rm.getAsString("Child.es", null));


        //these properties exist for Parent & Child, but will be shadowed by the es locale
        //Parent:
        assertEquals("Correct property Parent value not found.", "parentKey1.es", rm.getAsString("parentKey1", null));
        assertEquals("Correct property Parent value not found.", "parentKey2.es", rm.getAsString("parentKey2", null));
        //Child:
        assertEquals("Correct property Child value not found.", "childKey1.es", rm.getAsString("childKey1", null));
        assertEquals("Correct property Child value not found.", "childKey2.es", rm.getAsString("childKey2", null));

        //shadows both Child and default locale
        assertEquals("Shared property value not properly shadowed.", "Child.key1.es", rm.getAsString("key1", null));
        assertEquals("Shared property value not properly shadowed.", "Child.key2.es", rm.getAsString("key2", null));

        return true;
    }

    @Test
    public void testDefaultMapProps()
    {
        ResourceMap rm = createDefaultMap();
        assertTrue(checkDefaultLocaleProps(rm));
    }

    @Test
    public void testSpanishMapProps()
    {
        ResourceMap rm = createSpanishMap();
        assertTrue(checkSpanishLocaleProps(rm));
    }

    @Test
    public void testDefaultChangeToSpanish()
    {
        //start with default locale, verify props, then change to spanish locale, verify props
        ResourceMap rm = createDefaultMap();
        assertTrue(checkDefaultLocaleProps(rm));
        rm.setLocale(new Locale("es"));
        assertTrue(checkSpanishLocaleProps(rm));
    }

    @Test
    public void testSpanishChangeToDefault()
    {
        //start with spanish map, verify props, then change to default locale, verify props. N
        ResourceMap rm = createSpanishMap();
        assertTrue(checkSpanishLocaleProps(rm));
        Locale newLocale = new Locale(""); //should force use of non-qualified resource bundles
        rm.setLocale(newLocale);
        assertTrue(checkDefaultLocaleProps(rm));
    }

    @Test
    public void testDefaultToSpanishAndBack()
    {
        //start with default map, verify props, then change to spanish, verify props, finally change back to default and verify
        ResourceMap rm = createDefaultMap();
        assertTrue(checkDefaultLocaleProps(rm));
        rm.setLocale(new Locale("es"));
        assertTrue(checkSpanishLocaleProps(rm));

        rm.setLocale(new Locale(""));
        assertTrue(checkDefaultLocaleProps(rm));
    }

    @Test
    public void testSpanishToDefaultAndBack()
    {
        ResourceMap rm = createSpanishMap();
        assertTrue(checkSpanishLocaleProps(rm));
        Locale newLocale = new Locale("");
        rm.setLocale(newLocale);
        assertTrue(checkDefaultLocaleProps(rm));

        rm.setLocale(new Locale("es"));
        assertTrue(checkSpanishLocaleProps(rm));
    }

    private boolean localeChangeFlag;
    @Test
    public void testLocaleChangePropNotification()
    {
        ResourceMap rm = createDefaultMap();
        rm.addPropertyChangeListener("locale", new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt)
            {
                localeChangeFlag = true;
            }
        });

        rm.setLocale(new Locale("es"));
        assertTrue("property change notification for 'locale' not received" ,localeChangeFlag);

    }
}