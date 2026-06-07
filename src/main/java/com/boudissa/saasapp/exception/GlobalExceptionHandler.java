package com.boudissa.saasapp.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

/**
 * meilleure proposition Java 21 appliquée
 * J’ai remplacé l’approche ProblemDetail par un DTO local basé sur un record.
 * <p>
 * Pourquoi c’est plus compatible Java 21
 * record Java : parfait pour représenter une réponse immuable.
 * Pas de dépendance au type abstrait Spring org.springframework.web.ErrorResponse.
 * Structure JSON claire pour l’API.
 * Code simple et typé.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> handleException(final BusinessException ex, final HttpServletRequest request) {
        final HttpStatus status = getHttpStatus(ex);
        log.error("Business exception handled with status {}", status, ex);
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * On gere ici les erreurs de validation
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        final List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toValidationError)
                .toList();

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private ErrorResponse.ValidationError toValidationError(FieldError fieldError) {
        return ErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .code(fieldError.getCode())
                .message(fieldError.getDefaultMessage())
                .build();
    }

    private HttpStatus getHttpStatus(BusinessException ex) {
        return switch (ex) {
            case DuplicateResourceException ignored -> HttpStatus.CONFLICT;
            case ResourcesNotFoundException ignored -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
