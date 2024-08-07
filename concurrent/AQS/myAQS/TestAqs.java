package myAQS;

public class TestAqs {
    public static void main(String[] args) {
        MyLock myLock = new MyLock();

        for (int i = 1; i <= 5; i++) {
            new Thread(() -> {
                myLock.lock();
                //myLock.lock();    // 不可重入锁，会被阻塞在这
                System.out.println("......");
                try {
                    System.out.println(Thread.currentThread().getName() + "使用资源中...");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {

                    myLock.unlock();
                }
            }, "线程" + i).start();
        }

    }
}