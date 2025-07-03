// src/main/java/Batch1_POSG4/util/DeviceInfoUtil.java
package Batch1_POSG4.util;

import java.net.InetAddress;

public class DeviceInfoUtil {
    public static String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static String getDeviceInfo() {
        return System.getProperty("os.name")
             + " " + System.getProperty("os.version")
             + " | Java " + System.getProperty("java.version");
    }
}
