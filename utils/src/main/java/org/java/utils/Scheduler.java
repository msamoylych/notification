package org.java.utils;

import org.java.utils.lifecycle.SmartLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by msamoylych on 05.05.2017.
 */
@Component
public final class Scheduler extends SmartLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private final Queue<SchedulerTask> queue = new LinkedList<>();
    private ScheduledThreadPoolExecutor executor;

    @Override
    protected void doStart() {
        executor = new ScheduledThreadPoolExecutor(1, new SchedulerThreadFactory());
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        SchedulerTask task;
        while ((task = queue.poll()) != null) {
            long initialDelay = task.initialDelay - (System.currentTimeMillis() - task.createMillis);
            scheduleTask(task.task, initialDelay, task.period, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected void doStop() throws Exception {
        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            LOGGER.error("Await termination failed");
        }
    }

    @Override
    public int getPhase() {
        return 0;
    }


    public void scheduleTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        if (executor != null) {
            executor.scheduleAtFixedRate(task, initialDelay, period, unit);
        } else {
            SchedulerTask schedulerTask = new SchedulerTask();
            schedulerTask.task = task;
            schedulerTask.initialDelay = unit.toMillis(initialDelay);
            schedulerTask.period = unit.toMillis(period);
            queue.offer(schedulerTask);
        }
    }

    private static class SchedulerTask {
        long createMillis = System.currentTimeMillis();
        Runnable task;
        long initialDelay;
        long period;
    }

    private static class SchedulerThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "scheduler");
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    }
}
