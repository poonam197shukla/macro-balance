package com.macrobalance.address.entity;

import com.macrobalance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing a user's saved delivery address.
 *
 * <p>Each user may have up to 5 addresses. One address per user
 * can be marked as default ({@code isDefault = true}), which is
 * pre-selected during the checkout flow.
 *
 * <p>Extends {@link BaseEntity} which provides {@code id},
 * {@code createdAt}, {@code updatedAt}, {@code createdBy},
 * {@code updatedBy}, and {@code version} fields.
 */
@Entity
@Getter
@Setter
@Table(name = "addresses")
public class Address extends BaseEntity {

    /**
     * Foreign key reference to the owning user.
     * Stored as a plain Long to avoid a bidirectional JPA
     * relationship with the User entity.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Primary address line — building name, house number, street, area.
     * Required.
     */
    @Column(nullable = false, length = 255)
    private String line1;

    /**
     * Secondary address line — landmark, floor, apartment number.
     * Optional.
     */
    @Column(length = 255)
    private String line2;

    /**
     * City of the delivery address. Required.
     */
    @Column(nullable = false, length = 100)
    private String city;

    /**
     * State of the delivery address. Required.
     */
    @Column(nullable = false, length = 100)
    private String state;

    /**
     * 6-digit Indian postal code. Required.
     * Validated at the DTO layer using the regex {@code ^[1-9][0-9]{5}$}.
     */
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    /**
     * Country of the delivery address.
     * Defaults to {@code "India"} since MacroBalance currently
     * ships only within India.
     */
    @Column(nullable = false, length = 100)
    private String country = "India";

    /**
     * Whether this is the user's default delivery address.
     * Only one address per user should have this set to {@code true}.
     * Enforced at the service layer — when setting a new default,
     * all other addresses for the user are cleared first.
     */
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    /**
     * User-defined label for easy identification.
     * Typical values: {@code "Home"}, {@code "Work"}, {@code "Other"}.
     * Optional.
     */
    @Column(name = "label", length = 50)
    private String label;
}