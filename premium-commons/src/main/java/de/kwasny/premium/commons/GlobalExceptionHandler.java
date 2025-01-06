package de.kwasny.premium.commons;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ProblemDetail.forStatus;

/**
 * Basic error handling for rest APIs.
 *
 * @author Arthur Kwasny
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(produces = APPLICATION_PROBLEM_JSON_VALUE)
    public ResponseEntity<ProblemDetail> handleInvalidServiceDataException(InvalidServiceDataException ex, WebRequest request) throws Exception {
        LOG.info("handleInvalidServiceDataException: {}", ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setDetail(ex.getMessage());
        return ResponseEntity.of(detail).build();
    }

    @ExceptionHandler(produces = APPLICATION_PROBLEM_JSON_VALUE)
    public ResponseEntity<ProblemDetail> handleServiceException(ServiceException ex, WebRequest request) throws Exception {
        LOG.info("handleServiceException: {}", ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setDetail(ex.getMessage());
        return ResponseEntity.of(detail).build();
    }

    @ExceptionHandler(exception = {
        IOException.class,
    }, produces = APPLICATION_PROBLEM_JSON_VALUE)
    public ResponseEntity<ProblemDetail> handleIOException(Exception ex, WebRequest request) throws Exception {
        LOG.info("handleIOException: {}", ex.getMessage());
        ProblemDetail detail = forStatus(HttpStatus.BAD_GATEWAY);
        detail.setTitle(HttpStatus.BAD_GATEWAY.getReasonPhrase());
        detail.setDetail("Failed to communicate with an external service.");
        return ResponseEntity.of(detail).build();
    }

    @ExceptionHandler(produces = APPLICATION_PROBLEM_JSON_VALUE)
    public ResponseEntity<ProblemDetail> handleRestClientResponseException(RestClientResponseException ex, WebRequest request) throws Exception {
        LOG.info("handleRestClientResponseException: {}", ex.getMessage());
        ProblemDetail detail = objectMapper.readValue(ex.getResponseBodyAsString(), ProblemDetail.class);
        detail.setInstance(null);
        return ResponseEntity.of(detail).build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        LOG.info("handleMethodArgumentNotValid: {}", ex.getMessage());
        ProblemDetail detail = ex.getBody();

        // field errors
        Map<String, String> fieldErrors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));
        if (!fieldErrors.isEmpty()) {
            detail.setProperty("field_errors", fieldErrors);
        }

        // other errors
        List<String> errors = ex.getAllErrors().stream()
                .filter(err -> !(err instanceof FieldError))
                .map(ObjectError::getDefaultMessage)
                .toList();
        if (!errors.isEmpty()) {
            detail.setProperty("errors", errors);
        }

        return ResponseEntity.of(detail)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE)
                .build();
    }

}
