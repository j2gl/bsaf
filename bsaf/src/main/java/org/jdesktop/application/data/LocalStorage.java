package org.jdesktop.application.data;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This is an interface to a local storage.
 *
 * @author Sergey A. Malenkov
 */
public interface LocalStorage {

    /**
     * Opens an input stream to read from the entry
     * specified by the {@code name} parameter.
     * If the named entry cannot be opened for reading
     * then a {@code IOException} is thrown.
     *
     * @param name  the storage-dependent name
     * @return an {@code InputStream} object
     * @throws IOException if the specified name is invalid,
     *                     or an input stream cannot be opened
     */
    InputStream openInputStream(String name) throws IOException;

    /**
     * Opens an output stream to write to the entry
     * specified by the {@code name} parameter.
     * If the named entry cannot be opened for writing
     * then a {@code IOException} is thrown.
     * If the named entry does not exist it can be created.
     *
     * @param name  the storage-dependent name
     * @return an {@code OutputStream} object
     * @throws IOException if the specified name is invalid,
     *                     or an output stream cannot be opened
     */
    OutputStream openOutputStream(String name) throws IOException;

    /**
     * Deletes the entry specified by the {@code name} parameter.
     *
     * @param name  the storage-dependent name
     * @throws IOException if the specified name is invalid,
     *                     or an internal entry cannot be deleted
     */
    void deleteEntry(String name) throws IOException;
}
