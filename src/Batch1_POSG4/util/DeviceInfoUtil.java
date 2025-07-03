package Batch1_POSG4.util;

import java.net.InetAddress;

// Provides utility methods for retrieving device and environment information.
public class DeviceInfoUtil {

    // Returns the local IP address of the machine, or "unknown" if unavailable.
    public static String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    // Returns a string describing the OS and Java version.
    public static String getDeviceInfo() {
        return System.getProperty("os.name")
             + " " + System.getProperty("os.version")
             + " | Java " + System.getProperty("java.version");
    }
}