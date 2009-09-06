package org.jdesktop.application.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.jnlp.BasicService;
import javax.jnlp.PersistenceService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

/**
 * This is a local storage implementation
 * based on persistence service of JNLP.
 *
 * @author Sergey A. Malenkov
 */
public final class PersistenceStorage implements LocalStorage {

    private final BasicService bs;
    private final PersistenceService ps;
    private long size = 131072L;

    public/*TODO*/PersistenceStorage() throws UnavailableServiceException {
        bs = (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
        ps = (PersistenceService) ServiceManager.lookup("javax.jnlp.PersistenceService");
    }

    public long getStorageSize() {
        return size;
    }

    public void setStorageSize(long size) {
        this.size = size;
    }

    public InputStream openInputStream(String name) throws IOException {
        return new BufferedInputStream(ps.get(getURL(name)).getInputStream());
    }

    @Override
    public OutputStream openOutputStream(String name) throws IOException {
        return openOutputStream(name, false);
    }

    @Override
    public OutputStream openOutputStream(String name, boolean append) throws IOException {
        URL url = getURL(name);
        if (size > ps.create(url, size)) {
            throw new IOException("unable to create entry: " + name);
        }
        return new BufferedOutputStream(ps.get(url).getOutputStream(append));
    }

    public void deleteEntry(String name) throws IOException {
        ps.delete(getURL(name));
    }

    private URL getURL(String name) throws IOException {
        if (name == null) {
            throw new IOException("name is not set");
        }
        return new URL(bs.getCodeBase(), name);
    }
}
