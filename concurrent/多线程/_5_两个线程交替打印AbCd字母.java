/**
 * 两个线程交替打印AbCd...z字母，一个大写一个小写
 */
public class _5_两个线程交替打印AbCd字母 {

    private final Object lock = new Object();

    char letter = 'A';

    // 控制当前该打印的是大写还是小写，true-大写，false-小写
    private boolean printUpper = true;

    // isUpperThread表示打印线程的标识，true-大写线程打印，false-小写线程打印
    public void myPrint(boolean isUpperThread) {
        while (letter <= 'Z') {
            synchronized (lock) {
                // 轮到大写线程打印，小写线程先等待
                while (isUpperThread != printUpper) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (letter <= 'Z') {
                    if (printUpper) {
                        // 打印大写字母，下次打印小写字母
                        System.out.print(letter);
                        printUpper = false;
                    } else {
                        // 打印小写字母，下次打印大写字母
                        System.out.print(Character.toLowerCase(letter));
                        printUpper = true;
                    }
                    letter++;
                    lock.notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) {
        _5_两个线程交替打印AbCd字母 ap = new _5_两个线程交替打印AbCd字母();
        new Thread(() -> ap.myPrint(true)).start();
        new Thread(() -> ap.myPrint(false)).start();
    }
}
