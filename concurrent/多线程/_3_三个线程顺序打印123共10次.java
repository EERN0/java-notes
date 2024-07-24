/**
 * 线程A、B、C分别打印1、2、3，共打印10次
 */
public class _3_三个线程顺序打印123共10次 {

    // turn控制当前哪个线程打印，0-线程A，1-线程B，2-线程C
    // 线程A打印1，线程B打印2，线程C打印3，即打印turn+1
    int turn = 0;

    // 打印次数，共30次
    int cnt = 1;

    private final Object lock = new Object();

    public void myPrint(int threadId) {
        for (int i = 0; i < 10; i++) {
            synchronized (lock) {
                // 不是当前线程打印轮次，等待并放锁
                while (turn != threadId) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (cnt <= 30) {
                    System.out.println(Thread.currentThread().getName() + ": " + (turn + 1));
                    cnt++;
                    turn = (turn + 1) % 3;
                    lock.notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) {
        _3_三个线程顺序打印123共10次 ap = new _3_三个线程顺序打印123共10次();

        new Thread(() -> ap.myPrint(0), "A").start();
        new Thread(() -> ap.myPrint(1), "B").start();
        new Thread(() -> ap.myPrint(2), "C").start();
    }
}
