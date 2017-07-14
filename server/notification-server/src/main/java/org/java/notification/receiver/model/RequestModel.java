package org.java.notification.receiver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by msamoylych on 12.07.2017.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"system", "pushes"})
@XmlRootElement(name = "request")
public class RequestModel {

    public String system;

    @XmlElement(name = "push")
    @JsonProperty("pushes")
    public List<PushModel> pushes;
}
