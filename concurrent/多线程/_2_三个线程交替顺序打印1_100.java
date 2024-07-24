/**
 * 三个线程交替打印1~100
 */
public class _2_三个线程交替顺序打印1_100 {
    private int cnt = 1;
    private final Object lock = new Object();

    // 控制当前的cnt由哪个线程打印，0: t0打印 , 1: t1打印 , 2: t2打印
    private int turn = 0;

    public void myPrinter(int threadId) {
        while (cnt <= 100) {
            synchronized (lock) {
                // 没轮到threadId打印当前的cnt
                while (turn % 3 != threadId) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (turn % 3 == threadId && cnt <= 100) {
                    System.out.println(Thread.currentThread().getName() + ": " + cnt);
                    cnt++;
                    turn = (turn + 1) % 3;
                    lock.notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) {
        _2_三个线程交替顺序打印1_100 ap = new _2_三个线程交替顺序打印1_100();

        // 打印3n的线程
        new Thread(() -> ap.myPrinter(0), "t0").start();
        // 打印3n+1的线程
        new Thread(() -> ap.myPrinter(1), "t1").start();
        // 打印3n+2的线程
        new Thread(() -> ap.myPrinter(2), "t2").start();
    }
}