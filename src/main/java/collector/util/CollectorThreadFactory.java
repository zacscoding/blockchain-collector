package collector.util;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zacconding
 * @Date 2018-12-20
 * @GitHub : https://github.com/zacscoding
 */
public class CollectorThreadFactory implements ThreadFactory {

    private final static AtomicInteger THREAD_FACTORY_NUMBER = new AtomicInteger(0);

    private final AtomicInteger threadNumber = new AtomicInteger(0);
    private boolean daemon;
    private String threadName;

    public CollectorThreadFactory() {
        this("Blockchain-Collector", false);
    }

    public CollectorThreadFactory(String threadName) {
        this(threadName, false);
    }

    public CollectorThreadFactory(String threadName, boolean daemon) {
        Objects.requireNonNull(threadName, "Thread name must be not null");
        this.threadName = new StringBuilder(threadName)
            .append("-")
            .append(THREAD_FACTORY_NUMBER.incrementAndGet())
            .toString();
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = generateThreadName();
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setName(threadName);
        t.setDaemon(daemon);
        return t;
    }

    private String generateThreadName() {
        StringBuilder sb = new StringBuilder(threadName.length() + 8);
        return sb.append(threadName).append("-").append(threadNumber.incrementAndGet()).toString();
    }
}
