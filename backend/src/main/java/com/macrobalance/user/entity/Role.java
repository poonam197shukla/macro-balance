package com.macrobalance.user.entity;

/**
 * Enum representing the access role assigned to a user account.
 *
 * <p>Roles are stored as strings in the database ({@code EnumType.STRING})
 * and loaded into the Spring Security authentication context as
 * {@code GrantedAuthority} values prefixed with {@code ROLE_}.
 *
 * <p>Role-based access control is enforced in two places:
 * <ul>
 *   <li>{@code SecurityConfig} — path-level rules via {@code authorizeHttpRequests}</li>
 *   <li>{@code @PreAuthorize("hasRole('ADMIN')")} — method-level rules on
 *       admin endpoints across controllers</li>
 * </ul>
 */
public enum Role {

    /**
     * Standard customer account.
     * Can browse products, manage their cart, place orders,
     * manage addresses, and write reviews.
     */
    USER,

    /**
     * Administrator account.
     * All USER permissions plus the ability to manage products,
     * categories, and transition order statuses.
     * Assigned manually — there is no self-registration path for ADMIN.
     */
    ADMIN
}