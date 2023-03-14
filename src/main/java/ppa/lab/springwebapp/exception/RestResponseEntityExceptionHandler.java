package ppa.lab.springwebapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ppa.lab.springwebapp.model.dto.RestErrorResponse;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestResponseEntityExceptionHandler<T> extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { RestException.class })
    protected ResponseEntity<RestErrorResponse> handleHttpException (RestException ex, WebRequest request) {
        RestErrorResponse exceptionBean = buildErrorResponse(ex, request);
        return new ResponseEntity<>(exceptionBean, ex.getHttpStatus());
    }

    private RestErrorResponse buildErrorResponse(RestException ex, WebRequest request) {
        ServletWebRequest servletWebRequest = null;
        if (request instanceof ServletWebRequest) {
            servletWebRequest = (ServletWebRequest) request;
           return new RestErrorResponse(
                    LocalDateTime.now(),
                    Integer.toString(ex.getHttpStatus().value()),
                    ex.getMessage(),
                    ex.getHttpStatus().getReasonPhrase(),
                    servletWebRequest.getRequest().getRequestURI()
            );
        }
        else {
            return new RestErrorResponse(
                    LocalDateTime.now(),
                    HttpStatus.NOT_ACCEPTABLE.name(),
                    ex.getMessage(),
                    ex.getHttpStatus().getReasonPhrase(),
                    servletWebRequest.getContextPath());
        }
    }
}