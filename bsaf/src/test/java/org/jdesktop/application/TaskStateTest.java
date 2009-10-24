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
public class TaskStateTest{
    private enum State {
        PCL,
        SUCCEEDED,
        FAILED
    }



    public static class SimpleApplication extends WaitForStartupApplication {

        public boolean startupOnEDT;
    }

    public static class DoNothingTask extends Task<Void, Void> {

        private final Exception ex;
        private final Queue<State> queue;


        DoNothingTask(Exception ex, Queue<State> queue) {
            super(Application.getInstance(SimpleApplication.class), "DoNothingTask");
            this.ex = ex;
            this.queue = queue;
        }

        @Override
        protected Void doInBackground() throws Exception {
            
            if (ex!=null)
                throw ex;
            return null;
        }

        @Override
        protected void failed(Throwable cause) {
            queue.offer(State.FAILED);
        }

        @Override
        protected void succeeded(Void result) {
            queue.offer(State.SUCCEEDED);
        }
    }

    private static class PropertyChangeListenerImpl implements PropertyChangeListener {
        private final BlockingQueue<State> queue;

        public PropertyChangeListenerImpl(BlockingQueue<State> queue) {
            this.queue = queue;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (Task.PROP_DONE.equals(evt.getPropertyName())) {
                queue.offer(State.PCL);
            }
        }
    }

    @Before
    public void methodSetup()
    {
        SimpleApplication.launchAndWait(SimpleApplication.class);
    }

    @Test
    public void testSucceeded() throws InterruptedException {
        State result = runTask(null);

        assertNotNull(result);
        assertTrue(result == State.SUCCEEDED);
    }

    @Test
    public void testFailed() throws InterruptedException {
        State result = runTask(new Exception("Test Exception"));

        assertNotNull(result);
        assertTrue(result == State.FAILED);
    }

    private State runTask(Exception ex) throws InterruptedException {
        BlockingQueue<State> queue = new ArrayBlockingQueue<State>(2);
        DoNothingTask task = new DoNothingTask(ex, queue);
        task.addPropertyChangeListener(new PropertyChangeListenerImpl(queue));
        task.execute();
        queue.poll(1, TimeUnit.SECONDS);
        return queue.poll(1, TimeUnit.SECONDS);
    }
}
