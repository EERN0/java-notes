import sun.misc.Unsafe;

public class UnsafeDemo01 {
    public static void main(String[] args) {
        Unsafe unsafe = myUnsafe.reflectGetUnsafe();

        MyRunnable mr = new MyRunnable();
        new Thread(mr, "线程1").start();

        while (true) {
            boolean flag = mr.isFlag();
            // 加入读内存屏障: 会禁止读操作重排序，保证在这个屏障之前的所有读操作都已经完成，并且将缓存数据设为无效，重新从主存中进行加载
            unsafe.loadFence();

            if (flag) {
                System.out.println("主线程发现flag变更: " + flag);
                break;
            }
        }
        System.out.println("主线程结束");
    }
}

class MyRunnable implements Runnable {
    /*volatile*/ boolean flag = false;

    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "的flag: " + flag);

        // 添加了内存屏障，禁用缓存后，子线程将工作内存中修改后的flag通过主内存同步给了主线程，进而修改主线程中的工作内存
        flag = true;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}