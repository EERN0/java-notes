package 创建线程池;

public class _查看电脑最大线程数_ {
    public static void main(String[] args) {
        int count = Runtime.getRuntime().availableProcessors();
        System.out.println(count);      // 16
    }
}
