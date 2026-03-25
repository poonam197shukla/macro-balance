package com.macrobalance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(
                SecurityContextHolder.getContext().getAuthentication()
        ).map(authentication -> {
            if (authentication.isAuthenticated()) {
                return authentication.getName();
            }
            return "SYSTEM";
        });
    }
}
