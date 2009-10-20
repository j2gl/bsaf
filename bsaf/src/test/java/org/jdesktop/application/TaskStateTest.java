/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */
package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;

/**
 *
 * @author Illya Yalovyy
 */
public class TaskStateTest extends TestCase {

    private static boolean isAppLaunched = false;

    private AtomicBoolean stateMonitor = new AtomicBoolean();

    public static class SimpleApplication extends WaitForStartupApplication {

        public boolean startupOnEDT;
    }

    public static class DoNothingTask extends Task<Void, Void> {
        boolean flag = false;
        private final boolean throwException;
        private final AtomicBoolean stateMonitor;


        DoNothingTask(boolean throwException, AtomicBoolean stateMonitor) {
            super(Application.getInstance(SimpleApplication.class), "DoNothingTask");
            this.throwException = throwException;
            this.stateMonitor = stateMonitor;
        }

        @Override
        protected Void doInBackground() {
            
            if (throwException)
                throw new RuntimeException();
            return null;
        }

        @Override
        protected void failed(Throwable cause) {
            flag = stateMonitor.get();
        }

        @Override
        protected void succeeded(Void result) {
            System.out.println("succeeded");
            flag = stateMonitor.get();
        }
    }

    private static class PropertyChangeListenerImpl implements PropertyChangeListener {
        private final AtomicBoolean stateMonitor;

        public PropertyChangeListenerImpl(AtomicBoolean stateMonitor) {
            this.stateMonitor = stateMonitor;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (Task.PROP_DONE.equals(evt.getPropertyName()))
                System.out.println("propertyChange");
                stateMonitor.set(true);
        }
    }
    
    public TaskStateTest(String testName) {
        super(testName);
        if (!isAppLaunched) {
            SimpleApplication.launchAndWait(SimpleApplication.class);
            isAppLaunched = true;
        }
    }

    public void testSucceeded() {
        stateMonitor.set(false);
        DoNothingTask task = new DoNothingTask(false, stateMonitor);
        task.addPropertyChangeListener(new PropertyChangeListenerImpl(stateMonitor));
        task.execute();
        try {
            task.get();
        } catch (Exception ignore) {
        }
        assertTrue(task.isDone());
        assertTrue(task.flag);
    }
}
