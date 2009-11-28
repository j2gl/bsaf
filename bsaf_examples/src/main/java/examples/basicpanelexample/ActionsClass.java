/*
 * Copyright (C) 2009 Illya V. Yalovyy All rights reserved. Use is
 * subject to license terms.
 */

package examples.basicpanelexample;

import org.jdesktop.application.Action;

/**
 *
 * @author Illya Yalovyy
 */
public class ActionsClass {
    @Action
    public void sharedAction1() {
        System.out.println("sharedAction1");
    }

    @Action
    public void sharedAction2() {
        System.out.println("sharedAction2");
    }
}
