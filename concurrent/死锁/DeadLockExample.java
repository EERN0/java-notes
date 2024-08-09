/**
 * 产生死锁的原因：线程1获取了资源1，线程2获取了资源2，线程1继续获取资源2而产生阻塞，线程2继续获取资源1而产生阻塞
 * <p>
 * 解决方法：两个线程按顺序获取资源，线程1和线程2都先获取资源1再获取资源2，
 * 无论哪个线程先获取到资源1，另一个线程会因为无法获取资源1而阻塞，等先获取资源1的线程释放资源1后，另外一个线程才能获取资源1，这样了两个线程可以轮流获取到资源和资源2
 */
public class DeadLockExample {
    // 定义两个锁对象
    private static final Object resource1 = new Object();
    private static final Object resource2 = new Object();

    public static void main(String[] args) {

        // 创建两个线程，每个线程持有一个自愿并尝试获取另一个资源
        new Thread(() -> {
            synchronized (resource1) {
                System.out.println(Thread.currentThread().getName() + " holds resource 1.");
                try {
                    Thread.sleep(100); // 让线程1稍作等待，以确保线程2先拿到resource2
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " wait to acquire resource2...");

                synchronized (resource2) {
                    System.out.println("Thread 1 acquires resource 2.");
                }
            }
        }, "线程1").start();

        new Thread(() -> {
            synchronized (resource2) {
                System.out.println(Thread.currentThread().getName() + " holds resource 2.");
                try {
                    Thread.sleep(100); // 让线程2稍作等待，以确保线程1可以先拿到resource1
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " wait to acquire resource1...");

                synchronized (resource1) {
                    System.out.println("Thread 2 acquires resource 1.");
                }
            }
        }, "线程2").start();
    }
}