/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */
package org.jdesktop.application;

import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Illya Yalovyy
 */
public class TaskMonitorTest{

    public static final String MESSAGE_TASK0 = "* Task 0";
    public static final String MESSAGE_TASK1 = "* Task 1";    
    
    public static class SimpleApplication extends WaitForStartupApplication {

        public boolean startupOnEDT;
    }

    public static class LatchTask extends Task<Void, Void> {
        final CountDownLatch allTasksDoneLatch;
        final CountDownLatch startLatch;
        final LatchTask other;
        private final String message;
        volatile boolean fired = false;
        
        public LatchTask(String message, CountDownLatch startLatch, CountDownLatch allTasksDoneLatch, LatchTask other) {
            super(Application.getInstance(SimpleApplication.class));
            this.message = message;
            this.startLatch = startLatch;
            this.allTasksDoneLatch = allTasksDoneLatch;
            this.other = other;
        }

        @Override
        protected Void doInBackground() throws Exception {
            
            if (startLatch!=null) startLatch.await(500, TimeUnit.MILLISECONDS);
            
            setMessage(message);
            
            fired = true;
            
            if (other != null) {
                Application.getInstance().getContext().getTaskService().execute(other);
            }
            
            return null;
        }        
        
        @Override
        protected void finished() {
            if (other != null) {
                other.startLatch.countDown();
            }
            allTasksDoneLatch.countDown();
        }        
    }

    @Before
    public void methodSetup()
    {
        SimpleApplication.launchAndWait(SimpleApplication.class);
    }

    CountDownLatch allTasksDoneLatch = new CountDownLatch(2);
    CountDownLatch startLatch = new CountDownLatch(1);

    @Test
    public void testSucceeded() throws InterruptedException {
        
        TaskMonitor mon = Application.getInstance().getContext().getTaskMonitor();
        RecordingPropertyChangeListener pcl = new RecordingPropertyChangeListener();
        mon.addPropertyChangeListener(pcl);
        LatchTask t0 = new LatchTask(MESSAGE_TASK0, new CountDownLatch(1), allTasksDoneLatch, null);
        LatchTask t1 = new LatchTask(MESSAGE_TASK1, startLatch, allTasksDoneLatch, t0);
        Application.getInstance().getContext().getTaskService().execute(t1);

        allTasksDoneLatch.await(1000, TimeUnit.MILLISECONDS);
        
        assertTrue(t0.fired);
        assertTrue(t1.fired);
        assertTrue(pcl.messages.contains(MESSAGE_TASK0));
        assertTrue(pcl.messages.contains(MESSAGE_TASK1));
    }
    
    private class RecordingPropertyChangeListener implements PropertyChangeListener{
        List<String> messages = new ArrayList<String>();
        
        public void propertyChange(PropertyChangeEvent evt) {
            
            if (TaskMonitor.PROP_FOREGROUND_TASK.equals(evt.getPropertyName())) {
                // Wait until TaskMonitor insert listeners. may be this process should be synchronized?
                startLatch.countDown();
            }
            
            if (Task.PROP_MESSAGE.equals(evt.getPropertyName())){
                messages.add((String) evt.getNewValue());
            }
        }
    }
}
