package com.otp.otpservice.dto;

import com.otp.common.event.OtpChannel;
import com.otp.common.event.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request to send a new code.
 *
 * @param channel   how the code is delivered
 * @param recipient the email address or phone number
 * @param purpose   why the code is requested
 * @since 1.0.0
 */
public record SendOtpRequest(
        @NotNull OtpChannel channel,
        @NotBlank @Size(max = 255) String recipient,
        @NotNull OtpPurpose purpose) {
}
