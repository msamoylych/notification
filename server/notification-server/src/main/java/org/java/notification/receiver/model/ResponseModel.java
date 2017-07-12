package org.java.notification.receiver.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Created by msamoylych on 24.05.2017.
 */
@XmlRootElement(name = "result")
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

    public static class Result {

        public String id;

        public String error;

        public Long msgId;

        public Result() {
        }

        public Result(String id) {
            this.id = id;
        }
    }
}
