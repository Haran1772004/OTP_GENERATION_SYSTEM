package com.otp.otpservice.repository;

import com.otp.otpservice.entity.OtpRecord;
import com.otp.otpservice.entity.OtpStatus;
import com.otp.common.event.OtpPurpose;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Database access for one-time password records.
 *
 * @since 1.0.0
 */
public interface OtpRecordRepository
        extends JpaRepository<OtpRecord, Long> {

    /**
     * Finds a record by user, purpose, and lifecycle state.
     *
     * @param userId  the user identifier
     * @param purpose why the code was requested
     * @param status  the lifecycle state
     * @return the matching record, if present
     */
    Optional<OtpRecord> findByUserIdAndPurposeAndStatus(long userId,
            OtpPurpose purpose, OtpStatus status);
}
