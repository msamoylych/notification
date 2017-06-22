package org.java.notification.receiver.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by msamoylych on 24.05.2017.
 */
@XmlRootElement(name = "result")
public class ResultModel {

    @XmlElement
    public String error;

    @XmlElement
    public List<Result> results;

    public ResultModel() {
    }

    public ResultModel(String error) {
        this.error = error;
    }

    public static class Result {

        @XmlElement
        public String id;

        @XmlElement
        public String error;

        @XmlElement
        public Long msgId;
    }
}
