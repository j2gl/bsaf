/*
 * Copyright (C) 2011 Illya Yalovyy
 * Use is subject to license terms.
 */
package org.jdesktop.application;

import java.awt.event.ActionEvent;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Illya Yalovyy
 */
public class TaskServiceTest {
    
    public static final String CUSTOM_TASK_SERVICE_NAME = "TaskServiceWithHistory";
    public static final String ACTION1 = "actionWithTask1";
    public static final String ACTION2 = "actionWithTask2";

    public static class TaskServiceWithHistory {
        
    }    
    
    public static class ApplicationWithActions extends WaitForStartupApplication
    {

        @Override
        protected void initialize(String[] args) {
            super.initialize(args);
            this.getContext().addTaskService(new TaskService(CUSTOM_TASK_SERVICE_NAME));
        }
 
        @Action(name=ACTION1)
        public Task<Void, Void> actionWithTask1() {
            return new Task<Void, Void>(this) {

                @Override
                protected Void doInBackground() throws Exception {
                    // DO nothing
                    return null;
                }
            };
        }
 
        @Action(name=ACTION2, taskService=CUSTOM_TASK_SERVICE_NAME)
        public Task<Void, Void> actionWithTask2() {
            return new Task<Void, Void>(this) {

                @Override
                protected Void doInBackground() throws Exception {
                    // DO nothing
                    return null;
                }
            };
        }

    }
    
    @BeforeClass
    public static void unitSetup()
    {
        ApplicationWithActions.launchAndWait(ApplicationWithActions.class);
        
    }
    
    @Test
    public void testDefaultTaskService() {
        ApplicationContext context = Application.getInstance(ApplicationWithActions.class).getContext();
        ApplicationActionMap actionMap = context.getActionMap();
        javax.swing.Action action1 = actionMap.get(ACTION1);
        action1.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
        fail("Tast is not entirely implemented");
    }
}
