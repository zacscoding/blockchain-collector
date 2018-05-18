package org.blocksync.factory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.blocksync.entity.Node;
import org.springframework.util.StringUtils;

/**
 * @author zacconding
 * @Date 2018-05-15
 * @GitHub : https://github.com/zacscoding
 */
public class PrintStreamFactory {

    private static Map<String, PrintStream> printStreamMap;
    private static final String DEFAULT_LOG_DIR = "logs/";
    private static final ReentrantLock lock = new ReentrantLock();

    static {
        printStreamMap = new HashMap<>();
    }

    public static PrintStream getPrintStream(String logDir, String id) {
        PrintStream ps = printStreamMap.get(id);
        if (ps == null) {
            try {
                lock.lock();
                if ((ps = printStreamMap.get(id)) == null) {
                    ps = createPrintStream(logDir, id);
                    printStreamMap.put(id, ps);
                }
            } finally {
                lock.unlock();
            }
        }

        return ps;
    }

    public static PrintStream getPrintStream(String logDir, String prefix, Node node) {
        return getPrintStream(logDir, getLogName(prefix, node));
    }

    private static PrintStream createPrintStream(String logDir, String id) {
        if (!StringUtils.hasText(logDir)) {
            logDir = DEFAULT_LOG_DIR;
        }

        File dirFile = new File(logDir);

        if(!dirFile.exists()) {
            dirFile.mkdirs();
        }

        try {
            // create log file
            File file = new File(logDir, id + ".log");
            return new PrintStream(new FileOutputStream(file), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getLogName(String prefix, Node node) {
        return prefix + node.getName() + "-" + node.getUrl().substring("http://".length()).replace(':', '-');
    }
}
