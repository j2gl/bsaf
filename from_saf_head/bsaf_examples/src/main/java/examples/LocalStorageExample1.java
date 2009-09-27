/*
* Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
* subject to license terms.
*/

package examples;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Launcher;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * An example that demonstrates the default support
 * for storing and restoring data.
 * To try it, add some values to the list by pressing the top buttons
 * or by entering value string in the corresponding text field.
 * Then save the list (to a FILENAME), clear it, and load the saved list.
 *
 * @author Sergey A. Malenkov
 */
public class LocalStorageExample1 extends Application {

    private static final String FILENAME = "persistent.properties"; // NON-NLS: file name

    public static void main(String[] args) {
        Launcher.getInstance().launch(LocalStorageExample1.class, args);
    }

    private final JTextField field = new JTextField("Value", 2<<4); // NON-NLS: default value
    private final ListModel model = new ListModel();

    @Action
    public void addValue() {
        int index = model.list.size();
        model.list.add(field.getText().trim());
        model.fireContentsChanged(index, index);
    }

    @Action
    public void addRandomValue() {
        int index = model.list.size();
        model.list.add(Double.toString(Math.random()));
        model.fireContentsChanged(index, index);
    }

    @Action
    public void save() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    getContext().getStorage().getLocalStorage().openOutputStream(FILENAME));
            try {
                for (String value : model.list) {
                    writer.write(value);
                    writer.write('\n');
                }
            }
            finally {
                writer.close();
            }
        }
        catch (IOException exception) {
            showMessageDialog(exception);
        }
    }

    @Action
    public void load() {
        int lower = model.list.size();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getContext().getStorage().getLocalStorage().openInputStream(FILENAME)));
            try {
                String value = reader.readLine();
                while (value != null) {
                    model.list.add(value);
                    value = reader.readLine();
                }
            }
            finally {
                reader.close();
            }
        }
        catch (IOException exception) {
            showMessageDialog(exception);
        }
        int upper = model.list.size();
        if (lower < upper) {
            model.fireContentsChanged(lower, upper);
        }
    }

    @Action
    public void clear() {
        int index = model.list.size();
        if (index > 0) {
            model.list.clear();
            model.fireIntervalRemoved(0, index - 1);
        }
    }

    @Override
    protected void startup() {
        JButton addEntry = new JButton();
        JButton addRandomEntry = new JButton();
        JButton save = new JButton();
        JButton load = new JButton();
        JButton clear = new JButton();

        ActionMap actionMap = getContext().getActionMap();
        addEntry.setAction(actionMap.get("addValue")); // NON-NLS: method name
        addRandomEntry.setAction(actionMap.get("addRandomValue")); // NON-NLS: method name
        save.setAction(actionMap.get("save")); // NON-NLS: method name
        load.setAction(actionMap.get("load")); // NON-NLS: method name
        clear.setAction(actionMap.get("clear")); // NON-NLS: method name
        field.setAction(addEntry.getAction());

        JPanel controls = new JPanel();
        controls.add(field);
        controls.add(addEntry);
        controls.add(addRandomEntry);

        JPanel buttons = new JPanel();
        buttons.add(save);
        buttons.add(load);
        buttons.add(clear);

        JFrame frame = new JFrame(getClass().getSimpleName());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(BorderLayout.CENTER, new JScrollPane(new JList(model)));
        frame.add(BorderLayout.NORTH, controls);
        frame.add(BorderLayout.SOUTH, buttons);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void showMessageDialog(IOException exception) {
        JOptionPane.showMessageDialog(null,
                                      exception.getMessage(),
                                      exception.getClass().getSimpleName(),
                                      JOptionPane.ERROR_MESSAGE);
    }

    private static class ListModel extends AbstractListModel {
        private final List<String> list = new ArrayList<String>();

        public int getSize() {
            return list.size();
        }

        public Object getElementAt(int index) {
            return list.get(index);
        }

        void fireContentsChanged(int lower, int upper) {
            fireContentsChanged(this, lower, upper);
        }

        void fireIntervalRemoved(int lower, int upper) {
            fireIntervalRemoved(this, lower, upper);
        }
    }
}
