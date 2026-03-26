package com.macrobalance.auth.scheduler;

import com.macrobalance.auth.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OtpCleanupScheduler {

    private final OtpService otpService;

    // Runs every 1 hour
    @Scheduled(fixedRate = 3600000)
    public void cleanOtpTable() {
        otpService.cleanupOtps();
    }
}
