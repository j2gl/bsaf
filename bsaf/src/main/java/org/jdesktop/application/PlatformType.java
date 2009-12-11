/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

package org.jdesktop.application;

/**
 *
 * @author Illya Yalovyy
 */
public enum PlatformType {
    DEFAULT ("Default", ""),
    LINUX ("Linux", "lin", "linux"),
    OSX ("Mac os", "osx", "osx"),
    WINDOWS ("Windows", "Windows", "windows");

    private final String name;
    private final String resourcePrefix;
    private final String[] patterns;

    private PlatformType(String name, String resourcePrefix, String... patterns) {
        this.name = name;
        this.resourcePrefix = resourcePrefix;
        this.patterns = patterns;
    }

    public String getName() {
        return name;
    }

    public String[] getPatterns() {
        return patterns.clone();
    }

    public String getResourcePrefix() {
        return resourcePrefix;
    }

    @Override
    public String toString() {
        return name;
    }
}
