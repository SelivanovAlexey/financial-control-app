package app.core.errorhandling;

import app.core.errorhandling.exceptions.UserAlreadyExistsException;
import app.core.errorhandling.model.CommonExceptionJson;
import app.core.errorhandling.model.ValidationExceptionJson;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionJson> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));
        log.error("MethodArgumentNotValidException: {}.", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ValidationExceptionJson.builder().msg("Validation failed").errors(errors).build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CommonExceptionJson> badCredentialsExceptionHandler(BadCredentialsException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials", e);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<CommonExceptionJson> insufficientAuthenticationExceptionHandler(InsufficientAuthenticationException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Insufficient authentication", e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonExceptionJson> entityNotFoundExceptionHandler(EntityNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "Element not found", e);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonExceptionJson> dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
        return buildResponse(HttpStatus.CONFLICT, "Data violation on database", e);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<CommonExceptionJson> userAlreadyExistsExceptionHandler(UserAlreadyExistsException e) {
        return buildResponse(HttpStatus.CONFLICT, "User already exists", e);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CommonExceptionJson> usernameNotFoundExceptionHandler(UsernameNotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "User not found", e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonExceptionJson> unhandledExceptionHandler(Exception e) {
        log.error("An unexpected error occurs: {}.", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonExceptionJson.builder().msg("Something went wrong").cause(e.getMessage()).build());
    }

    private ResponseEntity<CommonExceptionJson> buildResponse(HttpStatus status, String msg, Exception e) {
        log.error("{}: {}.", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.status(status)
                .body(CommonExceptionJson.builder().msg(msg).cause(e.getMessage()).build());
    }
}
