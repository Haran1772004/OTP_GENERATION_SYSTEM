package com.otp.otpservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A recipient identified by an email address, phone number, or both.
 *
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    /**
     * Creates an entity for JPA.
     */
    protected User() {
    }

    private User(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    /**
     * Creates a user identified by an email address.
     *
     * @param email the normalized email address
     * @return a user with the given email address
     */
    public static User ofEmail(String email) {
        return new User(email, null);
    }

    /**
     * Creates a user identified by a phone number.
     *
     * @param phone the normalized phone number
     * @return a user with the given phone number
     */
    public static User ofPhone(String phone) {
        return new User(null, phone);
    }

    /**
     * Returns the database identifier.
     *
     * @return the user identifier, or {@code null} before persistence
     */
    public Long getId() {
        return id;
    }
}
