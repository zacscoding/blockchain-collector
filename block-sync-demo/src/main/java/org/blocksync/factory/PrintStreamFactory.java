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

    public static PrintStream getPrintStream(String logDir, String prefix, Node node) {
        String id = prefix + node.getName();
        PrintStream ps = printStreamMap.get(id);
        if (ps == null) {
            try {
                lock.lock();
                if ((ps = printStreamMap.get(prefix)) == null) {
                    ps = createPrintStream(logDir, prefix, node);
                    printStreamMap.put(id, ps);
                }
            } finally {
                lock.unlock();
            }
        }

        return ps;
    }

    private static PrintStream createPrintStream(String logDir, String prefix, Node node) {
        if (!StringUtils.hasText(logDir)) {
            logDir = DEFAULT_LOG_DIR;
        }

        try {
            // create log file
            String nodeName = prefix + node.getName() + "-" + node.getUrl().substring("http://".length()).replace(':', '-');
            File file = new File(logDir, nodeName + ".log");
            return new PrintStream(new FileOutputStream(file), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
