package com.otp.otpservice.controller;

import com.otp.otpservice.dto.MessageResponse;
import com.otp.otpservice.exception.InvalidRecipientException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Turns request errors into 400 responses with a short message.
 *
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles a request body that failed validation.
     *
     * @param ex the validation error
     * @return a 400 response naming the first invalid field
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Invalid request");
        return ResponseEntity.badRequest().body(new MessageResponse(message));
    }

    /**
     * Handles a request body that cannot be read, such as bad JSON or an
     * unknown enum value.
     *
     * @param ex the read error
     * @return a 400 response
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageResponse> handleUnreadable(
            HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(new MessageResponse("Malformed request body"));
    }

    /**
     * Handles an invalid email address or phone number.
     *
     * @param ex the recipient error
     * @return a 400 response with the reason
     */
    @ExceptionHandler(InvalidRecipientException.class)
    public ResponseEntity<MessageResponse> handleRecipient(
            InvalidRecipientException ex) {
        return ResponseEntity.badRequest()
                .body(new MessageResponse(ex.getMessage()));
    }
}
