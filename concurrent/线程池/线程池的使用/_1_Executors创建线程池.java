package 线程池的使用;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * public static ExecutorService newCachedThreadPool(): 创建一个没有上限的线程池
 * public static ExecutorService newFixedThreadPool(int nThreads): 创建有上限的线程池
 */
public class _1_Executors创建线程池 {
    public static void main(String[] args) throws InterruptedException {
        // 1.创建线程池实例
        ExecutorService pool1 = Executors.newCachedThreadPool();        // 线程数量没有上限
        ExecutorService pool2 = Executors.newFixedThreadPool(3);// 线程池中最大线程数为3

        // 2.把任务提交给线程池
        pool1.submit(new MyRunnable());
        pool1.submit(new MyRunnable());
        pool1.submit(new MyRunnable());
        pool1.submit(new MyRunnable());

        Thread.sleep(1000);
        System.out.println("主线程id: " + Thread.currentThread().getId());

        for (int i = 0; i < 10; i++) {
            pool2.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("线程池2, 正在执行的线程id是: " + Thread.currentThread().getId());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
        }

        // 3.关闭线程池
        pool1.shutdown();
        pool2.shutdown();
    }
}
