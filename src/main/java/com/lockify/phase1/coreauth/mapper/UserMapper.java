package com.lockify.phase1.coreauth.mapper;

import com.lockify.phase1.coreauth.dto.UserResponse;
import com.lockify.shared.domain.entity.Role;
import com.lockify.shared.domain.entity.User;
import com.lockify.phase1.coreauth.security.LockifyUserDetails;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entity <-> DTO mapping - response me sensitive fields expose nahi hote.
 */
public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(extractRoleNames(user.getRoles()))
                .build();
    }

    public static UserResponse toResponse(LockifyUserDetails userDetails) {
        return UserResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(userDetails.getRoleNames())
                .build();
    }

    private static Set<String> extractRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}
