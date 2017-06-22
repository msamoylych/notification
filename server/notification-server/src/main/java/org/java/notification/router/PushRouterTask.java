package org.java.notification.router;

import org.java.notification.push.Push;

/**
 * Created by msamoylych on 22.06.2017.
 */
public class PushRouterTask implements Runnable {

    private final Push<?> push;

    PushRouterTask(Push<?> push) {
        this.push = push;
    }

    @Override
    public void run() {

    }
}
