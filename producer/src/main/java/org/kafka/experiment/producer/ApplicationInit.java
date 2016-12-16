package org.kafka.experiment.producer;

import org.kafka.experiment.producer.stats.TimeStats;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class ApplicationInit {

    private static final Logger LOG = Logger.getLogger(ApplicationInit.class);

    @Autowired
    @Qualifier("timeTrackingScheduler")
    private ScheduledExecutorService timeTrackingScheduler;

    @Autowired
    @Qualifier("timeStats")
    private TimeStats timeStats;

    @Autowired
    private Environment environment;

    @PostConstruct
    private void timeTrackingSchedulerInit() {
        LOG.info("Start time tracking scheduler...");

        int bestTimeIntervalSecs = environment.getProperty(
                "app.time.tracking.scheduler.best.time.interval", Integer.class);

        timeTrackingScheduler.scheduleAtFixedRate(
                (Runnable) () -> timeStats.showStatsPerSeconds(),
                1, 1, TimeUnit.SECONDS); // every 1 second
        timeTrackingScheduler.scheduleAtFixedRate(
                (Runnable) () -> timeStats.showBestStats(),
                bestTimeIntervalSecs, bestTimeIntervalSecs, TimeUnit.SECONDS);

        LOG.info("Start time tracking scheduler...OK");
    }
}
