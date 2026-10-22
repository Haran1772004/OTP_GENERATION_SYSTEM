package com.otp.otpservice.service;

import com.otp.common.event.OtpChannel;
import com.otp.common.event.OtpEvent;
import com.otp.common.event.OtpPurpose;
import com.otp.otpservice.config.OtpProperties;
import com.otp.otpservice.entity.AuditEventType;
import com.otp.otpservice.entity.OtpRecord;
import com.otp.otpservice.entity.OtpStatus;
import com.otp.otpservice.entity.User;
import com.otp.otpservice.exception.InvalidRecipientException;
import com.otp.otpservice.repository.OtpRecordRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issues and verifies one-time passwords.
 *
 * @since 1.0.0
 */
@Service
public class OtpService {

    private final OtpRecordRepository otpRecordRepository;

    private final UserService userService;

    private final OtpCodeService codeService;

    private final OtpCache otpCache;

    private final RateLimitService rateLimitService;

    private final AuditService auditService;

    private final RecipientValidator recipientValidator;

    private final ApplicationEventPublisher eventPublisher;

    private final OtpProperties properties;

    /**
     * Creates the service.
     *
     * @param otpRecordRepository database access for OTP records
     * @param userService         finds and creates users
     * @param codeService         generates and hashes codes
     * @param otpCache            keeps live codes in Redis
     * @param rateLimitService    limits code requests
     * @param auditService        writes the audit log
     * @param recipientValidator  checks emails and phone numbers
     * @param eventPublisher      publishes events inside the application
     * @param properties          the OTP settings
     */
    public OtpService(OtpRecordRepository otpRecordRepository,
            UserService userService, OtpCodeService codeService,
            OtpCache otpCache, RateLimitService rateLimitService,
            AuditService auditService, RecipientValidator recipientValidator,
            ApplicationEventPublisher eventPublisher,
            OtpProperties properties) {
        this.otpRecordRepository = otpRecordRepository;
        this.userService = userService;
        this.codeService = codeService;
        this.otpCache = otpCache;
        this.rateLimitService = rateLimitService;
        this.auditService = auditService;
        this.recipientValidator = recipientValidator;
        this.eventPublisher = eventPublisher;
        this.properties = properties;
    }

    /**
     * Creates a new code and queues it for delivery.
     *
     * <p>Any code still pending for the same user and purpose is expired
     * first, so only one code is valid at a time.
     *
     * @param channel   how the code is delivered
     * @param recipient the email address or phone number
     * @param purpose   why the code is requested
     * @param ipAddress address the request came from
     * @return {@link SendOutcome#SENT}, or {@link SendOutcome#RATE_LIMITED}
     *         if the recipient asked for too many codes
     * @throws InvalidRecipientException if the recipient is not valid
     */
    @Transactional
    public SendOutcome sendOtp(OtpChannel channel, String recipient,
            OtpPurpose purpose, String ipAddress) {
        String target = recipientValidator.normalize(channel, recipient);
        if (!rateLimitService.isAllowed(channel, target)) {
            auditService.logEvent(null, AuditEventType.OTP_RATE_LIMITED,
                    "Too many " + purpose + " requests", ipAddress);
            return SendOutcome.RATE_LIMITED;
        }

        User user = userService.findOrCreate(channel, target);
        long userId = user.getId();
        expirePendingCode(userId, purpose);

        String code = codeService.generate();
        String hash = codeService.hash(userId, purpose, code);
        Instant expiresAt = Instant.now().plus(properties.getTtl());
        otpRecordRepository.save(
                new OtpRecord(userId, hash, purpose, channel, expiresAt));
        otpCache.store(userId, purpose, hash, properties.getTtl());
        auditService.logEvent(userId, AuditEventType.OTP_GENERATED,
                purpose + " code via " + channel, ipAddress);

        eventPublisher.publishEvent(new OtpEvent(userId, channel, target,
                purpose, code, (int) properties.getTtl().toMinutes()));
        return SendOutcome.SENT;
    }

    /**
     * Checks a code entered by a user.
     *
     * <p>Wrong tries are counted. When the limit is reached, the code is
     * locked and a new one has to be requested.
     *
     * @param channel   how the code was delivered
     * @param recipient the email address or phone number
     * @param purpose   why the code was requested
     * @param code      the code entered by the user
     * @param ipAddress address the request came from
     * @return the result of the check
     * @throws InvalidRecipientException if the recipient is not valid
     */
    @Transactional
    public VerifyOutcome verifyOtp(OtpChannel channel, String recipient,
            OtpPurpose purpose, String code, String ipAddress) {
        String target = recipientValidator.normalize(channel, recipient);
        Optional<User> user = userService.find(channel, target);
        if (user.isEmpty()) {
            auditService.logEvent(null, AuditEventType.OTP_FAILED,
                    "Code entered for an unknown recipient", ipAddress);
            return VerifyOutcome.NOT_FOUND;
        }

        long userId = user.get().getId();
        Optional<OtpRecord> pending = otpRecordRepository
                .findByUserIdAndPurposeAndStatus(
                        userId, purpose, OtpStatus.PENDING);
        if (pending.isEmpty()) {
            auditService.logEvent(userId, AuditEventType.OTP_FAILED,
                    "No active " + purpose + " code", ipAddress);
            return VerifyOutcome.NOT_FOUND;
        }

        OtpRecord record = pending.get();
        Optional<String> storedHash = otpCache.findHash(userId, purpose);
        if (storedHash.isEmpty()
                || record.getExpiresAt().isBefore(Instant.now())) {
            record.markExpired();
            otpCache.clear(userId, purpose);
            auditService.logEvent(userId, AuditEventType.OTP_EXPIRED,
                    purpose + " code expired", ipAddress);
            return VerifyOutcome.EXPIRED;
        }

        long attempts = otpCache.incrementAttempts(userId, purpose);
        if (codeService.matches(storedHash.get(), userId, purpose, code)) {
            record.markVerified(Instant.now());
            otpCache.clear(userId, purpose);
            auditService.logEvent(userId, AuditEventType.OTP_VERIFIED,
                    purpose + " code verified", ipAddress);
            return VerifyOutcome.VERIFIED;
        }

        record.recordAttempts((int) attempts);
        auditService.logEvent(userId, AuditEventType.OTP_FAILED,
                "Wrong " + purpose + " code, try " + attempts, ipAddress);
        if (attempts >= properties.getMaxVerifyAttempts()) {
            record.markFailed();
            otpCache.clear(userId, purpose);
            return VerifyOutcome.TOO_MANY_ATTEMPTS;
        }
        return VerifyOutcome.INVALID_CODE;
    }

    private void expirePendingCode(long userId, OtpPurpose purpose) {
          Optional<OtpRecord> pending = otpRecordRepository
            .findByUserIdAndPurposeAndStatus(
                    userId, purpose, OtpStatus.PENDING);
    if (pending.isPresent()) {
        OtpRecord old = pending.get();
        old.markExpired();
        otpRecordRepository.saveAndFlush(old);
    }
    }
}
