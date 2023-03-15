package ppa.lab.springwebapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);
    private static final String DEFAULT_ERROR_MSG = "error from %s : %s";

    @ExceptionHandler(value = { RestException.class })
    protected ResponseEntity<RestErrorResponse> handleHttpException (RestException ex, WebRequest request) {
        RestErrorResponse exceptionBean = buildErrorResponse(ex, request);
        return new ResponseEntity<>(exceptionBean, ex.getHttpStatus());
    }

    private RestErrorResponse buildErrorResponse(RestException ex, WebRequest request) {
        if(LOG.isErrorEnabled()) {
            LOG.error(DEFAULT_ERROR_MSG.formatted(request.getContextPath(), ex.getMessage()), ex);
        }
        if (request instanceof ServletWebRequest servletWebRequest) {
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
                    request.getContextPath());
        }
    }
}