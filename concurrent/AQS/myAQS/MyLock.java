package myAQS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyLock implements Lock {

    // 自定义一个独占锁(不可重入)，继承AQS，重写钩子方法
    class MySync extends AbstractQueuedSynchronizer {

        /**
         * 重写AQS的tryAcquire方法，独占方式，尝试获取资源（只会尝试1次）
         * cas尝试获取锁，获取锁成功，设置owner
         */
        @Override
        protected boolean tryAcquire(int arg) {
            // CAS 尝试修改state 为 1
            if (compareAndSetState(0, 1)) {
                // 加上了锁，并设置owner为当前线程
                setExclusiveOwnerThread(Thread.currentThread());
                System.out.println(Thread.currentThread().getName() + "获取锁成功");
                return true;
            }
            System.out.println(Thread.currentThread().getName() + "获取锁失败");
            return false;
        }

        /**
         * 独占方式，尝试释放资源。
         * owner置为null，state置为0就表示解锁了
         * 当前线程获取了锁，所以不用CAS判断
         */
        @Override
        protected boolean tryRelease(int arg) {
            // 置空锁的持有者
            setExclusiveOwnerThread(null);
            // 状态改为0. state是volatile修饰的，写volatile之后才会加写入屏障，让之前的操作都执行并提交到内存，对其它线程可见。所以写volatile放后面
            setState(0);

            System.out.println(Thread.currentThread().getName() + "[释放锁]成功!!");

            return true;
        }

        /**
         * 是否持有独占锁
         */
        @Override
        protected boolean isHeldExclusively() {
            // state等于1，表示持有锁
            return getState() == 1;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }
    }

    private final MySync sync = new MySync();


    /**
     * 加锁（不成功会进入等待队列）
     */
    @Override
    public void lock() {
        sync.acquire(1);
    }

    /**
     * 加锁，可打断
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    /**
     * 尝试加锁（只尝试一次）
     */
    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    /**
     * 尝试加锁，带超时
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    /**
     * 解锁
     */
    @Override
    public void unlock() {
        // release() 除了会调用 tryRelease() 设置state状态和修改owner为null，还会调用unpark()，唤醒阻塞队列中的线程
        sync.release(1);
    }

    /**
     * 创建条件变量
     */
    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }
}
