/*
* Copyright (C) 2009 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/
package org.jdesktop.application.tests.issue64;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;
import org.jdesktop.application.Launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Pavel Porvatov
 */
public class Issue64Test extends SingleFrameApplication {
    private boolean blockActionEnabled = true;

    public static void main(String[] args) {
        Launcher.getInstance().launch(Issue64Test.class, args);
    }

    protected void startup() {
        JFrame mainFrame = getMainFrame();

        mainFrame.add(createMainPanel());

        show(mainFrame);
    }

    private JComponent createMainPanel() {
        final Action blockAction = getContext().getActionMap().get("blockAction");

        final JProgressBar progressBar = new JProgressBar(0, 100);

        getContext().getTaskMonitor().addPropertyChangeListener("progress", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                progressBar.setValue((Integer) (evt.getNewValue()));
            }
        });

        blockAction.putValue(Action.NAME, "Block action");

        JButton btnBlockAction = new JButton(blockAction);

        JButton btnEnableAction = new JButton("Enable action");

        btnEnableAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                blockAction.setEnabled(true);
            }
        });

        JButton btnDisableAction = new JButton("Disable action");

        btnDisableAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                blockAction.setEnabled(false);
            }
        });

        JPanel result = new JPanel(new FlowLayout());

        result.add(btnBlockAction);
        result.add(progressBar);
        result.add(btnEnableAction);
        result.add(btnDisableAction);

        return result;
    }

    @org.jdesktop.application.Action(
        block = Task.BlockingScope.ACTION,
        enabledProperty = "blockActionEnabled"
    )
    public Task blockAction() {
        return new ActionTask();
    }

    public boolean isBlockActionEnabled() {
        return blockActionEnabled;
    }

    public void setBlockActionEnabled(boolean blockActionEnabled) {
        boolean oldValue = this.blockActionEnabled;

        this.blockActionEnabled = blockActionEnabled;

        firePropertyChange("blockActionEnabled", oldValue, blockActionEnabled);
    }

    private class ActionTask extends Task<Void, Void> {
        ActionTask() {
            super(Application.getInstance());
        }

        @Override
        protected Void doInBackground() throws InterruptedException {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(100);
                setProgress(i, 0, 99);
            }

            return null;
        }
    }
}
