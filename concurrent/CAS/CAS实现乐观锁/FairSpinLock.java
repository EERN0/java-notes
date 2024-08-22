package CAS实现乐观锁;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于cas实现一个可以「公平」调度的乐观锁
 * <p>
 * 「公平」通过队列保证
 */

public class FairSpinLock {
    // 定义一个Node类，用于表示等待队列中的每一个节点（线程）
    private static class Node {
        volatile Thread thread;
        volatile Node next;

        Node(Thread thread) {
            this.thread = thread;
        }
    }

    // 队列头尾指针
    private final AtomicReference<Node> tail = new AtomicReference<>(null);
    private final ThreadLocal<Node> currentNode = ThreadLocal.withInitial(() -> null);
    private final AtomicReference<Node> head = new AtomicReference<>(null);

    public void lock() {
        // 创建当前线程的节点
        Node node = new Node(Thread.currentThread());
        currentNode.set(node);

        // 获取前驱节点，将当前节点从队尾入队
        Node predecessor = tail.getAndSet(node);
        if (predecessor != null) {
            predecessor.next = node;

            // 当前线程node不是头节点，获取锁失败，自旋等待，直到前驱节点释放锁
            while (head.get() != node) {
                // 自旋等待
            }
        } else {
            head.set(node); // 当前线程node是第一个线程，直接获得锁
        }
    }

    public void unlock() {
        Node node = currentNode.get();

        if (node.next == null) {
            // 尝试将尾指针重置为null，如果成功说明没有线程在等待
            if (tail.compareAndSet(node, null)) {
                head.set(null);
                return;
            }

            // 如果失败，说明有其他线程已加入队列，等待next被更新
            while (node.next == null) {
                // 自旋等待
            }
        }

        // 唤醒下一个等待线程
        head.set(node.next);
        node.next = null;  // 清理引用
    }

    public static void main(String[] args) {
        FairSpinLock lock = new FairSpinLock();

        // 启动多个线程来测试锁
        Runnable task = () -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " acquired the lock");
                Thread.sleep(100);  // 模拟临界区操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + " released the lock");
            }
        };

        for (int i = 0; i < 5; i++) {
            new Thread(task, "Thread-" + i).start();
        }
    }
}
