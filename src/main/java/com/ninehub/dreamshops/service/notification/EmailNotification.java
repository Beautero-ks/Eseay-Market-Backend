package com.ninehub.dreamshops.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotification {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Easy Market - Email Verification");

            String verificationUrl = frontendUrl + "/verify-email?token=" + token;
            String text = String.format(
                    "Welcome to Easy Market!\n\n" +
                            "Please click the link below to verify your email address:\n%s\n\n" +
                            "This link will expire in 24 hours.\n\n" +
                            "If you didn't create an account with Easy Market, please ignore this email.\n\n" +
                            "Best regards,\nEasy Market Team",
                    verificationUrl
            );

            message.setText(text);
            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("Failed to send verification email");
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Easy Market - Password Reset");

            String resetUrl = frontendUrl + "/reset-password?token=" + token;
            String text = String.format(
                    "You have requested a password reset for your Easy Market account.\n\n" +
                            "Please click the link below to reset your password:\n%s\n\n" +
                            "This link will expire in 1 hour.\n\n" +
                            "If you didn't request a password reset, please ignore this email.\n\n" +
                            "Best regards,\nEasy Market Team",
                    resetUrl
            );

            message.setText(text);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
            throw new RuntimeException("Failed to send password reset email");
        }
    }

    public void sendWelcomeEmail(String to, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Welcome to Easy Market!");

            String text = String.format(
                    "Hello %s,\n\n" +
                            "Welcome to Easy Market! Your account has been successfully verified.\n\n" +
                            "You can now start shopping and enjoy our amazing products.\n\n" +
                            "Happy shopping!\n\n" +
                            "Best regards,\nEasy Market Team",
                    firstName
            );

            message.setText(text);
            mailSender.send(message);
            log.info("Welcome email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
        }
    }
}
