package com.lockify.phase6.authorization.controller;

import com.lockify.phase6.authorization.annotation.OwnResource;
import com.lockify.phase6.authorization.dto.ResourceRequest;
import com.lockify.phase6.authorization.dto.ResourceResponse;
import com.lockify.phase6.authorization.service.ResourceOwnershipService;
import com.lockify.phase1.coreauth.security.LockifyUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceOwnershipService resourceOwnershipService;

    @PostMapping
    public ResponseEntity<ResourceResponse> create(
            @AuthenticationPrincipal LockifyUserDetails user,
            @Valid @RequestBody ResourceRequest request
    ) {
        ResourceResponse response = resourceOwnershipService.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> list(@AuthenticationPrincipal LockifyUserDetails user) {
        return ResponseEntity.ok(resourceOwnershipService.listOwned(user.getId()));
    }

    @GetMapping("/{id}")
    @OwnResource(action = "READ")
    public ResponseEntity<ResourceResponse> getById(
            @AuthenticationPrincipal LockifyUserDetails user,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(resourceOwnershipService.getById(user.getId(), id, "READ"));
    }

    @PutMapping("/{id}")
    @OwnResource(action = "UPDATE")
    public ResponseEntity<ResourceResponse> update(
            @AuthenticationPrincipal LockifyUserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request
    ) {
        return ResponseEntity.ok(resourceOwnershipService.update(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    @OwnResource(action = "DELETE")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal LockifyUserDetails user,
            @PathVariable Long id
    ) {
        resourceOwnershipService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
