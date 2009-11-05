/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */
package org.jdesktop.application;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        FAILED,
        FINISHED
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

        @Override
        protected void finished() {
            queue.offer(State.FINISHED);
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
        List<State> result = runTask(null);

        assertArrayEquals(new State[] {
            State.PCL,
            State.SUCCEEDED,
            State.FINISHED
        }, result.toArray());
    }

    @Test
    public void testFailed() throws InterruptedException {
        List<State> result = runTask(new Exception("Test Exception"));

        assertArrayEquals(new State[] {
            State.PCL,
            State.FAILED,
            State.FINISHED
            }, result.toArray());
    }

    private List<State> runTask(Exception ex) throws InterruptedException {
        List<State> result = new ArrayList<State>();
        BlockingQueue<State> queue = new ArrayBlockingQueue<State>(3);
        DoNothingTask task = new DoNothingTask(ex, queue);
        task.addPropertyChangeListener(new PropertyChangeListenerImpl(queue));
        task.execute();
        result.add(queue.poll(1, TimeUnit.SECONDS));
        result.add(queue.poll(1, TimeUnit.SECONDS));
        result.add(queue.poll(1, TimeUnit.SECONDS));
        return result;
    }
}
