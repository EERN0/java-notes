import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "定时任务")
public class Scheduled {

    public static void main(String[] args) {
        ScheduledExecutorService schedule = new ScheduledThreadPoolExecutor(1);
        ScheduledExecutorService schedule2 = Executors.newScheduledThreadPool(1);

        log.debug("start...");
        //schedule.scheduleAtFixedRate(() -> {
        //    log.debug("running...");
        //    try {
        //        Thread.sleep(2);
        //    } catch (InterruptedException e) {
        //        throw new RuntimeException(e);
        //    }
        //}, 1, 1, TimeUnit.SECONDS);


        schedule2.scheduleWithFixedDelay(() -> {
            log.debug("running...");
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, 2, 1, TimeUnit.SECONDS);
    }
}
