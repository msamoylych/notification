package org.java.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by msamoylych on 20.04.2017.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final String BASE_PACKAGE = "org.java";

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        try {
            context = new AnnotationConfigApplicationContext(BASE_PACKAGE);
            Runtime.getRuntime().addShutdownHook(new ShutdownHook());
            LOGGER.info("Application started");
        } catch (Throwable th) {
            LOGGER.error("Start application error", th);
            System.exit(1);
        }
    }

    private static class ShutdownHook extends Thread {

        private ShutdownHook() {
            super("shutdown");
        }

        @Override
        public void run() {
            LOGGER.info("Stop application");
            context.close();
        }
    }
}
