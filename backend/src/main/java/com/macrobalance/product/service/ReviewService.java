package com.macrobalance.product.service;

import com.macrobalance.common.exception.BadRequestException;
import com.macrobalance.product.dto.CreateReviewRequest;
import com.macrobalance.product.dto.ReviewResponse;
import com.macrobalance.product.entity.Product;
import com.macrobalance.product.entity.ProductReview;
import com.macrobalance.product.repository.ProductRepository;
import com.macrobalance.product.repository.ProductReviewRepository;
import com.macrobalance.user.entity.User;
import com.macrobalance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository       productRepository;
    private final UserRepository          userRepository;

    public Page<ReviewResponse> getReviews(Long productId, Pageable pageable) {

        return reviewRepository
                .findByProductId(productId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ReviewResponse createReview(Long productId,
                                       Long userId,
                                       CreateReviewRequest request) {

        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new BadRequestException("You have already reviewed this product");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setBody(request.body());
        // isVerifiedPurchase stays false — set separately by order flow

        reviewRepository.save(review);

        // Recalculate and update product stats
        updateProductRatingStats(product);

        return toResponse(review);
    }

    @Transactional
    public ReviewResponse updateReview(Long productId,
                                       Long userId,
                                       CreateReviewRequest request) {

        ProductReview review = reviewRepository
                .findByProductIdAndUserId(productId, userId)
                .orElseThrow(() -> new BadRequestException("Review not found"));

        review.setRating(request.rating());
        review.setTitle(request.title());
        review.setBody(request.body());

        reviewRepository.save(review);

        Product product = review.getProduct();
        updateProductRatingStats(product);

        return toResponse(review);
    }

    @Transactional
    public void deleteReview(Long productId, Long userId) {

        ProductReview review = reviewRepository
                .findByProductIdAndUserId(productId, userId)
                .orElseThrow(() -> new BadRequestException("Review not found"));

        Product product = review.getProduct();

        reviewRepository.delete(review);

        updateProductRatingStats(product);
    }

    // Called by order service when order is DELIVERED
    @Transactional
    public void markVerifiedPurchase(Long productId, Long userId) {

        reviewRepository
                .findByProductIdAndUserId(productId, userId)
                .ifPresent(review -> {
                    review.setVerifiedPurchase(true);
                    reviewRepository.save(review);
                });
    }

    // ── Private helpers ────────────────────────────────────────

    private void updateProductRatingStats(Product product) {

        Double avg   = reviewRepository.calculateAvgRating(product.getId());
        int    count = reviewRepository.countByProductId(product.getId());

        product.setAvgRating(avg != null
                ? BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);

        product.setReviewCount(count);
        productRepository.save(product);
    }

    private ReviewResponse toResponse(ProductReview r) {
        return new ReviewResponse(
                r.getId(),
                r.getUser().getName(),
                r.getRating(),
                r.getTitle(),
                r.getBody(),
                r.isVerifiedPurchase(),
                r.getCreatedAt()
        );
    }
}