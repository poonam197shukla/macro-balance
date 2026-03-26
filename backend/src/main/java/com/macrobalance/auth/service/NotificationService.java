package com.macrobalance.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    public void sendEmailOtp(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your MacroBalance OTP");
        message.setText("Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes. Do not share it with anyone.");
        mailSender.send(message);
    }

    public void sendSmsOtp(String phone, String otp) {
        System.out.println("SMS OTP: " + otp);
    }

}
