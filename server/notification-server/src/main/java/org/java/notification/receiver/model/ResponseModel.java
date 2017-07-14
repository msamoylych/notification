package org.java.notification.receiver.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Created by msamoylych on 24.05.2017.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"error", "results"})
@XmlRootElement(name = "response")
public class ResponseModel {

    @JsonInclude(Include.NON_NULL)
    public String error;

    @JsonInclude(Include.NON_NULL)
    public List<Result> results;

    public ResponseModel() {
    }

    public ResponseModel(String error) {
        this.error = error;
    }

    public ResponseModel(List<Result> results) {
        this.results = results;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"id", "error", "msgId"})
    public static class Result {

        @JsonInclude(Include.NON_NULL)
        public String id;

        @JsonInclude(Include.NON_NULL)
        public String error;

        @JsonInclude(Include.NON_NULL)
        public Long msgId;

        public Result() {
        }

        public Result(String id) {
            this.id = id;
        }
    }
}
