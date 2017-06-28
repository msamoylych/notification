package org.java.notification.push;

import org.java.notification.Entity;
import org.java.notification.Message;
import org.java.notification.push.application.Application;
import org.java.notification.user.System;

/**
 * Created by msamoylych on 31.03.2017.
 */
public class Push<A extends Application> extends Entity implements Message {

    private System system;
    private String extId;
    private A application;
    private Device device;
    private State state;
    private String messageId;
    private String token;
    private String title;
    private String body;
    private String icon;
    private String pnsId;

    public Push() {
        this.state = State.NEW;
    }

    public System system() {
        return system;
    }

    public void system(System system) {
        this.system = system;
    }

    public String extId() {
        return extId;
    }

    public void extId(String extId) {
        this.extId = extId;
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

    public String pnsId() {
        return pnsId;
    }

    public void pnsId(String pnsId) {
        this.pnsId = pnsId;
    }
}