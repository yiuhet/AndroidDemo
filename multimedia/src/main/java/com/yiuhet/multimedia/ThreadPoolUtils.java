package com.yiuhet.multimedia;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yiuhet on 2018/8/14.
 * <p>
 * 线程池工具类
 */

public class ThreadPoolUtils {
    private ExecutorService cachePool = Executors.newCachedThreadPool();
    private ExecutorService singlePool = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();


    private static final class SingletonHolder {
        private static final ThreadPoolUtils INSTANCE = new ThreadPoolUtils();
    }

    private ThreadPoolUtils() {
    }

    public static void executeInSinglePool(Runnable r) {
        if (r != null) {
            SingletonHolder.INSTANCE.singlePool.execute(r);
        }
    }

    public static void executeInCachePool(Runnable r) {
        if (r != null) {
            SingletonHolder.INSTANCE.cachePool.execute(r);
        }
    }

    public static void executeInSingPoolDelay(Runnable r, long delay, TimeUnit unit) {
        if (r != null) {
            SingletonHolder.INSTANCE.singleThreadScheduledExecutor.schedule(r, delay, unit);
        }
    }

}
