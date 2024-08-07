import java.util.concurrent.CyclicBarrier;

/**
 * 10个线程同时执行run方法，保证公平性
 * <p>
 * 当一个线程调用 await() 时，如果它不是最后一个线程，它将阻塞自身并等待其他线程到达屏障点。
 * 只有当所有线程都调用了 await() 并且计数器归零时，CyclicBarrier 才会释放这些线程并允许它们继续执行。
 * 如果所有线程没有达到 await() 点，CyclicBarrier 会一直保持阻塞状态，从而实现同步控制。
 */
public class FairRunExample {
    public static void main(String[] args) {
        int threadNum = 10;
        CyclicBarrier cb = new CyclicBarrier(threadNum);

        // 第一批10个线程都到屏障点后，一起执行；第二批也是10个线程
        for (int i = 0; i < threadNum * 2; i++) {
            new Thread(new Task(cb)).start();
        }
    }
}

class Task implements Runnable {
    private CyclicBarrier barrier;

    public Task(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " is ready at " + System.currentTimeMillis());
            // 等待所有线程到达这一点
            barrier.await();

            // 这一块，所有线程打印的时间不一定相同
            System.out.println(Thread.currentThread().getName() + " starts running at " + System.currentTimeMillis());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
