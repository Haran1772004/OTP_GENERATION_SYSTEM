package com.otp.otpservice.repository;

import com.otp.otpservice.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Database access for OTP audit entries.
 *
 * @since 1.0.0
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
