package org.java.notification.receiver.model;

import org.java.notification.push.PNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by msamoylych on 24.05.2017.
 */
@XmlRootElement(name = "push")
public class PushModel {

    @XmlElement(required = true)
    public String id;

    public PNS pns;

    @XmlElement(name = "package")
    public String packageName;

    public String token;

    public String title;

    public String body;

    public String icon;

    @Override
    public String toString() {
        return "PushModel [id:" + id + ']';
    }
}
