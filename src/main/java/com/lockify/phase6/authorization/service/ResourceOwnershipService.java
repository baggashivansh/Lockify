package com.lockify.phase6.authorization.service;

import com.lockify.phase6.authorization.dto.ResourceRequest;
import com.lockify.phase6.authorization.dto.ResourceResponse;
import com.lockify.phase6.authorization.entity.UserResource;
import com.lockify.phase6.authorization.evaluator.AbacPolicyEvaluator;
import com.lockify.phase6.authorization.repository.UserResourceRepository;
import com.lockify.shared.domain.entity.User;
import com.lockify.shared.domain.repository.UserRepository;
import com.lockify.shared.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Resource ownership enforce karta hai - user sirf apne resources access kar sakta hai.
 * ABAC policies additional access allow kar sakti hain (same dept/location).
 */
@Service
@RequiredArgsConstructor
public class ResourceOwnershipService {

    private final UserResourceRepository userResourceRepository;
    private final UserRepository userRepository;
    private final AbacPolicyEvaluator abacPolicyEvaluator;

    @Transactional
    public ResourceResponse create(Long userId, ResourceRequest request) {
        User owner = loadUser(userId);
        UserResource resource = UserResource.builder()
                .owner(owner)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        return toResponse(userResourceRepository.save(resource));
    }

    @Transactional(readOnly = true)
    public ResourceResponse getById(Long userId, Long resourceId, String action) {
        UserResource resource = loadResource(resourceId);
        assertAccess(userId, resource, action);
        return toResponse(resource);
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> listOwned(Long userId) {
        return userResourceRepository.findByOwnerId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ResourceResponse update(Long userId, Long resourceId, ResourceRequest request) {
        UserResource resource = loadResource(resourceId);
        assertAccess(userId, resource, "UPDATE");

        resource.setTitle(request.getTitle());
        resource.setContent(request.getContent());
        return toResponse(userResourceRepository.save(resource));
    }

    @Transactional
    public void delete(Long userId, Long resourceId) {
        UserResource resource = loadResource(resourceId);
        if (!resource.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Sirf owner delete kar sakta hai");
        }
        userResourceRepository.delete(resource);
    }

    public void assertOwnership(Long userId, Long resourceId) {
        userResourceRepository.findByIdAndOwnerId(resourceId, userId)
                .orElseThrow(() -> new AccessDeniedException("Yeh resource aapka nahi hai"));
    }

    private void assertAccess(Long userId, UserResource resource, String action) {
        if (!abacPolicyEvaluator.isAllowed(userId, resource, action)) {
            throw new AccessDeniedException("ABAC policy access deny kar rahi hai");
        }
    }

    private UserResource loadResource(Long resourceId) {
        return userResourceRepository.findById(resourceId)
                .orElseThrow(() -> new AuthenticationException("Resource nahi mila"));
    }

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User nahi mila"));
    }

    private ResourceResponse toResponse(UserResource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .ownerId(resource.getOwner().getId())
                .title(resource.getTitle())
                .content(resource.getContent())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
