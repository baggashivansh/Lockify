package com.lockify.shared.domain.entity;

/**
 * Granular permissions - role se map hote hain.
 * Example: ADMIN ko CREATE + UPDATE, USER ko sirf READ.
 */
public enum PermissionName {
    READ,
    CREATE,
    UPDATE,
    DELETE
}
