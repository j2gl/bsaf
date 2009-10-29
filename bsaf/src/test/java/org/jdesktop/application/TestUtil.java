package org.jdesktop.application;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import javax.imageio.ImageIO;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.List;
import java.net.URL;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;


/** Various utility methods used by other Test classes
 *
 * @author Rob Ross
 * @version Date: Oct 15, 2009  11:41:09 AM
 */
public class TestUtil
{
    private static final String USER_DIR = System.getProperty("user.dir");
    public static final double EPSILON_DOUBLE = 0.0000000000000001;
    public static final float EPSILON_FLOAT = 0.0000001f;
    public final static String AbstractAppPropPath = "org.jdesktop.application.resource.abstractapp.resources.AbstractApplication";
    public final static String ConcreteAppPropPath = "org.jdesktop.application.resource.concrete.resources.ConcreteApplication";
    public final static String ExpressionsPropPath = "org.jdesktop.application.resource.resources.Expressions";
    public final static String ActionsPropsPath    = "org.jdesktop.application.resource.resources.Actions";

    public static BufferedImage getTestImage(String filePath)
    {
        URL url = TestUtil.class.getClassLoader().getResource(filePath);
        if (url == null)
        {
            String msg = String.format("Benchmark image not found at path '%s/%s'", USER_DIR, filePath);
            fail(msg);
        }
        BufferedImage bi = null;
        try
        {
            bi = ImageIO.read(url);
        }
        catch (Exception e)
        {
            String msg = String.format("Invalid image path '%s/%s'", USER_DIR, filePath);
            fail(msg);
        }

        return bi;
    }

    //helper method to deal with how to compare certain resource types when they don't have a suitable equals method
    public static void assertResourcesEqual(String msg, Object expected, Object actual)
    {
        Class objClass = expected.getClass();
        //Special case for testing equals with Images, Icons, URLs,
        if (objClass == EmptyBorder.class)
        {
            assertTrue(msg, TestUtil.equal((EmptyBorder) expected, (EmptyBorder) actual));
        }
        else if (objClass == URL.class)
        {
            assertTrue(msg, TestUtil.equal((URL) expected, (URL) actual));
        }
        else if (objClass == BufferedImage.class || objClass == Image.class)
        {
            assertTrue(msg, TestUtil.equal((BufferedImage) expected, (BufferedImage) actual));
        }
        else if (objClass == ImageIcon.class)
        {
            assertTrue(msg, TestUtil.equal((BufferedImage) ((ImageIcon) expected).getImage(), (BufferedImage) ((ImageIcon) actual).getImage()));
        }
        else if (objClass.isArray())
        {
            Class compClass = objClass.getComponentType();
            if (compClass == Integer.TYPE)
            {
                org.junit.Assert.assertArrayEquals(msg, (int[]) expected, (int[]) actual);
            }
            else if (compClass == Boolean.TYPE)
            {
                assertArrayEquals(msg, (boolean[]) expected, (boolean[]) actual);

            }
            else
            {
                org.junit.Assert.assertArrayEquals(msg, (Object[]) expected, (Object[]) actual);
            }

        }
        else
        {
            assertEquals(msg, expected, actual);
        }
    }

    //URL can do a DNS lookup when calling equals, so we avoid that by comparing string values.
    public static boolean equal(URL url1, URL url2)
    {
        if (url1 == url2)
        {
            return true;
        }

        if (url1 == null && url2 == null)
        {
            return true;
        }

        if (url1 == null && url2 != null)
        {
            return false;
        }
        return url1.toString().equals(url2.toString());        
    }

    //EmptyBorder does not override Object.equals, so we test Insets of each for equality
    public static boolean equal(EmptyBorder eb1, EmptyBorder eb2)
    {
        if (eb1 == eb2)
        {
            return true;
        }

        if (eb1 == null && eb2 == null)
        {
            return true;
        }

        if (eb1 == null & eb2 !=null)
        {
            return false;
        }

        return eb1.getBorderInsets().equals(eb2.getBorderInsets());
    }

    //return true if the two buffered images are equal, by comparing a few key fields. Good enough definition of "equality"
    // during unit testing
    public static boolean equal(BufferedImage bi1, BufferedImage bi2)
    {
        if (bi1 == bi2)
        {
            return true; //same instance
        }
        if (bi1 == null && bi2 != null)
        {
            return false;
        }
        if (bi2 == null && bi1 != null)
        {
            return false;
        }

        if (!(bi1.getHeight() == bi2.getHeight()))
        {
            return false;
        }

        if (!(bi1.getWidth() == bi2.getWidth()))
        {
            return false;
        }

        if (!(bi1.getColorModel().equals(bi2.getColorModel())))
        {
            return false;
        }

        if (!(bi1.getType() == bi2.getType()))
        {
            return false;
        }
        return true;
    }

    /**
     * Custom assertion method to compare two boolean arrays for equality
     * @param msg  message to be displayed if an AssertionError is thrown, when arrays are not equal
     * @param expected the expected boolean array
     * @param actual  the actual boolean array
     * @return true if both arrays are the same size and contain the same elements
     */
    public static boolean assertArrayEquals(String msg, boolean[] expected, boolean[] actual)
    {
        if (expected == actual)
        {
            return true;
        }
        if (expected == null && actual != null)
        {
            return false;
        }
        if (expected != null && actual == null)
        {
            return false;
        }

        if (expected.length != actual.length)
        {
            throw new AssertionError(msg + String.format(" Arrays not same size. expected.length=%s, actual.length=%s",expected.length, actual.length));
        }

        for (int i = 0, n = expected.length; i < n; i++)
        {
            if (expected[i] != actual[i])
            {
                throw new AssertionError(msg + String.format(" Arrays differ at index=%d. expected[%d]=%b, actual[%d]=%b", i,i, expected[i], i,actual[i]));
            }
        }
        return true;
    }


