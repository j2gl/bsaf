package org.jdesktop.application.data;

import java.io.IOException;

/**
 * This is an interface to a session storage.
 *
 * @author Sergey A. Malenkov
 */
public interface SessionStorage {

    void store(Object object, String name) throws IOException;

    void restore(Object object, String name) throws IOException;
}
