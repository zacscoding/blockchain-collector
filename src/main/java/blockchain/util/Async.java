package blockchain.util;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * modify : executorService()
 * origin source code : https://github.com/web3j/web3j
 */
public class Async {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public Async() {
    }

    public static <T> CompletableFuture<T> run(Callable<T> callable) {
        CompletableFuture<T> result = new CompletableFuture();
        CompletableFuture.runAsync(() -> {
            try {
                result.complete(callable.call());
            } catch (Throwable var3) {
                result.completeExceptionally(var3);
            }

        }, executor);
        return result;
    }

    private static int getCpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static ScheduledExecutorService defaultExecutorService() {
        return executorService(Executors.newScheduledThreadPool(getCpuCount()));
    }

    public static ScheduledExecutorService executorService(ScheduledExecutorService scheduledExecutorService) {
        Objects.requireNonNull(scheduledExecutorService, "scheduledExecutorService must be not null");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdown(scheduledExecutorService);
        }));
        return scheduledExecutorService;
    }

    private static void shutdown(ExecutorService executorService) {
        executorService.shutdown();

        try {
            if (!executorService.awaitTermination(60L, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60L, TimeUnit.SECONDS)) {
                    System.err.println("Thread pool did not terminate");
                }
            }
        } catch (InterruptedException var2) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdown(executor);
        }));
    }
}