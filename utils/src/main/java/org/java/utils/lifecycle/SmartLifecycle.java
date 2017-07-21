package org.java.utils.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by msamoylych on 31.03.2017.
 */
public abstract class SmartLifecycle implements org.springframework.context.SmartLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmartLifecycle.class);

    private final String name = this.getClass().getSimpleName();
    private final Object lifecycleMonitor = new Object();
    private boolean running = false;

    @Override
    public void start() {
        synchronized (lifecycleMonitor) {
            if (!running) {
                try {
                    LOGGER.info("{} starting...", name);
                    doStart();
                    running = true;
                    LOGGER.info("{} started", name);
                } catch (Throwable th) {
                    throw new LifecycleException("Start " + name + " failed", th);
                }
            }
        }
    }

    protected abstract void doStart() throws Exception;

    @Override
    public void stop(Runnable callback) {
        synchronized (lifecycleMonitor) {
            stop();
            callback.run();
        }
    }

    @Override
    public void stop() {
        synchronized (lifecycleMonitor) {
            if (running) {
                try {
                    LOGGER.info("{} stopping...", name);
                    doStop();
                    running = false;
                    LOGGER.info("{} stopped", name);
                } catch (Throwable th) {
                    throw new LifecycleException("Stop " + name + " failed", th);
                }
            }
        }
    }

    protected abstract void doStop() throws Exception;

    @Override
    public boolean isRunning() {
        synchronized (lifecycleMonitor) {
            return running;
        }
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }
}