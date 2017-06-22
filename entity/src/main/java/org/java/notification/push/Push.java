package org.java.notification.push;

import org.java.notification.Entity;
import org.java.notification.Message;
import org.java.notification.push.application.Application;

/**
 * Created by msamoylych on 31.03.2017.
 */
public class Push<A extends Application> extends Entity implements Message {

    private A application;

    private Device device;

    private State state;

    private String messageId;

    private String token;

    private String title;

    private String body;

    private String icon;

    public Push() {
        this.state = State.NEW;
    }

    public A application() {
        return application;
    }

    public void application(A application) {
        this.application = application;
    }

    public Device device() {
        return device;
    }

    public void device(Device device) {
        this.device = device;
    }

    public State state() {
        return state;
    }

    public void state(State state) {
        this.state = state;
    }

    public String messageId() {
        return messageId;
    }

    public void messageId(String messageId) {
        this.messageId = messageId;
    }

    public String token() {
        return token;
    }

    public void token(String token) {
        this.token = token;
    }

    public String title() {
        return title;
    }

    public void title(String title) {
        this.title = title;
    }

    public String body() {
        return body;
    }

    public void body(String body) {
        this.body = body;
    }

    public String icon() {
        return icon;
    }

    public void icon(String icon) {
        this.icon = icon;
    }
}