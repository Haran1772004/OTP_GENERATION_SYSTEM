package com.otp.otpservice.repository;

import com.otp.otpservice.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Database access for recipients.
 *
 * @since 1.0.0
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by email address.
     *
     * @param email the normalized email address
     * @return the matching user, if present
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by phone number.
     *
     * @param phone the normalized phone number
     * @return the matching user, if present
     */
    Optional<User> findByPhone(String phone);
}
