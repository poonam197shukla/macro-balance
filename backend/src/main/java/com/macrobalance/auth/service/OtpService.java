package com.macrobalance.auth.service;

import com.macrobalance.auth.entity.OtpRequest;
import com.macrobalance.auth.entity.OtpType;
import com.macrobalance.auth.repository.OtpRequestRepository;
import com.macrobalance.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRequestRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_ATTEMPTS = 3;

    public String generateOtp(String identifier, OtpType type) {

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);

        OtpRequest otpEntity = new OtpRequest();

        if (type == OtpType.EMAIL) {
            otpEntity.setEmail(identifier);
        } else {
            otpEntity.setPhone(identifier);
        }

        otpEntity.setType(type);
        otpEntity.setHashedOtp(passwordEncoder.encode(otp));
        otpEntity.setAttempts(0);
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otpEntity);

        return otp;
    }

    public void validateOtp(String identifier, String otp) {

        OtpRequest otpRequest = getLatestOtp(identifier);

        // 1. Expiry check
        if (otpRequest.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("OTP expired");
        }

        // 2. Attempts check
        if (otpRequest.getAttempts() >= MAX_ATTEMPTS) {
            throw new BadRequestException("Too many attempts. Try again later.");
        }

        // 3. Validate OTP
        boolean isValid = passwordEncoder.matches(otp, otpRequest.getHashedOtp());

        // 4. Increment attempts
        otpRequest.setAttempts(otpRequest.getAttempts() + 1);

        if (!isValid) {
            otpRepository.save(otpRequest);
            throw new BadRequestException("Invalid OTP");
        }

        // 5. Mark as used (IMPORTANT)
        otpRequest.setAttempts(MAX_ATTEMPTS); // lock further use

        otpRepository.save(otpRequest);
    }

    @Transactional
    public void cleanupOtps() {
        otpRepository.deleteExpiredOtps(LocalDateTime.now().minusHours(1));
    }

    private OtpRequest getLatestOtp(String identifier) {

        return identifier.contains("@") ?
                otpRepository.findTopByEmailOrderByCreatedAtDesc(identifier)
                .orElseThrow(() -> new BadRequestException("OTP not found")) :
                otpRepository.findTopByPhoneOrderByCreatedAtDesc(identifier)
                .orElseThrow(() -> new BadRequestException("OTP not found"));
    }

}
