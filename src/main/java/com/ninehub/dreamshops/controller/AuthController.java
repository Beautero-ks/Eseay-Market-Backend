package com.ninehub.dreamshops.controller;

import com.ninehub.dreamshops.dto.auth.*;
import com.ninehub.dreamshops.response.ApiResponse;
import com.ninehub.dreamshops.response.JwtResponse;
import com.ninehub.dreamshops.security.jwt.JwtUtils;
import com.ninehub.dreamshops.security.shopUser.ShopUserDetails;
import com.ninehub.dreamshops.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request){
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtTokenForUser(authentication);
            ShopUserDetails userDetails = (ShopUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(userDetails.getId(), jwt);

            return ResponseEntity.ok(new ApiResponse("Login successfully", jwtResponse));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok(
                    new ApiResponse("Registration successful! Please check your email to verify your account.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse("Registration failed: " + e.getMessage(), null)
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            log.info("User logged out successfully: {}", authentication.getName());
        }

        return ResponseEntity.ok(
                new ApiResponse("Logout successful!", null)
        );
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String code) {
        try {
            authService.verifyEmail(code);
            return ResponseEntity.ok(
                    new ApiResponse("Email verified successfully! You can now login to your account.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse("Email verification failed: " + e.getMessage(), null)
            );
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            authService.requestPasswordReset(request);
            return ResponseEntity.ok(
                    new ApiResponse("Password reset instructions have been sent to your email.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse("Password reset request failed: " + e.getMessage(), null)
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody PasswordChangeRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(
                    new ApiResponse("Password reset successfully! You can now login with your new password.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse("Password reset failed: " + e.getMessage(), null)
            );
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            Principal principal) {
        try {
            authService.updatePassword(principal.getName(), request);
            return ResponseEntity.ok(
                    new ApiResponse("Password updated successfully!", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse("Password update failed: " + e.getMessage(), null)
            );
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerificationEmail(@RequestParam String email) {
        try {
            authService.resendVerificationEmail(email);
            return ResponseEntity.ok(
                    new ApiResponse("Verification email sent! Please check your email.", null)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse("Failed to send verification email: " + e.getMessage(), null)
            );
        }
    }
}
