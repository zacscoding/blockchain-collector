package blockchain.util;

/**
 * @author zacconding
 * @Date 2018-09-26
 * @GitHub : https://github.com/zacscoding
 */
public class OSUtil {

    private static String OS;

    private static OSType platform;

    private static String lineSeparator;

    private OSUtil() {
    }

    public enum OSType {
        Any("any"), Linux("Linux"), Mac_OS("Mac OS"), Mac_OS_X("Mac OS X"), Windows("Windows"), OS2("OS/2"), Solaris("Solaris"), SunOS("SunOS"), MPEiX("MPE/iX"), HP_UX("HP-UX"), AIX("AIX"), OS390(
            "OS/390"), FreeBSD("FreeBSD"), Irix("Irix"), Digital_Unix("Digital Unix"), NetWare_411("NetWare"), OSF1("OSF1"), OpenVMS("OpenVMS"), Others("Others");

        OSType(String desc) {
            this.description = desc;
        }

        public String toString() {
            return description;
        }

        private String description;
    }

    public static String getLineSeparator() {
        return lineSeparator;
    }

    public static OSType getOSType() {
        return platform;
    }

    public static boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    public static boolean isMacOS() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
    }

    public static boolean isMacOSX() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public static boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    public static boolean isOS2() {
        return OS.indexOf("os/2") >= 0;
    }

    public static boolean isSolaris() {
        return OS.indexOf("solaris") >= 0;
    }

    public static boolean isSunOS() {
        return OS.indexOf("sunos") >= 0;
    }

    public static boolean isMPEiX() {
        return OS.indexOf("mpe/ix") >= 0;
    }

    public static boolean isHPUX() {
        return OS.indexOf("hp-ux") >= 0;
    }

    public static boolean isAix() {
        return OS.indexOf("aix") >= 0;
    }

    public static boolean isOS390() {
        return OS.indexOf("os/390") >= 0;
    }

    public static boolean isFreeBSD() {
        return OS.indexOf("freebsd") >= 0;
    }

    public static boolean isIrix() {
        return OS.indexOf("irix") >= 0;
    }

    public static boolean isDigitalUnix() {
        return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0;
    }

    public static boolean isNetWare() {
        return OS.indexOf("netware") >= 0;
    }

    public static boolean isOSF1() {
        return OS.indexOf("osf1") >= 0;
    }

    public static boolean isOpenVMS() {
        return OS.indexOf("openvms") >= 0;
    }

    static {
        OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("aix") >= 0) {
            platform = OSType.AIX;
        } else if ((OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0)) {
            platform = OSType.Digital_Unix;
        } else if (OS.indexOf("freebsd") >= 0) {
            platform = OSType.FreeBSD;
        } else if (OS.indexOf("hp-ux") >= 0) {
            platform = OSType.HP_UX;
        } else if (OS.indexOf("irix") >= 0) {
            platform = OSType.Irix;
        } else if (OS.indexOf("linux") >= 0) {
            platform = OSType.Linux;
        } else if (OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0) {
            platform = OSType.Mac_OS;
        } else if (OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0) {
            platform = OSType.Mac_OS_X;
        } else if (OS.indexOf("mpe/ix") >= 0) {
            platform = OSType.MPEiX;
        } else if (OS.indexOf("netware") >= 0) {
            platform = OSType.NetWare_411;
        } else if (OS.indexOf("openvms") >= 0) {
            platform = OSType.OpenVMS;
        } else if (OS.indexOf("os/2") >= 0) {
            platform = OSType.OS2;
        } else if (OS.indexOf("os/390") >= 0) {
            platform = OSType.OS390;
        } else if (OS.indexOf("osf1") >= 0) {
            platform = OSType.OSF1;
        } else if (OS.indexOf("solaris") >= 0) {
            platform = OSType.Solaris;
        } else if (OS.indexOf("sunos") >= 0) {
            platform = OSType.SunOS;
        } else if (OS.indexOf("windows") >= 0) {
            platform = OSType.Windows;
        } else {
            platform = OSType.Others;
        }

        lineSeparator = System.getProperty("line.separator");
    }
}
