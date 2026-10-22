package com.otp.otpservice.entity;

import com.otp.common.event.OtpChannel;
import com.otp.common.event.OtpPurpose;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A persisted one-time password and its verification state.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "otp_records")
public class OtpRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "otp_hash", nullable = false, length = 255)
    private String otpHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "purpose", nullable = false, length = 30)
    private OtpPurpose purpose;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "channel", nullable = false,
            columnDefinition = "otp_channel")
    private OtpChannel channel;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false,
            columnDefinition = "otp_status")
    private OtpStatus status = OtpStatus.PENDING;

    @Column(name = "attempts", nullable = false)
    private short attempts;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    /**
     * Creates an entity for JPA.
     */
    protected OtpRecord() {
    }

    /**
     * Creates a pending code record.
     *
     * @param userId    the user who requested the code
     * @param otpHash   the keyed hash of the code
     * @param purpose   why the code was requested
     * @param channel   how the code is delivered
     * @param expiresAt when the code expires
     */
    public OtpRecord(long userId, String otpHash, OtpPurpose purpose,
            OtpChannel channel, Instant expiresAt) {
        this.userId = userId;
        this.otpHash = otpHash;
        this.purpose = purpose;
        this.channel = channel;
        this.expiresAt = expiresAt;
        this.status = OtpStatus.PENDING;
    }

    /**
     * Returns the database identifier.
     *
     * @return the record identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the user identifier.
     *
     * @return the user identifier
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * Returns the stored keyed hash.
     *
     * @return the hash of the code
     */
    public String getOtpHash() {
        return otpHash;
    }

    /**
     * Returns the purpose for which the code was requested.
     *
     * @return the code purpose
     */
    public OtpPurpose getPurpose() {
        return purpose;
    }

    /**
     * Returns the delivery channel.
     *
     * @return the delivery channel
     */
    public OtpChannel getChannel() {
        return channel;
    }

    /**
     * Returns the current lifecycle state.
     *
     * @return the code status
     */
    public OtpStatus getStatus() {
        return status;
    }

    /**
     * Returns the number of failed attempts recorded.
     *
     * @return the failed-attempt count
     */
    public short getAttempts() {
        return attempts;
    }

    /**
     * Returns the code expiration time.
     *
     * @return the expiration time
     */
    public Instant getExpiresAt() {
        return expiresAt;
    }

    /**
     * Marks this code as expired.
     */
    public void markExpired() {
        status = OtpStatus.EXPIRED;
    }

    /**
     * Marks this code as successfully verified.
     *
     * @param verifiedAt when verification succeeded
     */
    public void markVerified(Instant verifiedAt) {
        status = OtpStatus.VERIFIED;
        this.verifiedAt = verifiedAt;
    }

    /**
     * Records the number of failed verification attempts.
     *
     * @param attempts the number of attempts made
     */
    public void recordAttempts(int attempts) {
        if (attempts < 0 || attempts > Short.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "Attempt count is outside the supported range");
        }
        this.attempts = (short) attempts;
    }

    /**
     * Marks this code as locked after too many failed attempts.
     */
    public void markFailed() {
        status = OtpStatus.FAILED;
    }
}
