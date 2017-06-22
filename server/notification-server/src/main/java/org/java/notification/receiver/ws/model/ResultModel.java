package org.java.notification.receiver.ws.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by msamoylych on 24.05.2017.
 */
@XmlRootElement(name = "result")
public class ResultModel {

    @XmlElement
    public String error;

    public ResultModel() {
    }

    public ResultModel(String error) {
        this.error = error;
    }
}
