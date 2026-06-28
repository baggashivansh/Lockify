package com.lockify.phase6.authorization.evaluator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lockify.phase6.authorization.entity.AbacPolicy;
import com.lockify.phase6.authorization.entity.UserAttribute;
import com.lockify.phase6.authorization.entity.UserResource;
import com.lockify.phase6.authorization.repository.AbacPolicyRepository;
import com.lockify.phase6.authorization.repository.UserAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ABAC policy evaluator - department/location/owner rules JSON se parse karke check karta hai.
 */
@Component
@RequiredArgsConstructor
public class AbacPolicyEvaluator {

    private static final String RESOURCE_TYPE = "DOCUMENT";

    private final AbacPolicyRepository abacPolicyRepository;
    private final UserAttributeRepository userAttributeRepository;
    private final ObjectMapper objectMapper;

    public boolean isAllowed(Long userId, UserResource resource, String action) {
        var policies = abacPolicyRepository.findByResourceAndActionAndActiveTrue(RESOURCE_TYPE, action);
        if (policies.isEmpty()) {
            return resource.getOwner().getId().equals(userId);
        }

        UserAttribute subjectAttrs = userAttributeRepository.findByUserId(userId).orElse(null);
        UserAttribute ownerAttrs = userAttributeRepository.findByUserId(resource.getOwner().getId()).orElse(null);

        for (AbacPolicy policy : policies) {
            if (evaluatePolicy(policy, userId, resource, subjectAttrs, ownerAttrs)) {
                return true;
            }
        }
        return false;
    }

    private boolean evaluatePolicy(AbacPolicy policy, Long userId, UserResource resource,
                                   UserAttribute subject, UserAttribute owner) {
        Map<String, Object> conditions = parseConditions(policy.getConditionJson());

        boolean ownerOnly = Boolean.TRUE.equals(conditions.get("ownerOnly"));
        if (ownerOnly && !resource.getOwner().getId().equals(userId)) {
            return false;
        }

        if (Boolean.TRUE.equals(conditions.get("departmentMatch"))) {
            if (subject == null || owner == null) {
                return false;
            }
            if (subject.getDepartment() == null || !subject.getDepartment().equals(owner.getDepartment())) {
                return false;
            }
        }

        if (Boolean.TRUE.equals(conditions.get("locationMatch"))) {
            if (subject == null || owner == null) {
                return false;
            }
            if (subject.getLocation() == null || !subject.getLocation().equals(owner.getLocation())) {
                return false;
            }
        }

        return true;
    }

    private Map<String, Object> parseConditions(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
}
