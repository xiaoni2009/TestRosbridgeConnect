package cn.muye.base.service.batch;

import cn.muye.base.service.ScheduledHandleService;
import cn.muye.base.service.imp.ScheduledHandleServiceImp;
import org.apache.log4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledHandle {
    private static Logger logger = Logger.getLogger(ScheduledHandle.class);

    private final ScheduledExecutorService scheduledExecutor;

    public ScheduledHandle(ScheduledExecutorService scheduledExecutor, String queueName) {
        this.scheduledExecutor = scheduledExecutor;
        this.rosHealthCheckScheduled();
        this.testPing();
    }

    /**
     * ros重连
     */
    public void rosHealthCheckScheduled() {
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("schedule rosHealthCheckScheduled started");
                    ScheduledHandleService service = new ScheduledHandleServiceImp();
                    service.rosHealthCheck();
                    logger.info("schedule rosHealthCheckScheduled");
                } catch (Exception e) {
                    logger.error("schedule rosHealthCheckScheduled exception", e);
                }
            }
        }, 25, 5, TimeUnit.SECONDS);
    }


    /**
     * testPing
     */
    public void testPing() {
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("schedule testPing start");
                    new ScheduledHandleServiceImp().testPing();
                } catch (Exception e) {
                    logger.error("schedule publishRosScheduled exception", e);
                }
            }
        }, 34, 1, TimeUnit.SECONDS);
    }
}
