package com.otp.otpservice.dto;

import com.otp.common.event.OtpChannel;
import com.otp.common.event.OtpPurpose;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request to check a code entered by a user.
 *
 * @param channel   how the code was delivered
 * @param recipient the email address or phone number
 * @param purpose   why the code was requested
 * @param otp       the code entered by the user
 * @since 1.0.0
 */
public record VerifyOtpRequest(
        @NotNull OtpChannel channel,
        @NotBlank @Size(max = 255) String recipient,
        @NotNull OtpPurpose purpose,
        @NotBlank @Pattern(regexp = "\\d{4,10}") String otp) {
}
