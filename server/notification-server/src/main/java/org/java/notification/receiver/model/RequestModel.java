package org.java.notification.receiver.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by msamoylych on 12.07.2017.
 */
@XmlRootElement(name = "request")
public class RequestModel {

    @XmlElement(name = "system")
    public String systemCode;

    public List<PushModel> pushes;
}
