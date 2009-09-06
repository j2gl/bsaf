package org.jdesktop.application.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.util.Locale.ENGLISH;

/**
 * This is a local storage implementation
 * based on local file system.
 *
 * @author Sergey A. Malenkov
 */
public final class FileSystemStorage implements LocalStorage {

    private File directory;

    /**
     * TODO:description.
     *
     * @param name    the application identifier
     * @param vendor  the vendor identifier
     * @throws SecurityException
     *         if a security manager exists and
     *         its {@code checkPropertyAccess} method
     *         doesn't allow access to system properties.
     */
    FileSystemStorage(String name, String vendor) {
        String os = getProperty("os.name");
        if (os == null) {
            // denied by security manager
        }
        else if (os.toLowerCase(ENGLISH).startsWith("mac os x")) {
            // ${user.home}/Library/Application Support/${app.name}
            setDirectory(null, "Library", "Application Support", name);
        }
        else if (os.contains("Windows")) {
            File appData = getAppData();
            if (appData != null) {
                // ${APPDATA}\{app.vendor}\${app.name}
                setDirectory(appData, vendor, name);
            }
            else {
                // ${user.home}\Application Data\${app.vendor}\${app.name}
                setDirectory(null, "Application Data", vendor, name);
            }
        }
        else {
            // ${userHome}/.${appName}/
            setDirectory(null, "." + name);
        }
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    @Override
    public InputStream openInputStream(String name) throws IOException {
        try {
            return new BufferedInputStream(new FileInputStream(getFile(name)));
        }
        catch (SecurityException exception) {
            throw new IOException("could not read from entry: " + name, exception);
        }
    }

    @Override
    public OutputStream openOutputStream(String name) throws IOException {
        return openOutputStream(name, false);
    }

    @Override
    public OutputStream openOutputStream(String name, boolean append) throws IOException {
        try {
            File file = getFile(name);
            File dir = file.getParentFile();
            if (!dir.isDirectory() && !dir.mkdirs()) {
                throw new IOException("couldn't create directory " + dir);
            }
            return new BufferedOutputStream(new FileOutputStream(file, append));
        }
        catch (SecurityException exception) {
            throw new IOException("could not write to entry: " + name, exception);
        }
    }

    @Override
    public void deleteEntry(String name) throws IOException {
        try {
            getFile(name).delete();
        }
        catch (SecurityException exception) {
            throw new IOException("could not delete entry: " + name, exception);
        }
    }

    private File getFile(String name) throws IOException {
        if (name == null) {
            throw new IOException("name is not set");
        }
        return new File(directory, name);
    }

    private void setDirectory(File directory, String... names) {
        String path = (directory != null)
                ? directory.getPath()
                : getProperty("user.home");

        StringBuilder sb = new StringBuilder(2 << 7);
        for (String name : names) {
            sb.append(name).append(File.separator);
        }
        this.directory = new File(path, sb.toString());
    }

    private static String getEnvironment(String name) {
        try {
            return System.getenv(name);
        }
        catch (SecurityException ignore) {
            return null;
        }
    }

    private static String getProperty(String name) {
        try {
            return System.getProperty(name);
        }
        catch (SecurityException ignore) {
            return null;
        }
    }

    private static File getAppData() {
        String name = getEnvironment("APPDATA");
        if (name != null) {
            name = name.trim();
            if (0 < name.length()) {
                File dir = new File(name);
                if (dir.isDirectory()) {
                    return dir;
                }
            }
        }
        return null;
    }

}
