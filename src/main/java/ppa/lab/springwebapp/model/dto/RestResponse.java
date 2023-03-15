package ppa.lab.springwebapp.model.dto;

import java.time.LocalDateTime;

public class RestResponse<T> implements AbstractRestReponse {
    private LocalDateTime timestamp;
    private String message;
    private String location;
    private T data;

    public RestResponse(LocalDateTime timestamp, String message, String location) {
        this.timestamp = timestamp;
        this.message = message;
        this.location = location;
    }

    public RestResponse(LocalDateTime timestamp, String message, String location, T data) {
        this.timestamp = timestamp;
        this.message = message;
        this.location = location;
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
