package com.otp.otpservice.service;

import com.otp.otpservice.entity.AuditEventType;
import com.otp.otpservice.entity.AuditLog;
import com.otp.otpservice.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

/**
 * Writes entries to the audit log.
 *
 * @since 1.0.0
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Creates the service.
     *
     * @param auditLogRepository database access for audit entries
     */
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Writes one audit entry.
     *
     * @param userId    id of the user concerned, or {@code null} if unknown
     * @param eventType what happened
     * @param details   extra information about the event
     * @param ipAddress address the request came from, or {@code null}
     */
    public void logEvent(Long userId, AuditEventType eventType,
            String details, String ipAddress) {
        auditLogRepository.save(
                new AuditLog(userId, eventType, details, ipAddress));
    }
}