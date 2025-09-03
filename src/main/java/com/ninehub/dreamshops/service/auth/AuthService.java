package com.ninehub.dreamshops.service.auth;

import com.ninehub.dreamshops.dto.auth.*;
import com.ninehub.dreamshops.enums.ValidationType;
import com.ninehub.dreamshops.execptions.AlreadyExistsException;
import com.ninehub.dreamshops.execptions.ResourceNotFoundException;
import com.ninehub.dreamshops.model.Role;
import com.ninehub.dreamshops.model.User;
import com.ninehub.dreamshops.model.Validation;
import com.ninehub.dreamshops.repositry.RoleRepository;
import com.ninehub.dreamshops.repositry.UserRepository;
import com.ninehub.dreamshops.security.jwt.JwtUtils;
import com.ninehub.dreamshops.service.notification.EmailNotification;
import com.ninehub.dreamshops.service.user.UserService;
import com.ninehub.dreamshops.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final EmailNotification emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    public void register(RegisterRequest request) {
        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        // Get default role
        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        // Create user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false) // User needs to verify email
                .roles(Set.of(userRole))
                .build();

        User savedUser = userRepository.save(user);

        // Send verification email
        validationService.createEmailVerificationCode(savedUser);

        log.info("User registered successfully: {}", savedUser.getEmail());
    }

    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Update last login
        User user = userRepository.findByEmail(request.getEmail());
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate JWT token with extended expiry if remember me is checked
        long codeExpiry = request.isRememberMe() ?
                jwtUtils.getRememberMeJwtExpirationMs() :
                jwtUtils.getJwtExpirationMs();

        return jwtUtils.generateTokenWithCustomExpiry(authentication, codeExpiry);
    }

    public void verifyEmail(String token) {
        Optional<Validation> validation = validationService.validateToken(token);

        if (validation.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired verification token");
        }

        Validation validationEntity = validation.get();

        if (validationEntity.getType() != ValidationType.EMAIL_VERIFICATION) {
            throw new IllegalArgumentException("Invalid token type");
        }

        User user = validationEntity.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        validationService.markTokenAsUsed(validationEntity);
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail());

        validationService.createPasswordResetToken(user);

        log.info("Password reset requested for user: {}", user.getEmail());
    }

    public void resetPassword(PasswordChangeRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Optional<Validation> validation = validationService.validateToken(request.getToken());

        if (validation.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }

        Validation validationEntity = validation.get();

        if (validationEntity.getType() != ValidationType.PASSWORD_RESET) {
            throw new IllegalArgumentException("Invalid token type");
        }

        User user = validationEntity.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        validationService.markTokenAsUsed(validationEntity);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    public void updatePassword(String email, UpdatePasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = userRepository.findByEmail(email);

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password updated successfully for user: {}", user.getEmail());
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user.isEnabled()) {
            throw new IllegalArgumentException("User is already verified");
        }

        validationService.createEmailVerificationCode(user);

        log.info("Verification email resent to: {}", email);
    }
}

