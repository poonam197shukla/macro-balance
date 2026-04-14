package com.macrobalance.common.exception;

import com.macrobalance.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Centralised exception handler for all controllers.
 *
 * <p>Intercepts exceptions thrown during request processing and converts
 * them into consistent {@link ApiResponse} payloads with appropriate
 * HTTP status codes. This prevents raw stack traces from leaking to
 * clients and ensures every error follows the standard response envelope.
 *
 * <p>Handler precedence (most specific to least specific):
 * <ol>
 *   <li>{@link MethodArgumentNotValidException} — Bean Validation failures → 400</li>
 *   <li>{@link BadCredentialsException} — wrong email or password → 401</li>
 *   <li>{@link AccessDeniedException} — insufficient role → 403</li>
 *   <li>{@link IllegalArgumentException} — domain rule violations → 400</li>
 *   <li>{@link jakarta.persistence.EntityNotFoundException} — resource not found → 404</li>
 *   <li>{@link Exception} — all other unhandled exceptions → 500</li>
 * </ol>
 *
 * <p>All unhandled exceptions are logged at ERROR level so they appear
 * in both the general and error-specific log files configured in
 * {@code logback-spring.xml}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation failures from {@code @Valid} on request bodies.
     *
     * <p>Collects all field-level constraint violations and joins them
     * into a single readable message, e.g.
     * {@code "postalCode: Invalid Indian postal code; rating: must be between 1 and 5"}.
     *
     * @param ex the validation exception containing all constraint violations
     * @return 400 Bad Request with a combined validation error message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, message, null));
    }

    /**
     * Handles authentication failures — wrong email or password.
     *
     * @param ex the bad credentials exception
     * @return 401 Unauthorized
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleBadCredentials(
            BadCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Invalid email or password", null));
    }

    /**
     * Handles authorisation failures — user is authenticated but lacks
     * the required role (e.g. a USER attempting an ADMIN endpoint).
     *
     * @param ex the access denied exception
     * @return 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(false, "Access denied", null));
    }

    /**
     * Handles domain rule violations thrown as {@link IllegalArgumentException}.
     *
     * <p>Used throughout the service layer for business logic errors such as:
     * invalid order status transitions, address limit exceeded,
     * duplicate reviews, insufficient stock.
     *
     * @param ex the illegal argument exception with a descriptive message
     * @return 400 Bad Request with the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    /**
     * Handles resource-not-found errors thrown by the service layer.
     *
     * <p>Service methods throw {@link jakarta.persistence.EntityNotFoundException}
     * when a requested entity does not exist or does not belong to the
     * current user (e.g. fetching another user's order).
     *
     * @param ex the entity not found exception
     * @return 404 Not Found with the exception message
     */
    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(
            jakarta.persistence.EntityNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, ex.getMessage(), null));
    }

    /**
     * Catch-all handler for any exception not matched by a more specific handler above.
     *
     * <p>Logs the full stack trace at ERROR level so it is captured by both
     * the general application log and the error-only log file.
     * Returns a generic message to the client — internal details are never
     * exposed in production responses.
     *
     * @param ex the unhandled exception
     * @return 500 Internal Server Error with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "An unexpected error occurred", null));
    }
}