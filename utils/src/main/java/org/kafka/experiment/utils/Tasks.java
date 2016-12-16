package org.kafka.experiment.utils;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Helper class for tasks management.
 */
public class Tasks {

    private final static Logger LOG = Logger.getLogger(Tasks.class);

    /**
     * Wait until all producers finish producing of messages
     *
     * @param tasks
     */
    public static void waitForAllFutures(List<Future<?>> tasks) {
        for (Future<?> future : tasks) {
            try {
                future.get();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
