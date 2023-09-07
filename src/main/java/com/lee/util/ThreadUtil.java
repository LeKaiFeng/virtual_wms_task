package com.lee.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtil {
    private static final Logger log = LoggerFactory.getLogger(ThreadUtil.class);

    private static ThreadFactory threadFactory = new SimpleThreadFactory();

    public static final int SLEEP_SHORT = 1000;
    public static final int SLEEP_MIDDLE = 20 * 1000;
    public static final int SLEEP_LONG = 3 * 60 * 1000;

    private static final int CORE_POOL_SIZE = 64;
    private static final int MAX_POOL_SIZE = 128;

    private static final ExecutorService POOL = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE
            , 60, TimeUnit.SECONDS
            , new LinkedBlockingQueue<>(1024)
            , new PrefixThreadFactory("common-thread-pool-"));

    public static void execute(Runnable task) {
        POOL.execute(task);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return POOL.submit(callable);
    }

    public static void sleepShort() {
        sleep(SLEEP_SHORT);
    }

    public static void sleepMiddle() {
        sleep(SLEEP_MIDDLE);
    }

    public static void sleepLong() {
        sleep(SLEEP_LONG);
    }

    public static Thread createThread(String name, Runnable runnable) {
        Thread thread = threadFactory.newThread(runnable);
        thread.setName(name);
        return thread;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep((long) (millis * 1));
        } catch (InterruptedException e) {
            log.error("sleep exception: {}", e.getMessage(), e);
        }
    }

    public static class SimpleThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    }

    public static class PrefixThreadFactory implements ThreadFactory {

        private final String prefix;
        private final AtomicInteger atomic = new AtomicInteger(0);

        public PrefixThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return ThreadUtil.createThread(prefix + atomic.getAndIncrement(), r);
        }
    }

}
