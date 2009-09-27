package org.jdesktop.application.data;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Implements default behaviour for LocalStorage
 * @author Vity
 * @since 1.9
 */
public abstract class AbstractLocalStorage implements LocalStorage {


    @Override
    public OutputStream openOutputStream(String name) throws IOException {
        return openOutputStream(name, false);
    }

}
