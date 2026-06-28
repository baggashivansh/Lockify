package com.lockify.phase2.account.controller;

import com.lockify.phase1.coreauth.security.LockifyUserDetails;
import com.lockify.phase2.account.dto.ForgotPasswordRequest;
import com.lockify.phase2.account.dto.MessageResponse;
import com.lockify.phase2.account.dto.ResetPasswordRequest;
import com.lockify.phase2.account.dto.VerifyEmailRequest;
import com.lockify.phase2.account.service.EmailVerificationService;
import com.lockify.phase2.account.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Account Security Controller - email verification aur password reset APIs.
 */
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountSecurityController {

    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;

    /** Module 7 - POST /api/account/verify-email */
    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return ResponseEntity.ok(emailVerificationService.verifyEmail(request.getToken()));
    }

    /** Module 7 - POST /api/account/resend-verification (protected) */
    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(@AuthenticationPrincipal LockifyUserDetails userDetails) {
        return ResponseEntity.ok(emailVerificationService.resendVerification(userDetails.getId()));
    }

    /** Module 8 - POST /api/account/forgot-password */
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.forgotPassword(request.getEmail()));
    }

    /** Module 8 - POST /api/account/reset-password */
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(passwordResetService.resetPassword(request.getToken(), request.getNewPassword()));
    }
}
