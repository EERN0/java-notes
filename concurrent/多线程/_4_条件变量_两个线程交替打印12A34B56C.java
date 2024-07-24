import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程交叉打印12A34B56C...5152Z
 */
public class _4_条件变量_两个线程交替打印12A34B56C {

    private final Lock lock = new ReentrantLock();
    private final Condition numberCondition = lock.newCondition();
    private final Condition letterCondition = lock.newCondition();

    // numberTurn控制打印数字还是字母，true打印数字，false打印字母
    boolean numberTurn = true;

    int num = 1;
    char letter = 'A';

    public void printNum() {
        // 打印1~52，共52个数字
        while (num <= 52) {
            lock.lock();
            try {
                // 当前要打印字母，等待并放锁
                while (!numberTurn) {
                    numberCondition.await();
                }

                System.out.print(num++);
                System.out.print(num++);

                // 唤醒打印字母的线程
                numberTurn = false;
                letterCondition.signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    public void printLetter() {
        // 打印A~Z
        while (letter <= 'Z') {
            lock.lock();
            try {
                // 当前要打印的是字母，等待并放锁
                while (numberTurn) {
                    letterCondition.await();

                }

                System.out.print(letter);
                letter++;
                // 唤醒打印数字的线程
                numberTurn = true;
                numberCondition.signal();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        _4_条件变量_两个线程交替打印12A34B56C ap = new _4_条件变量_两个线程交替打印12A34B56C();
        new Thread(ap::printNum).start();
        new Thread(ap::printLetter).start();
    }
}
