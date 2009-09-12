/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package org.jdesktop.application;

import org.jdesktop.application.data.FileSystemStorage;
import org.jdesktop.application.data.LocalStorage;

import junit.framework.TestCase;

import java.io.*;
import java.util.Scanner;

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
public class LocalStorageTest extends TestCase {
    private final File localStorageDirectory;
    private final ApplicationContext context = new ApplicationContext();

    public LocalStorageTest(String testName) {
        super(testName);
        String dirString = System.getProperty("LocalStorage.dir");
        if (dirString == null) {
            throw new Error("System property \"LocalStorage.dir\" not defined");
        }
        File dir = new File(dirString);
        if (!dir.exists()) {
            String msg = "Creating LocalStorage tmp directory \"" + dir + "\"";
            System.out.println(msg);
            if (!dir.mkdir()) {
                throw new Error(msg + " -failed-");
            }
        } else {
            String msg = "LocalStorage tmp directory: \"" + dir + "\"";
            System.out.println(msg);
        }
        if (!(dir.canRead() && dir.canWrite())) {
            String msg = "Can't access LocalStorage tmp directory \"" + dir + "\"";
            throw new Error(msg);
        }
        /* Initialize the LocalStorage directory here, to simplify debugging.
          */
        localStorageDirectory = dir;
        ((FileSystemStorage)context.getStorage().getLocalStorage()).setDirectory(localStorageDirectory);
    }

    public static class ABean {
        private boolean b = false;
        private String s = "not initialized";

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public boolean isB() {
            return b;
        }

        public void setB(boolean b) {
            this.b = b;
        }
    }

    public void testInputOutput() throws IOException {
        final LocalStorage localStorage = context.getStorage().getLocalStorage();
        final String f = "testFile.tmp";
        try {
            localStorage.deleteEntry(f);
        } catch (IOException e) {
            //ignore if the file does not exist
        }
        OutputStream out = localStorage.openOutputStream(f);//testing method openOutputStream
        out.write("start".getBytes());
        out.close();
        Scanner in = new Scanner(localStorage.openInputStream(f));
        assertEquals("Test write/read", "start", in.nextLine());
        in.close();
        //test append
        out = localStorage.openOutputStream(f, true);//append to file
        out.write("appended".getBytes());
        out.close();
        in = new Scanner(localStorage.openInputStream(f));
        assertEquals("Test write/read2", "startappended", in.nextLine());
        in.close();

        out = localStorage.openOutputStream(f, false);//no append
        out.write("start".getBytes());
        out.close();
        in = new Scanner(localStorage.openInputStream(f));
        assertEquals("Test write/read3", "start", in.nextLine());
        in.close();

        try {
            //test delete
            localStorage.deleteEntry(f);
        } catch (IOException e) {
            //ignore if the file does not exist
        }

        try {
            //file does not exists
            localStorage.openInputStream(f);
            throw new Error("Should throw IOexception - method deleteEntry does not work properly?");
        } catch (IOException e) {
            //ignore - OK
        }
    }

    public void testBasics() throws IOException {
        FileSystemStorage ls = (FileSystemStorage)context.getStorage().getLocalStorage();
        assertEquals("LocalStorage.getDirectory", localStorageDirectory, ls.getDirectory());
/*
TODO:rewrite
        ABean aBean = new ABean();
        aBean.setS("setS");
        aBean.setB(true);
        String filename = "aBean.xml";
        context.getSessionStorage().saveXML(aBean, filename);
        File file = new File(ls.getDirectory(), filename);
        assertTrue(filename + " exists", file.exists());
        assertTrue(filename + " is readable", file.canRead());
        Object o = context.getSessionStorage().loadXML(filename);
        File dir = localStorageDirectory;
        assertNotNull("Loaded " + dir + "/" + filename, o);
        assertTrue("Loaded " + dir + "/" + filename + " - ABean", o instanceof ABean);
        aBean = (ABean) o;
        assertEquals("aBean.getS()", "setS", aBean.getS());
        assertEquals("aBean.getB()", true, aBean.isB());
        ls.deleteEntry(filename);
        assertTrue(filename + " was deleted", !file.exists());
*/
    }
}