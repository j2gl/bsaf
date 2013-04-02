/*
 * Copyright (C) 2013 Kevin Greiner
 * Use is subject to license terms.
 */
package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Kevin Greiner
 */
public class TaskServiceMonitorTest {
    static final int totalTasks = 10000;
    static final long timePerTask = 5;
    static final int numThreads = 10;

    public static final class WaitingTask extends Task<Void, Void> {
        final CountDownLatch startSignal;
        final long timePerTask;

        public WaitingTask(Application application, CountDownLatch startSignal, long timePerTask) {
            super(application);

            this.startSignal = startSignal;
            this.timePerTask = timePerTask;
        }

        @Override
        protected Void doInBackground() {
            try {
                // wait for the signal
                startSignal.await();

                // Simulated work load
                Thread.sleep(timePerTask);
            } catch (InterruptedException ex) {
                //
            }

            return null;
        }
    }

    /**
     * Determine the average delay (overhead) for the TaskMonitor to report on
     * 10000 new tasks being added to a TaskService.
     */
    @Test
    public void testTaskServiceExecuteOverhead() throws InterruptedException {
        WaitForStartupApplication.launchAndWait(WaitForStartupApplication.class);

        WaitForStartupApplication application = Application.getInstance(WaitForStartupApplication.class);
        ApplicationContext context = application.getContext();
        TaskService taskService = context.getTaskService();
        TaskMonitor taskMonitor = context.getTaskMonitor();

        WaitingTask[] tasks = new WaitingTask[(int) totalTasks];

        final CountDownLatch startSignal = new CountDownLatch(1);
        // Create the tasks now to avoid counting the construction time
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new WaitingTask(application, startSignal, timePerTask);
        }

        // TaskService and TaskMonitor fire their property listeners in the EDL thread 
        // so use our own listener to know when the EDL has dispatched all of these events.
        final CountDownLatch incompleteExecutes = new CountDownLatch(tasks.length);
        final PropertyChangeListener taskFullyQueuedListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                List<Task> oldTasks = (List<Task>) evt.getOldValue();
                List<Task> newTasks = (List<Task>) evt.getNewValue();

                int newPendingExecution = newTasks.size() - oldTasks.size();

                while (newPendingExecution-- > 0) {
                    incompleteExecutes.countDown();
                }
            }
        };

        taskMonitor.addPropertyChangeListener("tasks", taskFullyQueuedListener);

        // Start timing
        long timeBeforeExecute = System.currentTimeMillis();

        // Queue up all of the tasks that are to be executed
        for (int i = 0; i < tasks.length; i++) {
            taskService.execute(tasks[i]);
        }

        // Wait for TaskMonitor to report all of the task execute calls
        // NOTE: None of these tasks may actually run because they are all waiting
        // for startSignal to count down.
        incompleteExecutes.await();

        // End timing
        long timeAfterExecute = System.currentTimeMillis();

        double executePerTask = ((double) timeAfterExecute - timeBeforeExecute) / totalTasks;

        // This is a completely arbitrary limit but 0.1 milliseconds seems reasonable
        // for adding a task to the task service.
        assertTrue("The average time to add a task to the TaskService (" + executePerTask + " ms) exceeds the target limit of 0.1 milliseconds.", executePerTask < 0.1);
    }

    /**
     * Test whether the monitor correctly updates after all tasks canceled via a shutdownNow() call.
     */
    @Test
    public void testMonitorResponse2ShutdownNow() throws InterruptedException {
        WaitForStartupApplication.launchAndWait(WaitForStartupApplication.class);

        WaitForStartupApplication application = Application.getInstance(WaitForStartupApplication.class);
        ApplicationContext context = application.getContext();
        TaskService taskService = context.getTaskService();
        TaskMonitor taskMonitor = context.getTaskMonitor();

        // TaskService and TaskMonitor fire their property listeners in the EDL thread 
        // so use our own listener to know when the EDL has dispatched all of these events.
        final CountDownLatch incompleteExecutes = new CountDownLatch(10);
        final PropertyChangeListener taskFullyQueuedListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                List<Task> oldTasks = (List<Task>) evt.getOldValue();
                List<Task> newTasks = (List<Task>) evt.getNewValue();

                int newPendingExecution = newTasks.size() - oldTasks.size();

                while (newPendingExecution-- > 0) {
                    incompleteExecutes.countDown();
                }
            }
        };

        taskMonitor.addPropertyChangeListener("tasks", taskFullyQueuedListener);

        // Queue up all of the tasks that are to be executed
        final CountDownLatch readySignal = new CountDownLatch(1);
        for (long i = 0, l = incompleteExecutes.getCount(); i < l; i++) {
            taskService.execute(new WaitingTask(application, readySignal, timePerTask));
        }

        // Wait for TaskMonitor to report all of the task execute calls
        // NOTE: None of these tasks may actually run because they are all waiting
        // for startSignal to count down.
        incompleteExecutes.await();

        taskService.shutdownNow();
        
        // The shutdown will generate a number of events which must complete before continuing.
         Thread.sleep(1000);

        assertEquals("taskMonitor.getTasks().size() == 0", 0, taskMonitor.getTasks().size());
    }
}
