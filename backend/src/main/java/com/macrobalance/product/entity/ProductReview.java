package com.macrobalance.product.entity;

import com.macrobalance.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "product_reviews",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_user_product_review",
                columnNames = {"user_id", "product_id"}
        ))
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Short rating;

    @Column(length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    // Set by system, never by user input
    @Column(name = "is_verified_purchase", nullable = false)
    private boolean isVerifiedPurchase = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}