/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package org.jdesktop.application;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.DefaultListModel;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Test the LocalStorage class.
 * <p/>
 * This test relies on a System Property that defines the directory
 * into which LocalStorage test files are read/written.  The property
 * is defined in nbproject/project.properties:
 * <pre>
 * test-sys-prop.LocalStorage.dir=${basedir}/${build.dir}/local-storage.tmp
 * </pre>
 * In other words, by default, files are written to the directory
 * whose relative name is "build/local-storage.tmp"
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class LocalStorageTest
{
    private  File localStorageDirectory;
    private  ApplicationContext context = new ApplicationContext();

    @Before
    public void methodSetup()
    {
        String dirString = System.getProperty("LocalStorage.dir");
        if (dirString == null)
        {
            throw new Error("System property \"LocalStorage.dir\" not defined");
        }
        File dir = new File(dirString);
        if (!dir.exists())
        {
            String msg = "Creating LocalStorage tmp directory \"" + dir + "\"";
            System.out.println(msg);
            if (!dir.mkdir())
            {
                throw new Error(msg + " -failed-");
            }
        }
        else
        {
            String msg = "LocalStorage tmp directory: \"" + dir + "\"";
            System.out.println(msg);
        }
        if (!(dir.canRead() && dir.canWrite()))
        {
            String msg = "Can't access LocalStorage tmp directory \"" + dir + "\"";
            throw new Error(msg);
        }
        /* Initialize the LocalStorage directory here, to simplify debugging.
       */
        localStorageDirectory = dir;
        context.getLocalStorage().setDirectory(localStorageDirectory);
    }


    public static class ABean
    {
        private URL url = null;
        private File file = null;
        private boolean b = false;
        private String s = "not initialized";

        public ABean() throws MalformedURLException {
        }

        public String getS() { return s; }

        public void setS(String s) { this.s = s; }

        public boolean isB() { return b; }

        public void setB(boolean b) { this.b = b; }

        public URL getUrl() { return url; }

        public void setUrl(URL url) { this.url = url; }

        public File getFile() { return file; }

        public void setFile(File file) { this.file = file; }
    }

    @Test
    public void testBasics() throws IOException {
        LocalStorage ls = context.getLocalStorage();
        assertEquals("LocalStorage.getDirectory", localStorageDirectory, ls.getDirectory());

        String expectedS = "setS";
        boolean expectedB = true;
        File expectedFile = new File("testFile");
        URL expectedURL = new URL("http://www.google.com");

        ABean aBean = new ABean();
        aBean.setS(expectedS);
        aBean.setB(expectedB);
        aBean.setFile(expectedFile);
        aBean.setUrl(expectedURL);

        String filename = "aBean.xml";
        ls.save(aBean, filename);
        File file = new File(ls.getDirectory(), filename);
        assertTrue(filename + " exists", file.exists());
        assertTrue(filename + " is readable", file.canRead());
        Object o = ls.load(filename);
        File dir = localStorageDirectory;
        assertNotNull("Loaded " + dir + "/" + filename, o);
        assertTrue("Loaded " + dir + "/" + filename + " - ABean", o instanceof ABean);
        aBean = (ABean) o;
        assertEquals("aBean.getS()", expectedS, aBean.getS());
        assertEquals("aBean.getB()", expectedB, aBean.isB());
        assertEquals("aBean.getURL()", expectedURL, aBean.getUrl());
        assertEquals("aBean.getFile()", expectedFile, aBean.getFile());

        ls.deleteFile(filename);
        assertTrue(file.getPath() + " was deleted", !file.exists());
    }

     @Test
     public void testInputOutput() throws IOException {
        final LocalStorage localStorage = context.getLocalStorage();
        final String f = "testFile.tmp";
        try {
            localStorage.deleteFile(f);
        } catch (IOException e) {
            //ignore if the file does not exist
        }
        OutputStream out = localStorage.openOutputFile(f);//testing method openOutputStream
        out.write("start".getBytes());
        out.close();
        Scanner in = new Scanner(localStorage.openInputFile(f));
        assertEquals("Test write/read", "start", in.nextLine());
        in.close();
        //test append
        out = localStorage.openOutputFile(f, true);//append to file
        out.write("appended".getBytes());
        out.close();
        in = new Scanner(localStorage.openInputFile(f));
        assertEquals("Test write/read2", "startappended", in.nextLine());
        in.close();

        out = localStorage.openOutputFile(f, false);//no append
        out.write("start".getBytes());
        out.close();
        in = new Scanner(localStorage.openInputFile(f));
        assertEquals("Test write/read3", "start", in.nextLine());
        in.close();

        try {
            //test delete
            localStorage.deleteFile(f);
        } catch (IOException e) {
            //ignore if the file does not exist
        }

        try {
            //file does not exists
            localStorage.openInputFile(f);
            throw new Error("Should throw IOexception - method deleteEntry does not work properly?");
        } catch (IOException e) {
            //ignore - OK
        }
    }
 
    @Test
    public void testDefaultListModel() throws IOException
    {
        LocalStorage ls = context.getLocalStorage();
        assertEquals("LocalStorage.getDirectory", localStorageDirectory, ls.getDirectory());
        
        DefaultListModel expected = new DefaultListModel();
        String member1 = "A";
        String member2 = "B";
        
        expected.addElement(member1);
        expected.addElement(member2);
        
        String filename = "defaultListModel.xml";
        File file = new File(ls.getDirectory(), filename);
        ls.save(expected, filename);
        
        DefaultListModel actual = (DefaultListModel) ls.load(filename);
        
        assertEquals("model.getSize()", expected.getSize(), actual.getSize());
        assertEquals("model.get(0)", expected.get(0), actual.get(0));
        assertEquals("model.get(1)", expected.get(1), actual.get(1));
        
        ls.deleteFile(filename);
        assertTrue(file.getPath()+ " was deleted", !file.exists());
    }
}