package com.ninehub.dreamshops.service.validation;

import com.ninehub.dreamshops.enums.ValidationType;
import com.ninehub.dreamshops.model.User;
import com.ninehub.dreamshops.model.Validation;
import com.ninehub.dreamshops.repositry.ValidationRepository;
import com.ninehub.dreamshops.service.notification.EmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ValidationService {
    private final ValidationRepository validationRepository;
    private final EmailNotification emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public String createEmailVerificationCode(User user) {
        String code = generateSecureCode();

        Validation validation = Validation.builder()
                .code(code)
                .email(user.getEmail())
                .type(ValidationType.EMAIL_VERIFICATION)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24)) // expired after 24 hours
                .build();

        validationRepository.save(validation);
        emailService.sendVerificationEmail(user.getEmail(), code);

        return code;
    }

    public String createPasswordResetToken(User user) {
        // Invalidate existing password reset tokens
        invalidateExistingCodes(user.getEmail(), ValidationType.PASSWORD_RESET);

        String code = generateSecureCode();

        Validation validation = Validation.builder()
                .code(code)
                .email(user.getEmail())
                .type(ValidationType.PASSWORD_RESET)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(1)) // expired after 1 hour
                .build();

        validationRepository.save(validation);
        emailService.sendPasswordResetEmail(user.getEmail(), code);

        return code;
    }

    public Optional<Validation> validateToken(String code) {
        Optional<Validation> validation = validationRepository.findByCodeAndUsedFalse(code);

        if (validation.isPresent() && validation.get().isValid()) {
            return validation;
        }

        return Optional.empty();
    }

    public void markTokenAsUsed(Validation validation) {
        validation.setUsed(true);
        validation.setUsedAt(LocalDateTime.now());
        validationRepository.save(validation);
    }

    private void invalidateExistingCodes(String email, ValidationType type) {
        List<Validation> existingTokens = validationRepository.findByUserEmailAndUsedFalse(email);
        existingTokens.stream()
                .filter(validation -> validation.getType() == type)
                .forEach(this::markTokenAsUsed);
    }

    private String generateSecureCode() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    // Clean up expired tokens every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<Validation> expiredTokens = validationRepository.findExpiredValidations(now);

        expiredTokens.forEach(this::markTokenAsUsed);

        // Delete old used tokens (older than 7 days)
        validationRepository.deleteByExpiresAtBeforeAndUsedTrue(now.minusDays(7));

        log.info("Cleaned up {} expired validation tokens", expiredTokens.size());
    }
}