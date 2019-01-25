package collector.util;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class ThreadUtil {

    /**
     * Getting stack trace with cursor
     *
     * e.g)
     * 1) cursor == 0
     *  methodB(ThreadUtil.java:52)
     *  methodA(ThreadUti.java:48)
     *  main(ThreadUtil.java:44)
     *
     * 2) cursor == -1
     * methodA(ThreadUti.java:48)
     * main(ThreadUtil.java:44)
     *
     * 3) cursor == 1
     * methodB(ThreadUtil.java:52)
     * methodA(ThreadUti.java:48)
     *
     * @param cursor @see above
     */
    public static String getStackTraceString(int cursor) {
        StackTraceElement[] elts = Thread.currentThread().getStackTrace();

        if (elts == null || elts.length == 1) {
            return "";
        }

        int start, size;

        if (cursor >= 0) {
            start = cursor + 2;
            size = elts.length;
        } else {
            start = 2;
            size = start - cursor + 1;
        }

        return getStackTraceString(elts, start, size);
    }

    /**
     * Getting [start, start + size -1] stack trace
     */
    public static String getStackTraceString(StackTraceElement[] se, int start, int size) {
        if (se == null) {
            return "";
        }

        if (size < 0) {
            size = 0;
        }

        size = Math.min(size, se.length);

        if (start >= size) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        final String newLine = System.getProperty("line.separator");

        for (int i = start; i < size; i++) {
            if (i != start) {
                sb.append("\t");
            }

            sb.append(se[i].toString());
            if (i != size - 1) {
                sb.append(newLine);
            }
        }

        return sb.toString();
    }

}
