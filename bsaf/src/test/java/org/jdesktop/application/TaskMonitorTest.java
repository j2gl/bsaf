/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */
package org.jdesktop.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Illya Yalovyy
 */
public class TaskMonitorTest{
    private enum State {
        PCL,
        SUCCEEDED,
        FAILED
    }

    public static class SimpleApplication extends WaitForStartupApplication {

        public boolean startupOnEDT;
    }

    public static class SimpleTask extends Task<Void, Void> {
        boolean fired = false;

        SimpleTask() {
            super(Application.getInstance(SimpleApplication.class), "SimpleTask");
        }

        @Override
        protected Void doInBackground() throws Exception {
            
            Thread.sleep(100);
            setMessage("SimpleTask message");
            fired = true;
            return null;
        }

    }
    
    public static class FireAnotherTask extends Task<Void, Void> {
        boolean fired = false;
        Task other;
        
        FireAnotherTask(Task other) {
            super(Application.getInstance(SimpleApplication.class), "FireAnotherTask");
            this.other = other;
        }

        @Override
        protected Void doInBackground() throws Exception {
            
            fired = true;
            Thread.sleep(50);
            setMessage("FireAnotherTask message");
            Application.getInstance().getContext().getTaskService().execute(other);
            return null;
        }
    }

    @Before
    public void methodSetup()
    {
        SimpleApplication.launchAndWait(SimpleApplication.class);
    }

    @Test
    public void testSucceeded() throws InterruptedException {
        TaskMonitor mon = Application.getInstance().getContext().getTaskMonitor();
        RecordingPropertyChangeListener pcl = new RecordingPropertyChangeListener();
        mon.addPropertyChangeListener(pcl);
        SimpleTask t0 = new SimpleTask();
        FireAnotherTask t1 = new FireAnotherTask(t0);
        Application.getInstance().getContext().getTaskService().execute(t1);

        Thread.sleep(200);
        assertTrue(t0.fired);
        assertTrue(t1.fired);
        assertTrue(pcl.gotFireAnotherMessage);
        assertTrue(pcl.gotSimpleTaskMessage);
    }

    private class RecordingPropertyChangeListener implements PropertyChangeListener{
        boolean gotFireAnotherMessage = false;
        boolean gotSimpleTaskMessage = false;
        
        public void propertyChange(PropertyChangeEvent evt) {
            if ("message".equals(evt.getPropertyName())){
                if (evt.getSource().getClass() == FireAnotherTask.class)
                    gotFireAnotherMessage = true;
                if (evt.getSource().getClass() == SimpleTask.class)
                    gotSimpleTaskMessage = true;
            }
        }
    }
}
