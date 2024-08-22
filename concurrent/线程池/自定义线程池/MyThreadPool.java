package 自定义线程池;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c.TestPool")
class TestPool {
    public static void main(String[] args) {
        MyThreadPool threadPool = new MyThreadPool(1, 2000, TimeUnit.MILLISECONDS, 1,
                (queue, task) -> {
                    //queue.put(task);                                             // 1.主线程死等
                    //queue.putWithTimeOut(task, 1500, TimeUnit.MILLISECONDS);     // 2.超时等待
                    //log.debug("拒绝策略--放弃执行任务");                            // 3.放弃执行任务
                    //throw new RuntimeException("拒绝策略--放弃执行任务，抛出异常");   // 4.放弃执行任务，并抛出异常
                    task.run();     // 5.让调用者自己执行，在主线程里面直接task.run()，会让主线程来执行任务
                });

        for (int i = 0; i < 3; i++) {
            int j = i;  // 变化的i不能用在lambda或多线程中，除非用原子类（raft统计选票可以改掉）

            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 模拟任务执行
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.debug("任务 {} 执行完毕..., {}", j, this);
                }
            });
        }
    }
}

// 任务拒绝策略
@FunctionalInterface
interface RejectPolicy<T> {
    void reject(BlockingQueue<T> queue, T task);
}

@Slf4j(topic = "c.MyThreadPool")
public class MyThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 线程集合
    private HashSet<Worker> workers = new HashSet<>();

    // 核心线程数
    private int coreSize;

    // 获取任务时的超时时间
    private long timeout;

    private TimeUnit timeUnit;

    // 任务拒绝策略
    private RejectPolicy<Runnable> rejectPolicy;

    public MyThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity, RejectPolicy<Runnable> rejectPolicy) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
        this.rejectPolicy = rejectPolicy;
    }

    // 线程池执行任务
    // 1.工作线程数小于核心线程数，创建线程执行任务；2.工作线程超过核心线程数，新任务加入队列，等待现有worker取出任务执行；
    // 3.任务队列满了，如果工作线程少于最大线程数，再创建新线程，如果工作线程等于最大线程数，执行任务拒绝策略
    public void execute(Runnable task) {
        synchronized (workers) {
            if (workers.size() < coreSize) {
                Worker worker = new Worker(task);
                log.debug("新增 worker-{}, task-{}", worker, task);
                workers.add(worker);
                worker.start();
            } else {
                // task加入任务队列
                //taskQueue.put(task);

                // 若任务队列满了，新任务执行任务拒绝策略: 死等、带超时时间的等待、让调用者放弃任务、让调用者放弃任务并抛出异常、让调用者自己执行任务
                taskQueue.tryPut(rejectPolicy, task);
            }
        }
    }


    // 工作线程worker
    class Worker extends Thread {
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        // 工作线程处理任务
        @Override
        public void run() {
            // 1. task不为空，执行任务，2. task执行完毕，线程接着从任务队列里取出任务执行
            //while (task != null || (task = taskQueue.take()) != null) {                               // 任务队列为空，工作线程死等
            while (task != null || (task = taskQueue.takeWithTimeOut(timeout, timeUnit)) != null) {     // 带超时时间，防止队列为空，worker线程死等
                try {
                    log.debug("正在执行...task-{}", task);
                    // 调用Runnable实现类的run方法，具体任务怎么执行的由任务自行定义
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    task = null;
                }
            }

            // 线程没任务执行了，从线程集合中销毁当前线程
            synchronized (workers) {
                log.debug("worker 被移除{}", this);
                workers.remove(this);
            }
        }
    }
}


/**
 * 阻塞队列
 */
@Slf4j(topic = "c.ThreadPool")
class BlockingQueue<T> {
    // 1.任务队列
    private Deque<T> queue = new ArrayDeque<>();

    // 2.锁
    private final ReentrantLock lock = new ReentrantLock();

    // 3.生产者条件变量（满等）
    private final Condition fullWaitSet = lock.newCondition();

    // 4.消费者条件变量（空等）
    private final Condition emptyWaitSet = lock.newCondition();

    // 5.容量
    private int capcity;

    public BlockingQueue(int capcity) {
        this.capcity = capcity;
    }

    // 阻塞获取
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    // 队列为空，消费者阻塞在条件变量上
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // 队列有元素，弹出队头
            T t = queue.removeFirst();
            // 生产一个空位，唤醒生产者
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 带超时时间的阻塞获取
     * 超时返回null
     */
    public T takeWithTimeOut(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            // 将 timeout 统一转换为纳秒
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    // 已经超时，返回null
                    if (nanos <= 0) {
                        return null;
                    }
                    // awaitNanos方法返回的是剩余等待时间
                    nanos = emptyWaitSet.awaitNanos(timeout);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            T t = queue.removeFirst();
            // 生产一个空位，唤醒生产者生产
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    // 阻塞添加
    public void put(T task) {
        lock.lock();
        try {
            // 队列已满，生产者阻塞在条件变量上，等待消费
            while (queue.size() == capcity) {
                try {
                    log.debug("等待加入任务队列 {} ...", task);
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // 队列有空位，队尾添加元素
            queue.addLast(task);
            log.debug("加入任务队列 {}", task);

            // 添加一个元素，唤醒消费者
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    // 带超时时间阻塞添加
    public boolean putWithTimeOut(T task, long timeout, TimeUnit timeUnit) {
        lock.lock();
        try {
            long nanos = timeUnit.toNanos(timeout);
            while (queue.size() == capcity) {
                try {
                    if (nanos <= 0) {
                        return false;
                    }
                    log.debug("等待加入任务队列 {} ...", task);
                    nanos = fullWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("加入任务队列 {}", task);
            queue.addLast(task);
            emptyWaitSet.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void tryPut(RejectPolicy<T> rejectPolicy, T task) {
        lock.lock();
        try {
            // 任务数 小于 队列容量，有空闲
            if (queue.size() < capcity) {
                queue.addLast(task);
                log.debug("加入任务队列 {}", task);
                // 添加一个元素，唤醒消费者
                emptyWaitSet.signal();
            } else {
                // 队列满，执行拒绝策略
                rejectPolicy.reject(this, task);
            }
        } finally {
            lock.unlock();
        }
    }

    // 任务数量
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
