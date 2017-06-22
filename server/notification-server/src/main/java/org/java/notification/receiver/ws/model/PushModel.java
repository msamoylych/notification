package org.java.notification.receiver.ws.model;

import org.java.notification.push.OS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by msamoylych on 24.05.2017.
 */
@XmlRootElement(name = "push")
public class PushModel {

    @XmlElement
    private OS os;

    @XmlElement(name = "package")
    private String packageName;

    @XmlElement
    private String token;

    @XmlElement
    private String title;

    @XmlElement
    private String body;

    public OS os() {
        return os;
    }

    public void os(OS os) {
        this.os = os;
    }

    public String packageName() {
        return packageName;
    }

    public void packageName(String packageName) {
        this.packageName = packageName;
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
}
