import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * 使用滑动窗口，统计一小时内访问的IP
 */
public class SlideWindowCnt {
    private Deque<AccessRecord> win;   // 队列作为滑动窗口
    private final Duration expireTime;  // 超时时间

    // 访问记录
    static class AccessRecord {
        String ip;
        LocalDateTime timestamp;

        public AccessRecord(String ip, LocalDateTime time) {
            this.ip = ip;
            this.timestamp = time;
        }
    }

    public SlideWindowCnt() {
        win = new LinkedList<>();
        //expireTime = Duration.ofHours(1); // 超时时间 1 小时
        expireTime = Duration.ofSeconds(3);
    }

    // 添加访问记录
    public void add(String ip) {
        LocalDateTime currentTime = LocalDateTime.now();
        win.add(new AccessRecord(ip, currentTime));
        clean(currentTime); // 移除队头超过一小时的IP
    }

    // 清除超过 1 小时的过期记录
    public void clean(LocalDateTime currentTime) {
        while (!win.isEmpty() && Duration.between(win.peekFirst().timestamp, currentTime).compareTo(expireTime) > 0) {
            win.pollFirst();
        }
    }

    // 统计最近 1 小时内访问的唯一 IP 数量
    public int countIps() {
        Set<String> IpSet = new HashSet<>();
        for (AccessRecord record : win) {
            IpSet.add(record.ip);
        }
        return IpSet.size();
    }

    public static void main(String[] args) throws InterruptedException {
        SlideWindowCnt slideWindowCnt = new SlideWindowCnt();
        slideWindowCnt.add("192.168.0.1");
        slideWindowCnt.add("192.168.0.2");
        slideWindowCnt.add("192.168.0.3");

        System.out.println("当前IP数：" + slideWindowCnt.countIps());

        Thread.sleep(4000); // 4s过后

        slideWindowCnt.add("192.168.0.4");
        System.out.println("当前IP数：" + slideWindowCnt.countIps());
    }
}
