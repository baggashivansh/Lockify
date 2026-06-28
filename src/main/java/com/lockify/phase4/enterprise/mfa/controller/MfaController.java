package com.lockify.phase4.enterprise.mfa.controller;

import com.lockify.phase4.enterprise.audit.annotation.Auditable;
import com.lockify.phase4.enterprise.mfa.dto.MfaEnableRequest;
import com.lockify.phase4.enterprise.mfa.dto.MfaSetupResponse;
import com.lockify.phase4.enterprise.mfa.dto.MfaVerifyRequest;
import com.lockify.phase4.enterprise.mfa.service.MfaService;
import com.lockify.phase1.coreauth.security.LockifyUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mfa")
@RequiredArgsConstructor
public class MfaController {

    private final MfaService mfaService;

    @PostMapping("/setup")
    @Auditable(action = "MFA_SETUP", resource = "MFA")
    public ResponseEntity<MfaSetupResponse> setup(@AuthenticationPrincipal LockifyUserDetails user) {
        return ResponseEntity.ok(mfaService.setupTotp(user.getId()));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verify(
            @AuthenticationPrincipal LockifyUserDetails user,
            @Valid @RequestBody MfaVerifyRequest request
    ) {
        boolean valid = mfaService.verify(user.getId(), request);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/enable")
    public ResponseEntity<Map<String, String>> enable(
            @AuthenticationPrincipal LockifyUserDetails user,
            @Valid @RequestBody MfaEnableRequest request
    ) {
        mfaService.enable(user.getId(), request);
        return ResponseEntity.ok(Map.of("message", "MFA enable ho gaya"));
    }

    @PostMapping("/disable")
    public ResponseEntity<Map<String, String>> disable(
            @AuthenticationPrincipal LockifyUserDetails user,
            @Valid @RequestBody MfaVerifyRequest request
    ) {
        mfaService.disable(user.getId(), request);
        return ResponseEntity.ok(Map.of("message", "MFA disable ho gaya"));
    }
}
