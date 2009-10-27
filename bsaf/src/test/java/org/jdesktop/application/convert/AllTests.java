package org.jdesktop.application.convert;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.URL;
import java.net.URLDecoder;

@RunWith(value = Suite.class)
@Suite.SuiteClasses(value =
   {
           TestStringToBoolean.class,
           TestStringToBoolean.TestDataValues.class,
           TestStringToCharacter.class,
           TestStringToColor.class,
           TestStringToColor.TestStrings.class,
           TestStringToDimension.class,
           TestStringToEmptyBorder.class,
           TestStringToFont.class,
           TestStringToImage.class,
           TestStringToImage.TestStringToBufferedImage.class,
           TestStringToImage.TestStringToIcon.class,
           TestStringToInsets.class,
           TestStringToKeyStroke.class,
           TestStringToKeyStroke.TestStrings.class,
           TestStringToNumber.class,
           TestStringToNumber.TestStringToByte.class,
           TestStringToNumber.TestStringToDouble.class,
           TestStringToNumber.TestStringToFloat.class,
           TestStringToNumber.TestStringToInteger.class,
           TestStringToNumber.TestStringToLong.class,
           TestStringToNumber.TestStringToShort.class,
           TestStringToPoint.class,
           TestStringToPoint2D_Double.class,
           TestStringToPoint2D_Float.class,
           TestStringToRect2D_Double.class,
           TestStringToRect2D_Float.class,
           TestStringToRectangle.class,
           TestStringToURI.class,
           TestStringToURL.class,
           TestConverterRegistry.class

   })
public class AllTests
{


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

        List<Class> cl = getClassesForPackage(packageName);
        printlnNameOfTestClasses(cl);
        System.out.println(String.format("\n"));
        printlnDefaultConverterInstantiations(cl);


    }

    private void printlnDefaultConverterInstantiations(List<Class> cl)
    {
        List<String> testClasses = new ArrayList<String>();
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


    public void printlnNameOfTestClasses(List<Class> cl)
    {
        List<String> testClasses = new ArrayList<String>();
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
     *
     * found this on : http://forums.sun.com/thread.jspa?threadID=341935&start=15&tstart=0
     */
    public static List<Class> getClassesForPackage(String pckgname) throws ClassNotFoundException
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
            System.out.println(String.format("files[%s] = %s/%s",i, baseDirPath, files[i]));
        }

        for (int i = 0; i < jars.size(); i++)
        {
            try
            {
                JarFile currentFile = new JarFile(baseDirPath+"/"+jars.get(i).toString());
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