    //diagnostic code to determine all classes and test class present in this package for inclusion in the default array
    //of ConverterRegistry, as well as determining all JUnit tests for the arguments to the SuiteClasses annotation

    //print all public classes in this package, as well as nested public static classes

    public void printMemberClasses() throws ClassNotFoundException, UnsupportedEncodingException
    {
        Class c = getClass();
        Package pack = c.getPackage();

        String packageName = pack.getName();
        //todo - remove println
        System.out.println(String.format("packageName = %s", packageName));

        /*       Class[] classes = getClasses("src."+packageName+".AllTests");
        for (Class aClass : classes)
        {
            //todo - remove println
            System.out.println(String.format("%s", aClass.getName()));
        }*/

        java.util.List<Class> cl = getClassesForPackage(packageName);
        printlnNameOfTestClasses(cl);
        System.out.println(String.format("\n"));
        printlnDefaultConverterInstantiations(cl);


    }

    private void printlnDefaultConverterInstantiations(java.util.List<Class> cl)
    {
        java.util.List<String> testClasses = new ArrayList<String>();
        for (Class klaz : cl)
        {
            if (!klaz.getSimpleName().contains("Test") && klaz.getSimpleName().contains("StringTo"))
            {
                String s = klaz.getCanonicalName().substring(klaz.getPackage().getName().length() + 1);
                testClasses.add(s);
            }
        }
        String[] classNames = testClasses.toArray(new String[0]);
        Arrays.sort(classNames);
        for (String s : classNames)
        {
            System.out.println(String.format("new %s(), ", s));
        }
    }


    public void printlnNameOfTestClasses(java.util.List<Class> cl)
    {
        java.util.List<String> testClasses = new ArrayList<String>();
        for (Class klaz : cl)
        {
            if (klaz.getSimpleName().contains("Test"))
            {
                String s = klaz.getCanonicalName().substring(klaz.getPackage().getName().length() + 1);
                testClasses.add(s);
            }
        }
        String[] classNames = testClasses.toArray(new String[0]);
        Arrays.sort(classNames);
        for (String s : classNames)
        {
            System.out.println(String.format("%s.class,", s));
        }
    }

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @param pckgname the package name to search
     * @return a list of classes that exist within that package
     * @throws ClassNotFoundException if something went wrong
     *                                <p/>
     *                                found this on : http://forums.sun.com/thread.jspa?threadID=341935&start=15&tstart=0
     */
    public static java.util.List<Class> getClassesForPackage(String pckgname) throws ClassNotFoundException
    {
        // This will hold a list of directories matching the pckgname. There may be more than one if a package is split over multiple jars/paths
        ArrayList<File> directories = new ArrayList<File>();
        try
        {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null)
            {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = pckgname.replace('.', '/');
            // Ask for all resources for the path
            Enumeration<URL> resources = cld.getResources(path);
            while (resources.hasMoreElements())
            {
                directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
            }
        }
        catch (NullPointerException x)
        {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
        }
        catch (UnsupportedEncodingException encex)
        {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
        }
        catch (IOException ioex)
        {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
        }

        ArrayList<Class> classes = new ArrayList<Class>();
        // For every directory identified capture all the .class files
        for (File directory : directories)
        {
            if (directory.exists())
            {
                // Get the list of the files contained in the package
                String[] files = directory.list();
                for (String file : files)
                {
                    // we are only interested in .class files
                    if (file.endsWith(".class"))
                    {
                        // removes the .class extension
                        classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
                    }
                }
            }
            else
            {
                throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
            }
        }
        return classes;
    }

    public static Class[] getClassesFromFileJarFile(String pckgname, String baseDirPath) throws ClassNotFoundException
    {
        ArrayList<Class> classes = new ArrayList<Class>();
        String path = pckgname.replace('.', '/') + "/";
        File mF = new File(baseDirPath);
        String[] files = mF.list();
        ArrayList jars = new ArrayList();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].endsWith(".jar")) jars.add(files[i]);
            //todo - remove println
            System.out.println(String.format("files[%s] = %s/%s", i, baseDirPath, files[i]));
        }

        for (int i = 0; i < jars.size(); i++)
        {
            try
            {
                JarFile currentFile = new JarFile(baseDirPath + "/" + jars.get(i).toString());
                for (Enumeration e = currentFile.entries(); e.hasMoreElements();)
                {
                    JarEntry current = (JarEntry) e.nextElement();
                    if (current.getName().length() > path.length() && current.getName().substring(0, path.length()).equals(path) && current.getName().endsWith(".class"))
                        classes.add(Class.forName(current.getName().replaceAll("/", ".").replace(".class", "")));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        Class[] classesA = new Class[classes.size()];
        classes.toArray(classesA);
        return classesA;
    }


    public static void main1(String[] args) throws ClassNotFoundException, UnsupportedEncodingException
    {
        //new AllTests().printMemberClasses();
/*        Class[] classes = AllTests.getClassesFromFileJarFile("org.junit", "./lib");
        for (Class c : classes)
        {
            //todo - remove println
            System.out.println(String.format("%s",c));
        }*/


        Class c1 = int.class;
        Class c2 = Integer.class;
        //todo - remove println
        System.out.println(String.format("c1=%s, c2=%s, Integer.TYPE=%s", c1.getCanonicalName(), c2.getCanonicalName(), Integer.TYPE));
        System.out.println(String.format("supercass: c1=%s, c2=%s", c1.getSuperclass() == null ? "null" : c1.getSuperclass().getName(), c2.getSuperclass().getCanonicalName()));
    }
}
