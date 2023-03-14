package ppa.lab.springwebapp.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ppa.lab.springwebapp.model.dto.RestResponse;

import java.time.LocalDateTime;

public class HttpResponseUtil {

    public static <T> ResponseEntity<T> buildRestResponse(T response) {
        return ResponseEntity.ok()
                .headers(setContentTypeJson(new HttpHeaders()))
                .body(response);
    }

    public static <T> ResponseEntity<RestResponse<T>> buildRestResponse(T response, String msg, String location, String reason) {
        RestResponse responseBean = new RestResponse(
                LocalDateTime.now()
                , msg
                , location
                , reason
                , response);
        return ResponseEntity.ok()
                .headers(setContentTypeJson(new HttpHeaders()))
                .body(responseBean);
    }

    public static HttpHeaders setContentTypeJson(HttpHeaders responseHeaders) {
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        responseHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        return responseHeaders;
    }

    public static HttpHeaders setCrossDomain(HttpHeaders responseHeaders) {
        ResponseEntity.HeadersBuilder response = ResponseEntity.noContent();
        responseHeaders.set("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        responseHeaders.set("Access-Control-Max-Age", "3600");
        responseHeaders.set("Access-Control-Allow-Headers",
                "x-requested-with, " + "Authorization," + " content-type, " + "If-Modified-Since, " + "Cache-Control, " + "Pragma");
        return responseHeaders;
    }
}
