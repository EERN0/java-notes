package 创建线程池;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class _2_ThreadPoolExecutor创建线程池 {
    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                3,
                6,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        //pool.submit(new MyRunnable());
        pool.submit(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread() + ": " + i);
            }
        });

        // 关闭线程池
        pool.shutdown();
    }
}
