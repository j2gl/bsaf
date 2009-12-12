package org.jdesktop.application.utils;

/**
 *
 * Class containing help methods on application level.
 * @author Vity
 */
public final class AppHelper {

    private static PlatformType activePlatformType = null;

    private AppHelper() {
    }


    /*
    * Defines the default value for the platform resource,
    * either "osx" or "default".
    */
    public static PlatformType platform() {
        if (activePlatformType != null)
            return activePlatformType;
        try {
            String osName = System.getProperty("os.name");
            if (osName != null) {
                osName = osName.toLowerCase();
                for (PlatformType platformType : PlatformType.values()) {
                    for (String pattern : platformType.getPatterns()) {
                        if (osName.startsWith(pattern)) {
                            return activePlatformType = platformType;
                        }
                    }
                }
            }
        } catch (SecurityException ignore) {
            //ignore
        }
        return activePlatformType = PlatformType.DEFAULT;
    }
}
