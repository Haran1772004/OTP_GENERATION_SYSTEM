package com.otp.otpservice.controller;

import com.otp.otpservice.config.OtpProperties;
import com.otp.otpservice.dto.MessageResponse;
import com.otp.otpservice.dto.SendOtpRequest;
import com.otp.otpservice.dto.VerifyOtpRequest;
import com.otp.otpservice.service.OtpService;
import com.otp.otpservice.service.SendOutcome;
import com.otp.otpservice.service.VerifyOutcome;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints to send and verify one-time passwords.
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/otp")
public class OtpController {

    private final OtpService otpService;

    private final OtpProperties properties;

    /**
     * Creates the controller.
     *
     * @param otpService issues and verifies codes
     * @param properties the OTP settings
     */
    public OtpController(OtpService otpService, OtpProperties properties) {
        this.otpService = otpService;
        this.properties = properties;
    }

    /**
     * Sends a new code to the recipient.
     *
     * @param request     the recipient, channel and purpose
     * @param httpRequest the incoming request, used for the client address
     * @return 200 if the code was sent, 429 if too many codes were requested
     */
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> send(
            @Valid @RequestBody SendOtpRequest request,
            HttpServletRequest httpRequest) {
        SendOutcome outcome = otpService.sendOtp(request.channel(),
                request.recipient(), request.purpose(),
                httpRequest.getRemoteAddr());
        if (outcome == SendOutcome.RATE_LIMITED) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new MessageResponse(
                            "Too many requests. Try again later."));
        }
        return ResponseEntity.ok(new MessageResponse(
                "Code sent. It expires in "
                        + properties.getTtl().toMinutes() + " minutes."));
    }

    /**
     * Checks a code entered by a user.
     *
     * @param request     the recipient, channel, purpose and code
     * @param httpRequest the incoming request, used for the client address
     * @return 200 if the code is correct, 400 if it is wrong or expired,
     *         429 if the code was locked after too many wrong tries
     */
    @PostMapping("/verify")
    public ResponseEntity<MessageResponse> verify(
            @Valid @RequestBody VerifyOtpRequest request,
            HttpServletRequest httpRequest) {
        VerifyOutcome outcome = otpService.verifyOtp(request.channel(),
                request.recipient(), request.purpose(), request.otp(),
                httpRequest.getRemoteAddr());
        return switch (outcome) {
            case VERIFIED -> ResponseEntity.ok(
                    new MessageResponse("Code verified."));
            case TOO_MANY_ATTEMPTS -> ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new MessageResponse(
                            "Too many wrong tries. Request a new code."));
            case INVALID_CODE, EXPIRED, NOT_FOUND -> ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Invalid or expired code."));
        };
    }
}
