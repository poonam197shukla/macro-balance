package com.macrobalance.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Abstract base class inherited by all auditable JPA entities.
 *
 * <p>Provides five common fields automatically managed by Spring Data JPA auditing:
 * <ul>
 *   <li>{@code id} — auto-incremented primary key</li>
 *   <li>{@code createdDate} — timestamp set once on insert</li>
 *   <li>{@code lastModifiedDate} — timestamp updated on every write</li>
 *   <li>{@code createdBy} — identifier of who created the record</li>
 *   <li>{@code updatedBy} — identifier of who last modified the record</li>
 *   <li>{@code version} — optimistic locking counter</li>
 * </ul>
 *
 * <p>Auditing is driven by {@link AuditingEntityListener}, which must be
 * registered on the main application class via
 * {@code @EnableJpaAuditing(auditorAwareRef = "auditorProvider")}.
 * The {@code auditorProvider} bean supplies the current principal (email)
 * for {@code createdBy} and {@code updatedBy}.
 *
 * <p><strong>Optimistic locking:</strong> the {@code @Version} field causes
 * JPA to append {@code WHERE version = ?} to all UPDATE statements.
 * If two concurrent transactions attempt to update the same row, the
 * second will receive an {@link jakarta.persistence.OptimisticLockException}
 * rather than silently overwriting the first write. This is especially
 * important for the {@link com.macrobalance.cart.entity.Cart} and
 * {@link com.macrobalance.product.entity.Product} entities.
 *
 * <p><strong>Note on timestamps:</strong> fields use {@link LocalDateTime}
 * (server timezone). For entities where timezone correctness is critical
 * (orders, payments, reviews), {@link java.time.Instant} is used directly
 * on the entity rather than inheriting from here.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    /**
     * Primary key, auto-incremented by the database.
     * Never set manually — assigned by Hibernate on first persist.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Timestamp when this record was first inserted.
     * Set automatically by {@link AuditingEntityListener} on persist.
     * Immutable after creation ({@code updatable = false}).
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Timestamp of the most recent update to this record.
     * Set automatically by {@link AuditingEntityListener} on every merge.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime lastModifiedDate;

    /**
     * Optimistic locking version counter managed by JPA.
     * Incremented automatically on every UPDATE.
     * Prevents lost updates when two transactions modify the same row
     * concurrently — the second write fails with
     * {@link jakarta.persistence.OptimisticLockException}.
     */
    @Version
    private Long version;

    /**
     * Identifier of the principal who created this record.
     * Populated automatically by the {@code AuditorAware} bean,
     * which returns the authenticated user's email, or {@code "SYSTEM"}
     * for operations performed outside a request context
     * (e.g. Flyway migrations, scheduled tasks).
     */
    @CreatedBy
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    /**
     * Identifier of the principal who last modified this record.
     * Updated automatically on every merge by the {@code AuditorAware} bean.
     * Same value as {@code createdBy} immediately after creation.
     */
    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    private String updatedBy;
}