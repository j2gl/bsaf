package org.jdesktop.application.data;

import java.util.prefs.Preferences;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 * The state manager implementation for the JTable class.
 *
 * @author Sergey A. Malenkov
 */
final class JTableState implements StateManager<JTable> {

    public void store(JTable table, Preferences preferences) {
        int count = table.getColumnCount();
        for (int i = 0; i < count; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            String name = "column." + column.getModelIndex();
            if (column.getResizable()) {
                preferences.putInt(name, column.getWidth());
            }
            else {
                preferences.remove(name);
            }
        }
        preferences.putInt("column.count", count);
    }

    public void restore(JTable table, Preferences preferences) {
        int count = preferences.getInt("column.count", Integer.MIN_VALUE);
        if (count == table.getColumnCount()) {
            for (int i = 0; i < count; i++) {
                TableColumn column = table.getColumnModel().getColumn(i);
                if (column.getResizable()) {
                    String name = "column." + column.getModelIndex();
                    int value = preferences.getInt(name, Integer.MIN_VALUE);
                    if (value != Integer.MIN_VALUE) {
                        column.setPreferredWidth(value);
                    }
                }
            }
        }
    }
}
