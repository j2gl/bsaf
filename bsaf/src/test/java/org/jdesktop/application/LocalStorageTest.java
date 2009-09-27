
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package org.jdesktop.application;

import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;

/**
 * Test the LocalStorage class.
 * <p>
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
	}
	else {
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
	context.getLocalStorage().setDirectory(localStorageDirectory);
    }
    
    public static class ABean {
	private boolean b = false;
	private String s = "not initialized";

	public String getS() { return s; }
	public void setS(String s) { this.s = s; }

	public boolean isB() { return b; }
	public void setB(boolean b) { this.b = b; }
    }
     
    public void testBasics() throws IOException {
	LocalStorage ls = context.getLocalStorage();
	assertEquals("LocalStorage.getDirectory", localStorageDirectory, ls.getDirectory());
	ABean aBean = new ABean();
	aBean.setS("setS");
	aBean.setB(true);
	String filename = "aBean.xml";
	ls.save(aBean, filename);
	File file = new File(ls.getDirectory(), filename);
	assertTrue(filename + " exists", file.exists());
	assertTrue(filename + " is readable", file.canRead());
	Object o = ls.load(filename);
	File dir = localStorageDirectory;
	assertNotNull("Loaded " + dir + "/" + filename, o);
	assertTrue("Loaded " + dir + "/" + filename +  " - ABean", o instanceof ABean);
	aBean = (ABean)o;
	assertEquals("aBean.getS()", "setS", aBean.getS());
	assertEquals("aBean.getB()", true, aBean.isB());
	ls.deleteFile(filename);
	assertTrue(filename + " was deleted", !file.exists());
    }
}