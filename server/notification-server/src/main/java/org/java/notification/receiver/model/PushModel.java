package org.java.notification.receiver.model;

import org.java.notification.push.OS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by msamoylych on 24.05.2017.
 */
@XmlRootElement(name = "push")
public class PushModel {

    @XmlElement(required = true)
    public String id;

    @XmlElement
    public OS os;

    @XmlElement(name = "package")
    public String packageName;

    @XmlElement
    public String token;

    @XmlElement
    public String title;

    @XmlElement
    public String body;

    @XmlElement
    public String icon;
}
