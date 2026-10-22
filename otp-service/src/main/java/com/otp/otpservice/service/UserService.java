package com.otp.otpservice.service;

import com.otp.common.event.OtpChannel;
import com.otp.otpservice.entity.User;
import com.otp.otpservice.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Finds and creates users by their email address or phone number.
 *
 * @since 1.0.0
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Creates the service.
     *
     * @param userRepository database access for users
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds the user that owns the recipient.
     *
     * @param channel   the delivery channel
     * @param recipient the email address or phone number
     * @return the user, if one exists
     */
    public Optional<User> find(OtpChannel channel, String recipient) {
        return switch (channel) {
            case EMAIL -> userRepository.findByEmail(recipient);
            case SMS -> userRepository.findByPhone(recipient);
        };
    }

    /**
     * Finds the user that owns the recipient, or creates one.
     *
     * @param channel   the delivery channel
     * @param recipient the email address or phone number
     * @return the existing or newly created user
     */
    public User findOrCreate(OtpChannel channel, String recipient) {
        return find(channel, recipient).orElseGet(() -> userRepository.save(
                channel == OtpChannel.EMAIL
                        ? User.ofEmail(recipient)
                        : User.ofPhone(recipient)));
    }
}