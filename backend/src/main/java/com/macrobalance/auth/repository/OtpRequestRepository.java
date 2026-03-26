package com.macrobalance.auth.repository;

import com.macrobalance.auth.entity.OtpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRequestRepository extends JpaRepository<OtpRequest, Long> {

    Optional<OtpRequest> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<OtpRequest> findTopByPhoneOrderByCreatedAtDesc(String phone);

    @Modifying
    @Query("DELETE FROM OtpRequest o WHERE o.expiresAt < :time")
    void deleteExpiredOtps(@Param("time") LocalDateTime time);
}