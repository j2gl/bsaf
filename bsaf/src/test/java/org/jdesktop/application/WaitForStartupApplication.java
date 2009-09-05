/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package org.jdesktop.application;

/**
 * Support for launching an application from a non-EDT thread and
 * waiting until its startup method has finished running on the EDT.
 */
public class WaitForStartupApplication extends Application {
    private static final Object lock = new Object(); // static: Application is a singleton
    private boolean started = false;

    /**
     * Unblock the launchAndWait() method.
     */
    @Override
    protected void startup() {
        synchronized (lock) {
            started = true;
            System.out.println("about to notify");
            lock.notifyAll();
        }
    }

    boolean isStarted() {
        return started;
    }

    /**
     * Launch the specified subclsas of WaitForStartupApplication and block
     * (wait) until it's startup() method has run.
     */
    public static void launchAndWait(Class<? extends WaitForStartupApplication> applicationClass) {
        System.out.println("launchAndWait");
        synchronized (lock) {
            Launcher.getInstance().launch(applicationClass, new String[]{});
            while (true) {
                try {
                    System.out.println("about to wait");
                    lock.wait();
                }
                catch (InterruptedException e) {
                    System.err.println("launchAndWait interrupted!");
                    break;
                }
                Application app = Application.getInstance(WaitForStartupApplication.class);
                if (app instanceof WaitForStartupApplication) {
                    if (((WaitForStartupApplication) app).isStarted()) {
                        break;
                    }
                }
            }
        }
    }
}
