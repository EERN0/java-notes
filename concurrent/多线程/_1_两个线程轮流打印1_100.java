/**
 * 两个线程轮流打印1~100
 */
public class _1_两个线程轮流打印1_100 {
    private int cnt = 1;
    private final Object lock = new Object();

    /**
     * 根据isOdd标志位决定打印奇数或偶数
     * isOdd为true且cnt为奇数时，打印奇数；isOdd为false且cnt为偶数时，打印偶数
     *
     * @param isOdd true：打印奇数，false：打印偶数
     */
    public void printNum(boolean isOdd) {
        while (cnt <= 100) {
            synchronized (lock) {
                // 不满足打印条件，wait等待并释放锁
                while ((isOdd && cnt % 2 == 0) || (!isOdd && cnt % 2 == 1)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                // 满足条件，打印后唤醒阻塞的线程
                if (cnt <= 100) {
                    System.out.println(Thread.currentThread().getName() + "[" + (isOdd ? "odd" : "even") + "]: " + cnt);
                    cnt++;
                    lock.notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) {
        _1_两个线程轮流打印1_100 ap = new _1_两个线程轮流打印1_100();

        // 打印奇数的线程
        new Thread(() -> ap.printNum(true), "t1").start();
        // 打印偶数的线程
        new Thread(() -> ap.printNum(false), "t2").start();
    }
}
