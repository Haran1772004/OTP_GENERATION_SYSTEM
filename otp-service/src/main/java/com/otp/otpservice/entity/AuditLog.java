package com.otp.otpservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * An audit entry for an OTP service event.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "event_type", nullable = false,
            columnDefinition = "audit_event_type")
    private AuditEventType eventType;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Creates an entity for JPA.
     */
    protected AuditLog() {
    }

    /**
     * Creates an audit entry.
     *
     * @param userId    the user concerned, or {@code null} if unknown
     * @param eventType what happened
     * @param details   extra information about the event
     * @param ipAddress the request address, or {@code null}
     */
    public AuditLog(Long userId, AuditEventType eventType, String details,
            String ipAddress) {
        this.userId = userId;
        this.eventType = eventType;
        this.details = details;
        this.ipAddress = ipAddress;
    }
}
